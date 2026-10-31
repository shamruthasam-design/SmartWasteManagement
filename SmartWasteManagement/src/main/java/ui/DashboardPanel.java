package ui;

import model.DashboardStats;
import service.ReportService;
import utils.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Dashboard: statistic cards, weekly / monthly charts, category split and recent activities. */
public class DashboardPanel extends JPanel implements Refreshable {
    private final ReportService service = new ReportService();

    private final StatCard collected = new StatCard("Total Waste Collected", "\u25C9", Theme.TEAL);
    private final StatCard segregated = new StatCard("Total Waste Segregated", "\u2714", Theme.ACCENT);
    private final StatCard recyclable = new StatCard("Recyclable Waste", "\u267B", Theme.INFO);
    private final StatCard organic = new StatCard("Organic Waste", "\u273F", Theme.ACCENT);
    private final StatCard hazardous = new StatCard("Hazardous Waste", "\u26A0", Theme.DANGER);
    private final StatCard nonRecyclable = new StatCard("Non-Recyclable Waste", "\u2298", Theme.PURPLE);
    private final StatCard pending = new StatCard("Pending Requests", "\u25D4", Theme.WARNING);
    private final StatCard completed = new StatCard("Completed Collections", "\u2611", Theme.ACCENT);

    private final JPanel weeklyHolder = new JPanel(new BorderLayout());
    private final JPanel monthlyHolder = new JPanel(new BorderLayout());
    private final JPanel pieHolder = new JPanel(new BorderLayout());
    private final JPanel statusHolder = new JPanel(new BorderLayout());
    private final JPanel activityList = new JPanel();

    public DashboardPanel() {
        super(new BorderLayout());
        setOpaque(false);

        JPanel body = new ScrollBody(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(24, 28, 28, 28));

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        column.add(buildHeader());
        column.add(Box.createVerticalStrut(18));

        JPanel cards = new JPanel(new GridLayout(2, 4, 16, 16));
        cards.setOpaque(false);
        for (StatCard c : new StatCard[]{collected, segregated, recyclable, organic,
                hazardous, nonRecyclable, pending, completed}) cards.add(c);
        column.add(cards);
        column.add(Box.createVerticalStrut(16));

        JPanel charts = new JPanel(new GridLayout(1, 3, 16, 0));
        charts.setOpaque(false);
        charts.add(chartCard("Weekly Collection (last 7 days, kg)", weeklyHolder));
        charts.add(chartCard("Monthly Trend (last 6 months, kg)", monthlyHolder));
        charts.add(chartCard("Waste by Category", pieHolder));
        charts.setPreferredSize(new Dimension(600, 310));
        charts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));
        column.add(charts);
        column.add(Box.createVerticalStrut(16));

        activityList.setOpaque(false);
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        JPanel bottom = new JPanel(new GridLayout(1, 2, 16, 0));
        bottom.setOpaque(false);
        bottom.add(chartCard("Recent Activities", activityList));
        bottom.add(chartCard("Collection Requests by Status", statusHolder));
        bottom.setPreferredSize(new Dimension(600, 320));
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        column.add(bottom);

        body.add(column, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        add(scroll, BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildHeader() {
        JLabel hello = new JLabel("Welcome back, " + Session.getUser().fullName());
        hello.setFont(Theme.font(Font.BOLD, 24f));
        hello.setForeground(Theme.TEXT);
        JLabel date = new JLabel("Here is what is happening with waste management today \u2022 "
                + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        date.setForeground(Theme.MUTED);
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 4));
        p.setOpaque(false);
        p.add(hello);
        p.add(date);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return p;
    }

    private JPanel chartCard(String title, JComponent content) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 8), Theme.CARD, 20);
        card.setBorder(new EmptyBorder(16, 18, 14, 18));
        JLabel l = new JLabel(title);
        l.setFont(Theme.font(Font.BOLD, 14f));
        l.setForeground(Theme.TEXT);
        content.setOpaque(false);
        card.add(l, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private static void swap(JPanel holder, JComponent chart) {
        holder.removeAll();
        holder.add(chart, BorderLayout.CENTER);
        holder.revalidate();
        holder.repaint();
    }

    @Override
    public void refresh() {
        try {
            DashboardStats s = service.dashboard();
            collected.setValue(kg(s.collected()));
            segregated.setValue(kg(s.segregated()));
            recyclable.setValue(kg(s.recyclable()));
            organic.setValue(kg(s.organic()));
            hazardous.setValue(kg(s.hazardous()));
            nonRecyclable.setValue(kg(s.nonRecyclable()));
            pending.setValue(String.valueOf(s.pending()));
            completed.setValue(String.valueOf(s.completed()));

            swap(weeklyHolder, ChartStyler.bar(service.trend("Daily"), "kg", Theme.ACCENT));
            swap(monthlyHolder, ChartStyler.line(service.trend("Monthly"), "kg", Theme.TEAL));
            swap(pieHolder, ChartStyler.pie(service.categoryTotals("Monthly")));
            swap(statusHolder, ChartStyler.bar(service.collectionStatus(), "requests", Theme.INFO));

            activityList.removeAll();
            List<String[]> items = service.recentActivity(8);
            for (String[] a : items) activityList.add(activityRow(a[0], a[1]));
            activityList.revalidate();
            activityList.repaint();
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }

    private JPanel activityRow(String message, String time) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));
        JLabel dot = new JLabel("\u25CF");
        dot.setForeground(Theme.ACCENT);
        JLabel text = new JLabel(message);
        text.setForeground(Theme.TEXT);
        JLabel when = new JLabel(time);
        when.setForeground(Theme.MUTED);
        when.setFont(Theme.font(Font.PLAIN, 11f));
        row.add(dot, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);
        row.add(when, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return row;
    }

    private static String kg(double v) { return String.format("%,.1f kg", v); }

    /** Panel that always matches the width of the scroll pane viewport (so the dashboard is responsive). */
    private static class ScrollBody extends JPanel implements Scrollable {
        ScrollBody(LayoutManager lm) { super(lm); }
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 20; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 120; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
