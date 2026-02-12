package nca.scc.com.admin.rutas.ruta;

import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import nca.scc.com.admin.rutas.ruta.entity.enums.TipoRuta;
import nca.scc.com.admin.rutas.pasajero.PasajeroRepository;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.rutas.bus.BusRepository;
import nca.scc.com.admin.rutas.bus.entity.Bus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("RutaService - Validaciones de Asignación de Estudiante")
public class RutaServiceTest {

    @Mock
    private RutaRepository rutaRepository;

    @Mock
    private PasajeroRepository pasajeroRepository;

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private RutaService rutaService;

    private Pasajero estudianteActivo;
    private Ruta rutaRecogida;
    private Ruta rutaLlevada;
    private Bus bus;

    @BeforeEach
    public void setUp() {
        // Preparar datos de prueba
        estudianteActivo = new Pasajero();
        estudianteActivo.setId("est-001");
        estudianteActivo.setActivo(true);

        rutaRecogida = new Ruta();
        rutaRecogida.setId("ruta-recogida-001");
        rutaRecogida.setNombre("Recogida Mañana");
        rutaRecogida.setTipoRuta(TipoRuta.RECOGIDA);
        rutaRecogida.setHoraInicio("07:00");
        rutaRecogida.setHoraFin("08:00");
        rutaRecogida.setFecha("2026-02-05");
//        rutaRecogida.setEstudiantes(new ArrayList<>());
        rutaRecogida.setCapacidadActual(0);

        rutaLlevada = new Ruta();
        rutaLlevada.setId("ruta-llevada-001");
        rutaLlevada.setNombre("Llevada Tarde");
        rutaLlevada.setTipoRuta(TipoRuta.LLEVADA);
        rutaLlevada.setHoraInicio("14:00");
        rutaLlevada.setHoraFin("15:00");
        rutaLlevada.setFecha("2026-02-05");
//        rutaLlevada.setEstudiantes(new ArrayList<>());
        rutaLlevada.setCapacidadActual(0);

        bus = new Bus();
        bus.setId("bus-001");
        bus.setCapacidad(40);
    }

    @Test
    @DisplayName("Debe validar exitosamente asignación de primer estudiante a ruta RECOGIDA")
    public void testValidarEstudianteEnRuta_HappyPath_Recogida() {
        // Arrange
        when(pasajeroRepository.findById("est-001")).thenReturn(Optional.of(estudianteActivo));
        when(rutaRepository.findById("ruta-recogida-001")).thenReturn(Optional.of(rutaRecogida));
        when(rutaRepository.findAll()).thenReturn(List.of(rutaRecogida, rutaLlevada));
        when(busRepository.findById("bus-001")).thenReturn(Optional.of(bus));

        rutaRecogida.setBusId("bus-001");

        // Act & Assert
        assertDoesNotThrow(() -> rutaService.validarEstudianteEnRuta("est-001", "ruta-recogida-001"));

        verify(pasajeroRepository, times(1)).findById("est-001");
        verify(rutaRepository, times(1)).findById("ruta-recogida-001");
    }

