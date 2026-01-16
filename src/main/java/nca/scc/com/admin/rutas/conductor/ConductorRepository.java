package nca.scc.com.admin.rutas.conductor;

import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConductorRepository extends JpaRepository<Conductor, String> {
}
