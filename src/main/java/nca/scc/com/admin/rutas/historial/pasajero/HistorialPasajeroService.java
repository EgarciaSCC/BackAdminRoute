package nca.scc.com.admin.rutas.historial.pasajero;

import jakarta.transaction.Transactional;
import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.historial.dto.DropoffPasajeroRequest;
import nca.scc.com.admin.rutas.historial.dto.PickupPasajeroRequest;
import nca.scc.com.admin.rutas.historial.pasajero.entity.HistorialPasajero;
import nca.scc.com.admin.rutas.historial.enums.TipoEventoPasajero;
import nca.scc.com.admin.rutas.historial.ruta.HistorialRutaRepository;
import nca.scc.com.admin.rutas.historial.ruta.entity.HistorialRuta;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialPasajeroService {

    private final HistorialPasajeroRepository repository;
    private final HistorialRutaRepository historialRutaRepository;

    public HistorialPasajeroService(
            HistorialPasajeroRepository repository,
            HistorialRutaRepository historialRutaRepository
    ) {
        this.repository = repository;
        this.historialRutaRepository = historialRutaRepository;
    }

    @Transactional
    public void registrarPickup(PickupPasajeroRequest request) {

        HistorialRuta historialRuta = historialRutaRepository
                .findById(request.getHistorialRutaId())
                .orElseThrow(() -> new NotFoundException("HistorialRuta no existe"));

        boolean yaRecogido = repository.existsByHistorialRutaIdAndPasajeroIdAndEvento(
                request.getHistorialRutaId(),
                request.getPasajeroId(),
                TipoEventoPasajero.PICKUP
        );

        if (yaRecogido) {
            return; // idempotente
        }

        HistorialPasajero evento = new HistorialPasajero();
        evento.setHistorialRutaId(historialRuta.getId());
        evento.setRutaId(request.getRutaId());
        evento.setPasajeroId(request.getPasajeroId());
        evento.setFecha(request.getFecha());
        evento.setEvento(TipoEventoPasajero.PICKUP);

        repository.save(evento);

        historialRuta.setEstudiantesRecogidos(
                historialRuta.getEstudiantesRecogidos() + 1
        );
        historialRutaRepository.save(historialRuta);
    }

    @Transactional
    public void registrarDropoff(DropoffPasajeroRequest request) {

        HistorialRuta historialRuta = historialRutaRepository
                .findById(request.getHistorialRutaId())
                .orElseThrow(() -> new NotFoundException("HistorialRuta no existe"));

        HistorialPasajero evento = new HistorialPasajero();
        evento.setHistorialRutaId(historialRuta.getId());
        evento.setRutaId(request.getRutaId());
        evento.setPasajeroId(request.getPasajeroId());
        evento.setFecha(request.getFecha());
        evento.setEvento(TipoEventoPasajero.DROPOFF);

        repository.save(evento);

        historialRuta.setEstudiantesDejados(
                historialRuta.getEstudiantesDejados() + 1
        );
        historialRutaRepository.save(historialRuta);
    }

    public List<HistorialPasajero> getByRutaAndFecha(String rutaId, String fecha) {
        return repository.findByRutaIdAndFechaOrderByTimestampAsc(rutaId, fecha);
    }

    public List<HistorialPasajero> getByPasajero(String pasajeroId) {
        return repository.findByPasajeroIdOrderByTimestampDesc(pasajeroId);
    }
}
