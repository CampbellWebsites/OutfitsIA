package com.marinne.wardrobe.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a saved outfit scheduled for a specific date with optional context.
 */
public final class PlannerAssignment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final LocalDate date;
    private UUID outfitId;
    private String event;
    private String notes;

    public PlannerAssignment(LocalDate date, UUID outfitId, String event, String notes) {
        this.date = Objects.requireNonNull(date, "date");
        this.outfitId = outfitId;
        this.event = event == null ? "" : event.trim();
        this.notes = notes == null ? "" : notes.trim();
    }

    public LocalDate getDate() {
        return date;
    }

    public UUID getOutfitId() {
        return outfitId;
    }

    public void setOutfitId(UUID outfitId) {
        this.outfitId = outfitId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event == null ? "" : event.trim();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes.trim();
    }
}
