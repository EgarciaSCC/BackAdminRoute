package nca.scc.com.admin.rutas.historial.pasajero;

import nca.scc.com.admin.rutas.historial.pasajero.entity.HistorialPasajero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialPasajeroRepository
        extends JpaRepository<HistorialPasajero, String> {

    List<HistorialPasajero> findByRutaIdAndFechaOrderByTimestampAsc(
            String rutaId, String fecha);

    List<HistorialPasajero> findByPasajeroIdOrderByTimestampDesc(
            String pasajeroId);

    boolean existsByHistorialRutaIdAndPasajeroIdAndEvento(
            String historialRutaId,
            String pasajeroId,
            Enum evento
    );
}
