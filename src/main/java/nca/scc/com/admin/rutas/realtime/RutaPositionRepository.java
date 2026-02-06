package nca.scc.com.admin.rutas.realtime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RutaPositionRepository extends JpaRepository<RutaPosition, String> {

    @Query("SELECT p FROM RutaPosition p WHERE p.rutaId = :rutaId ORDER BY p.createdAt DESC")
    List<RutaPosition> findByRutaIdOrderByCreatedAtDesc(@Param("rutaId") String rutaId);

    @Query("SELECT p FROM RutaPosition p WHERE p.rutaId = :rutaId ORDER BY p.createdAt DESC")
    List<RutaPosition> findLatestByRutaId(@Param("rutaId") String rutaId, org.springframework.data.domain.Pageable pageable);

    default Optional<RutaPosition> findLastPosition(String rutaId) {
        var list = findLatestByRutaId(rutaId, org.springframework.data.domain.PageRequest.of(0,1));
        if (list == null || list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0));
    }
}
