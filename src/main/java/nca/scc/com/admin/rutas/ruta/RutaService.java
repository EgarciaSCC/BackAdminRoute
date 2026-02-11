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

    /**
     * CREAR RUTA CON VALIDACIÓN DE OWNERSHIP
     * Reglas:
     * - ROLE_SCHOOL: Solo rutas de sus propias sedes
     * - ROLE_TRANSPORT: Rutas con su tenant TRANSPORT
     * - ROLE_ADMIN: Sin restricciones
     * - Validar existencia de referencias
     * - Establecer tenant automáticamente
     */
    public Ruta create(Ruta ruta) {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        // STEP 1: Validar acceso basado en rol
        if (role == Role.ROLE_SCHOOL) {
            // ROLE_SCHOOL: Solo puede crear rutas en sus sedes
            Sede sede = sedeRepository.findById(ruta.getSedeId())
                    .orElseThrow(() -> new NotFoundException("Sede no encontrada: " + ruta.getSedeId()));

            if (!sede.getTenant().equals(tenant)) {
                log.warn("DENEGADO: ROLE_SCHOOL intenta crear ruta en sede ajena - tenant: {}", tenant);
                throw new IllegalStateException("No tiene permiso para crear rutas en esta sede");
            }
            ruta.setTenant(tenant);  // Sede suya
        } else if (role == Role.ROLE_TRANSPORT) {
            // ROLE_TRANSPORT: Puede crear rutas asignándolas a sus conductores/coordinadores
            ruta.setTenant(tenant);  // Su tenant TRANSPORT
        } else if (role != Role.ROLE_ADMIN) {
            throw new IllegalStateException("Solo ROLE_ADMIN y ROLE_TRANSPORT pueden crear rutas");
        }

        // STEP 2: Validar referencias (bus, conductor, coordinador, sede, estudiantes)
        validateRutaReferences(ruta);

        // STEP 3: Validar estudiantes pertenecen a sedes autorizadas
        validateCrossTenantsAccess(ruta);

        // STEP 4: Guardar
        Ruta saved = repository.save(ruta);
        log.info("✅ Ruta creada: {} - Tenant: {} - Rol: {}", saved.getId(), tenant, role);
        return saved;
    }

    /**
     * LISTAR RUTAS CON VISIBILIDAD BASADA EN OWNERSHIP + CROSS-TENANT
     * ROLE_ADMIN: Ve todas
     * ROLE_SCHOOL: Ve rutas de su colegio + rutas donde tiene estudiantes
     * ROLE_TRANSPORT: Ve rutas suyas + rutas donde tiene estudiantes asignados
     * - Si es conductor/coordinador: Ve solo rutas asignadas
     * - Si es admin.transport: Ve rutas de su tenant
     */
    public List<Ruta> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role == Role.ROLE_ADMIN) {
            return repository.findAll();
        }

        if (role == Role.ROLE_SCHOOL && tenant != null) {
            // Rutas propiedad del colegio
            return repository.findRutasVisiblesAlColegio(tenant);
        }

        if (role == Role.ROLE_TRANSPORT && tenant != null) {
            // Estrategia: si tiene conductorId/coordinadorId en usuario, es driver específico
            // Si no, es admin.transport que ve todas sus rutas
            String personaId = SecurityUtils.getUserIdClaim();

            // Intentar: ¿es un driver asignado? (tiene rutas asignadas)
            List<Ruta> rutasAsignadas = repository.findByAsignadoA(personaId);
            if (!rutasAsignadas.isEmpty()) {
                return rutasAsignadas;  // Es conductor/coordinador específico
            }

            // Sino: es admin.transport, ve rutas de su tenant TRANSPORT
            return repository.findRutasVisiblesAlTransport(tenant);
        }

        return List.of();
    }

    /**
     * OBTENER RUTA CON VALIDATION DE OWNERSHIP + CROSS-TENANT ACCESS
     *
     * Patterns:
     * 1. Ownership: Usuario puede ver rutas de su tenant
     * 2. Cross-Tenant: Usuario puede ver ruta si tiene un estudiante/persona asignada
     */
    public Ruta getById(String id) {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        Ruta ruta = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada: " + id));

        // Validar acceso
        if (!canAccessRoute(ruta, role, tenant)) {
            log.warn("DENEGADO: {} intenta acceder a ruta {} (tenant: {})", role, id, tenant);
            throw new NotFoundException("Ruta no encontrada");
        }

        return ruta;
    }

    /**
     * ACTUALIZAR RUTA CON VALIDACIÓN DE OWNERSHIP
     *
     * Solo el dueño de la ruta puede actualizar
     */
    public Ruta update(String id, Ruta rutaUpdate) {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        Ruta ruta = getById(id);  // Valida acceso

        // Validar que solo owner puede actualizar
        if (!ruta.getTenant().equals(tenant) && role != Role.ROLE_ADMIN) {
            log.warn("DENEGADO: {} intenta actualizar ruta ajena {}", role, id);
            throw new IllegalStateException("No tiene permiso para actualizar esta ruta");
        }

        // Actualizar solo campos permitidos
        if (rutaUpdate.getNombre() != null) ruta.setNombre(rutaUpdate.getNombre());
        if (rutaUpdate.getEstado() != null) ruta.setEstado(rutaUpdate.getEstado());
        if (rutaUpdate.getHoraInicio() != null) ruta.setHoraInicio(rutaUpdate.getHoraInicio());
        if (rutaUpdate.getHoraFin() != null) ruta.setHoraFin(rutaUpdate.getHoraFin());

        // NO permitir cambiar ownership
        // NO permitir cambiar estudiantes (usar asignarEstudianteARuta)

        Ruta updated = repository.save(ruta);
        log.info("✅ Ruta actualizada: {}", id);
        return updated;
    }

    /**
     * ELIMINAR RUTA CON VALIDACIÓN DE OWNERSHIP
     */
    public void delete(String id) {
        Ruta ruta = getById(id);  // Valida acceso

        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (!ruta.getTenant().equals(tenant) && role != Role.ROLE_ADMIN) {
            log.warn("DENEGADO: {} intenta eliminar ruta ajena {}", role, id);
            throw new IllegalStateException("No tiene permiso para eliminar esta ruta");
        }

        repository.deleteById(id);
        paradasTemporales.remove(id);
        log.info("✅ Ruta eliminada: {}", id);
    }

    // ===== MÉTODOS AUXILIARES DE VALIDACIÓN =====

    /**
     * Valida si un usuario puede acceder a una ruta
     * Soporta: Ownership + Cross-Tenant (a través de assignment)
     */
    private boolean canAccessRoute(Ruta ruta, Role role, String tenant) {
        // Ownership: es el dueño
        if (tenant.contains(ruta.getTenant())) {
            if (role == Role.ROLE_SCHOOL || role == Role.ROLE_TRANSPORT) {
                return true;
            }
        }

        // Cross-Tenant: asignado a esta ruta (ROLE_TRANSPORT como conductor/coordinador)
        if (role == Role.ROLE_TRANSPORT) {
            String personaId = SecurityUtils.getUserIdClaim();
            return ruta.getConductorId() != null && ruta.getConductorId().equals(personaId) ||
                   ruta.getCoordinadorId() != null && ruta.getCoordinadorId().equals(personaId);
        }

        return false;
    }

    /**
     * Valida que todas las referencias existan
     */
    private void validateRutaReferences(Ruta ruta) {
        if (ruta.getBusId() != null && !ruta.getBusId().isBlank()) {
            if (!busRepository.existsById(ruta.getBusId())) {
                throw new NotFoundException("Bus no encontrado: " + ruta.getBusId());
            }
        }
        if (ruta.getConductorId() != null && !ruta.getConductorId().isBlank()) {
            if (!conductorRepository.existsById(ruta.getConductorId())) {
                throw new NotFoundException("Conductor no encontrado: " + ruta.getConductorId());
            }
        }
        if (ruta.getCoordinadorId() != null && !ruta.getCoordinadorId().isBlank()) {
            if (!coordinadorRepository.existsById(ruta.getCoordinadorId())) {
                throw new NotFoundException("Coordinador no encontrado: " + ruta.getCoordinadorId());
            }
        }
        if (ruta.getSedeId() != null && !ruta.getSedeId().isBlank()) {
            if (!sedeRepository.existsById(ruta.getSedeId())) {
                throw new NotFoundException("Sede no encontrada: " + ruta.getSedeId());
            }
        }
        if (ruta.getEstudiantes() != null) {
            for (String pId : ruta.getEstudiantes()) {
                if (!pasajeroRepository.existsById(pId)) {
                    throw new NotFoundException("Pasajero no encontrado: " + pId);
                }
            }
        }
    }

    /**
     * Valida que los estudiantes asignados pertenezcan a sedes autorizadas
     * Pattern: Estudiantes pueden venir de múltiples SCHOOL tenants
     * PERO solo si el TRANSPORT tiene acceso a esas sedes
     */
    private void validateCrossTenantsAccess(Ruta ruta) {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (ruta.getEstudiantes() == null || ruta.getEstudiantes().isEmpty()) {
            return;
        }

        // Para ROLE_TRANSPORT: validar que los estudiantes pertenecen a sedes que administra
        if (role == Role.ROLE_TRANSPORT) {
            List<Sede> sedesAutorizadas = sedeRepository.findByTransportId(tenant);
            for (String estudianteId : ruta.getEstudiantes()) {
                Pasajero p = pasajeroRepository.findById(estudianteId)
                        .orElseThrow(() -> new NotFoundException("Estudiante no encontrado: " + estudianteId));

                // Validar que la sede del estudiante es administrada por este transport
                boolean sedeAutorizada = sedesAutorizadas.stream()
                        .anyMatch(s -> s.getId().equals(p.getSedeId()));

                if (!sedeAutorizada) {
                    log.warn("DENEGADO: Estudiante {} de sede ajena asignado a ruta del TRANSPORT {}", estudianteId, tenant);
                    throw new IllegalStateException("No tiene permiso para asignar estudiantes de esa sede");
                }
            }
        }

        // Para ROLE_SCHOOL: validar que estudiantes son del colegio
        if (role == Role.ROLE_SCHOOL) {
            for (String estudianteId : ruta.getEstudiantes()) {
                Pasajero p = pasajeroRepository.findById(estudianteId)
                        .orElseThrow(() -> new NotFoundException("Estudiante no encontrado: " + estudianteId));

                if (!p.getTenant().equals(tenant)) {
                    log.warn("DENEGADO: Estudiante {} de colegio ajeno asignado por ROLE_SCHOOL {}", estudianteId, tenant);
                    throw new IllegalStateException("Solo puede asignar estudiantes de su colegio");
                }
            }
        }
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

    /**
     * Verifica si existen cruces de horarios entre rutas asignadas al mismo conductor.
     *
     * @param conductorId ID del conductor a verificar.
     * @return true si hay cruces de horarios, false en caso contrario.
     */
    public boolean verificarCrucesDeHorarios(String conductorId) {
        List<Ruta> rutas = repository.findByConductorId(conductorId);

        for (int i = 0; i < rutas.size(); i++) {
            Ruta ruta1 = rutas.get(i);
            LocalTime inicio1 = LocalTime.parse(ruta1.getHoraInicio(), TIME_FORMATTER);
            LocalTime fin1 = LocalTime.parse(ruta1.getHoraFin(), TIME_FORMATTER);

            for (int j = i + 1; j < rutas.size(); j++) {
                Ruta ruta2 = rutas.get(j);
                LocalTime inicio2 = LocalTime.parse(ruta2.getHoraInicio(), TIME_FORMATTER);
                LocalTime fin2 = LocalTime.parse(ruta2.getHoraFin(), TIME_FORMATTER);

                // Verificar si hay solapamiento
                if (inicio1.isBefore(fin2) && inicio2.isBefore(fin1)) {
                    log.warn("Cruce de horarios detectado entre rutas {} y {} para el conductor {}",
                             ruta1.getId(), ruta2.getId(), conductorId);
                    return true;
                }
            }
        }

        return false;
    }
}
