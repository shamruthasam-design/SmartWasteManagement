package service;

import dao.ActivityDAO;
import dao.ReportDAO;
import model.DashboardStats;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Builds the data for the dashboard and the reports module. Period is "Daily", "Weekly" or "Monthly". */
public class ReportService {
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd MMM");
    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMM yyyy");

    private final ReportDAO dao = new ReportDAO();
    private final ActivityDAO activity = new ActivityDAO();

    public DashboardStats dashboard() {
        Map<String, Integer> counts = dao.collectionCounts();
        return new DashboardStats(dao.completedCollectionKg(), dao.segregatedKg(),
                dao.categoryKg("Recyclable"), dao.categoryKg("Biodegradable"),
                dao.categoryKg("Hazardous"), dao.categoryKg("Non-Recyclable"),
                counts.get("Pending"), counts.get("Assigned"), counts.get("Completed"));
    }

    public List<String[]> recentActivity(int limit) { return activity.recent(limit); }

    public Map<String, Integer> collectionStatus() { return dao.collectionCounts(); }

    public Map<String, Integer> staffPerformance() { return dao.completedByStaff(); }

    /** First day covered by the period. */
    public LocalDate periodStart(String period) {
        LocalDate today = LocalDate.now();
        return switch (period) {
            case "Daily" -> today.minusDays(6);
            case "Weekly" -> today.minusDays(55);
            default -> YearMonth.now().minusMonths(5).atDay(1);
        };
    }

    public Map<String, Double> categoryTotals(String period) {
        return dao.totalsByCategory(periodStart(period));
    }

    /** Ordered (oldest first) label -> total kg, used for the trend charts. */
    public Map<String, Double> trend(String period) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Double> perDay = dao.totalsByDay(periodStart(period));
        Map<String, Double> out = new LinkedHashMap<>();
        if (period.equals("Daily")) {
            for (int i = 6; i >= 0; i--) {
                LocalDate d = today.minusDays(i);
                out.put(d.format(DAY), perDay.getOrDefault(d, 0.0));
            }
        } else if (period.equals("Weekly")) {
            for (int w = 7; w >= 0; w--) {
                LocalDate end = today.minusDays(7L * w);
                LocalDate start = end.minusDays(6);
                double sum = 0;
                for (Map.Entry<LocalDate, Double> e : perDay.entrySet()) {
                    if (!e.getKey().isBefore(start) && !e.getKey().isAfter(end)) sum += e.getValue();
                }
                out.put(start.format(DAY), sum);
            }
        } else {
            YearMonth current = YearMonth.now();
            for (int m = 5; m >= 0; m--) {
                YearMonth ym = current.minusMonths(m);
                double sum = 0;
                for (Map.Entry<LocalDate, Double> e : perDay.entrySet()) {
                    if (YearMonth.from(e.getKey()).equals(ym)) sum += e.getValue();
                }
                out.put(ym.format(MONTH), sum);
            }
        }
        return out;
    }
}
