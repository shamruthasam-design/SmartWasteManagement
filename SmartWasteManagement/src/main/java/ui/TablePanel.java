package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Base class for every data page: title, action buttons, search box, filter combo and a sortable table.
 * Subclasses add buttons with addAction() and fill the table with setRows() inside refresh().
 */
public abstract class TablePanel extends JPanel implements Refreshable {
    protected final DefaultTableModel model;
    protected final JTable table;
    protected final TableRowSorter<DefaultTableModel> sorter;
    protected final JTextField searchField = new JTextField();
    protected final JComboBox<String> filterBox;
    protected final JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    protected final JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
    private final JLabel countLabel = new JLabel();
    private final int filterColumn;

    protected TablePanel(String title, String subtitle, String[] columns, int filterColumn,
                         String[] filterOptions, int... badgeColumns) {
        super(new BorderLayout(0, 18));
        this.filterColumn = filterColumn;
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // ---- header ----
        JLabel t = new JLabel(title);
        t.setFont(Theme.font(Font.BOLD, 24f));
        t.setForeground(Theme.TEXT);
        JLabel s = new JLabel(subtitle);
        s.setFont(Theme.font(Font.PLAIN, 13f));
        s.setForeground(Theme.MUTED);
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 3));
        titles.setOpaque(false);
        titles.add(t);
        titles.add(s);
        actionBar.setOpaque(false);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titles, BorderLayout.WEST);
        header.add(actionBar, BorderLayout.EAST);

        // ---- toolbar (search + filter) ----
        searchField.putClientProperty("JTextField.placeholderText", "Search anything...");
        searchField.setPreferredSize(new Dimension(300, 38));
        filterBox = new JComboBox<>(filterOptions == null ? new String[]{"All"} : filterOptions);
        filterBox.setPreferredSize(new Dimension(190, 38));
        countLabel.setForeground(Theme.MUTED);
        toolbar.setOpaque(false);
        toolbar.add(searchField);
        if (filterOptions != null) toolbar.add(filterBox);
        toolbar.add(countLabel);

        JPanel north = new JPanel(new BorderLayout(0, 16));
        north.setOpaque(false);
        north.add(header, BorderLayout.NORTH);
        north.add(toolbar, BorderLayout.SOUTH);

        // ---- table ----
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return getRowCount() == 0 || getValueAt(0, c) == null ? Object.class : getValueAt(0, c).getClass();
            }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setDefaultRenderer(Object.class, new PaddedRenderer());
        table.setDefaultRenderer(Integer.class, new PaddedRenderer());
        table.setDefaultRenderer(Double.class, new PaddedRenderer());
        for (int c : badgeColumns) table.getColumnModel().getColumn(c).setCellRenderer(new BadgeRenderer());
        JTableHeader th = table.getTableHeader();
        th.setReorderingAllowed(false);
        th.setFont(Theme.font(Font.BOLD, 12f));
        table.getColumnModel().getColumn(0).setMaxWidth(70);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.CARD);
        RoundedPanel card = new RoundedPanel(new BorderLayout(), Theme.CARD, 20);
        card.setBorder(new EmptyBorder(8, 8, 8, 8));
        card.add(scroll, BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);

        UiUtil.onChange(searchField, this::applyFilter);
        filterBox.addActionListener(e -> applyFilter());
    }

    protected RoundButton addAction(String text, Color color, Runnable action) {
        RoundButton b = new RoundButton(text, color);
        b.addActionListener(e -> action.run());
        actionBar.add(b);
        return b;
    }

    /** Replaces the table contents and re-applies search / filter. */
    protected void setRows(List<Object[]> rows) {
        model.setRowCount(0);
        for (Object[] r : rows) model.addRow(r);
        applyFilter();
    }

    protected void applyFilter() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        String q = searchField.getText().trim();
        if (!q.isEmpty()) filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(q)));
        String f = (String) filterBox.getSelectedItem();
        if (filterColumn >= 0 && f != null && !f.startsWith("All")) {
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(f) + "$", filterColumn));
        }
        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            RowFilter<Object, Object> combined = RowFilter.andFilter(filters);
            sorter.setRowFilter(combined);
        }
        countLabel.setText(table.getRowCount() + " records");
    }

    /** Returns the ID (first column) of the selected row, or -1 after showing a warning. */
    protected int requireSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            Notify.warn(this, "Please select a row in the table first.");
            return -1;
        }
        return (Integer) model.getValueAt(table.convertRowIndexToModel(viewRow), 0);
    }
}
