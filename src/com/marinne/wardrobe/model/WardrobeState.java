package com.marinne.wardrobe.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Persisted application state with helpers to mutate wardrobe items, outfits, and planner assignments.
 */
public final class WardrobeState {

    private final Path dataDir;
    private final Path uploadDir;
    private final Path storageFile;

    private final List<StateListener> listeners = new CopyOnWriteArrayList<>();

    private AppState state;

    public WardrobeState(Path workspaceRoot) {
        this.dataDir = workspaceRoot.resolve("data");
        this.uploadDir = dataDir.resolve("uploads");
        this.storageFile = dataDir.resolve("wardrobe_state.ser");
        ensureDirectories();
        this.state = loadState().orElseGet(() -> {
            AppState fresh = AppState.withDefaults();
            saveInternal(fresh);
            return fresh;
        });
    }

    public Path getUploadDir() {
        return uploadDir;
    }

    public List<WardrobeItem> getItems() {
        return List.copyOf(state.items);
    }

    public List<Outfit> getOutfits() {
        return List.copyOf(state.outfits);
    }

    public Map<LocalDate, PlannerAssignment> getPlanner() {
        return Map.copyOf(state.planner);
    }

    public List<FriendProfile> getFriends() {
        return List.copyOf(state.friends);
    }

    public void registerListener(StateListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(StateListener listener) {
        listeners.remove(listener);
    }

    public WardrobeItem addItem(WardrobeItem item) {
        state.items.add(item);
        state.items.sort(Comparator.comparing(WardrobeItem::getCategory).thenComparing(WardrobeItem::getName));
        saveInternal(state);
        listeners.forEach(StateListener::onItemsChanged);
        return item;
    }

    public void updateItem(WardrobeItem updated) {
        state.items.replaceAll(existing -> existing.getId().equals(updated.getId()) ? updated : existing);
        state.items.sort(Comparator.comparing(WardrobeItem::getCategory).thenComparing(WardrobeItem::getName));
        saveInternal(state);
        listeners.forEach(StateListener::onItemsChanged);
    }

    public void removeItem(UUID itemId) {
        state.items.removeIf(item -> item.getId().equals(itemId));
        state.outfits.forEach(outfit -> {
            java.util.List<ClothingCategory> categoriesToClear = outfit.getSelections().entrySet().stream()
                    .filter(entry -> entry.getValue().equals(itemId))
                    .map(Map.Entry::getKey)
                    .toList();
            categoriesToClear.forEach(category -> outfit.setSelection(category, null));
        });
        saveInternal(state);
        listeners.forEach(StateListener::onItemsChanged);
    }

    public Outfit saveOutfit(Outfit outfit) {
        boolean exists = state.outfits.stream().anyMatch(o -> o.getId().equals(outfit.getId()));
        if (exists) {
            state.outfits.replaceAll(existing -> existing.getId().equals(outfit.getId()) ? outfit : existing);
        } else {
            state.outfits.add(outfit);
        }
        state.outfits.sort(Comparator.comparing(Outfit::getName));
        saveInternal(state);
        listeners.forEach(StateListener::onOutfitsChanged);
        return outfit;
    }

    public void deleteOutfit(UUID outfitId) {
        state.outfits.removeIf(outfit -> outfit.getId().equals(outfitId));
        state.planner.values().removeIf(entry -> outfitId.equals(entry.getOutfitId()));
        saveInternal(state);
        listeners.forEach(StateListener::onOutfitsChanged);
        listeners.forEach(StateListener::onPlannerChanged);
    }

    public void assignOutfit(LocalDate date, UUID outfitId, String event, String notes) {
        if (outfitId == null) {
            state.planner.remove(date);
        } else {
            state.planner.put(date, new PlannerAssignment(date, outfitId, event, notes));
        }
        saveInternal(state);
        listeners.forEach(StateListener::onPlannerChanged);
    }

    public Optional<WardrobeItem> findItem(UUID id) {
        return state.items.stream().filter(item -> item.getId().equals(id)).findFirst();
    }

    public Optional<Outfit> findOutfit(UUID id) {
        return state.outfits.stream().filter(outfit -> outfit.getId().equals(id)).findFirst();
    }

    private Optional<AppState> loadState() {
        if (Files.notExists(storageFile)) {
            return Optional.empty();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(storageFile))) {
            return Optional.of((AppState) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void saveInternal(AppState snapshot) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(storageFile))) {
            out.writeObject(snapshot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ensureDirectories() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize data directory", e);
        }
    }

    /**
     * Container for all persisted data.
     */
    private static final class AppState implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private final List<WardrobeItem> items;
        private final List<Outfit> outfits;
        private final Map<LocalDate, PlannerAssignment> planner;
        private final List<FriendProfile> friends;

        private AppState() {
            this.items = new ArrayList<>();
            this.outfits = new ArrayList<>();
            this.planner = new HashMap<>();
            this.friends = new ArrayList<>();
        }

        private static AppState withDefaults() {
            AppState state = new AppState();
            DefaultDataSeeder.populate(state);
            return state;
        }
    }

