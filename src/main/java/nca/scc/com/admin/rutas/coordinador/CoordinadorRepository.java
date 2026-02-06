package nca.scc.com.admin.rutas.coordinador;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;

import java.util.List;
import java.util.Optional;

public interface CoordinadorRepository extends JpaRepository<Coordinador, String> {

    Optional<Coordinador> findByCedula(String cedula);

    List<Coordinador> findByTenant(String tenant);

    @Query("SELECT c FROM Coordinador c WHERE c.tenant = :tenant AND c.activo = true")
    List<Coordinador> findActivosByTenant(@Param("tenant") String tenant);

    long countByTenant(String tenant);
}
