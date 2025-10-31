package com.marinne.wardrobe.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A named combination of wardrobe items that can be recalled, edited, and scheduled in the planner.
 */
public final class Outfit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String name;
    private final EnumMap<ClothingCategory, UUID> selections;
    private final LinkedHashSet<String> tags;
    private String notes;
    private final LocalDateTime createdAt;

    public Outfit(UUID id,
                  String name,
                  Map<ClothingCategory, UUID> selections,
                  Set<String> tags,
                  String notes,
                  LocalDateTime createdAt) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.name = name == null || name.isBlank() ? "Untitled Look" : name.trim();
        this.selections = new EnumMap<>(ClothingCategory.class);
        if (selections != null) {
            this.selections.putAll(selections);
        }
        this.tags = new LinkedHashSet<>();
        if (tags != null) {
            tags.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .forEach(this.tags::add);
        }
        this.notes = notes == null ? "" : notes;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        }
    }

    public Map<ClothingCategory, UUID> getSelections() {
        return Collections.unmodifiableMap(selections);
    }

    public void setSelection(ClothingCategory category, UUID itemId) {
        if (category != null) {
            if (itemId == null) {
                selections.remove(category);
            } else {
                selections.put(category, itemId);
            }
        }
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public void addTag(String tag) {
        if (tag != null) {
            String normalized = tag.trim().toLowerCase();
            if (!normalized.isEmpty()) {
                tags.add(normalized);
            }
        }
    }

    public void removeTag(String tag) {
        if (tag != null) {
            tags.remove(tag.trim().toLowerCase());
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if (notes != null) {
            this.notes = notes;
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}
