package nca.scc.com.admin.rutas.pasajero;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;

import java.util.List;
import java.util.Optional;

public interface PasajeroRepository extends JpaRepository<Pasajero, String> {

    // ===== TENANT-BASED QUERIES (ROLE_ADMIN_SCHOOL) =====

    Optional<Pasajero> findByMatricula(String matricula);

    Optional<Pasajero> findById(String id);

    /**
     * Estudiantes de un tenant específico (colegio)
     */
    List<Pasajero> findByTenant(String tenant);

    List<Pasajero> findBySedeId(String sedeId);

    List<Pasajero> findBySedeIdAndTenant(String sedeId, String tenant);

    @Query("SELECT p FROM Pasajero p WHERE p.tenant = :tenant AND p.activo = true")
    List<Pasajero> findActivosByTenant(@Param("tenant") String tenant);

    // ===== CROSS-TENANT QUERIES (ROLE_ADMIN_TRANSPORT & ROLE_TRANSPORT) =====

    @Query("SELECT p FROM Pasajero p WHERE p.id = :id AND p.tenant IN (SELECT c.tenant FROM Conductor c WHERE c.id = :transportId)")
    Optional<Pasajero> findByIdAndTransportId(@Param("id") String id, @Param("transportId") String transportId);

    /**
     * Estudiantes de sedes administradas por un TRANSPORT tenant
     */
    @Query("SELECT DISTINCT p FROM Pasajero p, Sede s WHERE p.sedeId = s.id AND s.transportId = :transportId")
    List<Pasajero> findBySedeTransportId(@Param("transportId") String transportId);

    // Queries con MEMBER OF deshabilitadas - H2 no soporta correctamente
    // findByRutaId, findByRutaIdIn, existsInRuta removidas
    // Usar RutaService.getFullById() + mapeo manual si es necesario

    /**
     * Verificar padre-estudiante (para ROLE_PARENT)
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pasajero p WHERE p.id IN :ids AND p.padreId = :padreId")
    boolean existsAnyByIdsAndPadreId(@Param("ids") List<String> ids, @Param("padreId") String padreId);

    boolean existsByIdAndPadreId(String id, String padreId);

    /**
     * Estudiantes de una sede que un TRANSPORT puede ver
     * (visible SOLO si administra la sede)
     */
    @Query("SELECT p FROM Pasajero p, Sede s WHERE p.sedeId = s.id AND s.transportId = :transportId AND s.id = :sedeId")
    List<Pasajero> findBySedeIdAdministrado(@Param("sedeId") String sedeId, @Param("transportId") String transportId);

    @Query("SELECT c.tenant FROM Conductor c WHERE c.id = :id")
    Optional<String> findTenantById(@Param("id") String id);

    @Query(value = "SELECT p.* FROM pasajero p " +
            "JOIN ruta_estudiantes re ON re.pasajeros = p.id WHERE re.ruta_id = :rutaId",
            nativeQuery = true
    )
    List<Pasajero> findPasajerosByRutaNative(@Param("rutaId") String rutaId);

    // Nuevo: listar por padreId (para ROLE_PARENT)
    List<Pasajero> findByPadreId(String padreId);
}
