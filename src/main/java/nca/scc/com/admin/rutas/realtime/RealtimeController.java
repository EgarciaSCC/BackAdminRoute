package nca.scc.com.admin.rutas.realtime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import nca.scc.com.admin.rutas.ruta.RutaRepository;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import nca.scc.com.admin.rutas.sede.SedeRepository;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.security.SecurityUtils;
import nca.scc.com.admin.rutas.auth.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/realtime")
@Tag(name = "Realtime", description = "Real-time position tracking and GeoJSON features")
public class RealtimeController {

    private final RutaRepository rutaRepository;
    private final SedeRepository sedeRepository;
    private final RutaPositionService positionService;
    private final SimpMessagingTemplate messagingTemplate;

    public RealtimeController(RutaRepository rutaRepository, SedeRepository sedeRepository, RutaPositionService positionService, SimpMessagingTemplate messagingTemplate) {
        this.rutaRepository = rutaRepository;
        this.sedeRepository = sedeRepository;
        this.positionService = positionService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/positions")
    @Operation(summary = "Get current positions", description = "Retrieve real-time positions for all routes accessible to the user")
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(type = "object")))
    public Map<String, Object> positions() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        List<String> allowedRutas;

        if (role == Role.ROLE_TRANSPORT && tenant != null) {
            List<Sede> sedes = sedeRepository.findByTransportId(tenant);
            var sedeIds = sedes.stream().map(Sede::getId).toList();
            allowedRutas = rutaRepository.findAll().stream().filter(r -> r.getSedeId() != null && sedeIds.contains(r.getSedeId())).map(Ruta::getId).collect(Collectors.toList());
        } else if (role == Role.ROLE_SCHOOL && tenant != null) {
            allowedRutas = rutaRepository.findAll().stream().filter(r -> tenant.equals(r.getSedeId())).map(Ruta::getId).collect(Collectors.toList());
        } else {
            allowedRutas = rutaRepository.findAll().stream().map(Ruta::getId).collect(Collectors.toList());
        }

        Map<String, RealtimePositionDTO> data = new HashMap<>();

        for (String rutaId : allowedRutas) {
            positionService.last(rutaId).ifPresent(p -> {
                RealtimePositionDTO dto = new RealtimePositionDTO(p.getLat(), p.getLng(), p.getHeading(), p.getSpeed(), "just now", 0);
                dto.setRutaId(rutaId);
                data.put(rutaId, dto);
            });
        }

        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("data", data);
        return r;
    }

    // Endpoint para que la app de conductor publique su posición actual (compatibilidad existente)
    @PostMapping("/positions")
    @Operation(summary = "Post position", description = "Publish current position for a route")
    @ApiResponse(responseCode = "201", description = "Position created")
    @ApiResponse(responseCode = "400", description = "Invalid payload")
    public ResponseEntity<?> postPosition(@RequestBody RealtimePositionDTO dto) {
        // El DTO debe contener rutaId, lat, lng, heading, speed
        if (dto == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing body");
        if (dto.getLat() == 0 || dto.getLng() == 0 || dto.getRutaId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }
        RutaPosition pos = new RutaPosition(dto.getRutaId(), dto.getLat(), dto.getLng(), dto.getHeading(), dto.getSpeed());
        RutaPosition saved = positionService.save(pos);

        // Publish to websocket topic for this route
        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");
        Map<String, Object> geom = new HashMap<>();
        geom.put("type", "Point");
        List<Double> coords = new ArrayList<>();
        coords.add(saved.getLng());
        coords.add(saved.getLat());
        geom.put("coordinates", coords);
        feature.put("geometry", geom);
        Map<String, Object> props = new HashMap<>();
        props.put("rutaId", saved.getRutaId());
        props.put("speed", saved.getSpeed());
        props.put("heading", saved.getHeading());
        props.put("timestamp", saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : "");
        feature.put("properties", props);
        feature.put("id", saved.getRutaId() + "-pos");

        messagingTemplate.convertAndSend("/topic/positions/" + saved.getRutaId(), feature);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Nuevo endpoint: aceptar un GeoJSON Feature (Mapbox) enviado por la app del conductor.
     * Estructura mínima esperada (Feature):
     * {
     *   "type": "Feature",
     *   "geometry": { "type": "Point", "coordinates": [lng, lat] },
     *   "properties": { "rutaId": "...", "speed": 12, "heading": 90, ... },
     *   "id": "optional-id"
     * }
     */
    @PostMapping("/positions/feature")
    @Operation(summary = "Post GeoJSON feature position", description = "Publish a GeoJSON Feature with position data")
    @ApiResponse(responseCode = "201", description = "Feature processed")
    @ApiResponse(responseCode = "400", description = "Invalid GeoJSON feature")
    public ResponseEntity<?> postFeaturePosition(@RequestBody Map<String, Object> feature) {
        if (feature == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing body");
        Object geometryObj = feature.get("geometry");
        if (!(geometryObj instanceof Map)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid geometry");
        Map<?, ?> geometry = (Map<?, ?>) geometryObj;
        Object coordsObj = geometry.get("coordinates");
        if (!(coordsObj instanceof List)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid coordinates");
        List<?> coords = (List<?>) coordsObj;
        if (coords.size() < 2) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Coordinates must be [lng, lat]");

        double lng, lat;
        try {
            Object o0 = coords.get(0);
            Object o1 = coords.get(1);
            lng = o0 instanceof Number ? ((Number) o0).doubleValue() : Double.parseDouble(o0.toString());
            lat = o1 instanceof Number ? ((Number) o1).doubleValue() : Double.parseDouble(o1.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid coordinate values");
        }

        // properties may contain rutaId
        String rutaId = null;
        Object propsObj = feature.get("properties");
        if (propsObj instanceof Map) {
            Map<?, ?> props = (Map<?, ?>) propsObj;
            if (props.get("rutaId") != null) rutaId = props.get("rutaId").toString();
            else if (props.get("routeId") != null) rutaId = props.get("routeId").toString();
        }
        // fallback: feature.id may be used as rutaId
        if ((rutaId == null || rutaId.isBlank()) && feature.get("id") != null) {
            rutaId = feature.get("id").toString();
        }

        if (rutaId == null || rutaId.isBlank()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("rutaId not found in properties or id");

        Integer heading = null;
        Integer speed = null;
        if (propsObj instanceof Map) {
            Map<?, ?> props = (Map<?, ?>) propsObj;
            try { if (props.get("heading") != null) heading = ((Number) props.get("heading")).intValue(); } catch (Exception ignored) {}
            try { if (props.get("speed") != null) speed = ((Number) props.get("speed")).intValue(); } catch (Exception ignored) {}
        }

        RutaPosition pos = new RutaPosition(rutaId, lat, lng, heading, speed);
        RutaPosition saved = positionService.save(pos);

        // Publish Feature to websocket topic
        messagingTemplate.convertAndSend("/topic/positions/" + saved.getRutaId(), feature);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Nuevo endpoint: devolver FeatureCollection GeoJSON con las últimas posiciones por ruta.
     * Esto deja el payload listo para renderizar en Mapbox en la UI de padres/admins.
     */
    @GetMapping("/positions/geojson")
    @Operation(summary = "Get GeoJSON FeatureCollection", description = "Retrieve positions as GeoJSON FeatureCollection")
    @ApiResponse(responseCode = "200", description = "GeoJSON FeatureCollection", content = @Content(schema = @Schema(type = "object")))
    public Map<String, Object> positionsGeoJson() {
        Map<String, Object> base = positions();
        @SuppressWarnings("unchecked")
        Map<String, RealtimePositionDTO> data = (Map<String, RealtimePositionDTO>) base.get("data");

        Map<String, Object> fc = new HashMap<>();
        fc.put("type", "FeatureCollection");
        List<Map<String, Object>> features = new ArrayList<>();

        if (data != null) {
            for (Map.Entry<String, RealtimePositionDTO> e : data.entrySet()) {
                String rutaId = e.getKey();
                RealtimePositionDTO dto = e.getValue();
                if (dto == null) continue;
                Map<String, Object> feature = new HashMap<>();
                feature.put("type", "Feature");
                Map<String, Object> geometry = new HashMap<>();
                geometry.put("type", "Point");
                List<Double> coords = new ArrayList<>();
                coords.add(dto.getLng());
                coords.add(dto.getLat());
                geometry.put("coordinates", coords);
                feature.put("geometry", geometry);

                Map<String, Object> props = new HashMap<>();
                props.put("rutaId", rutaId);
                props.put("speed", dto.getSpeed());
                props.put("heading", dto.getHeading());
                props.put("lastUpdate", dto.getLastUpdate());
                feature.put("properties", props);
                feature.put("id", rutaId + "-pos");
                features.add(feature);
            }
        }

        fc.put("features", features);
        return fc;
    }
}
