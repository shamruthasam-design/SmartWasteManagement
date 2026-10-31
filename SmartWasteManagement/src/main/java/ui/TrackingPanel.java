package ui;

import model.CollectionRequest;
import service.CollectionService;
import utils.Constants;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/** Collection Tracking module: live status, assigned staff and completed-collection history. */
public class TrackingPanel extends TablePanel {
    private final CollectionService service = new CollectionService();
    private final JLabel summary = new JLabel();
    private List<CollectionRequest> data = new ArrayList<>();

    public TrackingPanel() {
        super("Collection Tracking", "Track progress of every request and view collection history",
                new String[]{"ID", "Location", "Category", "Quantity (kg)", "Assigned Staff", "Date", "Status", "Completed On"},
                6, Constants.prepend("All Status", Constants.COLLECTION_STATUS), 2, 6);
        toolbar.add(summary);
        addAction("Mark Completed", Theme.ACCENT, this::complete);
        addAction("Refresh", Theme.CARD_HOVER, this::refresh);
        refresh();
    }

    @Override
    public void refresh() {
        try {
            data = service.findAll();
            List<Object[]> rows = new ArrayList<>();
            int p = 0, a = 0, c = 0;
            for (CollectionRequest r : data) {
                rows.add(new Object[]{r.id(), r.location(), r.category(), r.quantity(),
                        r.staffName() == null ? "Unassigned" : r.staffName(), r.date().toString(), r.status(), r.completedAt()});
                switch (r.status()) {
                    case "Pending" -> p++;
                    case "Assigned" -> a++;
                    default -> c++;
                }
            }
            setRows(rows);
            summary.setText("<html><span style='color:#F59E0B'>\u25CF " + p + " Pending</span> &nbsp;&nbsp; "
                    + "<span style='color:#3B82F6'>\u25CF " + a + " Assigned</span> &nbsp;&nbsp; "
                    + "<span style='color:#10B981'>\u25CF " + c + " Completed</span></html>");
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }

    private void complete() {
        int id = requireSelection();
        if (id < 0) return;
        CollectionRequest r = data.stream().filter(x -> x.id() == id).findFirst().orElse(null);
        if (r == null) return;
        if (r.status().equals("Completed")) {
            Notify.warn(this, "This collection is already completed.");
            return;
        }
        try {
            service.updateStatus(r, "Completed");
            refresh();
            Notify.success(this, "Collection marked as completed.");
        } catch (Exception ex) {
            Notify.fail(this, ex);
        }
    }
}
