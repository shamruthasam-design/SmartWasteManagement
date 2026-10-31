package ui;

import model.CollectionRequest;
import model.Staff;
import service.CollectionService;
import service.StaffService;
import utils.Constants;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/** Staff Management module (admin only): add, edit, delete staff and assign tasks. */
public class StaffPanel extends TablePanel {
    private final StaffService service = new StaffService();
    private final CollectionService collectionService = new CollectionService();
    private List<Staff> data = new ArrayList<>();

    public StaffPanel() {
        super("Staff Management", "Manage collection staff and their assignments",
                new String[]{"ID", "Name", "Phone", "Email", "Area", "Status", "Open Tasks"},
                5, Constants.prepend("All Status", Constants.STAFF_STATUS), 5);
        addAction("+ Add Staff", Theme.ACCENT, () -> openForm(null));
        addAction("Edit", Theme.INFO, this::editSelected);
        addAction("Assign Task", Theme.TEAL, this::assignTask);
        addAction("Delete", Theme.DANGER, this::deleteSelected);
        refresh();
    }

    @Override
    public void refresh() {
        try {
            data = service.findAll();
            List<Object[]> rows = new ArrayList<>();
            for (Staff s : data) {
                rows.add(new Object[]{s.id(), s.name(), s.phone(), s.email(), s.area(), s.status(), s.openTasks()});
            }
            setRows(rows);
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }

    private Staff selected() {
        int id = requireSelection();
        if (id < 0) return null;
        return data.stream().filter(s -> s.id() == id).findFirst().orElse(null);
    }

    private void editSelected() {
        Staff s = selected();
        if (s != null) openForm(s);
    }

    private void deleteSelected() {
        Staff s = selected();
        if (s == null) return;
        if (!Notify.confirm(this, "Delete staff member \"" + s.name() + "\"?\nTheir requests will become unassigned.")) return;
        try {
            service.delete(s);
            refresh();
            Notify.success(this, "Staff member deleted.");
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }

    private void assignTask() {
        Staff s = selected();
        if (s == null) return;
        if (!s.status().equals("Active")) {
            Notify.warn(this, "Inactive staff cannot be assigned tasks.");
            return;
        }
        List<CollectionRequest> pending = collectionService.pendingRequests();
        if (pending.isEmpty()) {
            Notify.warn(this, "There are no pending requests to assign.");
            return;
        }
        FormDialog d = new FormDialog(this, "Assign Collection Task", "Assign a pending request to " + s.name());
        JComboBox<CollectionRequest> box = new JComboBox<>(pending.toArray(new CollectionRequest[0]));
        d.addRow("Pending request", box);
        d.showDialog(() -> {
            try {
                collectionService.assign((CollectionRequest) box.getSelectedItem(), s);
                refresh();
                Notify.success(this, "Task assigned to " + s.name() + ".");
                return true;
            } catch (Exception ex) {
                Notify.fail(d, ex);
                return false;
            }
        });
    }

    private void openForm(Staff s) {
        FormDialog d = new FormDialog(this, s == null ? "Add Staff Member" : "Edit Staff Member", "Staff contact details");
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();
        JTextField area = new JTextField();
        JComboBox<String> status = new JComboBox<>(Constants.STAFF_STATUS);
        if (s != null) {
            name.setText(s.name());
            phone.setText(s.phone());
            email.setText(s.email());
            area.setText(s.area());
            status.setSelectedItem(s.status());
        }
        d.addRow("Full name", name);
        d.addRow("Phone", phone);
        d.addRow("Email (optional)", email);
        d.addRow("Assigned area", area);
        d.addRow("Status", status);
        d.showDialog(() -> {
            try {
                String st = (String) status.getSelectedItem();
                if (s == null) service.add(name.getText(), phone.getText(), email.getText(), area.getText(), st);
                else service.update(s.id(), name.getText(), phone.getText(), email.getText(), area.getText(), st);
                refresh();
                Notify.success(this, s == null ? "Staff member added." : "Staff member updated.");
                return true;
            } catch (Exception ex) {
                Notify.fail(d, ex);
                return false;
            }
        });
    }
}
