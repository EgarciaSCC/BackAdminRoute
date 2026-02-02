package nca.scc.com.admin.rutas.driver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Respuesta GET /api/driver/routes/history. Contrato frontend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverRouteHistoryResponse {

    private List<DriverRoutePreview> routes;
    private PaginationDto pagination;
    private SummaryDto summary;

    public List<DriverRoutePreview> getRoutes() { return routes; }
    public void setRoutes(List<DriverRoutePreview> routes) { this.routes = routes; }
    public PaginationDto getPagination() { return pagination; }
    public void setPagination(PaginationDto pagination) { this.pagination = pagination; }
    public SummaryDto getSummary() { return summary; }
    public void setSummary(SummaryDto summary) { this.summary = summary; }

    public static class PaginationDto {
        private int page;
        private int limit;
        private long total;
        private int totalPages;

        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }

    public static class SummaryDto {
        private int totalRoutes;
        private int totalStudentsTransported;
        private double averageDuration;  // minutos

        public int getTotalRoutes() { return totalRoutes; }
        public void setTotalRoutes(int totalRoutes) { this.totalRoutes = totalRoutes; }
        public int getTotalStudentsTransported() { return totalStudentsTransported; }
        public void setTotalStudentsTransported(int totalStudentsTransported) { this.totalStudentsTransported = totalStudentsTransported; }
        public double getAverageDuration() { return averageDuration; }
        public void setAverageDuration(double averageDuration) { this.averageDuration = averageDuration; }
    }
}
