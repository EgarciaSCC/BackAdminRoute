package nca.scc.com.admin.rutas.realtime;

public class RealtimePositionDTO {
    private double lat;
    private double lng;
    private Integer heading;
    private Integer speed;
    private String lastUpdate;
    private Integer progress;

    public RealtimePositionDTO() {}

    public RealtimePositionDTO(double lat, double lng, Integer heading, Integer speed, String lastUpdate, Integer progress) {
        this.lat = lat;
        this.lng = lng;
        this.heading = heading;
        this.speed = speed;
        this.lastUpdate = lastUpdate;
        this.progress = progress;
    }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
    public Integer getHeading() { return heading; }
    public void setHeading(Integer heading) { this.heading = heading; }
    public Integer getSpeed() { return speed; }
    public void setSpeed(Integer speed) { this.speed = speed; }
    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }
}
