package nca.scc.com.admin.rutas;

import nca.scc.com.admin.rutas.auth.UsuarioRepository;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import nca.scc.com.admin.rutas.bus.BusRepository;
import nca.scc.com.admin.rutas.bus.entity.Bus;
import nca.scc.com.admin.rutas.bus.entity.enums.BusState;
import nca.scc.com.admin.rutas.bus.entity.enums.MotorType;
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
import nca.scc.com.admin.rutas.novedad.entity.enums.EstadoAprobacion;
import nca.scc.com.admin.rutas.auth.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Profile("default")
@Component
public class SeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedData.class);

    private final BusRepository busRepository;
    private final ConductorRepository conductorRepository;
    private final CoordinadorRepository coordinadorRepository;
    private final RutaRepository rutaRepository;
    private final SedeRepository sedeRepository;
    private final PasajeroRepository pasajeroRepository;
    private final NovedadRepository novedadRepository;
    private final HistorialRutaRepository historialRutaRepository;
    private final UsuarioRepository usuarioRepository;

    public SeedData(BusRepository busRepository,
                    ConductorRepository conductorRepository,
                    CoordinadorRepository coordinadorRepository,
                    RutaRepository rutaRepository,
                    SedeRepository sedeRepository,
                    PasajeroRepository pasajeroRepository,
                    NovedadRepository novedadRepository,
                    HistorialRutaRepository historialRutaRepository,
                    UsuarioRepository usuarioRepository) {
        this.busRepository = busRepository;
        this.conductorRepository = conductorRepository;
        this.coordinadorRepository = coordinadorRepository;
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

        // Buses: 10
        List<Bus> buses = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            buses.add(new Bus(null, "PLACA" + i, 20 + i, "Marca" + i, "Modelo" + i, "2025-01-01", "2025-12-31", MotorType.combustible, null, BusState.activo));
        }
        List<Bus> savedBuses = busRepository.saveAll(buses);
        log.info("Saved buses: {}", busRepository.count());

        // Conductores: 10
        List<Conductor> conductores = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            conductores.add(new Conductor(null, "Conductor " + i, "CED" + i, "30000000" + i, "LIC" + i, ConductorState.disponible));
        }
        List<Conductor> savedConductors = conductorRepository.saveAll(conductores);
        log.info("Saved conductores: {}", conductorRepository.count());

        // Coordinadores: 10
        List<Coordinador> coordinadores = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            coordinadores.add(new Coordinador(null, "Coordinador " + i, "CEDC" + i, "30010000" + i, "coord" + i + "@example.com", CoordinadorState.disponible));
        }
        List<Coordinador> savedCoordinators = coordinadorRepository.saveAll(coordinadores);
        log.info("Saved coordinadores: {}", coordinadorRepository.count());

        // Sedes: 10 (todas asociadas a transport-1)
        List<Sede> sedes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Sede s = new Sede(null, "Sede " + i, "Direccion " + i, "Ciudad", 4.7 + i * 0.001, -74.0 + i * 0.001);
            s.setTransportId(transport1);
            sedes.add(s);
        }
        List<Sede> savedSedes = sedeRepository.saveAll(sedes);
        log.info("Saved sedes: {}", sedeRepository.count());

        // Pasajeros: 10, distribuir por sedes
        List<Pasajero> pasajeros = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Pasajero p = new Pasajero(null, "Estudiante " + i, "Curso " + i, "Dir " + i, "Barrio", 4.7 + i * 0.001, -74.0 + i * 0.001, false);
            p.setSedeId(savedSedes.get((i - 1) % savedSedes.size()).getId());
            pasajeros.add(p);
        }
        pasajeroRepository.saveAll(pasajeros);
        log.info("Saved pasajeros: {}", pasajeroRepository.count());

        // Rutas: 10, asignar bus/conductor/coordinador/sede round-robin
        List<Ruta> rutas = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Ruta r = new Ruta();
            r.setNombre("Ruta " + i);
            r.busId(savedBuses.get((i - 1) % savedBuses.size()).getId());
            r.conductorId(savedConductors.get((i - 1) % savedConductors.size()).getId());
            r.coordinadorId(savedCoordinators.get((i - 1) % savedCoordinators.size()).getId());
            r.sedeId(savedSedes.get((i - 1) % savedSedes.size()).getId());
            rutas.add(r);
        }
        List<Ruta> savedRutas = rutaRepository.saveAll(rutas);
        log.info("Saved rutas: {}", rutaRepository.count());

        // Novedades: 10, asignadas a rutas
        List<Novedad> novedades = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Novedad n = new Novedad();
            n.setRutaId(savedRutas.get((i - 1) % savedRutas.size()).getId());
            n.setTitulo("Novedad " + i);
            n.setMensaje("Mensaje de novedad " + i);
            n.setTipo(NovedadTipo.info);
            n.setCategoria(NovedadCategoria.otro);
            n.setRequiereAprobacion(false);
            n.setCreadoPor("Sistema");
            n.setRolCreador(RolCreador.coordinador);
            n.setCreatedAt(LocalDateTime.now());
            n.setLeida(false);
            novedades.add(n);
        }
        novedadRepository.saveAll(novedades);
        log.info("Saved novedades: {}", novedadRepository.count());

        // Historiales: 10, asociados a rutas
        List<HistorialRuta> historiales = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            HistorialRuta h = new HistorialRuta();
            h.setRutaId(savedRutas.get((i - 1) % savedRutas.size()).getId());
            h.setFecha("2025-01-0" + ((i % 9) + 1));
            h.setHoraInicio("07:00");
            h.setHoraFin("08:00");
            h.setEstudiantesRecogidos(i);
            h.setEstudiantesTotales(10);
            h.setKmRecorridos(5.0 + i);
            h.setEstado(nca.scc.com.admin.rutas.historial.entity.enums.EstadoHistorialRuta.completada);
            historiales.add(h);
        }
        historialRutaRepository.saveAll(historiales);
        log.info("Saved historiales: {}", historialRutaRepository.count());

        // Usuarios: 1 transport admin + 10 school admins (total 11)
        String adminPass = BCrypt.hashpw("admin123", BCrypt.gensalt());
        List<Usuario> usuarios = new ArrayList<>();
        Usuario u0 = new Usuario("Admin Transporte", "transp-admin", adminPass, transport1, Role.ROLE_TRANSPORT);
        u0.setEmail("transp-admin@example.com");
        u0.setConductorId(savedConductors.get(0).getId());
        usuarios.add(u0);
        for (int i = 1; i <= 10; i++) {
            String pass = BCrypt.hashpw("user" + i, BCrypt.gensalt());
            Usuario u = new Usuario("Admin Sede " + i, "sede-admin-" + i, pass, savedSedes.get(i - 1).getId(), Role.ROLE_SCHOOL);
            u.setEmail("sede-admin-" + i + "@example.com");
            usuarios.add(u);
        }
        usuarioRepository.saveAll(usuarios);
        log.info("Saved usuarios: {}", usuarioRepository.count());

        log.info("Seed data population finished.");
    }
}
