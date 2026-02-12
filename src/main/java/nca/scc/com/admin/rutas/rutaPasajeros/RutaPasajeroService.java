package nca.scc.com.admin.rutas.rutaPasajeros;

import nca.scc.com.admin.rutas.auth.Role;
import nca.scc.com.admin.rutas.auth.UsuarioRepository;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import nca.scc.com.admin.rutas.conductor.ConductorRepository;
import nca.scc.com.admin.rutas.historial.enums.TipoEventoPasajero;
import nca.scc.com.admin.rutas.historial.pasajero.HistorialPasajeroRepository;
import nca.scc.com.admin.rutas.historial.pasajero.entity.HistorialPasajero;
import nca.scc.com.admin.rutas.historial.ruta.HistorialRutaRepository;
import nca.scc.com.admin.rutas.pasajero.PasajeroRepository;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.rutas.ruta.RutaRepository;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import nca.scc.com.admin.rutas.ruta.entity.enums.TipoRuta;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.RutaPasajeroId;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.enums.EstadoRutaPasajeros;
import org.springframework.security.oauth2.jwt.Jwt;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.RutaPasajero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static nca.scc.com.admin.security.SecurityUtils.getJwt;

@Service
public class RutaPasajeroService {

    private static final Logger log = LoggerFactory.getLogger(RutaPasajero.class);
    private final RutaPasajeroRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ConductorRepository conductorRepository;
    private final PasajeroRepository pasajeroRepository;
    private final HistorialRutaRepository historialRutaRepository;
    private final RutaRepository rutaRepository;
    private final HistorialPasajeroRepository historialPasajeroRepository;

    public RutaPasajeroService(RutaPasajeroRepository repository, UsuarioRepository usuarioRepository, ConductorRepository conductorRepository, PasajeroRepository pasajeroRepository, HistorialRutaRepository historialRutaRepository, RutaRepository rutaRepository, HistorialPasajeroRepository historialPasajeroRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.conductorRepository = conductorRepository;
        this.pasajeroRepository = pasajeroRepository;
        this.historialRutaRepository = historialRutaRepository;
        this.rutaRepository = rutaRepository;
        this.historialPasajeroRepository = historialPasajeroRepository;
    }

    public boolean resolveDriverFromAuth() {
        Jwt jwt = getJwt();
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }
        String username = jwt.getSubject();
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario no encontrado"));
        if (user.getRole() != Role.ROLE_TRANSPORT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol ROLE_TRANSPORT requerido");
        }

        // Primero intentar conductorId (conductor)
        String conductorId = user.getConductorId();
        if (conductorId != null && !conductorId.isBlank()) {
            return conductorRepository.findById(conductorId).isPresent();
                    //.orElseThrow(() -> new NotFoundException("Conductor no encontrado: " + conductorId));
        }

        // Si no tiene conductorId, es coordinador - retornar un "Conductor" proxy
        // que representa al coordinador (ambos usan la misma lógica de rutas)
        String coordinadorId = user.getCoordinadorId();
        if (coordinadorId != null && !coordinadorId.isBlank()) {
            return true;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Ni conductorId ni coordinadorId asociados al usuario");
    }

    public RutaPasajero findByIdRutaAndIdPasajero(String idRuta, String idPasajero) {
        if (!resolveDriverFromAuth()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ni conductorId ni coordinadorId asociados al usuario");
        }

        if (isUserAuthorizedForPasajero(idPasajero)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Acceso denegado a usuario para este pasajero");
        }

        return repository.findByIdRutaAndIdPasajero(idRuta, idPasajero);
    }

    public void deleteByIdRutaAndIdPasajero(String idRuta, String idPasajero) {
        repository.deleteByIdRutaAndIdPasajero(idRuta, idPasajero);
    }

    public RutaPasajero findByIdRutaAndIdPasajeroAndEstado(String idRuta, String idPasajero, String estado) {
        return repository.findByIdRutaAndIdPasajeroAndEstado(idRuta, idPasajero, estado);
    }

    public RutaPasajero updateEstadoByIdRutaAndIdPasajero(String idRuta, String idPasajero, String estado) {
        if (!resolveDriverFromAuth()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ni conductorId ni coordinadorId asociados al usuario");
        }

        if (isUserAuthorizedForPasajero(idPasajero)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Acceso denegado a usuario para este pasajero");
        }
        int update = repository.updateEstadoByIdRutaAndIdPasajero(idRuta, idPasajero, estado);
        if (update == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar estado. RutaPasajero no actualizado");
        }
        RutaPasajero rp = repository.findByIdRutaAndIdPasajero(idRuta, idPasajero);
        if (rp == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar estado. RutaPasajero no encontrado después de actualización");
        }

        return rp;
    }

