package nca.scc.com.admin.rutas.ruta.dto;

import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import nca.scc.com.admin.rutas.pasajero.dto.PasajeroPublicDTO;
import nca.scc.com.admin.rutas.auth.Role;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RutaRoleDTO {
    private Ruta ruta;
    private List<PasajeroPublicDTO> estudiantes;

    public RutaRoleDTO() {}

    public static RutaRoleDTO from(Ruta r, List<Pasajero> pasajeros, Role role) {
        RutaRoleDTO dto = new RutaRoleDTO();
        dto.ruta = r;
        if (pasajeros == null) {
            dto.estudiantes = null;
        } else {
            List<PasajeroPublicDTO> list = new ArrayList<>();
            for (Pasajero p : pasajeros) {
                list.add(PasajeroPublicDTO.from(p, role));
            }
            dto.estudiantes = list;
        }
        return dto;
    }

    public Ruta getRuta() { return ruta; }
    public void setRuta(Ruta ruta) { this.ruta = ruta; }
    public List<PasajeroPublicDTO> getEstudiantes() { return estudiantes; }
    public void setEstudiantes(List<PasajeroPublicDTO> estudiantes) { this.estudiantes = estudiantes; }
}
