package nca.scc.com.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de política de retención de datos
 * Define cuántos días se mantienen registros de diferentes tipos
 */
@Configuration
public class DataRetentionConfig {

    @Value("${app.data-retention.historial-dias:365}")
    private int diasRetencionHistorial;

    @Value("${app.data-retention.ubicacion-dias:7}")
    private int diasRetencionUbicacion;

    @Value("${app.data-retention.novedad-dias:90}")
    private int diasRetencionNovedad;

    @Value("${app.data-retention.logs-dias:30}")
    private int diasRetencionLogs;

    public int getDiasRetencionHistorial() {
        return diasRetencionHistorial;
    }

    public int getDiasRetencionUbicacion() {
        return diasRetencionUbicacion;
    }

    public int getDiasRetencionNovedad() {
        return diasRetencionNovedad;
    }

    public int getDiasRetencionLogs() {
        return diasRetencionLogs;
    }

    @Override
    public String toString() {
        return "DataRetentionConfig{" +
                "diasRetencionHistorial=" + diasRetencionHistorial +
                ", diasRetencionUbicacion=" + diasRetencionUbicacion +
                ", diasRetencionNovedad=" + diasRetencionNovedad +
                ", diasRetencionLogs=" + diasRetencionLogs +
                '}';
    }
}
