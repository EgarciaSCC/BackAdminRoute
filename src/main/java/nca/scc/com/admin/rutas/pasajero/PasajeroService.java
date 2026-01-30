package nca.scc.com.admin.rutas.pasajero;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.security.SecurityUtils;
import nca.scc.com.admin.rutas.auth.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasajeroService {

    private final PasajeroRepository repository;

    public PasajeroService(PasajeroRepository repository) {
        this.repository = repository;
    }

    public Pasajero create(Pasajero pasajero) {
        return repository.save(pasajero);
    }

    public List<Pasajero> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role != null && role == Role.ROLE_TRANSPORT && tenant != null) {
            return repository.findBySedeTransportId(tenant);
        } else if (role != null && role == Role.ROLE_SCHOOL && tenant != null) {
            return repository.findBySedeId(tenant);
        } else {
            // Default: return all (could be restricted to admins)
            return repository.findAll();
        }
    }

    public Pasajero getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pasajero not found: " + id));
    }

    public Pasajero update(String id, Pasajero pasajero) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Pasajero not found: " + id);
        }
        pasajero.setId(id);
        return repository.save(pasajero);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Pasajero not found: " + id);
        }
        repository.deleteById(id);
    }
}
