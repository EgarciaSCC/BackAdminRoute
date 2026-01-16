package nca.scc.com.admin.rutas.ruta;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.ruta.dto.ParadaTemporalDTO;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RutaService {

    private final RutaRepository repository;

    // almacenamiento en memoria para paradas temporales por ruta
    private final Map<String, List<ParadaTemporalDTO>> paradasTemporales = new ConcurrentHashMap<>();

    public RutaService(RutaRepository repository) {
        this.repository = repository;
    }

    public Ruta create(Ruta ruta) {
        return repository.save(ruta);
    }

    public List<Ruta> listAll() {
        return repository.findAll();
    }

    public Ruta getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ruta not found: " + id));
    }

    public Ruta update(String id, Ruta ruta) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Ruta not found: " + id);
        }
        ruta.setId(id);
        return repository.save(ruta);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Ruta not found: " + id);
        }
        repository.deleteById(id);
        paradasTemporales.remove(id);
    }

    // Paradas temporales API (in-memory)
    public ParadaTemporalDTO createParadaTemporal(String rutaId, ParadaTemporalDTO dto) {
        // validar existencia de ruta
        if (!repository.existsById(rutaId)) {
            throw new NotFoundException("Ruta not found: " + rutaId);
        }
        dto.setCreatedAt(Instant.now());
        dto.setExpiraAt(dto.getCreatedAt().plus(24, ChronoUnit.HOURS));
        dto.setEstado("pendiente");
        paradasTemporales.computeIfAbsent(rutaId, k -> new ArrayList<>()).add(dto);
        return dto;
    }

    public List<ParadaTemporalDTO> listParadasTemporales(String rutaId) {
        if (!repository.existsById(rutaId)) {
            throw new NotFoundException("Ruta not found: " + rutaId);
        }
        return paradasTemporales.getOrDefault(rutaId, new ArrayList<>());
    }

    public ParadaTemporalDTO approveParadaTemporal(String rutaId, String paradaId, String aprobadoPor) {
        ParadaTemporalDTO p = findParadaOrThrow(rutaId, paradaId);
        p.setEstado("aprobada");
        p.setAprobadoPor(aprobadoPor);
        p.setFechaAprobacion(Instant.now());
        // TODO: actualizar direccion del estudiante / notificaciones
        return p;
    }

    public ParadaTemporalDTO rejectParadaTemporal(String rutaId, String paradaId, String aprobadoPor, String comentario) {
        ParadaTemporalDTO p = findParadaOrThrow(rutaId, paradaId);
        p.setEstado("rechazada");
        p.setAprobadoPor(aprobadoPor);
        p.setFechaAprobacion(Instant.now());
        p.setComentario(comentario);
        return p;
    }

    private ParadaTemporalDTO findParadaOrThrow(String rutaId, String paradaId) {
        List<ParadaTemporalDTO> list = paradasTemporales.get(rutaId);
        if (list == null) throw new NotFoundException("No paradas for ruta: " + rutaId);
        return list.stream().filter(p -> p.getId().equals(paradaId))
                .findFirst().orElseThrow(() -> new NotFoundException("Parada not found: " + paradaId));
    }

    // Exponer todas las paradas temporales del sistema (opcional)
    public List<ParadaTemporalDTO> allParadasTemporales() {
        return paradasTemporales.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
}
