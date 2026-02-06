package nca.scc.com.admin.rutas.ruta;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.ruta.dto.ParadaTemporalDTO;
import nca.scc.com.admin.rutas.ruta.dto.RutaResponseDTO;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import nca.scc.com.admin.rutas.sede.SedeRepository;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.rutas.bus.BusRepository;
import nca.scc.com.admin.rutas.bus.entity.Bus;
import nca.scc.com.admin.rutas.conductor.ConductorRepository;
import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import nca.scc.com.admin.rutas.coordinador.CoordinadorRepository;
import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;
import nca.scc.com.admin.rutas.pasajero.PasajeroRepository;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.security.SecurityUtils;
import nca.scc.com.admin.rutas.auth.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RutaService {

    private static final Logger log = LoggerFactory.getLogger(RutaService.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final RutaRepository repository;
    private final SedeRepository sedeRepository;
    private final BusRepository busRepository;
    private final ConductorRepository conductorRepository;
    private final CoordinadorRepository coordinadorRepository;
    private final PasajeroRepository pasajeroRepository;

    // almacenamiento en memoria para paradas temporales por ruta
    private final Map<String, List<ParadaTemporalDTO>> paradasTemporales = new ConcurrentHashMap<>();

    public RutaService(RutaRepository repository,
                       SedeRepository sedeRepository,
                       BusRepository busRepository,
                       ConductorRepository conductorRepository,
                       CoordinadorRepository coordinadorRepository,
                       PasajeroRepository pasajeroRepository) {
        this.repository = repository;
        this.sedeRepository = sedeRepository;
        this.busRepository = busRepository;
        this.conductorRepository = conductorRepository;
        this.coordinadorRepository = coordinadorRepository;
        this.pasajeroRepository = pasajeroRepository;
    }

    // Crear ruta validando existencia de referencias
    public Ruta create(Ruta ruta) {
        // validar bus
        if (ruta.getBusId() != null && !ruta.getBusId().isBlank()) {
            if (!busRepository.existsById(ruta.getBusId())) {
                throw new NotFoundException("Bus not found: " + ruta.getBusId());
            }
        }
        // validar conductor
        if (ruta.getConductorId() != null && !ruta.getConductorId().isBlank()) {
            if (!conductorRepository.existsById(ruta.getConductorId())) {
                throw new NotFoundException("Conductor not found: " + ruta.getConductorId());
            }
        }
        // validar coordinador
        if (ruta.getCoordinadorId() != null && !ruta.getCoordinadorId().isBlank()) {
            if (!coordinadorRepository.existsById(ruta.getCoordinadorId())) {
                throw new NotFoundException("Coordinador not found: " + ruta.getCoordinadorId());
            }
        }
        // validar sede
        if (ruta.getSedeId() != null && !ruta.getSedeId().isBlank()) {
            if (!sedeRepository.existsById(ruta.getSedeId())) {
                throw new NotFoundException("Sede not found: " + ruta.getSedeId());
            }
        }
        // validar estudiantes
        if (ruta.getEstudiantes() != null) {
            for (String pid : ruta.getEstudiantes()) {
                if (!pasajeroRepository.existsById(pid)) {
                    throw new NotFoundException("Pasajero not found: " + pid);
                }
            }
        }
        // No forzamos estado: la entidad Ruta tiene por defecto DRAFT en @PrePersist
        return repository.save(ruta);
    }

    public List<Ruta> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        // Administradores pueden ver rutas en DRAFT y PUBLISHED
        if (role == Role.ROLE_ADMIN) {
            return repository.findAll();
        }

        if (role == Role.ROLE_TRANSPORT && tenant != null) {
            List<Sede> sedes = sedeRepository.findByTransportId(tenant);
            var sedeIds = sedes.stream().map(Sede::getId).toList();
            return repository.findAll().stream()
                    .filter(r -> r.getSedeId() != null && sedeIds.contains(r.getSedeId()))
                    .toList();
        } else if (role == Role.ROLE_SCHOOL && tenant != null) {
            return repository.findAll().stream()
                    .filter(r -> tenant.equals(r.getSedeId()))
                    .toList();
        }

        return repository.findAll();
    }

    public Ruta getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta not found: " + id));
    }

    public Ruta update(String id, Ruta ruta) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Ruta not found: " + id);
        }
        ruta.setId(id);
        return repository.save(ruta);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Ruta not found: " + id);
        }
        repository.deleteById(id);
        paradasTemporales.remove(id);
    }

    // Publishes a draft route: cambia estado de DRAFT a ACTIVE
    public Ruta publish(String id) {
        Ruta r = getById(id);
        if (r.getEstado() != null && r.getEstado().equalsIgnoreCase("ACTIVE")) {
            return r; // ya publicado
        }
        r.setEstado("ACTIVE");
        return repository.save(r);
    }

    // Nuevo: devolver DTO completo para una ruta
    public RutaResponseDTO getFullById(String id) {
        Ruta r = getById(id);
        Bus bus = null;
        Conductor conductor = null;
        Coordinador coordinador = null;
        Sede sede = null;
        List<Pasajero> pasajeros = new ArrayList<>();

        if (r.getBusId() != null && !r.getBusId().isBlank()) {
            bus = busRepository.findById(r.getBusId()).orElse(null);
        }
        if (r.getConductorId() != null && !r.getConductorId().isBlank()) {
            conductor = conductorRepository.findById(r.getConductorId()).orElse(null);
        }
        if (r.getCoordinadorId() != null && !r.getCoordinadorId().isBlank()) {
            coordinador = coordinadorRepository.findById(r.getCoordinadorId()).orElse(null);
        }
        if (r.getSedeId() != null && !r.getSedeId().isBlank()) {
            sede = sedeRepository.findById(r.getSedeId()).orElse(null);
        }
        if (r.getEstudiantes() != null) {
            for (String pid : r.getEstudiantes()) {
                pasajeroRepository.findById(pid).ifPresent(pasajeros::add);
            }
        }
        return new RutaResponseDTO(r, bus, conductor, coordinador, sede, pasajeros);
    }

    public List<RutaResponseDTO> listAllFull() {
        return listAll().stream().map(r -> getFullById(r.getId())).collect(Collectors.toList());
    }

    /**
     * Valida si un estudiante puede ser asignado a una ruta específica.
     *
     * Reglas de validación:
     * 1. Máximo 2 rutas por día (1 RECOGIDA + 1 LLEVADA)
     * 2. No solapamiento de horarios
     * 3. Capacidad del bus no excedida
     * 4. Estudiante existe y es activo
     *
     * @param estudianteId ID del estudiante
     * @param rutaId ID de la ruta destino
     * @throws IllegalStateException si alguna validación falla
     */
    public void validarEstudianteEnRuta(String estudianteId, String rutaId) {
        log.debug("Validando asignación de estudiante {} a ruta {}", estudianteId, rutaId);

        // Validar que el estudiante existe
        Pasajero estudiante = pasajeroRepository.findById(estudianteId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado: " + estudianteId));

        if (estudiante.getActivo() == null || !estudiante.getActivo()) {
            throw new IllegalStateException("Estudiante inactivo no puede ser asignado: " + estudianteId);
        }

        // Validar que la ruta existe
        Ruta rutaDestino = repository.findById(rutaId)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada: " + rutaId));

        if (rutaDestino.getTipoRuta() == null) {
            throw new IllegalStateException("Ruta sin tipo especificado: " + rutaId);
        }

        if (rutaDestino.getHoraInicio() == null || rutaDestino.getHoraFin() == null) {
            throw new IllegalStateException("Ruta sin horarios especificados: " + rutaId);
        }

        if (rutaDestino.getFecha() == null) {
            throw new IllegalStateException("Ruta sin fecha especificada: " + rutaId);
        }

        // Obtener todas las rutas del estudiante en el mismo día
        List<Ruta> rutasDelDia = repository.findAll().stream()
                .filter(r -> r.getEstudiantes() != null && r.getEstudiantes().contains(estudianteId))
                .filter(r -> r.getFecha() != null && r.getFecha().equals(rutaDestino.getFecha()))
                .toList();

        log.debug("Estudiante {} tiene {} rutas en el día {}", estudianteId, rutasDelDia.size(), rutaDestino.getFecha());        // Validación 1: Máximo 2 rutas por día
        if (rutasDelDia.size() >= 2) {
            throw new IllegalStateException(String.format(
                    "Estudiante %s ya tiene asignadas 2 rutas en el día %s. Máximo permitido: 2",
                    estudianteId, rutaDestino.getFecha()));
        }

        // Validación 2: Máximo 1 ruta por tipo (RECOGIDA y LLEVADA)
        long rutasDelMismoTipo = rutasDelDia.stream()
                .filter(r -> r.getTipoRuta() == rutaDestino.getTipoRuta())
                .count();

        if (rutasDelMismoTipo > 0) {
            throw new IllegalStateException(String.format(
                    "Estudiante %s ya tiene asignada una ruta de tipo %s en el día %s. No se puede asignar dos rutas del mismo tipo",
                    estudianteId, rutaDestino.getTipoRuta(), rutaDestino.getFecha()));
        }

        // Validación 3: Verificar solapamiento de horarios
        for (Ruta rutaExistente : rutasDelDia) {
            if (horariosSeSuperponen(rutaExistente.getHoraInicio(), rutaExistente.getHoraFin(),
                    rutaDestino.getHoraInicio(), rutaDestino.getHoraFin())) {
                throw new IllegalStateException(String.format(
                        "Conflicto de horarios: Estudiante %s ya tiene ruta de %s a %s. Nueva ruta: %s a %s",
                        estudianteId, rutaExistente.getHoraInicio(), rutaExistente.getHoraFin(),
                        rutaDestino.getHoraInicio(), rutaDestino.getHoraFin()));
            }
        }

        // Validación 4: Verificar capacidad del bus
        if (rutaDestino.getBusId() != null && !rutaDestino.getBusId().isBlank()) {
            Bus bus = busRepository.findById(rutaDestino.getBusId())
                    .orElseThrow(() -> new NotFoundException("Bus no encontrado: " + rutaDestino.getBusId()));

           int capacidadTotal = bus.getCapacidad();
           int capacidadActual = rutaDestino.getCapacidadActual() != null ? rutaDestino.getCapacidadActual() : 0;

            if (capacidadActual >= capacidadTotal) {
                throw new IllegalStateException(String.format(
                        "Bus %s sin capacidad disponible. Capacidad: %d/%d",
                        rutaDestino.getBusId(), capacidadActual, capacidadTotal));
            }
        }

        log.info("Validación exitosa para estudiante {} en ruta {}", estudianteId, rutaId);
    }

    /**
     * Verifica si dos rangos horarios se superponen.
     * Formato esperado: "HH:mm"
     */
    private boolean horariosSeSuperponen(String inicio1, String fin1, String inicio2, String fin2) {
        try {
            LocalTime i1 = LocalTime.parse(inicio1, TIME_FORMATTER);
            LocalTime f1 = LocalTime.parse(fin1, TIME_FORMATTER);
            LocalTime i2 = LocalTime.parse(inicio2, TIME_FORMATTER);
            LocalTime f2 = LocalTime.parse(fin2, TIME_FORMATTER);

            // Dos rangos [a,b] y [c,d] se superponen si: a < d AND c < b
            return i1.isBefore(f2) && i2.isBefore(f1);
        } catch (Exception e) {
            log.warn("Error al parsear horarios: {} - {} vs {} - {}", inicio1, fin1, inicio2, fin2, e);
            return false;
        }
    }

    /**
     * Asigna un estudiante a una ruta tras validación exitosa.
     */
    public Ruta asignarEstudianteARuta(String rutaId, String estudianteId) {
        log.debug("Asignando estudiante {} a ruta {}", estudianteId, rutaId);

        // Validar todas las reglas de negocio
        validarEstudianteEnRuta(estudianteId, rutaId);

        Ruta ruta = getById(rutaId);

        // Inicializar lista de estudiantes si es nula
        if (ruta.getEstudiantes() == null) {
            ruta.setEstudiantes(new ArrayList<>());
        }

        // Añadir el estudiante a la lista
        ruta.getEstudiantes().add(estudianteId);

        // Incrementar capacidad actual
        Integer capacidadActual = ruta.getCapacidadActual() != null ? ruta.getCapacidadActual() : 0;
        ruta.setCapacidadActual(capacidadActual + 1);

        Ruta actualizada = repository.save(ruta);
        log.info("Estudiante {} asignado a ruta {} exitosamente", estudianteId, rutaId);
        return actualizada;
    }

    // Paradas temporales API (in-memory)
    public ParadaTemporalDTO createParadaTemporal(String rutaId, ParadaTemporalDTO dto) {
        // validar existencia de ruta
        if (!repository.existsById(rutaId)) {
            throw new NotFoundException("Ruta not found: " + rutaId);
        }
        dto.setCreatedAt(Instant.now());
        dto.setExpiraAt(dto.getCreatedAt().plus(24, ChronoUnit.HOURS));
        dto.setEstado("pendiente");
        paradasTemporales.computeIfAbsent(rutaId, k -> new ArrayList<>()).add(dto);
        return dto;
    }

    public List<ParadaTemporalDTO> listParadasTemporales(String rutaId) {
        if (!repository.existsById(rutaId)) {
            throw new NotFoundException("Ruta not found: " + rutaId);
        }
        return paradasTemporales.getOrDefault(rutaId, new ArrayList<>());
    }

    public ParadaTemporalDTO approveParadaTemporal(String rutaId, String paradaId, String aprobadoPor) {
        ParadaTemporalDTO p = findParadaOrThrow(rutaId, paradaId);
        p.setEstado("aprobada");
        p.setAprobadoPor(aprobadoPor);
        p.setFechaAprobacion(Instant.now());
        // TODO: actualizar direccion del estudiante / notificaciones
        return p;
    }

    public ParadaTemporalDTO rejectParadaTemporal(String rutaId, String paradaId, String aprobadoPor, String comentario) {
        ParadaTemporalDTO p = findParadaOrThrow(rutaId, paradaId);
        p.setEstado("rechazada");
        p.setAprobadoPor(aprobadoPor);
        p.setFechaAprobacion(Instant.now());
        p.setComentario(comentario);
        return p;
    }

    private ParadaTemporalDTO findParadaOrThrow(String rutaId, String paradaId) {
        List<ParadaTemporalDTO> list = paradasTemporales.get(rutaId);
        if (list == null) throw new NotFoundException("No paradas for ruta: " + rutaId);
        return list.stream().filter(p -> p.getId().equals(paradaId))
                .findFirst().orElseThrow(() -> new NotFoundException("Parada not found: " + paradaId));
    }
}