    @Test
    @DisplayName("Debe rechazar asignación si estudiante está inactivo")
    public void testValidarEstudianteEnRuta_EstudianteInactivo() {
        // Arrange
        Pasajero estudianteInactivo = new Pasajero();
        estudianteInactivo.setId("est-002");
        estudianteInactivo.setActivo(false);

        when(pasajeroRepository.findById("est-002")).thenReturn(Optional.of(estudianteInactivo));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> rutaService.validarEstudianteEnRuta("est-002", "ruta-recogida-001"), "Estudiante inactivo no puede ser asignado");
    }

    @Test
    @DisplayName("Debe rechazar asignación si estudiante ya tiene 2 rutas en el día")
    public void testValidarEstudianteEnRuta_Max2RutasPorDia() {
        // Arrange
        Ruta rutaExistente1 = new Ruta();
        rutaExistente1.setId("ruta-existente-1");
        rutaExistente1.setTipoRuta(TipoRuta.RECOGIDA);
        rutaExistente1.setFecha("2026-02-05");
//        rutaExistente1.setEstudiantes(List.of("est-001"));

        Ruta rutaExistente2 = new Ruta();
        rutaExistente2.setId("ruta-existente-2");
        rutaExistente2.setTipoRuta(TipoRuta.LLEVADA);
        rutaExistente2.setFecha("2026-02-05");
//        rutaExistente2.setEstudiantes(List.of("est-001"));

        when(pasajeroRepository.findById("est-001")).thenReturn(Optional.of(estudianteActivo));
        when(rutaRepository.findById("ruta-recogida-001")).thenReturn(Optional.of(rutaRecogida));
        when(rutaRepository.findAll()).thenReturn(List.of(rutaExistente1, rutaExistente2));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> rutaService.validarEstudianteEnRuta("est-001", "ruta-recogida-001"), "Máximo 2 rutas por día");
    }

    @Test
    @DisplayName("Debe rechazar asignación si estudiante ya tiene ruta del mismo tipo")
    public void testValidarEstudianteEnRuta_DobleRecogida() {
        // Arrange
        Ruta rutaRecogidaExistente = new Ruta();
        rutaRecogidaExistente.setId("ruta-recogida-existente");
        rutaRecogidaExistente.setTipoRuta(TipoRuta.RECOGIDA);
        rutaRecogidaExistente.setFecha("2026-02-05");
//        rutaRecogidaExistente.setEstudiantes(List.of("est-001"));

        Ruta nuevaRutaRecogida = new Ruta();
        nuevaRutaRecogida.setId("ruta-recogida-nueva");
        nuevaRutaRecogida.setNombre("Nueva Recogida");
        nuevaRutaRecogida.setTipoRuta(TipoRuta.RECOGIDA);
        nuevaRutaRecogida.setHoraInicio("08:30");
        nuevaRutaRecogida.setHoraFin("09:30");
        nuevaRutaRecogida.setFecha("2026-02-05");
//        nuevaRutaRecogida.setEstudiantes(new ArrayList<>());

        when(pasajeroRepository.findById("est-001")).thenReturn(Optional.of(estudianteActivo));
        when(rutaRepository.findById("ruta-recogida-nueva")).thenReturn(Optional.of(nuevaRutaRecogida));
        when(rutaRepository.findAll()).thenReturn(List.of(rutaRecogidaExistente, nuevaRutaRecogida));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> rutaService.validarEstudianteEnRuta("est-001", "ruta-recogida-nueva"), "No se puede asignar dos rutas del mismo tipo");
    }

    @Test
    @DisplayName("Debe rechazar asignación si hay solapamiento de horarios")
    public void testValidarEstudianteEnRuta_OverlapHorario() {
        // Arrange
        Ruta rutaConFlict = new Ruta();
        rutaConFlict.setId("ruta-conflict");
        rutaConFlict.setTipoRuta(TipoRuta.RECOGIDA);
        rutaConFlict.setHoraInicio("07:00");
        rutaConFlict.setHoraFin("08:00");
        rutaConFlict.setFecha("2026-02-05");
//        rutaConFlict.setEstudiantes(List.of("est-001"));

        Ruta rutaNueva = new Ruta();
        rutaNueva.setId("ruta-nueva");
        rutaNueva.setNombre("Ruta Conflictiva");
        rutaNueva.setTipoRuta(TipoRuta.LLEVADA);
        rutaNueva.setHoraInicio("07:30"); // Solapamiento: 07:30-08:30 intersecta con 07:00-08:00
        rutaNueva.setHoraFin("08:30");
        rutaNueva.setFecha("2026-02-05");
//        rutaNueva.setEstudiantes(new ArrayList<>());

        when(pasajeroRepository.findById("est-001")).thenReturn(Optional.of(estudianteActivo));
        when(rutaRepository.findById("ruta-nueva")).thenReturn(Optional.of(rutaNueva));
        when(rutaRepository.findAll()).thenReturn(List.of(rutaConFlict, rutaNueva));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> rutaService.validarEstudianteEnRuta("est-001", "ruta-nueva"), "Conflicto de horarios");
    }

    @Test
    @DisplayName("Debe rechazar asignación si bus está lleno")
    public void testValidarEstudianteEnRuta_BusCapacidadAgotada() {
        // Arrange
        Bus busLleno = new Bus();
        busLleno.setId("bus-lleno");
        busLleno.setCapacidad(10);

        Ruta rutaLlena = new Ruta();
        rutaLlena.setId("ruta-llena");
        rutaLlena.setTipoRuta(TipoRuta.RECOGIDA);
        rutaLlena.setHoraInicio("07:00");
        rutaLlena.setHoraFin("08:00");
        rutaLlena.setFecha("2026-02-05");
        rutaLlena.setBusId("bus-lleno");
        rutaLlena.setCapacidadActual(10); // Lleno
//        rutaLlena.setEstudiantes(new ArrayList<>());

        when(pasajeroRepository.findById("est-001")).thenReturn(Optional.of(estudianteActivo));
        when(rutaRepository.findById("ruta-llena")).thenReturn(Optional.of(rutaLlena));
        when(rutaRepository.findAll()).thenReturn(List.of(rutaLlena));
        when(busRepository.findById("bus-lleno")).thenReturn(Optional.of(busLleno));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> rutaService.validarEstudianteEnRuta("est-001", "ruta-llena"), "Bus sin capacidad disponible");
    }

    @Test
    @DisplayName("Debe asignar exitosamente estudiante a ruta y incrementar capacidad")
    public void testAsignarEstudianteARuta_Exitoso() {
        // Arrange
        rutaRecogida.setBusId("bus-001");

        when(pasajeroRepository.findById("est-001")).thenReturn(Optional.of(estudianteActivo));
        when(rutaRepository.findById("ruta-recogida-001")).thenReturn(Optional.of(rutaRecogida));
        when(rutaRepository.findAll()).thenReturn(List.of(rutaRecogida));
        when(busRepository.findById("bus-001")).thenReturn(Optional.of(bus));
        when(rutaRepository.save(any(Ruta.class))).thenReturn(rutaRecogida);

        // Act
        Ruta resultado = rutaService.asignarEstudianteARuta("ruta-recogida-001", "est-001");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getCapacidadActual());
//        assertTrue(resultado.getEstudiantes().contains("est-001"));
        verify(rutaRepository, times(1)).save(any(Ruta.class));
    }
}
