package com.marinne.wardrobe.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents Marinne and her friends that can be styled inside the application.
 */
public final class FriendProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String description;
    private final String avatarImagePath;
    private final String headshotImagePath;

    public FriendProfile(String id,
                         String name,
                         String description,
                         String avatarImagePath,
                         String headshotImagePath) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = description == null ? "" : description;
        this.avatarImagePath = avatarImagePath;
        this.headshotImagePath = headshotImagePath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatarImagePath() {
        return avatarImagePath;
    }

    public String getHeadshotImagePath() {
        return headshotImagePath;
    }

    @Override
    public String toString() {
        return name;
    }
}
