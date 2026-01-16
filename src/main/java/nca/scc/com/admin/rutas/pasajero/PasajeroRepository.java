package nca.scc.com.admin.rutas.pasajero;

import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasajeroRepository extends JpaRepository<Pasajero, String> {
}
