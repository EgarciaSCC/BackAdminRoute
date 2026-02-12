package nca.scc.com.admin.rutas.rutaPasajeros;

import io.lettuce.core.dynamic.annotation.Param;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.RutaPasajero;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.enums.EstadoRutaPasajeros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RutaPasajeroRepository  extends JpaRepository<RutaPasajero, String> {

    @Query("SELECT rp FROM RutaPasajero rp WHERE rp.id.rutaId = :idRuta")
    List<RutaPasajero> findByIdRuta(String idRuta);
    @Query("SELECT rp FROM RutaPasajero rp WHERE rp.id.rutaId = :idRuta AND rp.id.pasajeroId = :idPasajero")
    RutaPasajero findByIdRutaAndIdPasajero(String idRuta, String idPasajero);
    @Query("DELETE FROM RutaPasajero rp WHERE rp.id.rutaId = :idRuta AND rp.id.pasajeroId = :idPasajero")
    void deleteByIdRutaAndIdPasajero(String idRuta, String idPasajero);
    @Query("SELECT rp FROM RutaPasajero rp WHERE rp.id.rutaId = :idRuta AND rp.id.pasajeroId = :idPasajero AND rp.estado = :estado")
    RutaPasajero findByIdRutaAndIdPasajeroAndEstado(String idRuta, String idPasajero, String estado);
    @Modifying
    @Query("UPDATE RutaPasajero rp SET rp.estado = :estado WHERE rp.id.rutaId = :idRuta AND rp.id.pasajeroId = :idPasajero")
    int updateEstadoByIdRutaAndIdPasajero(String idRuta, String idPasajero, String estado);

    @Modifying(clearAutomatically = true)
    @Query("""
    UPDATE RutaPasajero rp
    SET rp.pickupAt = :pickupAt,
        rp.estado = :nuevoEstado
    WHERE rp.id.rutaId = :idRuta
      AND rp.id.pasajeroId = :idPasajero
      AND rp.estado = :estadoActual
""")
    int updatePickupAtByIdRutaAndIdPasajero(
            @Param("idRuta") String idRuta,
            @Param("idPasajero") String idPasajero,
            @Param("pickupAt") LocalDateTime pickupAt,
            @Param("estadoActual") EstadoRutaPasajeros estadoActual,
            @Param("nuevoEstado") EstadoRutaPasajeros nuevoEstado
    );

    @Modifying(clearAutomatically = true)
    @Query("""
    UPDATE RutaPasajero rp
    SET rp.dropoffAt = :dropoffAt,
        rp.estado = :nuevoEstado
    WHERE rp.id.rutaId = :idRuta
      AND rp.id.pasajeroId = :idPasajero
      AND rp.estado = :estadoActual
""")
    int updateDropoffAtByIdRutaAndIdPasajero(
            @Param("idRuta") String idRuta,
            @Param("idPasajero") String idPasajero,
            @Param("dropoffAt") LocalDateTime dropoffAt,
            @Param("estadoActual") EstadoRutaPasajeros estadoActual,
            @Param("nuevoEstado") EstadoRutaPasajeros nuevoEstado
    );

    @Query("SELECT rp FROM RutaPasajero rp WHERE rp.id.pasajeroId = :idPasajero")
    List<RutaPasajero> findByIdPasajero(String idPasajero);
}
