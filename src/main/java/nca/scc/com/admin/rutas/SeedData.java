package nca.scc.com.admin.rutas;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Profile("dev")
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

    public SeedData(BusRepository busRepository,
                    ConductorRepository conductorRepository,
                    CoordinadorRepository coordinadorRepository,
                    RutaRepository rutaRepository,
                    SedeRepository sedeRepository,
                    PasajeroRepository pasajeroRepository,
                    NovedadRepository novedadRepository) {
        this.busRepository = busRepository;
        this.conductorRepository = conductorRepository;
        this.coordinadorRepository = coordinadorRepository;
        this.rutaRepository = rutaRepository;
        this.sedeRepository = sedeRepository;
        this.pasajeroRepository = pasajeroRepository;
        this.novedadRepository = novedadRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting seed data population...");

        // Buses
        Bus b1 = new Bus(null, "ABC123", 40, "Mercedes", "Sprinter", "2025-06-01", "2025-12-31", MotorType.combustible, null, BusState.activo);
        Bus b2 = new Bus(null, "DEF456", 30, "Volvo", "XC90", "2025-05-15", "2025-11-30", MotorType.hibrido, null, BusState.mantenimiento);
        Bus b3 = new Bus(null, "GHI789", 25, "Iveco", "Daily", "2024-12-01", "2024-12-31", MotorType.otro, "eléctrico", BusState.inactivo);
        List<Bus> savedBuses = busRepository.saveAll(Arrays.asList(b1, b2, b3));
        log.info("Saved buses: {}", busRepository.count());

        // Conductores
        Conductor c1 = new Conductor(null, "Carlos Perez", "100200300", "3001112222", "A12345", ConductorState.disponible);
        Conductor c2 = new Conductor(null, "María Gómez", "200300400", "3003334444", "B98765", ConductorState.asignado);
        List<Conductor> savedConductors = conductorRepository.saveAll(Arrays.asList(c1, c2));
        log.info("Saved conductors: {}", conductorRepository.count());

        // Coordinadores
        Coordinador co1 = new Coordinador(null, "Laura Ruiz", "300400500", "3005556666", "laura@example.com", CoordinadorState.disponible);
        Coordinador co2 = new Coordinador(null, "José Martinez", "400500600", "3007778888", "jose@example.com", CoordinadorState.inactivo);
        List<Coordinador> savedCoordinators = coordinadorRepository.saveAll(Arrays.asList(co1, co2));
        log.info("Saved coordinators: {}", coordinadorRepository.count());

        // Sedes
        Sede s1 = new Sede(null, "Sede Central", "Calle 100 #10-20", "Bogotá", 4.711, -74.072);
        Sede s2 = new Sede(null, "Sede Norte", "Av. Norte 50", "Bogotá", 4.750, -74.040);
        sedeRepository.saveAll(Arrays.asList(s1, s2));
        log.info("Saved sedes: {}", sedeRepository.count());

        // Pasajeros
        Pasajero p1 = new Pasajero(null, "Juanito Pérez", "5A", "Calle 1 #2-3", "Chapinero", 4.710, -74.070, false);
        Pasajero p2 = new Pasajero(null, "Ana Gómez", "4B", "Calle 2 #3-4", "Suba", 4.760, -74.050, false);
        pasajeroRepository.saveAll(Arrays.asList(p1, p2));
        log.info("Saved pasajeros: {}", pasajeroRepository.count());

        // Rutas (asociamos ids generados de buses/conductores/coordinadores)
        Ruta r1 = new Ruta();
        r1.setNombre("Ruta Centro-Occidente");
        if (!savedBuses.isEmpty()) r1.busId(savedBuses.get(0).getId());
        if (!savedConductors.isEmpty()) r1.conductorId(savedConductors.get(0).getId());
        if (!savedCoordinators.isEmpty()) r1.coordinadorId(savedCoordinators.get(0).getId());

        Ruta r2 = new Ruta();
        r2.setNombre("Ruta Norte-Sur");
        if (savedBuses.size() > 1) r2.busId(savedBuses.get(1).getId());
        if (savedConductors.size() > 1) r2.conductorId(savedConductors.get(1).getId());
        if (savedCoordinators.size() > 1) r2.coordinadorId(savedCoordinators.get(1).getId());

        rutaRepository.saveAll(Arrays.asList(r1, r2));
        log.info("Saved rutas: {}", rutaRepository.count());

        // Novedades por defecto
        Novedad n1 = new Novedad();
        n1.setRutaId(r1.getId());
        n1.setTitulo("Cambio de horario");
        n1.setMensaje("La ruta tendrá un cambio de horario el próximo lunes");
        n1.setTipo(NovedadTipo.info);
        n1.setCategoria(NovedadCategoria.cambio_horario);
        n1.setRequiereAprobacion(false);
        n1.setEstadoAprobacion(null);
        n1.setCreadoPor("Sistema");
        n1.setRolCreador(RolCreador.coordinador);
        n1.setEstudianteId(null);
        n1.setCreatedAt(LocalDateTime.now());
        n1.setLeida(false);

        Novedad n2 = new Novedad();
        n2.setRutaId(r2.getId());
        n2.setTitulo("Novedad: retraso");
        n2.setMensaje("El bus llegará con 15 minutos de retraso");
        n2.setTipo(NovedadTipo.alerta);
        n2.setCategoria(NovedadCategoria.incidente);
        n2.setRequiereAprobacion(true);
        n2.setEstadoAprobacion(null);
        n2.setCreadoPor("María Pérez");
        n2.setRolCreador(RolCreador.coordinador);
        n2.setEstudianteId(null);
        n2.setCreatedAt(LocalDateTime.now());
        n2.setLeida(false);

        novedadRepository.saveAll(Arrays.asList(n1, n2));
        log.info("Saved novedades: {}", novedadRepository.count());

        log.info("Seed data population finished.");
    }
}
