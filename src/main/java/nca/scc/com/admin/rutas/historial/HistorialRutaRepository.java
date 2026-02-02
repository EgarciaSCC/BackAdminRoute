package nca.scc.com.admin.rutas.historial;

import nca.scc.com.admin.rutas.historial.entity.HistorialRuta;
import nca.scc.com.admin.rutas.historial.entity.enums.EstadoHistorialRuta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface HistorialRutaRepository extends JpaRepository<HistorialRuta, String> {

    List<HistorialRuta> findByRutaIdInAndFechaOrderByHoraInicioAsc(Collection<String> rutaIds, String fecha);

    Page<HistorialRuta> findByRutaIdInAndFechaBetweenAndEstadoOrderByFechaDescHoraInicioDesc(
            Collection<String> rutaIds, String startDate, String endDate, EstadoHistorialRuta estado, Pageable pageable);

    List<HistorialRuta> findByRutaIdInAndFechaBetweenAndEstado(
            Collection<String> rutaIds, String startDate, String endDate, EstadoHistorialRuta estado);
}
