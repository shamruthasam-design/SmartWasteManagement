package model;

import java.time.LocalDate;

/** One entry in the waste segregation register. */
public record WasteRecord(int id, String category, double quantity, LocalDate date, String status, String notes) { }
