package nca.scc.com.admin.rutas;

import nca.scc.com.admin.rutas.auth.UsuarioRepository;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import nca.scc.com.admin.rutas.bus.BusRepository;
import nca.scc.com.admin.rutas.bus.entity.Bus;
import nca.scc.com.admin.rutas.bus.entity.enums.BusState;
import nca.scc.com.admin.rutas.bus.entity.enums.MotorType;
import nca.scc.com.admin.rutas.colegio.ColegioRepository;
import nca.scc.com.admin.rutas.colegio.entity.Colegio;
import nca.scc.com.admin.rutas.conductor.ConductorRepository;
import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import nca.scc.com.admin.rutas.conductor.entity.enums.ConductorState;
import nca.scc.com.admin.rutas.coordinador.CoordinadorRepository;
import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;
import nca.scc.com.admin.rutas.coordinador.entity.enums.CoordinadorState;
import nca.scc.com.admin.rutas.historial.enums.EstadoHistorialRuta;
import nca.scc.com.admin.rutas.novedad.NovedadRepository;
import nca.scc.com.admin.rutas.novedad.entity.Novedad;
import nca.scc.com.admin.rutas.novedad.entity.enums.NovedadCategoria;
import nca.scc.com.admin.rutas.novedad.entity.enums.NovedadTipo;
import nca.scc.com.admin.rutas.novedad.entity.enums.RolCreador;
import nca.scc.com.admin.rutas.ruta.RutaRepository;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import nca.scc.com.admin.rutas.rutaPasajeros.RutaPasajeroRepository;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.RutaPasajero;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.RutaPasajeroId;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.enums.EstadoRutaPasajeros;
import nca.scc.com.admin.rutas.sede.SedeRepository;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.rutas.pasajero.PasajeroRepository;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.rutas.historial.ruta.HistorialRutaRepository;
import nca.scc.com.admin.rutas.historial.ruta.entity.HistorialRuta;
import nca.scc.com.admin.rutas.auth.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Profile("default")
@Component
public class SeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedData.class);

    private final BusRepository busRepository;
    private final ConductorRepository conductorRepository;
    private final CoordinadorRepository coordinadorRepository;
    private final ColegioRepository colegioRepository;
    private final RutaRepository rutaRepository;
    private final SedeRepository sedeRepository;
    private final PasajeroRepository pasajeroRepository;
    private final NovedadRepository novedadRepository;
    private final HistorialRutaRepository historialRutaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RutaPasajeroRepository rutaPasajeroRepository;

    public SeedData(BusRepository busRepository,
                    ConductorRepository conductorRepository,
                    CoordinadorRepository coordinadorRepository,
                    ColegioRepository colegioRepository,
                    RutaRepository rutaRepository,
                    SedeRepository sedeRepository,
                    PasajeroRepository pasajeroRepository,
                    NovedadRepository novedadRepository,
                    HistorialRutaRepository historialRutaRepository,
                    UsuarioRepository usuarioRepository, RutaPasajeroRepository rutaPasajeroRepository) {
        this.busRepository = busRepository;
        this.conductorRepository = conductorRepository;
        this.coordinadorRepository = coordinadorRepository;
        this.colegioRepository = colegioRepository;
        this.rutaRepository = rutaRepository;
        this.sedeRepository = sedeRepository;
        this.pasajeroRepository = pasajeroRepository;
        this.novedadRepository = novedadRepository;
        this.historialRutaRepository = historialRutaRepository;
        this.usuarioRepository = usuarioRepository;
        this.rutaPasajeroRepository = rutaPasajeroRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting seed data population...");

        String transport1 = "transport-1";
        String defaultTenant = "default-tenant";

        // ========== CREAR COLEGIO ==========
        Colegio colegio = new Colegio(
                null,
                "NIT-001",
                "Colegio Simón Bolívar",
                "Carrera 7 # 123",
                "Bogotá",
                "Rector Principal",
                "colegio@example.com",
                transport1,
                "https://example.com/logo.png"
        );
        Colegio savedColegio = colegioRepository.save(colegio);
        log.info("✅ Colegio creado: {}", savedColegio.getId());

        // ========== CREAR SEDE ==========
        Sede sede = new Sede(
                null,
                savedColegio.getId(),
                "Sede Principal",
                "Carrera 7 # 123, Bogotá",
                "Bogotá",
                4.7110,
                -74.0721,
                transport1
        );
        sede.setTransportId(transport1);
        Sede savedSede = sedeRepository.save(sede);
        log.info("✅ Sede creada: {}", savedSede.getId());

        // ========== CREAR BUS ==========
        Bus bus = new Bus(
                null,
                "ABC-001",
                40,
                "Chevrolet",
                "Modelo 2023",
                "2025-01-01",
                "2026-12-31",
                MotorType.combustible,
                null,
                BusState.activo
        );
        Bus savedBus = busRepository.save(bus);
        log.info("✅ Bus creado: {}", savedBus.getId());

        // ========== CREAR CONDUCTORES ==========
        // Conductor principal que verá las rutas asignadas
        Conductor conductor = new Conductor(
                null,
                "Juan Pérez García",
                "CC",
                "1088123456",
                "LIC-2025-001",
                ConductorState.disponible,
                String.join(",", defaultTenant, transport1),
                "A1"
        );
        Conductor savedConductor = conductorRepository.save(conductor);
        log.info("✅ Conductor creado: {}", savedConductor.getId());

        // Conductor 2: Admin Transporte (para que admin.transport vea rutas asignadas en today)
        Conductor conductorAdmin = new Conductor(
                null,
                "Admin Transporte Conductor",
                "CC",
                "1088654321",
                "LIC-2025-ADMIN",
                ConductorState.disponible,
                String.join(",", defaultTenant, transport1),
                "A1"
        );
        Conductor savedConductorAdmin = conductorRepository.save(conductorAdmin);
        log.info("✅ Conductor 2 (Admin) creado: {}", savedConductorAdmin.getId());

        // ========== CREAR COORDINADOR ==========
        Coordinador coordinador = new Coordinador(
                null,
                "María López García",
                "CC",
                "1087654321",
                "coord@example.com",
                CoordinadorState.activo,
                String.join(",", defaultTenant, transport1)
        );
        Coordinador savedCoordinador = coordinadorRepository.save(coordinador);
        log.info("✅ Coordinador creado: {}", savedCoordinador.getId());

        // ========== CREAR ESTUDIANTES CON PARADAS ==========
        // Parada 2: 1 estudiante
        Pasajero est1 = new Pasajero(
                null,
                "Carlos Rodríguez",
                "MAT-2026-001",
                "4to Primaria",
                "Cra 5 #10-25",
                "San Alejo",
                4.7115,
                -74.0725,
                savedSede.getId(),
                null,
                transport1
        );

        // Parada 3: 2 estudiantes
        Pasajero est2 = new Pasajero(
                null,
                "Ana Martínez",
                "MAT-2026-002",
                "5to Primaria",
                "Cra 6 #12-30",
                "Chapinero",
                4.7120,
                -74.0730,
                savedSede.getId(),
                null,
                transport1
        );
        Pasajero est3 = new Pasajero(
                null,
                "Pedro González",
                "MAT-2026-003",
                "5to Primaria",
                "Cra 6 #12-35",
                "Chapinero",
                4.7122,
                -74.0728,
                savedSede.getId(),
                null,
                transport1
        );

        // Parada 4: 3 estudiantes
        Pasajero est4 = new Pasajero(
                null,
                "Lucía Fernández",
                "MAT-2026-004",
                "3ro Primaria",
                "Cra 8 #15-40",
                "Usaquén",
                4.7130,
                -74.0735,
                savedSede.getId(),
                null,
                transport1
        );
        Pasajero est5 = new Pasajero(
                null,
                "Diego Torres",
                "MAT-2026-005",
                "4to Primaria",
                "Cra 9 #16-50",
                "Usaquén",
                4.7135,
                -74.0732,
                savedSede.getId(),
                null,
                transport1
        );
        Pasajero est6 = new Pasajero(
                null,
                "Sofía Ramírez",
                "MAT-2026-006",
                "6to Primaria",
                "Cra 10 #18-60",
                "Usaquén",
                4.7140,
                -74.0738,
                savedSede.getId(),
                null,
                transport1
        );

        List<Pasajero> estudiantes = List.of(est1, est2, est3, est4, est5, est6);
        List<Pasajero> savedEstudiantes = pasajeroRepository.saveAll(estudiantes);
        log.info("✅ {} estudiantes creados", savedEstudiantes.size());

        // ========== CREAR PADRES CON CUENTAS DE USUARIO ==========
        String padrePassword = "padre123";
        // CVE-2025-22228: Validar longitud máxima de contraseña (BCrypt límite: 72 caracteres)
        validatePasswordLength(padrePassword);
        // Mitigar uso vulnerable de BCrypt.hashpw
        String padrePasswordHash = generateSecurePasswordHash(padrePassword);

        // Padre 1: padre de Carlos (est1)
        Usuario padreUser1 = new Usuario(
                "Roberto Rodríguez",
                "padre_roberto",
                padrePasswordHash,
                transport1,
                Role.ROLE_SCHOOL
        );
        padreUser1.setEmail("padre.roberto@example.com");
        Usuario savedPadreUser1 = usuarioRepository.save(padreUser1);

        // Actualizar estudiante 1 con padreId
        est1.setPadreId(savedPadreUser1.getId());
        pasajeroRepository.save(est1);
        log.info("✅ Padre 1 creado: {} (padre de Carlos) - username: padre_roberto", savedPadreUser1.getId());

        // Padre 2: padre de Ana y Pedro (est2, est3)
        Usuario padreUser2 = new Usuario(
                "Francisco Martínez",
                "padre_francisco",
                padrePasswordHash,
                transport1,
                Role.ROLE_SCHOOL
        );
        padreUser2.setEmail("padre.francisco@example.com");
        Usuario savedPadreUser2 = usuarioRepository.save(padreUser2);

        // Actualizar estudiantes 2 y 3 con padreId
        est2.setPadreId(savedPadreUser2.getId());
        est3.setPadreId(savedPadreUser2.getId());
        pasajeroRepository.saveAll(List.of(est2, est3));
        log.info("✅ Padre 2 creado: {} (padre de Ana y Pedro) - username: padre_francisco", savedPadreUser2.getId());

        // Padre 3: padre de Lucía y Diego (est4, est5)
        Usuario padreUser3 = new Usuario(
                "Patricia Fernández",
                "padre_patricia",
                padrePasswordHash,
                transport1,
                Role.ROLE_SCHOOL
        );
        padreUser3.setEmail("padre.patricia@example.com");
        Usuario savedPadreUser3 = usuarioRepository.save(padreUser3);

        est4.setPadreId(savedPadreUser3.getId());
        est5.setPadreId(savedPadreUser3.getId());
        pasajeroRepository.saveAll(List.of(est4, est5));
        log.info("✅ Padre 3 creado: {} (padre de Lucía y Diego) - username: padre_patricia", savedPadreUser3.getId());

        // Padre 4: padre de Sofía (est6)
        Usuario padreUser4 = new Usuario(
                "Gustavo Ramírez",
                "padre_gustavo",
                padrePasswordHash,
                transport1,
                Role.ROLE_SCHOOL
        );
        padreUser4.setEmail("padre.gustavo@example.com");
        Usuario savedPadreUser4 = usuarioRepository.save(padreUser4);

        est6.setPadreId(savedPadreUser4.getId());
        pasajeroRepository.save(est6);
        log.info("✅ Padre 4 creado: {} (padre de Sofía) - username: padre_gustavo", savedPadreUser4.getId());

        // ========== CREAR RUTA FULL PARA HOY ==========
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime rutaStart = now.plusMinutes(30);
        LocalDateTime rutaEnd = rutaStart.plusHours(1);

        Ruta ruta = new Ruta();
        ruta.setNombre("RECOGIDA MATINAL - Hoy");
        ruta.busId(savedBus.getId());
        ruta.conductorId(savedConductor.getId());  // ✅ Asignar al conductor principal Juan Pérez García
        ruta.coordinadorId(savedCoordinador.getId());
        ruta.sedeId(savedSede.getId());
        ruta.setTenant(transport1);

        // Configurar día de la semana actual
        ruta.setTipoRuta(nca.scc.com.admin.rutas.ruta.entity.enums.TipoRuta.RECOGIDA);
        ruta.setEstado("ACTIVE");

        // Guardar hora de inicio y fin
        ruta.setHoraInicio(String.format("%02d:%02d", rutaStart.getHour(), rutaStart.getMinute()));
        ruta.setHoraFin(String.format("%02d:%02d", rutaEnd.getHour(), rutaEnd.getMinute()));

        Ruta savedRuta = rutaRepository.save(ruta);
        log.info("✅ Ruta creada: {} (Asignada a conductor: Juan Pérez García y coordinador: María López) (Hora inicio: {}, Fin: {})",
                 savedRuta.getId(),
                 ruta.getHoraInicio(),
                 ruta.getHoraFin());

        RutaPasajeroId rpId1 = new RutaPasajeroId(savedRuta.getId(), est1.getId());
        RutaPasajeroId rpId2 = new RutaPasajeroId(savedRuta.getId(), est2.getId());
        RutaPasajeroId rpId3 = new RutaPasajeroId(savedRuta.getId(), est3.getId());
        RutaPasajeroId rpId4 = new RutaPasajeroId(savedRuta.getId(), est4.getId());
        RutaPasajeroId rpId5 = new RutaPasajeroId(savedRuta.getId(), est5.getId());
        RutaPasajeroId rpId6 = new RutaPasajeroId(savedRuta.getId(), est6.getId());
        rutaPasajeroRepository.saveAll(List.of(
                new RutaPasajero(rpId1, EstadoRutaPasajeros.PENDIENTE),
                new RutaPasajero(rpId2, EstadoRutaPasajeros.PENDIENTE),
                new RutaPasajero(rpId3, EstadoRutaPasajeros.PENDIENTE),
                new RutaPasajero(rpId4, EstadoRutaPasajeros.PENDIENTE),
                new RutaPasajero(rpId5, EstadoRutaPasajeros.PENDIENTE),
                new RutaPasajero(rpId6, EstadoRutaPasajeros.PENDIENTE)
        ));
        log.info("✅ 6 estudiantes asignados a la ruta de hoy");

        // ========== CREAR HISTORIAL PARA HOY ==========
        String todayDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        HistorialRuta historial = new HistorialRuta();
        historial.setRutaId(savedRuta.getId());
        historial.setFecha(todayDate);
        historial.setHoraInicio(ruta.getHoraInicio());
        historial.setHoraFin(ruta.getHoraFin());
        historial.setEstudiantesRecogidos(6);
        historial.setEstudiantesTotales(6);
        historial.setKmRecorridos(12.5);
        historial.setEstado(EstadoHistorialRuta.completada);

        HistorialRuta savedHistorial = historialRutaRepository.save(historial);
        log.info("✅ Historial creado para hoy: {}", savedHistorial.getId());

        // ========== CREAR SEGUNDA SEDE: COLEGIO SAN JOSÉ EN BARRANQUILLA ==========
        Sede sedeSanJose = new Sede(
                null,
                savedColegio.getId(),
                "Colegio San José - Barranquilla",
                "Carrera 45 # 72-15",
                "Barranquilla",
                10.9905,   // Latitud Barranquilla
                -74.7975,  // Longitud Barranquilla
                transport1
        );
        sedeSanJose.setTransportId(transport1);
        Sede savedSedeSanJose = sedeRepository.save(sedeSanJose);
        log.info("✅ Sede San José Barranquilla creada: {}", savedSedeSanJose.getId());

        // ========== CREAR ESTUDIANTES PARA RUTA SAN JOSÉ ==========
        // Estudiantes en diferentes paradas del barrio
        Pasajero estSJ1 = new Pasajero(
                null,
                "Miguel Ángel Vélez",
                "MAT-2026-SJ001",
                "3ro Primaria",
                "Carrera 42 #71-20, Barranquilla",
                "Prado",
                10.9915,
                -74.7985,
                savedSedeSanJose.getId(),
                null,
                transport1
        );

        Pasajero estSJ2 = new Pasajero(
                null,
                "Isabella Martínez",
                "MAT-2026-SJ002",
                "4to Primaria",
                "Carrera 48 #73-40, Barranquilla",
                "El Prado",
                10.9925,
                -74.7965,
                savedSedeSanJose.getId(),
                null,
                transport1
        );

        Pasajero estSJ3 = new Pasajero(
                null,
                "Andrés Felipe López",
                "MAT-2026-SJ003",
                "5to Primaria",
                "Carrera 51 #75-30, Barranquilla",
                "San Alejo",
                10.9935,
                -74.7955,
                savedSedeSanJose.getId(),
                null,
                transport1
        );

        Pasajero estSJ4 = new Pasajero(
                null,
                "Valentina Rodríguez",
                "MAT-2026-SJ004",
                "6to Primaria",
                "Carrera 55 #77-50, Barranquilla",
                "Murillo",
                10.9945,
                -74.7945,
                savedSedeSanJose.getId(),
                null,
                transport1
        );

        List<Pasajero> estudiantesSJ = List.of(estSJ1, estSJ2, estSJ3, estSJ4);
        List<Pasajero> savedEstudiantesSJ = pasajeroRepository.saveAll(estudiantesSJ);
        log.info("✅ {} estudiantes San José creados", savedEstudiantesSJ.size());

        // ========== CREAR RUTA PROGRAMADA CON 5 PARADAS ==========
        LocalDateTime rutaSJStart = now.plusMinutes(30);
        LocalDateTime rutaSJEnd = rutaSJStart.plusHours(2);

        Ruta rutaSanJose = new Ruta();
        rutaSanJose.setNombre("RUTA BARRANQUILLA - RECOGIDA TARDE");
        rutaSanJose.busId(savedBus.getId());
        rutaSanJose.conductorId(savedConductor.getId());  // ✅ Conductor Juan Pérez
        rutaSanJose.coordinadorId(savedCoordinador.getId());  // ✅ Coordinadora María López
        rutaSanJose.sedeId(savedSedeSanJose.getId());
        rutaSanJose.setTenant(transport1);

        // Configurar como ruta programada (PROGRAMMED en lugar de ACTIVE)
        rutaSanJose.setTipoRuta(nca.scc.com.admin.rutas.ruta.entity.enums.TipoRuta.RECOGIDA);
        rutaSanJose.setEstado("PROGRAMMED");  // Estado programada, no activa

        // Guardar horas de inicio y fin
        rutaSanJose.setHoraInicio(String.format("%02d:%02d", rutaSJStart.getHour(), rutaSJStart.getMinute()));
        rutaSanJose.setHoraFin(String.format("%02d:%02d", rutaSJEnd.getHour(), rutaSJEnd.getMinute()));

        Ruta savedRutaSanJose = rutaRepository.save(rutaSanJose);
        log.info("✅ Ruta SAN JOSÉ Programada creada: {} (Estado: PROGRAMMED)", savedRutaSanJose.getId());
        log.info("   📍 Parada 1 (Inicio): Colegio San José - Carrera 45 #72-15");
        log.info("   📍 Parada 2: Prado - Carrera 42 #71-20 (1 estudiante: Miguel)");
        log.info("   📍 Parada 3: El Prado - Carrera 48 #73-40 (1 estudiante: Isabella)");
        log.info("   📍 Parada 4: San Alejo - Carrera 51 #75-30 (1 estudiante: Andrés)");
        log.info("   📍 Parada 5: Murillo - Carrera 55 #77-50 (1 estudiante: Valentina)");
        log.info("   📍 Parada 6 (Final): Retorno a Colegio San José");
        log.info("   👨‍✈️  Conductor: Juan Pérez García");
        log.info("   👩‍✈️  Coordinadora: María López García");
        log.info("   ⏰ Hora Inicio: {} | Fin: {}", rutaSanJose.getHoraInicio(), rutaSanJose.getHoraFin());

        //guardar estudiantes en la ruta programada
        RutaPasajeroId rutaPasajeroId1 = new RutaPasajeroId(savedRutaSanJose.getId(), estSJ1.getId());
        rutaPasajeroRepository.save(new RutaPasajero(rutaPasajeroId1, EstadoRutaPasajeros.PENDIENTE));
        log.info("✅ Estudiante {} asignado a ruta programada SAN JOSÉ", estSJ1.getNombre());

        RutaPasajeroId rutaPasajeroId2 = new RutaPasajeroId(savedRutaSanJose.getId(), estSJ2.getId());
        rutaPasajeroRepository.save(new RutaPasajero(rutaPasajeroId2, EstadoRutaPasajeros.PENDIENTE));
        log.info("✅ Estudiante {} asignado a ruta programada SAN JOSÉ", estSJ2.getNombre());

        RutaPasajeroId rutaPasajeroId3 = new RutaPasajeroId(savedRutaSanJose.getId(), estSJ3.getId());
        rutaPasajeroRepository.save(new RutaPasajero(rutaPasajeroId3, EstadoRutaPasajeros.PENDIENTE));
        log.info("✅ Estudiante {} asignado a ruta programada SAN JOSÉ", estSJ3.getNombre());

        RutaPasajeroId rutaPasajeroId4 = new RutaPasajeroId(savedRutaSanJose.getId(), estSJ4.getId());
        rutaPasajeroRepository.save(new RutaPasajero(rutaPasajeroId4, EstadoRutaPasajeros.PENDIENTE));
        log.info("✅ Estudiante {} asignado a ruta programada SAN JOSÉ", estSJ4.getNombre());

        // ========== CREAR USUARIOS PARA CONDUCTOR Y COORDINADOR ==========
        String conductorPassword = "conductor123";
        String coordinadorPassword = "coordinador123";

        // CVE-2025-22228: Validar longitud máxima de contraseña (BCrypt límite: 72 caracteres)
        validatePasswordLength(conductorPassword);
        validatePasswordLength(coordinadorPassword);

        // Mitigar uso vulnerable de BCrypt.hashpw
        String conductorPasswordHash = generateSecurePasswordHash(conductorPassword);
        String coordinadorPasswordHash = generateSecurePasswordHash(coordinadorPassword);
        String adminPassword = "admin123";
        String adminPass = generateSecurePasswordHash(adminPassword);

        // Crear usuario para el conductor
        Usuario usuarioConductor = new Usuario(
                "Juan Pérez García",
                "conductor.juan",
                conductorPasswordHash,
                String.join(",", defaultTenant, transport1),
                Role.ROLE_TRANSPORT
        );
        usuarioConductor.setEmail("conductor.juan@example.com");
        usuarioConductor.setConductorId(savedConductor.getId());
        usuarioRepository.save(usuarioConductor);
        log.info("✅ Usuario Conductor creado - Username: conductor.juan - Password: {}", conductorPassword);

        // Crear usuario para el coordinador
        Usuario usuarioCoordinador = new Usuario(
                "María López García",
                "coordinador.maria",
                coordinadorPasswordHash,
                String.join(",", defaultTenant, transport1),
                Role.ROLE_TRANSPORT
        );
        usuarioCoordinador.setEmail("coordinador.maria@example.com");
        usuarioCoordinador.setCoordinadorId(savedCoordinador.getId());
        usuarioRepository.save(usuarioCoordinador);
        log.info("✅ Usuario Coordinador creado - Username: coordinador.maria - Password: {}", coordinadorPassword);

        // ========== CREAR USUARIOS DE ADMIN ==========
        String plainPassword = "admin123";
        // CVE-2025-22228: Validar longitud máxima de contraseña (BCrypt límite: 72 caracteres)
        validatePasswordLength(plainPassword);

        // Verificar inmediatamente que el hash es válido
        if (!BCrypt.checkpw(plainPassword, adminPass)) {
            log.error("❌ CRITICAL: BCrypt hash validation FAILED immediately after creation!");
            log.error("Password: {}, Hash: {}", plainPassword, adminPass);
            return;
        }
        log.info("✅ Admin password hash created and verified: OK");

        // Admin de transporte
        Usuario adminTransport = new Usuario(
                "Admin Transporte",
                "admin.transport",
                adminPass,
                transport1,
                Role.ROLE_TRANSPORT
        );
        adminTransport.setEmail("admin.transport@example.com");
        adminTransport.setConductorId(savedConductorAdmin.getId());  // ✅ Vinculado al conductor admin
        usuarioRepository.save(adminTransport);
        log.info("✅ Admin Transporte creado (vinculado a conductor admin)");

        // Admin de sede/colegio
        Usuario adminSede = new Usuario(
                "Admin Colegio",
                "admin.colegio",
                adminPass,
                savedSede.getId(),
                Role.ROLE_SCHOOL
        );
        adminSede.setEmail("admin.colegio@example.com");
        usuarioRepository.save(adminSede);
        log.info("✅ Admin Colegio creado");

        // Admin general
        Usuario admin = new Usuario(
                "Admin Sistema",
                "admin",
                adminPass,
                defaultTenant,
                Role.ROLE_ADMIN
        );
        admin.setEmail("admin@example.com");
        usuarioRepository.save(admin);
        log.info("✅ Admin Sistema creado");

        // ========== CREAR NOVEDADES ==========
        Novedad novedad = new Novedad();
        novedad.setRutaId(savedRuta.getId());
        novedad.setTitulo("Retraso leve");
        novedad.setMensaje("La ruta tiene un retraso de 5 minutos");
        novedad.setTipo(NovedadTipo.info);
        novedad.setCategoria(NovedadCategoria.otro);
        novedad.setRequiereAprobacion(false);
        novedad.setCreadoPor("Sistema");
        novedad.setRolCreador(RolCreador.coordinador);
        novedad.setCreatedAt(LocalDateTime.now());
        novedad.setLeida(false);
        novedadRepository.save(novedad);
        log.info("✅ Novedad creada");

        // ========== CREAR RUTAS PROGRAMADAS PARA 20 MINUTOS POSTERIORES ==========
        LocalDateTime rutaProgramadaStart = now.plusMinutes(20);
        LocalDateTime rutaProgramadaEnd = rutaProgramadaStart.plusHours(1);

        Ruta rutaProgramada = new Ruta();
        rutaProgramada.setNombre("RUTA PROGRAMADA - 20 MINUTOS");
        rutaProgramada.busId(savedBus.getId());
        rutaProgramada.conductorId(savedConductor.getId());
        rutaProgramada.coordinadorId(savedCoordinador.getId());
        rutaProgramada.sedeId(savedSede.getId());
        rutaProgramada.setTenant(transport1);

        rutaProgramada.setTipoRuta(nca.scc.com.admin.rutas.ruta.entity.enums.TipoRuta.RECOGIDA);
        rutaProgramada.setEstado("PROGRAMMED");
        rutaProgramada.setHoraInicio(String.format("%02d:%02d", rutaProgramadaStart.getHour(), rutaProgramadaStart.getMinute()));
        rutaProgramada.setHoraFin(String.format("%02d:%02d", rutaProgramadaEnd.getHour(), rutaProgramadaEnd.getMinute()));
        rutaRepository.save(rutaProgramada);
        log.info("✅ Ruta programada creada: {}", rutaProgramada.getNombre());

        RutaPasajeroId rpIdP1 = new RutaPasajeroId(rutaProgramada.getId(), est1.getId());
        RutaPasajeroId rpIdP2 = new RutaPasajeroId(rutaProgramada.getId(), est2.getId());
        RutaPasajeroId rpIdP3 = new RutaPasajeroId(rutaProgramada.getId(), est3.getId());
        rutaPasajeroRepository.saveAll(List.of(
                new RutaPasajero(rpIdP1, EstadoRutaPasajeros.PENDIENTE),
                new RutaPasajero(rpIdP2, EstadoRutaPasajeros.PENDIENTE),
                new RutaPasajero(rpIdP3, EstadoRutaPasajeros.PENDIENTE)
        ));
        log.info("✅ 3 estudiantes asignados a la ruta programada");

        // ========== CREAR 3 RUTAS COMPLETADAS ==========
        for (int i = 1; i <= 3; i++) {
            Ruta rutaCompletada = new Ruta();
            rutaCompletada.setNombre("RUTA COMPLETADA - " + i);
            rutaCompletada.busId(savedBus.getId());
            rutaCompletada.conductorId(savedConductor.getId());
            rutaCompletada.coordinadorId(savedCoordinador.getId());
            rutaCompletada.sedeId(savedSede.getId());
            rutaCompletada.setTenant(transport1);

            rutaCompletada.setTipoRuta(nca.scc.com.admin.rutas.ruta.entity.enums.TipoRuta.RECOGIDA);
            rutaCompletada.setEstado("COMPLETED");
            rutaCompletada.setHoraInicio("08:00");
            rutaCompletada.setHoraFin("09:00");
            rutaRepository.save(rutaCompletada);
            log.info("✅ Ruta completada creada: {}", rutaCompletada.getNombre());
            RutaPasajeroId rpIdC1 = new RutaPasajeroId(rutaCompletada.getId(), est4.getId());
            RutaPasajeroId rpIdC2 = new RutaPasajeroId(rutaCompletada.getId(), est5.getId());
            RutaPasajeroId rpIdC3 = new RutaPasajeroId(rutaCompletada.getId(), est6.getId());
            rutaPasajeroRepository.saveAll(List.of(
                    new RutaPasajero(rpIdC1, EstadoRutaPasajeros.PENDIENTE),
                    new RutaPasajero(rpIdC2, EstadoRutaPasajeros.PENDIENTE),
                    new RutaPasajero(rpIdC3, EstadoRutaPasajeros.PENDIENTE)
            ));
        }

        // ========== CREAR AGENDAS DE RUTAS PARA DOS DÍAS POSTERIORES ==========
        for (int day = 1; day <= 2; day++) {
            LocalDateTime agendaStart = now.plusDays(day).withHour(7).withMinute(0);
            LocalDateTime agendaEnd = agendaStart.plusHours(1);

            Ruta agendaRuta = new Ruta();
            agendaRuta.setNombre("AGENDA RUTA - DÍA " + day);
            agendaRuta.busId(savedBus.getId());
            agendaRuta.conductorId(savedConductor.getId());
            agendaRuta.coordinadorId(savedCoordinador.getId());
            agendaRuta.sedeId(savedSede.getId());
            agendaRuta.setTenant(transport1);

            agendaRuta.setTipoRuta(nca.scc.com.admin.rutas.ruta.entity.enums.TipoRuta.RECOGIDA);
            agendaRuta.setEstado("PROGRAMMED");
            agendaRuta.setHoraInicio(String.format("%02d:%02d", agendaStart.getHour(), agendaStart.getMinute()));
            agendaRuta.setHoraFin(String.format("%02d:%02d", agendaEnd.getHour(), agendaEnd.getMinute()));
            rutaRepository.save(agendaRuta);
            log.info("✅ Agenda de ruta creada para el día {}: {}", day, agendaRuta.getNombre());
            RutaPasajeroId rpIdA1 = new RutaPasajeroId(agendaRuta.getId(), est4.getId());
            RutaPasajeroId rpIdA2 = new RutaPasajeroId(agendaRuta.getId(), est5.getId());
            RutaPasajeroId rpIdA3 = new RutaPasajeroId(agendaRuta.getId(), est6.getId());
            rutaPasajeroRepository.saveAll(List.of(
                    new RutaPasajero(rpIdA1, EstadoRutaPasajeros.PENDIENTE),
                    new RutaPasajero(rpIdA2, EstadoRutaPasajeros.PENDIENTE),
                    new RutaPasajero(rpIdA3, EstadoRutaPasajeros.PENDIENTE)
            ));
        }

        log.warn("Métodos setTenants no existen en Conductor y Coordinador. Revisar implementación.");

        // Ajustar rutas para que sean visibles en ambos tenants
        List<Ruta> rutas = rutaRepository.findAll();
        for (Ruta rutaItem : rutas) {
            rutaItem.setTenant(String.join(",", defaultTenant, transport1));
            rutaRepository.save(rutaItem);
        }

        log.info("\n╔════════════════════════════════════════════════════════════════════════════════════╗");
        log.info("║                    ✅ SEED DATA COMPLETADO - 2 RUTAS CREADAS                      ║");
        log.info("║                                                                                    ║");
        log.info("║  📍 RUTA 1: RECOGIDA MATINAL (Estado: ACTIVE)                                     ║");
        log.info("║  ════════════════════════════════════════════════════════════════════════════     ║");
        log.info("║  ID: {}                                              ║", savedRuta.getId());
        log.info("║  Sede: Sede Principal (Bogotá)  |  Estudiantes: 6                                ║");
        log.info("║  Horario: {} a {}                                                   ║", ruta.getHoraInicio(), ruta.getHoraFin());
        log.info("║  Paradas: 5 (1 inicio + 3 intermedias + 1 final)                                 ║");
        log.info("║                                                                                    ║");
        log.info("║  📍 RUTA 2: RUTA BARRANQUILLA - RECOGIDA TARDE (Estado: PROGRAMMED)              ║");
        log.info("║  ════════════════════════════════════════════════════════════════════════════     ║");
        log.info("║  ID: {}                                        ║", savedRutaSanJose.getId());
        log.info("║  Sede: Colegio San José (Barranquilla)  |  Estudiantes: 4                        ║");
        log.info("║  Horario: {} a {}                                                   ║", rutaSanJose.getHoraInicio(), rutaSanJose.getHoraFin());
        log.info("║  Paradas: 6 (1 inicio + 4 intermedias + 1 final) ← TESTING CON 5 PARADAS         ║");
        log.info("║                                                                                    ║");
        log.info("║  👨‍✈️  CONDUCTOR:                                                                  ║");
        log.info("║     Nombre: Juan Pérez García                                                     ║");
        log.info("║     Usuario: conductor.juan  |  Contraseña: conductor123                          ║");
        log.info("║     Acceso: Ambas rutas asignadas                                                 ║");
        log.info("║                                                                                    ║");
        log.info("║  👩‍✈️  COORDINADORA:                                                               ║");
        log.info("║     Nombre: María López García                                                    ║");
        log.info("║     Usuario: coordinador.maria  |  Contraseña: coordinador123                     ║");
        log.info("║     Acceso: Ambas rutas asignadas                                                 ║");
        log.info("║                                                                                    ║");
        log.info("║  🔐 ADMIN USERS:                                                                  ║");
        log.info("║     admin / admin123 (ROLE_ADMIN)                                                 ║");
        log.info("║     admin.transport / admin123 (ROLE_TRANSPORT)                                   ║");
        log.info("║     admin.colegio / admin123 (ROLE_SCHOOL)                                       ║");
        log.info("║                                                                                    ║");
        log.info("║  👨‍👧‍👦 PADRES (ROLE_SCHOOL):                                                         ║");
        log.info("║     padre_roberto / padre123  →  Carlos                                           ║");
        log.info("║     padre_francisco / padre123  →  Ana, Pedro                                     ║");
        log.info("║     padre_patricia / padre123  →  Lucía, Diego                                    ║");
        log.info("║     padre_gustavo / padre123  →  Sofía                                            ║");
        log.info("║                                                                                    ║");
        log.info("║  🧪 TESTING SUGERIDO:                                                             ║");
        log.info("║     ✓ GET /api/driver/routes/today - Ver rutas asignadas                         ║");
        log.info("║     ✓ GET /api/driver/routes/{id} - Ver detalle de ruta                          ║");
        log.info("║     ✓ POST /reportar-recogida - Registrar recogida de estudiante                 ║");
        log.info("║     ✓ POST /reportar-no-abordaje - Reportar no abordaje                          ║");
        log.info("║     ✓ POST /reportar-novedad - Crear novedad predefinida o libre                 ║");
        log.info("║     ✓ POST /completar-ruta - Finalizar recorrido y generar reporte              ║");
        log.info("║                                                                                    ║");
        log.info("║  ⚡ REGENERACIÓN: Ambas rutas se crean con HORA ACTUAL + 30 MIN cada startup    ║");
        log.info("║     Permite testing continuo sin modificar datos                                  ║");
        log.info("║                                                                                    ║");
        log.info("║  ✅ SISTEMA LISTO PARA PRUEBAS DE RECORRIDO Y FUNCIONALIDADES                    ║");
        log.info("╚════════════════════════════════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Valida que la contraseña no exceda la longitud máxima permitida por BCrypt (72 caracteres).
     * Mitigación para CVE-2025-22228: Spring Security BCryptPasswordEncoder does not enforce
     * maximum password length.
     *
     * @param password la contraseña a validar
     * @throws IllegalArgumentException si la contraseña excede 72 caracteres
     */
    private void validatePasswordLength(String password) {
        // BCrypt trunca contraseñas mayores a 72 caracteres, lo que puede afectar la seguridad
        final int MAX_PASSWORD_LENGTH = 72;
        if (password != null && password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Password length exceeds maximum allowed (%d characters). " +
                                    "Current length: %d characters. " +
                                    "Please use a shorter password.",
                            MAX_PASSWORD_LENGTH, password.length()));
        }
    }

    // Mitigar uso vulnerable de BCrypt.hashpw
    private String generateSecurePasswordHash(String password) {
        validatePasswordLength(password);
        return BCrypt.hashpw(password, BCrypt.gensalt(12)); // Uso seguro validado
    }
}
