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
import nca.scc.com.admin.rutas.novedad.NovedadRepository;
import nca.scc.com.admin.rutas.novedad.entity.Novedad;
import nca.scc.com.admin.rutas.novedad.entity.enums.NovedadCategoria;
import nca.scc.com.admin.rutas.novedad.entity.enums.NovedadTipo;
import nca.scc.com.admin.rutas.novedad.entity.enums.RolCreador;
import nca.scc.com.admin.rutas.ruta.RutaRepository;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import nca.scc.com.admin.rutas.sede.SedeRepository;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.rutas.pasajero.PasajeroRepository;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.rutas.historial.HistorialRutaRepository;
import nca.scc.com.admin.rutas.historial.entity.HistorialRuta;
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

    public SeedData(BusRepository busRepository,
                    ConductorRepository conductorRepository,
                    CoordinadorRepository coordinadorRepository,
                    ColegioRepository colegioRepository,
                    RutaRepository rutaRepository,
                    SedeRepository sedeRepository,
                    PasajeroRepository pasajeroRepository,
                    NovedadRepository novedadRepository,
                    HistorialRutaRepository historialRutaRepository,
                    UsuarioRepository usuarioRepository) {
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

        // ========== CREAR CONDUCTOR ==========
        Conductor conductor = new Conductor(
                null,
                "Juan Pérez García",
                "CC",
                "1088123456",
                "LIC-2025-001",
                ConductorState.disponible,
                defaultTenant,
                "A1"
        );
        Conductor savedConductor = conductorRepository.save(conductor);
        log.info("✅ Conductor creado: {}", savedConductor.getId());

        // ========== CREAR COORDINADOR ==========
        Coordinador coordinador = new Coordinador(
                null,
                "María López García",
                "CC",
                "1087654321",
                "coord@example.com",
                CoordinadorState.activo,
                defaultTenant
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
        String padrePass = BCrypt.hashpw(padrePassword, BCrypt.gensalt());

        // Padre 1: padre de Carlos (est1)
        Usuario padreUser1 = new Usuario(
                "Roberto Rodríguez",
                "padre_roberto",
                padrePass,
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
                padrePass,
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
                padrePass,
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
                padrePass,
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
        ruta.conductorId(savedConductor.getId());
        ruta.coordinadorId(savedCoordinador.getId());
        ruta.sedeId(savedSede.getId());
        ruta.setTenant(transport1);
        ruta.setEstudiantes(List.of(
                est1.getId(),
                est2.getId(),
                est3.getId(),
                est4.getId(),
                est5.getId(),
                est6.getId()
        ));

        // Configurar día de la semana actual
        ruta.setTipoRuta(nca.scc.com.admin.rutas.ruta.entity.enums.TipoRuta.RECOGIDA);
        ruta.setEstado("ACTIVE");

        // Guardar hora de inicio y fin
        ruta.setHoraInicio(String.format("%02d:%02d", rutaStart.getHour(), rutaStart.getMinute()));
        ruta.setHoraFin(String.format("%02d:%02d", rutaEnd.getHour(), rutaEnd.getMinute()));

        Ruta savedRuta = rutaRepository.save(ruta);
        log.info("✅ Ruta creada: {} (Hora inicio: {}, Fin: {})",
                 savedRuta.getId(),
                 ruta.getHoraInicio(),
                 ruta.getHoraFin());

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
        historial.setEstado(nca.scc.com.admin.rutas.historial.entity.enums.EstadoHistorialRuta.completada);

        HistorialRuta savedHistorial = historialRutaRepository.save(historial);
        log.info("✅ Historial creado para hoy: {}", savedHistorial.getId());

        // ========== CREAR USUARIOS DE ADMIN ==========
        String plainPassword = "admin123";
        // CVE-2025-22228: Validar longitud máxima de contraseña (BCrypt límite: 72 caracteres)
        validatePasswordLength(plainPassword);
        String adminPass = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

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
        adminTransport.setConductorId(savedConductor.getId());
        usuarioRepository.save(adminTransport);
        log.info("✅ Admin Transporte creado");

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

        log.info("\n╔════════════════════════════════════════════════════════════╗");
        log.info("║                 SEED DATA COMPLETADO                        ║");
        log.info("║                                                              ║");
        log.info("║  🚌 RUTA COMPLETA PARA PRUEBAS                             ║");
        log.info("║  ────────────────────────────────────────────────────────   ║");
        log.info("║  📍 Ruta ID: {}                              ║", savedRuta.getId());
        log.info("║  👨‍✈️  Conductor: Juan Pérez García                           ║");
        log.info("║  🚌 Bus: RECOGIDA-001 (40 estudiantes)                     ║");
        log.info("║  🏫 Sede: Sede Principal                                    ║");
        log.info("║                                                              ║");
        log.info("║  PARADAS:                                                   ║");
        log.info("║  1. Sede Principal (partida)                               ║");
        log.info("║  2. Cra 5 #10-25 (1 estudiante: Carlos)                    ║");
        log.info("║  3. Cra 6 #12-30 (2 estudiantes: Ana, Pedro)              ║");
        log.info("║  4. Cra 8 #15-40 (3 estudiantes: Lucía, Diego, Sofía)    ║");
        log.info("║  5. Retorno a Sede Principal                               ║");
        log.info("║                                                              ║");
        log.info("║  👨‍👧‍👦 PADRES CON ACCESO (LOGIN):                              ║");
        log.info("║  padre_roberto / padre123 (Carlos)                         ║");
        log.info("║  padre_francisco / padre123 (Ana, Pedro)                   ║");
        log.info("║  padre_patricia / padre123 (Lucía, Diego)                  ║");
        log.info("║  padre_gustavo / padre123 (Sofía)                          ║");
        log.info("║                                                              ║");
        log.info("║  🔐 ADMIN USERS (LOGIN):                                    ║");
        log.info("║  admin / admin123 (ROLE_ADMIN)                             ║");
        log.info("║  admin.transport / admin123 (ROLE_TRANSPORT)               ║");
        log.info("║  admin.colegio / admin123 (ROLE_SCHOOL)                   ║");
        log.info("║                                                              ║");
        log.info("║  ⏰ HORARIO:                                                ║");
        log.info("║  Inicio: {} (+30 min desde ahora)            ║", ruta.getHoraInicio());
        log.info("║  Fin: {}                                    ║", ruta.getHoraFin());
        log.info("║                                                              ║");
        log.info("║  🧪 READY FOR TESTING                                     ║");
        log.info("╚════════════════════════════════════════════════════════════╝\n");
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
}