    @Transactional
    public RutaPasajero updatePickupAtByIdRutaAndIdPasajero(RutaPasajero rutaPasajero) {
        RutaPasajeroId rutaPasajeroId = rutaPasajero.getId();
        String idRuta = rutaPasajeroId.getRutaId();
        String idPasajero = rutaPasajeroId.getPasajeroId();
        LocalDateTime pickupAt = rutaPasajero.getPickupAt();
        if (!resolveDriverFromAuth()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El Usuario no es Conductor o Coordinador");
        }

        if (!isUserAuthorizedForPasajero(idPasajero)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Acceso denegado a usuario para este pasajero");
        }

        //validar si la ruta esta en estado iniciada, si no, no permitir marcar recogida
        RutaPasajero rp = repository.findByIdRutaAndIdPasajero(idRuta, idPasajero);
        if (rp == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ruta: " + idRuta + " y pasajero: " + idPasajero + "no estan vinculados");
        }

        Ruta ruta = rutaRepository.findByIdRuta(idRuta);
        if (ruta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ruta no encontrada: " + idRuta);
        }

        if (!ruta.getEstado().equals("STARTED") && !ruta.getEstado().equals("INICIADA")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede marcar recogida. Ruta no se ha INICIADO");
        }

        if (!ruta.getTipoRuta().equals(TipoRuta.RECOGIDA)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede marcar recogida. Esta ruta no es LLEVADA a casa");
        }

        //validar que el estudiante no haya sido marcado como recogido previamente
        if (rp.getPickupAt() != null && !rp.getEstado().equals(EstadoRutaPasajeros.PENDIENTE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede marcar recogida. El pasajero ya fue marcado como recogido previamente");
        }

        rp.setPickupAt(pickupAt);
        rp.setEstado(EstadoRutaPasajeros.PICKED_UP);
        RutaPasajero update = repository.save(rp);
        repository.flush();

        // Crear historial de pasajero recogido
        HistorialPasajero hp = new HistorialPasajero();
        hp.setPasajeroId(idPasajero);
        hp.setFecha(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        hp.setTimestamp(LocalDateTime.now());
        hp.setEvento(TipoEventoPasajero.PICKUP);
        hp.setRutaId(idRuta);
        hp.setNota("Recogiendo pasajero: " + idPasajero);
        historialPasajeroRepository.save(hp);
        historialPasajeroRepository.flush();
        return update;
    }

    @Transactional
    public RutaPasajero updateDropoffAtByIdRutaAndIdPasajero(RutaPasajero rutaPasajero) {
        RutaPasajeroId rutaPasajeroId = rutaPasajero.getId();
        String idRuta = rutaPasajeroId.getRutaId();
        String idPasajero = rutaPasajeroId.getPasajeroId();
        LocalDateTime dropoffAt = rutaPasajero.getPickupAt();
        if (!resolveDriverFromAuth()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ni conductorId ni coordinadorId asociados al usuario");
        }

        if (!isUserAuthorizedForPasajero(idPasajero)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Acceso denegado a usuario para este pasajero");
        }

        //validar si la ruta esta en estado iniciada, si no, no permitir marcar recogida
        RutaPasajero rp = repository.findByIdRutaAndIdPasajero(idRuta, idPasajero);
        if (rp == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RutaPasajero no encontrado para ruta: " + idRuta + " y pasajero: " + idPasajero);
        }

        Ruta ruta = rutaRepository.findByIdRuta(idRuta);
        if (ruta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ruta no encontrada: " + idRuta);
        }

        if (!ruta.getEstado().equals("STARTED") && !ruta.getEstado().equals("INICIADA")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede marcar recogida. Ruta no se ha INICIADA");
        }

        if (!ruta.getTipoRuta().equals(TipoRuta.LLEVADA)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede marcar recogida. Esta ruta no es para RECOGIDA de estudiantes");
        }

        //validar que el estudiante no haya sido marcado como recogido previamente
        if (rp.getDropoffAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede marcar dejado. El pasajero ya fue marcado como dejado previamente");
        }

        rp.setDropoffAt(dropoffAt);
        rp.setEstado(EstadoRutaPasajeros.DROPPED_OFF);
        RutaPasajero update = repository.save(rp);

        // Crear historial de pasajero recogido
        HistorialPasajero hp = new HistorialPasajero();
        hp.setPasajeroId(idPasajero);
        hp.setFecha(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        hp.setTimestamp(LocalDateTime.now());
        hp.setEvento(TipoEventoPasajero.PICKUP);
        hp.setRutaId(idRuta);
        hp.setNota("Recogiendo pasajero: " + idPasajero);
        historialPasajeroRepository.save(hp);
        return update;
    }

    private boolean isUserAuthorizedForPasajero(String idPasajero) {
        String tenant = Objects.requireNonNull(getJwt()).getClaimAsString("tid");
        Pasajero pasajero = pasajeroRepository.findById(idPasajero).orElseThrow();
        return tenant.contains(pasajero.getTenant());
    }
}
