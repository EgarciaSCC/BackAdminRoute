package nca.scc.com.admin.rutas.sede;

import nca.scc.com.admin.rutas.sede.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SedeRepository extends JpaRepository<Sede, String> {

    // ===== OWNERSHIP-BASED QUERIES =====

    /**
     * Sedes propiedad de un tenant (SCHOOL = colegio)
     */
    List<Sede> findByTenant(String tenant);

    /**
     * Sedes administradas por un TRANSPORT tenant
     */
    List<Sede> findByTransportId(String transportId);

    /**
     * Sedes de un colegio específico
     */
    List<Sede> findByColegioId(String colegioId);

    /**
     * Sede con validación de ownership
     */
    @Query("SELECT s FROM Sede s WHERE s.id = :sedeId AND s.tenant = :tenant")
    Optional<Sede> findByIdAndTenant(@Param("sedeId") String sedeId, @Param("tenant") String tenant);

    // ===== CROSS-TENANT VISIBILITY (via Route Aggregation Root) =====

    /**
     * Sedes donde un TRANSPORT puede ver estudiantes
     * (sedes que administra)
     */
    @Query("SELECT DISTINCT s FROM Sede s WHERE s.transportId = :transportId")
    List<Sede> findSedesVisiblesAlTransport(@Param("transportId") String transportId);

    /**
     * Sedes de un colegio (para ROLE_SCHOOL)
     */
    @Query("SELECT s FROM Sede s WHERE s.colegioId = :colegioId AND s.tenant = :colegioTenant")
    List<Sede> findByColegioIdAndTenant(@Param("colegioId") String colegioId, @Param("colegioTenant") String colegioTenant);
}
