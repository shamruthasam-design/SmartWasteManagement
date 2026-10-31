package ui;

import service.ReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/** Reports and Analytics: daily / weekly / monthly charts, category statistics and staff performance. */
public class ReportsPanel extends JPanel implements Refreshable {
    private final ReportService service = new ReportService();
    private final JComboBox<String> period = new JComboBox<>(new String[]{"Daily", "Weekly", "Monthly"});
    private final JPanel pieHolder = new JPanel(new BorderLayout());
    private final JPanel trendHolder = new JPanel(new BorderLayout());
    private final JPanel staffHolder = new JPanel(new BorderLayout());
    private final JLabel pieTitle = cardTitle("");
    private final JLabel trendTitle = cardTitle("");
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Category", "Total (kg)", "Share"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public ReportsPanel() {
        super(new BorderLayout(0, 18));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(Theme.font(Font.BOLD, 24f));
        title.setForeground(Theme.TEXT);
        JLabel sub = new JLabel("Waste statistics and collection performance");
        sub.setForeground(Theme.MUTED);
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 3));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(sub);

        JLabel pl = new JLabel("Report period:");
        pl.setForeground(Theme.MUTED);
        period.setPreferredSize(new Dimension(150, 38));
        period.addActionListener(e -> refresh());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(pl);
        right.add(period);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titles, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new PaddedRenderer());
        table.getColumnModel().getColumn(0).setCellRenderer(new BadgeRenderer());
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.CARD);
        JPanel tableBox = new JPanel(new BorderLayout());
        tableBox.setOpaque(false);
        tableBox.add(scroll, BorderLayout.CENTER);

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.add(card(pieTitle, pieHolder));
        grid.add(card(trendTitle, trendHolder));
        grid.add(card(cardTitle("Collection Performance (completed per staff)"), staffHolder));
        grid.add(card(cardTitle("Category-wise Statistics"), tableBox));

        add(header, BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);
        refresh();
    }

    private static JLabel cardTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.font(Font.BOLD, 14f));
        l.setForeground(Theme.TEXT);
        return l;
    }

    private JPanel card(JLabel title, JComponent content) {
        RoundedPanel c = new RoundedPanel(new BorderLayout(0, 8), Theme.CARD, 20);
        c.setBorder(new EmptyBorder(16, 18, 14, 18));
        content.setOpaque(false);
        c.add(title, BorderLayout.NORTH);
        c.add(content, BorderLayout.CENTER);
        return c;
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
            String p = (String) period.getSelectedItem();
            String span = switch (p) {
                case "Daily" -> "last 7 days";
                case "Weekly" -> "last 8 weeks";
                default -> "last 6 months";
            };
            Map<String, Double> categories = service.categoryTotals(p);
            pieTitle.setText(p + " report - waste by category (" + span + ")");
            trendTitle.setText(p + " waste collected (" + span + ", kg)");
            swap(pieHolder, ChartStyler.pie(categories));
            Map<String, Double> trend = service.trend(p);
            swap(trendHolder, p.equals("Monthly") ? ChartStyler.line(trend, "kg", Theme.TEAL)
                    : ChartStyler.bar(trend, "kg", Theme.ACCENT));
            swap(staffHolder, ChartStyler.bar(service.staffPerformance(), "completed", Theme.INFO));

            double total = categories.values().stream().mapToDouble(Double::doubleValue).sum();
            tableModel.setRowCount(0);
            for (Map.Entry<String, Double> e : categories.entrySet()) {
                double share = total == 0 ? 0 : e.getValue() * 100 / total;
                tableModel.addRow(new Object[]{e.getKey(), String.format("%,.2f", e.getValue()), String.format("%.1f%%", share)});
            }
            tableModel.addRow(new Object[]{"Total", String.format("%,.2f", total), "100%"});
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }
}
