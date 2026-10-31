package ui;

import model.CollectionRequest;
import model.Staff;
import service.CollectionService;
import service.StaffService;
import utils.Constants;
import utils.Session;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Waste Collection Management module: create, edit, assign and update collection requests. */
public class CollectionPanel extends TablePanel {
    private final CollectionService service = new CollectionService();
    private final StaffService staffService = new StaffService();
    private List<CollectionRequest> data = new ArrayList<>();

    public CollectionPanel() {
        super("Waste Collection", "Create requests, assign staff and update their status",
                new String[]{"ID", "Location", "Category", "Quantity (kg)", "Assigned Staff", "Collection Date", "Status"},
                6, Constants.prepend("All Status", Constants.COLLECTION_STATUS), 2, 6);
        addAction("+ New Request", Theme.ACCENT, () -> openForm(null));
        addAction("Edit", Theme.INFO, this::editSelected);
        addAction("Update Status", Theme.TEAL, this::updateStatus);
        if (Session.isAdmin()) addAction("Delete", Theme.DANGER, this::deleteSelected);
        refresh();
    }

    @Override
    public void refresh() {
        try {
            data = service.findAll();
            List<Object[]> rows = new ArrayList<>();
            for (CollectionRequest r : data) {
                rows.add(new Object[]{r.id(), r.location(), r.category(), r.quantity(),
                        r.staffName() == null ? "Unassigned" : r.staffName(), r.date().toString(), r.status()});
            }
            setRows(rows);
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }

    private CollectionRequest selected() {
        int id = requireSelection();
        if (id < 0) return null;
        return data.stream().filter(r -> r.id() == id).findFirst().orElse(null);
    }

    private void editSelected() {
        CollectionRequest r = selected();
        if (r != null) openForm(r);
    }

    private void deleteSelected() {
        CollectionRequest r = selected();
        if (r == null) return;
        if (!Notify.confirm(this, "Delete the collection request at \"" + r.location() + "\"?")) return;
        try {
            service.delete(r);
            refresh();
            Notify.success(this, "Collection request deleted.");
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }

    private void updateStatus() {
        CollectionRequest r = selected();
        if (r == null) return;
        FormDialog d = new FormDialog(this, "Update Status", r.location());
        JComboBox<String> status = new JComboBox<>(Constants.COLLECTION_STATUS);
        status.setSelectedItem(r.status());
        d.addRow("New status", status);
        d.showDialog(() -> {
            try {
                service.updateStatus(r, (String) status.getSelectedItem());
                refresh();
                Notify.success(this, "Status updated.");
                return true;
            } catch (Exception ex) {
                Notify.fail(d, ex);
                return false;
            }
        });
    }

    private void openForm(CollectionRequest r) {
        FormDialog d = new FormDialog(this, r == null ? "New Collection Request" : "Edit Collection Request",
                "Where should the waste be collected?");
        JTextField location = new JTextField();
        JComboBox<String> category = new JComboBox<>(Constants.CATEGORIES);
        JTextField qty = new JTextField();
        JComboBox<Staff> staff = new JComboBox<>();
        staff.addItem(Staff.UNASSIGNED);
        for (Staff s : staffService.findActive()) staff.addItem(s);
        JSpinner date = FormDialog.dateField(LocalDate.now());
        if (r != null) {
            location.setText(r.location());
            category.setSelectedItem(r.category());
            qty.setText(String.valueOf(r.quantity()));
            date.setValue(FormDialog.toDate(r.date()));
            if (r.staffId() != null) {
                for (int i = 0; i < staff.getItemCount(); i++) {
                    if (staff.getItemAt(i).id() == r.staffId()) staff.setSelectedIndex(i);
                }
            }
        }
        d.addRow("Location", location);
        d.addRow("Waste category", category);
        d.addRow("Quantity (kg)", qty);
        d.addRow("Assign staff", staff);
        d.addRow("Collection date", date);
        d.showDialog(() -> {
            try {
                String cat = (String) category.getSelectedItem();
                Staff st = (Staff) staff.getSelectedItem();
                LocalDate dt = FormDialog.getDate(date);
                if (r == null) service.add(location.getText(), cat, qty.getText(), st, dt);
                else service.update(r, location.getText(), cat, qty.getText(), st, dt);
                refresh();
                Notify.success(this, r == null ? "Collection request created." : "Collection request updated.");
                return true;
            } catch (Exception ex) {
                Notify.fail(d, ex);
                return false;
            }
        });
    }
}
