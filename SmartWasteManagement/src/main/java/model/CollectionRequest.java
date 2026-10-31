package model;

import java.time.LocalDate;

/** A waste collection request. staffId/staffName are null while the request is unassigned. */
public record CollectionRequest(int id, String location, String category, double quantity,
                                Integer staffId, String staffName, LocalDate date,
                                String status, String completedAt) {
    @Override
    public String toString() {
        return "#" + id + "  " + location + "  (" + category + ", " + quantity + " kg)";
    }
}
