package nca.scc.com.admin.rutas.realtime;

import nca.scc.com.admin.rutas.ruta.RutaRepository;
import nca.scc.com.admin.rutas.sede.SedeRepository;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.security.SecurityUtils;
import nca.scc.com.admin.rutas.auth.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/realtime")
public class RealtimeController {

    private final RutaRepository rutaRepository;
    private final SedeRepository sedeRepository;

    public RealtimeController(RutaRepository rutaRepository, SedeRepository sedeRepository) {
        this.rutaRepository = rutaRepository;
        this.sedeRepository = sedeRepository;
    }

    @GetMapping("/positions")
    public Map<String, Object> positions() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        List<String> allowedRutas;

        if (role != null && role == Role.ROLE_TRANSPORT && tenant != null) {
            List<Sede> sedes = sedeRepository.findByTransportId(tenant);
            var sedeIds = sedes.stream().map(Sede::getId).toList();
            allowedRutas = rutaRepository.findAll().stream().filter(r -> r.sedeId() != null && sedeIds.contains(r.sedeId())).map(r -> r.getId()).collect(Collectors.toList());
        } else if (role != null && role == Role.ROLE_SCHOOL && tenant != null) {
            allowedRutas = rutaRepository.findAll().stream().filter(r -> tenant.equals(r.sedeId())).map(r -> r.getId()).collect(Collectors.toList());
        } else {
            allowedRutas = rutaRepository.findAll().stream().map(r -> r.getId()).collect(Collectors.toList());
        }

        Map<String, RealtimePositionDTO> data = new HashMap<>();
        // Simulación: solo agregamos posiciones si la ruta está permitida
        if (allowedRutas.stream().anyMatch(id -> id != null && id.startsWith("ruta-1"))) {
            data.put("ruta-1", new RealtimePositionDTO(4.7110, -74.0721, 45, 35, "2 min ago", 25));
        }
        if (allowedRutas.stream().anyMatch(id -> id != null && id.startsWith("ruta-2"))) {
            data.put("ruta-2", new RealtimePositionDTO(4.6520, -74.0640, 120, 28, "1 min ago", 60));
        }
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("data", data);
        return r;
    }
}
