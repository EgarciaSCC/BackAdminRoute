package nca.scc.com.admin.rutas.coordinador;

import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordinadorRepository extends JpaRepository<Coordinador, String> {
}
