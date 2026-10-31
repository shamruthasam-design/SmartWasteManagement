package service;

import dao.ActivityDAO;
import dao.WasteDAO;
import model.WasteRecord;
import utils.Validator;

import java.time.LocalDate;
import java.util.List;

/** Business rules for waste segregation records. */
public class WasteService {
    private final WasteDAO dao = new WasteDAO();
    private final ActivityDAO activity = new ActivityDAO();

    public List<WasteRecord> findAll() { return dao.findAll(); }

    public void add(String category, String qtyText, LocalDate date, String status, String notes) {
        double qty = Validator.positive(qtyText, "Quantity");
        dao.insert(category, qty, date, status, notes == null ? "" : notes.trim());
        activity.log("Waste record added: " + category + " " + qty + " kg");
    }

    public void update(int id, String category, String qtyText, LocalDate date, String status, String notes) {
        double qty = Validator.positive(qtyText, "Quantity");
        dao.update(id, category, qty, date, status, notes == null ? "" : notes.trim());
        activity.log("Waste record #" + id + " updated");
    }

    public void markSegregated(WasteRecord r) {
        dao.updateStatus(r.id(), "Segregated");
        activity.log("Waste segregated: " + r.category() + " " + r.quantity() + " kg");
    }

    public void delete(int id) {
        dao.delete(id);
        activity.log("Waste record #" + id + " deleted");
    }
}
