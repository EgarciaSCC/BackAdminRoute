package nca.scc.com.admin.rutas.realtime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/realtime")
public class RealtimeController {

    @GetMapping("/positions")
    public Map<String, Object> positions() {
        Map<String, RealtimePositionDTO> data = new HashMap<>();
        data.put("ruta-1", new RealtimePositionDTO(4.7110, -74.0721, 45, 35, "2 min ago", 25));
        data.put("ruta-2", new RealtimePositionDTO(4.6520, -74.0640, 120, 28, "1 min ago", 60));
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("data", data);
        return r;
    }
}
