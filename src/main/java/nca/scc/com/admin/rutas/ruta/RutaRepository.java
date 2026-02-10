package nca.scc.com.admin.rutas.ruta;

import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RutaRepository extends JpaRepository<Ruta, String> {

    // ===== OWNERSHIP-BASED QUERIES =====

    /**
     * Rutas propiedad de un tenant específico (SCHOOL o TRANSPORT)
     */
    List<Ruta> findByTenant(String tenant);

    /**
     * Rutas de un tenant con estado específico
     */
    @Query("SELECT r FROM Ruta r WHERE r.tenant = :tenant AND r.estado = :estado")
    List<Ruta> findByTenantAndEstado(@Param("tenant") String tenant, @Param("estado") String estado);

    // ===== ASSIGNMENT-BASED QUERIES =====

    /**
     * Rutas asignadas a un conductor (visible para ROLE_TRANSPORT)
     */
    List<Ruta> findByConductorId(String conductorId);

    /**
     * Rutas asignadas a un coordinador (visible para ROLE_TRANSPORT)
     */
    List<Ruta> findByCoordinadorId(String coordinadorId);

    /**
     * Rutas asignadas a conductor O coordinador
     */
    @Query("SELECT r FROM Ruta r WHERE r.conductorId = :personaId OR r.coordinadorId = :personaId")
    List<Ruta> findByAsignadoA(@Param("personaId") String personaId);

    // ===== CROSS-TENANT VISIBILITY (via Route Aggregation Root) =====

    /**
     * Obtener ruta con validación de ownership
     */
    @Query("SELECT r FROM Ruta r WHERE r.id = :rutaId AND r.tenant = :tenant")
    Optional<Ruta> findByIdAndTenant(@Param("rutaId") String rutaId, @Param("tenant") String tenant);

    /**
     * Rutas que incluyen estudiantes de una sede específica
     * NOTA: Query simplificada - evitar MEMBER OF en H2
     */
    @Query("SELECT r FROM Ruta r WHERE r.sedeId = :sedeId")
    List<Ruta> findRutasConEstudiantesDeSede(@Param("sedeId") String sedeId);

    /**
     * Rutas que un TRANSPORT tenant puede ver
     * (rutas propias únicamente - filtering en service layer)
     */
    @Query("SELECT r FROM Ruta r WHERE r.tenant = :transportTenant")
    List<Ruta> findRutasVisiblesAlTransport(@Param("transportTenant") String transportTenant);

    /**
     * Rutas que un SCHOOL tenant puede ver
     * (rutas propias únicamente - filtering en service layer)
     */
    @Query("SELECT r FROM Ruta r WHERE r.tenant = :schoolTenant")
    List<Ruta> findRutasVisiblesAlColegio(@Param("schoolTenant") String schoolTenant);
}
