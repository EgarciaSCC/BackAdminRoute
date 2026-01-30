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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RutaService {

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
        if (ruta.busId() != null && !ruta.busId().isBlank()) {
            if (!busRepository.existsById(ruta.busId())) {
                throw new NotFoundException("Bus not found: " + ruta.busId());
            }
        }
        // validar conductor
        if (ruta.conductorId() != null && !ruta.conductorId().isBlank()) {
            if (!conductorRepository.existsById(ruta.conductorId())) {
                throw new NotFoundException("Conductor not found: " + ruta.conductorId());
            }
        }
        // validar coordinador
        if (ruta.coordinadorId() != null && !ruta.coordinadorId().isBlank()) {
            if (!coordinadorRepository.existsById(ruta.coordinadorId())) {
                throw new NotFoundException("Coordinador not found: " + ruta.coordinadorId());
            }
        }
        // validar sede
        if (ruta.sedeId() != null && !ruta.sedeId().isBlank()) {
            if (!sedeRepository.existsById(ruta.sedeId())) {
                throw new NotFoundException("Sede not found: " + ruta.sedeId());
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
        return repository.save(ruta);
    }

    public List<Ruta> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role != null && role == Role.ROLE_TRANSPORT && tenant != null) {
            List<Sede> sedes = sedeRepository.findByTransportId(tenant);
            var sedeIds = sedes.stream().map(Sede::getId).toList();
            return repository.findAll().stream().filter(r -> r.sedeId() != null && sedeIds.contains(r.sedeId())).collect(Collectors.toList());
        } else if (role != null && role == Role.ROLE_SCHOOL && tenant != null) {
            return repository.findAll().stream().filter(r -> tenant.equals(r.sedeId())).collect(Collectors.toList());
        } else {
            return repository.findAll();
        }
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

    // Nuevo: devolver DTO completo para una ruta
    public RutaResponseDTO getFullById(String id) {
        Ruta r = getById(id);
        Bus bus = null;
        Conductor conductor = null;
        Coordinador coordinador = null;
        Sede sede = null;
        List<Pasajero> pasajeros = new ArrayList<>();

        if (r.busId() != null && !r.busId().isBlank()) {
            bus = busRepository.findById(r.busId()).orElse(null);
        }
        if (r.conductorId() != null && !r.conductorId().isBlank()) {
            conductor = conductorRepository.findById(r.conductorId()).orElse(null);
        }
        if (r.coordinadorId() != null && !r.coordinadorId().isBlank()) {
            coordinador = coordinadorRepository.findById(r.coordinadorId()).orElse(null);
        }
        if (r.sedeId() != null && !r.sedeId().isBlank()) {
            sede = sedeRepository.findById(r.sedeId()).orElse(null);
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

    // Exponer todas las paradas temporales del sistema (opcional)
    public List<ParadaTemporalDTO> allParadasTemporales() {
        return paradasTemporales.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
}
