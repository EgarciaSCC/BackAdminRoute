package nca.scc.com.admin.rutas.pasajero;

import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PasajeroRepository extends JpaRepository<Pasajero, String> {
    List<Pasajero> findBySedeId(String sedeId);

    @Query("select p from Pasajero p, nca.scc.com.admin.rutas.sede.entity.Sede s where p.sedeId = s.id and s.transportId = :transportId")
    List<Pasajero> findBySedeTransportId(@Param("transportId") String transportId);
}
