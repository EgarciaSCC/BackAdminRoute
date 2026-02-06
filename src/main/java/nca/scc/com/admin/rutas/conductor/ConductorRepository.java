package nca.scc.com.admin.rutas.conductor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import nca.scc.com.admin.rutas.conductor.entity.enums.ConductorState;

import java.util.List;
import java.util.Optional;

public interface ConductorRepository extends JpaRepository<Conductor, String> {

    Optional<Conductor> findByCedula(String cedula);

    Optional<Conductor> findByLicencia(String licencia);

    List<Conductor> findByTenant(String tenant);

    @Query("SELECT c FROM Conductor c WHERE c.tenant = :tenant AND c.estado = :estado AND c.activo = true")
    List<Conductor> findByTenantAndEstado(@Param("tenant") String tenant, @Param("estado") ConductorState estado);

    @Query("SELECT c FROM Conductor c WHERE c.tenant = :tenant AND c.activo = true")
    List<Conductor> findActivosByTenant(@Param("tenant") String tenant);

    long countByTenant(String tenant);
}
