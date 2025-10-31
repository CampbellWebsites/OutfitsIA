package com.marinne.wardrobe.model;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a single wardrobe item that can be layered on the avatar,
 * tagged for recommendations, and saved inside outfits.
 */
public final class WardrobeItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String name;
    private ClothingCategory category;
    private final LinkedHashSet<String> tags;
    private String colorHex; // Optional color accent for UI styling
    private String imagePath; // Stored as a path relative to the workspace root or uploads directory
    private final LocalDateTime createdAt;

    public WardrobeItem(UUID id,
                        String name,
                        ClothingCategory category,
                        Set<String> tags,
                        String colorHex,
                        String imagePath,
                        LocalDateTime createdAt) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.name = Objects.requireNonNullElse(name, "Unnamed Item");
        this.category = Objects.requireNonNull(category, "category");
        this.tags = new LinkedHashSet<>();
        if (tags != null) {
            tags.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .forEach(this.tags::add);
        }
        this.colorHex = colorHex == null ? "#FFFFFF" : colorHex;
        this.imagePath = imagePath;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }

    public static WardrobeItem create(String name,
                                      ClothingCategory category,
                                      Set<String> tags,
                                      String colorHex,
                                      Path imagePath) {
        return new WardrobeItem(UUID.randomUUID(),
                name,
                category,
                tags,
                colorHex,
                imagePath == null ? null : imagePath.toString(),
                LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNullElse(name, this.name);
    }

    public ClothingCategory getCategory() {
        return category;
    }

    public void setCategory(ClothingCategory category) {
        this.category = Objects.requireNonNull(category);
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

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        if (colorHex != null && !colorHex.isBlank()) {
            this.colorHex = colorHex;
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(Path imagePath) {
        this.imagePath = imagePath == null ? null : imagePath.toString();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WardrobeItem item)) {
            return false;
        }
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name + " (" + category.getDisplayName() + ")";
    }
}
