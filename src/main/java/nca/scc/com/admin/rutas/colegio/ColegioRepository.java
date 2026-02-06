package nca.scc.com.admin.rutas.colegio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import nca.scc.com.admin.rutas.colegio.entity.Colegio;

import java.util.List;
import java.util.Optional;

public interface ColegioRepository extends JpaRepository<Colegio, String> {

    Optional<Colegio> findByNit(String nit);

    List<Colegio> findByTenant(String tenant);

    @Query("SELECT c FROM Colegio c WHERE c.tenant = :tenant AND c.activo = true")
    List<Colegio> findActivosByTenant(@Param("tenant") String tenant);

    long countByTenant(String tenant);
}
