package service;

import dao.ActivityDAO;
import dao.StaffDAO;
import model.Staff;
import utils.Validator;

import java.util.List;

/** Business rules for staff management. */
public class StaffService {
    private final StaffDAO dao = new StaffDAO();
    private final ActivityDAO activity = new ActivityDAO();

    public List<Staff> findAll() { return dao.findAll(); }

    public List<Staff> findActive() { return dao.findActive(); }

    public void add(String name, String phone, String email, String area, String status) {
        Staff s = build(0, name, phone, email, area, status);
        dao.insert(s);
        activity.log("Staff " + s.name() + " added");
    }

    public void update(int id, String name, String phone, String email, String area, String status) {
        Staff s = build(id, name, phone, email, area, status);
        dao.update(s);
        activity.log("Staff " + s.name() + " updated");
    }

    public void delete(Staff s) {
        dao.delete(s.id());
        activity.log("Staff " + s.name() + " removed");
    }

    private Staff build(int id, String name, String phone, String email, String area, String status) {
        return new Staff(id, Validator.required(name, "Name"), Validator.phone(phone),
                Validator.optionalEmail(email), area == null ? "" : area.trim(), status, 0);
    }
}
