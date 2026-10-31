package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BooleanSupplier;

/** Reusable modal form: add rows with addRow(), then call showDialog(onSave). onSave returns true to close. */
public class FormDialog extends JDialog {
    private final JPanel form = new JPanel(new GridBagLayout());
    private int row = 0;

    public FormDialog(Component parent, String title, String subtitle) {
        super(parent == null ? null : SwingUtilities.getWindowAncestor(parent), title, ModalityType.APPLICATION_MODAL);
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(Theme.CARD);
        root.setBorder(new EmptyBorder(24, 28, 22, 28));

        JLabel t = new JLabel(title);
        t.setFont(Theme.font(Font.BOLD, 20f));
        t.setForeground(Theme.TEXT);
        JLabel s = new JLabel(subtitle);
        s.setFont(Theme.font(Font.PLAIN, 12f));
        s.setForeground(Theme.MUTED);
        JPanel head = new JPanel(new GridLayout(2, 1, 0, 4));
        head.setOpaque(false);
        head.add(t);
        head.add(s);

        form.setOpaque(false);
        root.add(head, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        setContentPane(root);
    }

    public void addRow(String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(8, 0, 0, 0);
        JLabel l = new JLabel(label);
        l.setForeground(Theme.MUTED);
        l.setFont(Theme.font(Font.PLAIN, 12f));
        form.add(l, c);
        c.gridy = row++;
        c.insets = new Insets(4, 0, 0, 0);
        field.setPreferredSize(new Dimension(380, 38));
        form.add(field, c);
    }

    public void showDialog(BooleanSupplier onSave) {
        RoundButton save = new RoundButton("Save", Theme.ACCENT);
        RoundButton cancel = new RoundButton("Cancel", Theme.CARD_HOVER);
        save.addActionListener(e -> {
            if (onSave.getAsBoolean()) dispose();
        });
        cancel.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(cancel);
        buttons.add(save);
        ((JPanel) getContentPane()).add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);
        pack();
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    // ---- small helpers for date fields ----

    public static JSpinner dateField(LocalDate date) {
        JSpinner spinner = new JSpinner(new SpinnerDateModel(toDate(date), null, null, Calendar.DAY_OF_MONTH));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    public static Date toDate(LocalDate d) {
        return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate getDate(JSpinner spinner) {
        return ((Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
