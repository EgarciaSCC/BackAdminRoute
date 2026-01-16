package nca.scc.com.admin.rutas.bus;

import nca.scc.com.admin.rutas.bus.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, String> {

}
