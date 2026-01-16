package nca.scc.com.admin.rutas.sede;

import nca.scc.com.admin.rutas.sede.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedeRepository extends JpaRepository<Sede, String> {
}
