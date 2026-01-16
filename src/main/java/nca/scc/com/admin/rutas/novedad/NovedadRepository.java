package nca.scc.com.admin.rutas.novedad;

import nca.scc.com.admin.rutas.novedad.entity.Novedad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovedadRepository extends JpaRepository<Novedad, String> {
}
