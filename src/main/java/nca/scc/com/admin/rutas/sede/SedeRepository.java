package nca.scc.com.admin.rutas.sede;

import nca.scc.com.admin.rutas.sede.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeRepository extends JpaRepository<Sede, String> {
    List<Sede> findByTransportId(String transportId);
}
