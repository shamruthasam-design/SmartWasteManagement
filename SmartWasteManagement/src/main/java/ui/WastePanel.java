package ui;

import model.WasteRecord;
import service.WasteService;
import utils.Constants;
import utils.Session;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Waste Segregation module: add, edit, classify and delete waste records. */
public class WastePanel extends TablePanel {
    private final WasteService service = new WasteService();
    private List<WasteRecord> data = new ArrayList<>();

    public WastePanel() {
        super("Waste Segregation", "Record waste and classify it into the four categories",
                new String[]{"ID", "Category", "Quantity (kg)", "Date", "Status", "Notes"},
                1, Constants.prepend("All Categories", Constants.CATEGORIES), 1, 4);
        addAction("+ Add Waste", Theme.ACCENT, () -> openForm(null));
        addAction("Edit", Theme.INFO, this::editSelected);
        addAction("Mark Segregated", Theme.TEAL, this::markSegregated);
        if (Session.isAdmin()) addAction("Delete", Theme.DANGER, this::deleteSelected);
        refresh();
    }

    @Override
    public void refresh() {
        try {
            data = service.findAll();
            List<Object[]> rows = new ArrayList<>();
            for (WasteRecord w : data) {
                rows.add(new Object[]{w.id(), w.category(), w.quantity(), w.date().toString(), w.status(), w.notes()});
            }
            setRows(rows);
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }

    private WasteRecord selected() {
        int id = requireSelection();
        if (id < 0) return null;
        return data.stream().filter(w -> w.id() == id).findFirst().orElse(null);
    }

    private void editSelected() {
        WasteRecord r = selected();
        if (r != null) openForm(r);
    }

    private void markSegregated() {
        WasteRecord r = selected();
        if (r == null) return;
        try {
            service.markSegregated(r);
            refresh();
            Notify.success(this, "Waste marked as segregated.");
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }

    private void deleteSelected() {
        WasteRecord r = selected();
        if (r == null) return;
        if (!Notify.confirm(this, "Delete waste record #" + r.id() + " (" + r.category() + ")?")) return;
        try {
            service.delete(r.id());
            refresh();
            Notify.success(this, "Waste record deleted.");
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }

    private void openForm(WasteRecord r) {
        FormDialog d = new FormDialog(this, r == null ? "Add Waste Record" : "Edit Waste Record",
                "Enter the waste details below");
        JComboBox<String> category = new JComboBox<>(Constants.CATEGORIES);
        JTextField qty = new JTextField();
        JSpinner date = FormDialog.dateField(LocalDate.now());
        JComboBox<String> status = new JComboBox<>(Constants.WASTE_STATUS);
        JTextField notes = new JTextField();
        if (r != null) {
            category.setSelectedItem(r.category());
            qty.setText(String.valueOf(r.quantity()));
            date.setValue(FormDialog.toDate(r.date()));
            status.setSelectedItem(r.status());
            notes.setText(r.notes());
        }
        d.addRow("Waste category", category);
        d.addRow("Quantity (kg)", qty);
        d.addRow("Date", date);
        d.addRow("Segregation status", status);
        d.addRow("Notes (optional)", notes);
        d.showDialog(() -> {
            try {
                String cat = (String) category.getSelectedItem();
                String st = (String) status.getSelectedItem();
                LocalDate dt = FormDialog.getDate(date);
                if (r == null) service.add(cat, qty.getText(), dt, st, notes.getText());
                else service.update(r.id(), cat, qty.getText(), dt, st, notes.getText());
                refresh();
                Notify.success(this, r == null ? "Waste record added." : "Waste record updated.");
                return true;
            } catch (Exception ex) {
                Notify.fail(d, ex);
                return false;
            }
        });
    }
}
