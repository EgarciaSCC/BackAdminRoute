package nca.scc.com.admin.rutas.driver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Resumen de ruta para el dashboard del conductor. Contrato frontend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverRoutePreview {

    private String id;
    private String name;
    private String direction;  // "to_school" | "from_school"
    private String status;    // "not_started" | "in_progress" | "completed"
    private String estimatedStartTime;  // "HH:MM"
    private String estimatedEndTime;    // "HH:MM"
    private String actualStartTime;     // "HH:MM" - solo para completadas
    private String actualEndTime;       // "HH:MM" - solo para completadas
    private int stopsCount;
    private int studentsCount;
    private Integer studentsTransported;  // solo para completadas
    private String busPlate;
    private String busId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEstimatedStartTime() { return estimatedStartTime; }
    public void setEstimatedStartTime(String estimatedStartTime) { this.estimatedStartTime = estimatedStartTime; }
    public String getEstimatedEndTime() { return estimatedEndTime; }
    public void setEstimatedEndTime(String estimatedEndTime) { this.estimatedEndTime = estimatedEndTime; }
    public String getActualStartTime() { return actualStartTime; }
    public void setActualStartTime(String actualStartTime) { this.actualStartTime = actualStartTime; }
    public String getActualEndTime() { return actualEndTime; }
    public void setActualEndTime(String actualEndTime) { this.actualEndTime = actualEndTime; }
    public int getStopsCount() { return stopsCount; }
    public void setStopsCount(int stopsCount) { this.stopsCount = stopsCount; }
    public int getStudentsCount() { return studentsCount; }
    public void setStudentsCount(int studentsCount) { this.studentsCount = studentsCount; }
    public Integer getStudentsTransported() { return studentsTransported; }
    public void setStudentsTransported(Integer studentsTransported) { this.studentsTransported = studentsTransported; }
    public String getBusPlate() { return busPlate; }
    public void setBusPlate(String busPlate) { this.busPlate = busPlate; }
    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
}
