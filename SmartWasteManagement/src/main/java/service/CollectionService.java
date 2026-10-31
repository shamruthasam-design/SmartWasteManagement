package service;

import dao.ActivityDAO;
import dao.CollectionDAO;
import model.CollectionRequest;
import model.Staff;
import utils.AppException;
import utils.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/** Business rules for collection requests and their status workflow. */
public class CollectionService {
    private final CollectionDAO dao = new CollectionDAO();
    private final ActivityDAO activity = new ActivityDAO();

    public List<CollectionRequest> findAll() { return dao.findAll(); }

    /** Requests that still need a staff member. */
    public List<CollectionRequest> pendingRequests() {
        return dao.findAll().stream().filter(r -> r.status().equals("Pending")).collect(Collectors.toList());
    }

    public void add(String location, String category, String qtyText, Staff staff, LocalDate date) {
        String loc = Validator.required(location, "Location");
        double qty = Validator.positive(qtyText, "Quantity");
        Integer staffId = staffId(staff);
        dao.insert(loc, category, qty, staffId, date, staffId == null ? "Pending" : "Assigned");
        activity.log("New collection request: " + loc);
    }

    public void update(CollectionRequest old, String location, String category, String qtyText, Staff staff, LocalDate date) {
        String loc = Validator.required(location, "Location");
        double qty = Validator.positive(qtyText, "Quantity");
        Integer staffId = staffId(staff);
        String status = old.status().equals("Completed") ? "Completed" : (staffId == null ? "Pending" : "Assigned");
        dao.update(old.id(), loc, category, qty, staffId, date, status);
        activity.log("Collection request #" + old.id() + " updated");
    }

    public void updateStatus(CollectionRequest r, String status) {
        if (!status.equals("Pending") && r.staffId() == null) {
            throw new AppException("Assign a staff member to this request before changing its status.");
        }
        dao.updateStatus(r.id(), status);
        activity.log("Collection at " + r.location() + " marked " + status);
    }

    public void assign(CollectionRequest r, Staff staff) {
        dao.assign(r.id(), staff.id());
        activity.log("Staff " + staff.name() + " assigned to " + r.location());
    }

    public void delete(CollectionRequest r) {
        dao.delete(r.id());
        activity.log("Collection request at " + r.location() + " deleted");
    }

    private Integer staffId(Staff staff) {
        return (staff == null || staff.id() == 0) ? null : staff.id();
    }
}
