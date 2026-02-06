package nca.scc.com.admin.rutas.pasajero;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;

import java.util.List;
import java.util.Optional;

public interface PasajeroRepository extends JpaRepository<Pasajero, String> {

    Optional<Pasajero> findByMatricula(String matricula);

    List<Pasajero> findByTenant(String tenant);

    List<Pasajero> findBySedeId(String sedeId);

    List<Pasajero> findBySedeIdAndTenant(String sedeId, String tenant);

    @Query("SELECT p FROM Pasajero p WHERE p.tenant = :tenant AND p.activo = true")
    List<Pasajero> findActivosByTenant(@Param("tenant") String tenant);

    @Query("select p from Pasajero p, nca.scc.com.admin.rutas.sede.entity.Sede s where p.sedeId = s.id and s.transportId = :transportId")
    List<Pasajero> findBySedeTransportId(@Param("transportId") String transportId);

    // Comprueba si existe un pasajero con id en la lista y cuyo padreId coincide con el padre dado
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pasajero p WHERE p.id IN :ids AND p.padreId = :padreId")
    boolean existsAnyByIdsAndPadreId(@Param("ids") List<String> ids, @Param("padreId") String padreId);

    boolean existsByIdAndPadreId(String id, String padreId);
}
