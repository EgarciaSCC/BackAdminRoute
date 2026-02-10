package nca.scc.com.admin.rutas.driver;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.auth.UsuarioRepository;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import nca.scc.com.admin.rutas.auth.Role;
import nca.scc.com.admin.rutas.bus.BusRepository;
import nca.scc.com.admin.rutas.bus.entity.Bus;
import nca.scc.com.admin.rutas.conductor.ConductorRepository;
import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import nca.scc.com.admin.rutas.driver.dto.DriverRouteHome;
import nca.scc.com.admin.rutas.driver.dto.DriverRoutePreview;
import nca.scc.com.admin.rutas.driver.dto.DriverRouteHistoryResponse;
import nca.scc.com.admin.rutas.historial.HistorialRutaRepository;
import nca.scc.com.admin.rutas.historial.entity.HistorialRuta;
import nca.scc.com.admin.rutas.historial.entity.enums.EstadoHistorialRuta;
import nca.scc.com.admin.rutas.ruta.RutaRepository;
import nca.scc.com.admin.rutas.ruta.RutaService;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final UsuarioRepository usuarioRepository;
    private final ConductorRepository conductorRepository;
    private final RutaRepository rutaRepository;
    private final HistorialRutaRepository historialRutaRepository;
    private final BusRepository busRepository;
    private final RutaService rutaService;

    public DriverService(UsuarioRepository usuarioRepository,
                         ConductorRepository conductorRepository,
                         RutaRepository rutaRepository,
                         HistorialRutaRepository historialRutaRepository,
                         BusRepository busRepository,
                         RutaService rutaService) {
        this.usuarioRepository = usuarioRepository;
        this.conductorRepository = conductorRepository;
        this.rutaRepository = rutaRepository;
        this.historialRutaRepository = historialRutaRepository;
        this.busRepository = busRepository;
        this.rutaService = rutaService;
    }

    /**
     * Resuelve el conductor/coordinador asociado al usuario autenticado.
     * Requiere rol ROLE_TRANSPORT y conductorId O coordinadorId en Usuario.
     *
     * @return Conductor si el usuario tiene conductorId, coordinador si tiene coordinadorId
     * @throws ResponseStatusException si no tiene ninguno o rol es incorrecto
     */
    public Conductor resolveDriverFromAuth() {
        Jwt jwt = getJwt();
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }
        String username = jwt.getSubject();
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario no encontrado"));
        if (user.getRole() != Role.ROLE_TRANSPORT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol ROLE_TRANSPORT requerido");
        }

        // Primero intentar conductorId (conductor)
        String conductorId = user.getConductorId();
        if (conductorId != null && !conductorId.isBlank()) {
            return conductorRepository.findById(conductorId)
                    .orElseThrow(() -> new NotFoundException("Conductor no encontrado: " + conductorId));
        }

        // Si no tiene conductorId, es coordinador - retornar un "Conductor" proxy
        // que representa al coordinador (ambos usan la misma lógica de rutas)
        String coordinadorId = user.getCoordinadorId();
        if (coordinadorId != null && !coordinadorId.isBlank()) {
            // Crear un objeto Conductor que represente al coordinador
            // Esto permite que el coordinador use la misma lógica de getRoutesToday()
            Conductor proxy = new Conductor();
            proxy.setId(coordinadorId);
            proxy.setNombre(user.getNombre());
            // Marcar como coordinador para futuras validaciones
            return proxy;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Ni conductorId ni coordinadorId asociados al usuario");
    }

    public DriverRouteHome getRoutesToday() {
        Conductor driver = resolveDriverFromAuth();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<Ruta> rutasAsignadas = getAssignedRoutes(driver);
        if (rutasAsignadas.isEmpty()) {
            return emptyRouteHomeTodayResponse(driver.getId(), driver.getNombre(), today);
        }
        Set<String> rutaIds = rutasAsignadas.stream().map(Ruta::getId).collect(Collectors.toSet());
        List<HistorialRuta> historiales = historialRutaRepository.findByRutaIdInAndFechaOrderByHoraInicioAsc(rutaIds, today);
        List<Ruta> activeRoutes = new ArrayList<>();
        List<Ruta> scheduledRoutes = new ArrayList<>();
        List<Ruta> completedRoutes = new ArrayList<>();

        activeRoutes = rutasAsignadas.stream()
                .filter(Ruta -> Ruta.getEstado() != null && Ruta.getEstado().equalsIgnoreCase("ACTIVE"))
                .toList();

        scheduledRoutes = rutasAsignadas.stream()
                .filter(Ruta -> Ruta.getEstado() != null && Ruta.getEstado().equalsIgnoreCase("PROGRAMMED"))
                .toList();

        completedRoutes = rutasAsignadas.stream()
                .filter(Ruta -> Ruta.getEstado() != null && Ruta.getEstado().equalsIgnoreCase("COMPLETED"))
                .toList();

        DriverRouteHome resp = new DriverRouteHome();
        resp.setDriverId(driver.getId());
        resp.setDriverName(driver.getNombre());
        resp.setDate(today);
        resp.setActiveRoutes(activeRoutes);
        resp.setScheduledRoutes(scheduledRoutes);
        resp.setCompletedRoutes(completedRoutes);
        return resp;
    }

    public List<DriverRoutePreview> getRoutesProgrammed() {
        Conductor driver = resolveDriverFromAuth();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<Ruta> rutasAsignadas = getAssignedRoutes(driver);
        if (rutasAsignadas.isEmpty()) {
            return List.of();
        }

        Set<String> rutaIds = rutasAsignadas.stream().map(Ruta::getId).collect(Collectors.toSet());
        List<HistorialRuta> historiales = historialRutaRepository.findByRutaIdInAndFechaOrderByHoraInicioAsc(rutaIds, today);

        LocalTime nowPlus30 = LocalTime.now().plusMinutes(30);
        List<DriverRoutePreview> scheduled = new ArrayList<>();

        for (HistorialRuta h : historiales) {
            Ruta ruta = rutaRepository.findById(h.getRutaId()).orElse(null);
            if (ruta == null) continue;
            DriverRoutePreview preview = toPreview(h, ruta);
            LocalTime start = parseTime(h.getHoraInicio());
            if (start != null && start.isAfter(nowPlus30)) {
                scheduled.add(preview);
            }
        }

        return scheduled;
    }

    public List<DriverRoutePreview> getRoutesHistory() {
        Conductor driver = resolveDriverFromAuth();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<Ruta> rutasAsignadas = getAssignedRoutes(driver);
        if (rutasAsignadas.isEmpty()) {
            return List.of();
        }

        Set<String> rutaIds = rutasAsignadas.stream().map(Ruta::getId).collect(Collectors.toSet());
        List<HistorialRuta> historiales = historialRutaRepository.findByRutaIdInAndFechaOrderByHoraInicioAsc(rutaIds, today);

        List<DriverRoutePreview> completed = new ArrayList<>();

        for (HistorialRuta h : historiales) {
            Ruta ruta = rutaRepository.findById(h.getRutaId()).orElse(null);
            if (ruta == null) continue;
            DriverRoutePreview preview = toPreview(h, ruta);
            if (h.getEstado() == EstadoHistorialRuta.completada) {
                completed.add(preview);
            }
        }

        return completed;
    }

    private List<Ruta> getAssignedRoutes(Conductor driver) {
        List<Ruta> rutasAsignadas = new ArrayList<>();
        rutasAsignadas.addAll(rutaRepository.findByConductorId(driver.getId()));
        rutasAsignadas.addAll(rutaRepository.findByCoordinadorId(driver.getId()));
        return rutasAsignadas.stream().distinct().toList();
    }

    public DriverRouteHistoryResponse getRoutesHistory(String startDate, String endDate, int page, int limit) {
        Conductor driver = resolveDriverFromAuth();
        List<Ruta> rutasDelConductor = rutaRepository.findByConductorId(driver.getId());
        if (rutasDelConductor.isEmpty()) {
            return emptyHistoryResponse(page, limit);
        }
        Set<String> rutaIds = rutasDelConductor.stream().map(Ruta::getId).collect(Collectors.toSet());

        String start = startDate != null && !startDate.isBlank() ? startDate : "2000-01-01";
        String end = endDate != null && !endDate.isBlank() ? endDate : "2099-12-31";
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        Page<HistorialRuta> pageResult = historialRutaRepository.findByRutaIdInAndFechaBetweenAndEstadoOrderByFechaDescHoraInicioDesc(
                rutaIds, start, end, EstadoHistorialRuta.completada, PageRequest.of(Math.max(0, page - 1), safeLimit));

        List<DriverRoutePreview> routes = new ArrayList<>();
        for (HistorialRuta h : pageResult.getContent()) {
            Ruta ruta = rutaRepository.findById(h.getRutaId()).orElse(null);
            if (ruta != null) {
                routes.add(toPreview(h, ruta));
            }
        }

        List<HistorialRuta> allForSummary = historialRutaRepository.findByRutaIdInAndFechaBetweenAndEstado(
                rutaIds, start, end, EstadoHistorialRuta.completada);
        int totalStudents = allForSummary.stream().mapToInt(HistorialRuta::getEstudiantesRecogidos).sum();
        double avgDuration = 0;
        if (!allForSummary.isEmpty()) {
            long totalMin = 0;
            int count = 0;
            for (HistorialRuta h : allForSummary) {
                LocalTime hi = parseTime(h.getHoraInicio());
                LocalTime hf = parseTime(h.getHoraFin());
                if (hi != null && hf != null) {
                    totalMin += java.time.Duration.between(hi, hf).toMinutes();
                    count++;
                }
            }
            avgDuration = count > 0 ? (double) totalMin / count : 0;
        }

        DriverRouteHistoryResponse resp = new DriverRouteHistoryResponse();
        resp.setRoutes(routes);
        DriverRouteHistoryResponse.PaginationDto pag = new DriverRouteHistoryResponse.PaginationDto();
        pag.setPage(page);
        pag.setLimit(safeLimit);
        pag.setTotal(pageResult.getTotalElements());
        pag.setTotalPages(pageResult.getTotalPages());
        resp.setPagination(pag);
        DriverRouteHistoryResponse.SummaryDto sum = new DriverRouteHistoryResponse.SummaryDto();
        sum.setTotalRoutes((int) pageResult.getTotalElements());
        sum.setTotalStudentsTransported(totalStudents);
        sum.setAverageDuration(avgDuration);
        resp.setSummary(sum);
        return resp;
    }

    private DriverRoutePreview toPreview(HistorialRuta h, Ruta ruta) {
        DriverRoutePreview p = new DriverRoutePreview();
        p.setId(h.getId());
        p.setName(ruta.getNombre() != null ? ruta.getNombre() : "");
        p.setDirection("to_school");
        boolean completed = h.getEstado() == EstadoHistorialRuta.completada;
        p.setStatus(completed ? "completed" : (isInProgress(h) ? "in_progress" : "not_started"));
        p.setEstimatedStartTime(h.getHoraInicio());
        p.setEstimatedEndTime(h.getHoraFin());
        if (completed) {
            p.setActualStartTime(h.getHoraInicio());
            p.setActualEndTime(h.getHoraFin());
            p.setStudentsTransported(h.getEstudiantesRecogidos());
        }
        try {
            p.setStopsCount(rutaService.listParadasTemporales(ruta.getId()).size());
        } catch (Exception e) {
            p.setStopsCount(0);
        }
        p.setStudentsCount(h.getEstudiantesTotales());
        Bus bus = ruta.busId() != null ? busRepository.findById(ruta.busId()).orElse(null) : null;
        if (bus != null) {
            p.setBusId(bus.getId());
            p.setBusPlate(bus.getPlaca() != null ? bus.getPlaca() : "");
        } else {
            p.setBusId(ruta.busId());
            p.setBusPlate("");
        }
        return p;
    }

    private boolean isInProgress(HistorialRuta h) {
        LocalTime now = LocalTime.now();
        LocalTime hi = parseTime(h.getHoraInicio());
        LocalTime hf = parseTime(h.getHoraFin());
        if (hi == null || hf == null) return false;
        return !now.isBefore(hi) && now.isBefore(hf);
    }

    private static LocalTime parseTime(String time) {
        if (time == null || time.isBlank()) return null;
        try {
            return LocalTime.parse(time.trim(), TIME_FMT);
        } catch (Exception e) {
            return null;
        }
    }

    private static DriverRouteHome emptyRouteHomeTodayResponse(String driverId, String driverName, String date) {
        DriverRouteHome r = new DriverRouteHome();
        r.setDriverId(driverId);
        r.setDriverName(driverName);
        r.setDate(date);
        r.setActiveRoutes(List.of());
        r.setScheduledRoutes(List.of());
        r.setCompletedRoutes(List.of());
        return r;
    }

    private static DriverRouteHistoryResponse emptyHistoryResponse(int page, int limit) {
        DriverRouteHistoryResponse r = new DriverRouteHistoryResponse();
        r.setRoutes(List.of());
        DriverRouteHistoryResponse.PaginationDto pag = new DriverRouteHistoryResponse.PaginationDto();
        pag.setPage(page);
        pag.setLimit(limit);
        pag.setTotal(0);
        pag.setTotalPages(0);
        r.setPagination(pag);
        DriverRouteHistoryResponse.SummaryDto sum = new DriverRouteHistoryResponse.SummaryDto();
        sum.setTotalRoutes(0);
        sum.setTotalStudentsTransported(0);
        sum.setAverageDuration(0);
        r.setSummary(sum);
        return r;
    }

    private static Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt) {
            return (Jwt) auth.getPrincipal();
        }
        return null;
    }
}
