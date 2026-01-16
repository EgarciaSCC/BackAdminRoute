package nca.scc.com.admin.rutas.historial;

import nca.scc.com.admin.rutas.historial.entity.HistorialRuta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialRutaRepository extends JpaRepository<HistorialRuta, String> {
}