    /**
     * Allows panels to react when the underlying wardrobe data changes.
     */
    public interface StateListener {
        default void onItemsChanged() {}

        default void onOutfitsChanged() {}

        default void onPlannerChanged() {}
    }

    /**
     * Seed data for first-run experience. The paths reference project images bundled with the repository.
     */
    private static final class DefaultDataSeeder {

        private DefaultDataSeeder() {
        }

        private static void populate(AppState state) {
            // Friends
            state.friends.add(new FriendProfile("marinne", "Marinne",
                    "Your client who wants stress-free outfit planning.",
                    "marinne.png",
                    "MarinneHead.png"));
            state.friends.add(new FriendProfile("caitlyn", "Caitlyn",
                    "Award-winning actress who loves statement looks.",
                    "caitlyn.png",
                    "CaitlynHead.png"));
            state.friends.add(new FriendProfile("campbell", "Campbell",
                    "Trendsetter with a bold wardrobe.",
                    "campbell.png",
                    "CampbellHead.png"));
            state.friends.add(new FriendProfile("jules", "Jules",
                    "Creative director who lives for smart layers.",
                    "jules.png",
                    "JulesHead.png"));
            state.friends.add(new FriendProfile("aliki", "Aliki",
                    "Pro athlete balancing comfort and glamour.",
                    "aliki.png",
                    "AlikiHead.png"));

            // Wardrobe items sourced from existing project images.
            state.items.add(WardrobeItem.create("White T-Shirt",
                    ClothingCategory.TOP,
                    Set.of("casual", "sporty", "neutrals"),
                    "#f5f5f5",
                    Path.of("top_tshirt.png")));
            state.items.add(WardrobeItem.create("Silk Blouse",
                    ClothingCategory.TOP,
                    Set.of("elevated", "polished", "work"),
                    "#f7e6da",
                    Path.of("top_blouse.png")));
            state.items.add(WardrobeItem.create("Cable Sweater",
                    ClothingCategory.TOP,
                    Set.of("cozy", "casual", "layering"),
                    "#d2c1a7",
                    Path.of("top_sweater.png")));
            state.items.add(WardrobeItem.create("Sport Tank",
                    ClothingCategory.TOP,
                    Set.of("sporty", "summer", "minimal"),
                    "#f9d4cb",
                    Path.of("top_tank.png")));
            state.items.add(WardrobeItem.create("Striped Button-Up",
                    ClothingCategory.TOP,
                    Set.of("tailored", "work", "smart"),
                    "#cbe0f6",
                    Path.of("top_button.png")));

            state.items.add(WardrobeItem.create("Blue Jeans",
                    ClothingCategory.BOTTOM,
                    Set.of("casual", "denim", "weekend"),
                    "#3c5a7a",
                    Path.of("bottom_jeans.png")));
            state.items.add(WardrobeItem.create("Tailored Slacks",
                    ClothingCategory.BOTTOM,
                    Set.of("work", "polished", "neutral"),
                    "#6d6d6d",
                    Path.of("bottom_slacks.png")));
            state.items.add(WardrobeItem.create("Athletic Shorts",
                    ClothingCategory.BOTTOM,
                    Set.of("sporty", "summer", "active"),
                    "#d86b5f",
                    Path.of("bottom_shorts.png")));
            state.items.add(WardrobeItem.create("Pleated Skirt",
                    ClothingCategory.BOTTOM,
                    Set.of("feminine", "polished", "swingy"),
                    "#ebcfd5",
                    Path.of("bottom_skirt.png")));
            state.items.add(WardrobeItem.create("Black Leggings",
                    ClothingCategory.BOTTOM,
                    Set.of("sporty", "comfort", "minimal"),
                    "#333333",
                    Path.of("bottom_leggings.png")));

            state.items.add(WardrobeItem.create("Everyday Sundress",
                    ClothingCategory.DRESS,
                    Set.of("casual", "summer", "breezy"),
                    "#f5d8aa",
                    Path.of("dress_sun.png")));
            state.items.add(WardrobeItem.create("Evening Gown",
                    ClothingCategory.DRESS,
                    Set.of("formal", "event", "glam"),
                    "#c4c8f6",
                    Path.of("dress_formal.png")));
            state.items.add(WardrobeItem.create("Business Dress",
                    ClothingCategory.DRESS,
                    Set.of("work", "tailored", "presentation"),
                    "#6b7c91",
                    Path.of("dress_business.png")));
            state.items.add(WardrobeItem.create("Cocktail Dress",
                    ClothingCategory.DRESS,
                    Set.of("party", "evening", "glam"),
                    "#d7536f",
                    Path.of("dress_cocktail.png")));

            state.items.add(WardrobeItem.create("Pearl Necklace",
                    ClothingCategory.ACCESSORY,
                    Set.of("classic", "formal", "polished"),
                    "#f0ede4",
                    Path.of("acc_necklace.png")));
            state.items.add(WardrobeItem.create("Baseball Cap",
                    ClothingCategory.ACCESSORY,
                    Set.of("sporty", "casual", "weekend"),
                    "#2d3a63",
                    Path.of("acc_hat.png")));
            state.items.add(WardrobeItem.create("Silk Scarf",
                    ClothingCategory.ACCESSORY,
                    Set.of("layering", "polished", "pattern"),
                    "#ffb89f",
                    Path.of("acc_scarf.png")));
            state.items.add(WardrobeItem.create("Headband",
                    ClothingCategory.ACCESSORY,
                    Set.of("sporty", "hair", "casual"),
                    "#f4c4d8",
                    Path.of("acc_headband.png")));
            state.items.add(WardrobeItem.create("Statement Necklace",
                    ClothingCategory.ACCESSORY,
                    Set.of("bold", "evening", "event"),
                    "#f7d16b",
                    Path.of("acc_statement.png")));

            state.items.add(WardrobeItem.create("Sneakers",
                    ClothingCategory.SHOES,
                    Set.of("sporty", "casual", "comfort"),
                    "#ffffff",
                    Path.of("shoes_sneakers.png")));
            state.items.add(WardrobeItem.create("Strappy Heels",
                    ClothingCategory.SHOES,
                    Set.of("formal", "event", "elegant"),
                    "#f4cfd2",
                    Path.of("shoes_heels.png")));
            state.items.add(WardrobeItem.create("Ballet Flats",
                    ClothingCategory.SHOES,
                    Set.of("polished", "comfort", "work"),
                    "#fef0e0",
                    Path.of("shoes_flats.png")));
            state.items.add(WardrobeItem.create("Ankle Boots",
                    ClothingCategory.SHOES,
                    Set.of("casual", "fall", "statement"),
                    "#594539",
                    Path.of("shoes_boots.png")));
            state.items.add(WardrobeItem.create("Leather Sandals",
                    ClothingCategory.SHOES,
                    Set.of("summer", "casual", "vacation"),
                    "#d1a275",
                    Path.of("shoes_sandals.png")));
        }
    }
}
