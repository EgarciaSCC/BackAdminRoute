package nca.scc.com.admin.rutas.sede;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.security.SecurityUtils;
import nca.scc.com.admin.rutas.auth.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SedeService {

    private final SedeRepository repository;

    public SedeService(SedeRepository repository) {
        this.repository = repository;
    }

    public Sede create(Sede sede) {
        return repository.save(sede);
    }

    public List<Sede> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role != null && role == Role.ROLE_TRANSPORT && tenant != null) {
            return repository.findByTransportId(tenant);
        } else if (role != null && role == Role.ROLE_SCHOOL && tenant != null) {
            return repository.findAll().stream().filter(s -> s.getId().equals(tenant)).toList();
        } else {
            return repository.findAll();
        }
    }

    public Sede getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sede not found: " + id));
    }

    public Sede update(String id, Sede sede) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Sede not found: " + id);
        }
        sede.setId(id);
        return repository.save(sede);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Sede not found: " + id);
        }
        repository.deleteById(id);
    }
}
