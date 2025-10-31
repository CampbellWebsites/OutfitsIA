package com.marinne.wardrobe.model;

import java.util.Arrays;
import java.util.List;

/**
 * Logical groupings of wardrobe items that determine how they are layered on the avatar
 * and which combinations are considered complete outfits.
 */
public enum ClothingCategory {
    TOP("Top"),
    BOTTOM("Bottom"),
    DRESS("Dress"),
    SHOES("Shoes"),
    ACCESSORY("Accessory"),
    OUTERWEAR("Outerwear"),
    BAG("Bag");

    private final String displayName;

    ClothingCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Categories that are considered core for an everyday outfit when dresses are not used.
     */
    public static List<ClothingCategory> coreSeparates() {
        return Arrays.asList(TOP, BOTTOM, SHOES, ACCESSORY);
    }

    /**
     * Categories that pair naturally with a dress based outfit.
     */
    public static List<ClothingCategory> coreWithDress() {
        return Arrays.asList(DRESS, SHOES, ACCESSORY);
    }
}
