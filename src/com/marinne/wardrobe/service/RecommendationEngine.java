package com.marinne.wardrobe.service;

import com.marinne.wardrobe.model.ClothingCategory;
import com.marinne.wardrobe.model.WardrobeItem;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Generates mix-and-match outfit recommendations based on tag similarity and color pairings.
 */
public final class RecommendationEngine {

    private RecommendationEngine() {
    }

    public static List<Recommendation> generate(WardrobeItem anchor, List<WardrobeItem> allItems) {
        Objects.requireNonNull(anchor, "anchor");
        List<WardrobeItem> candidates = allItems.stream()
                .filter(item -> !item.getId().equals(anchor.getId()))
                .collect(Collectors.toList());

        List<ClothingCategory> required = anchor.getCategory() == ClothingCategory.DRESS
                ? ClothingCategory.coreWithDress()
                : ClothingCategory.coreSeparates();

        // Ensure anchor's category is included in the final outfit.
        EnumMap<ClothingCategory, WardrobeItem> baseSelection = new EnumMap<>(ClothingCategory.class);
        baseSelection.put(anchor.getCategory(), anchor);

        Map<ClothingCategory, List<ScoredItem>> scored = required.stream()
                .distinct()
                .collect(Collectors.toMap(category -> category,
                        category -> scoreCandidates(anchor, category, candidates),
                        (a, b) -> a,
                        () -> new EnumMap<>(ClothingCategory.class)));

        // Build outfit combinations from the top three rated items per category to keep runtime small.
        List<Recommendation> recommendations = new ArrayList<>();
        buildRecommendations(baseSelection, scored, required, 0, new EnumMap<>(ClothingCategory.class), recommendations);

        recommendations.sort((a, b) -> Double.compare(b.score, a.score));
        return recommendations.stream().limit(5).collect(Collectors.toList());
    }

    private static void buildRecommendations(EnumMap<ClothingCategory, WardrobeItem> baseSelection,
                                             Map<ClothingCategory, List<ScoredItem>> scored,
                                             List<ClothingCategory> required,
                                             int index,
                                             EnumMap<ClothingCategory, WardrobeItem> current,
                                             List<Recommendation> output) {
        if (index >= required.size()) {
            EnumMap<ClothingCategory, WardrobeItem> outfit = new EnumMap<>(baseSelection);
            current.forEach(outfit::put);
            double score = computeOutfitScore(outfit);
            output.add(new Recommendation(outfit, score));
            return;
        }

        ClothingCategory category = required.get(index);
        if (baseSelection.containsKey(category)) {
            buildRecommendations(baseSelection, scored, required, index + 1, current, output);
            return;
        }

        List<ScoredItem> options = scored.getOrDefault(category, List.of());
        if (options.isEmpty()) {
            // Skip this category if no options are available.
            buildRecommendations(baseSelection, scored, required, index + 1, current, output);
            return;
        }

        int limit = Math.min(3, options.size());
        for (int i = 0; i < limit; i++) {
            ScoredItem entry = options.get(i);
            current.put(category, entry.item);
            buildRecommendations(baseSelection, scored, required, index + 1, current, output);
            current.remove(category);
        }
    }

    private static List<ScoredItem> scoreCandidates(WardrobeItem anchor,
                                                    ClothingCategory targetCategory,
                                                    List<WardrobeItem> candidates) {
        Set<String> anchorTags = new HashSet<>(anchor.getTags());

        List<WardrobeItem> relevant = candidates.stream()
                .filter(item -> item.getCategory() == targetCategory)
                .collect(Collectors.toList());

        if (relevant.isEmpty()) {
            return List.of();
        }

        LocalDateTime newest = relevant.stream()
                .map(WardrobeItem::getCreatedAt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        LocalDateTime oldest = relevant.stream()
                .map(WardrobeItem::getCreatedAt)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        return relevant.stream()
                .map(item -> {
                    double score = 0;
                    Set<String> shared = new HashSet<>(anchorTags);
                    shared.retainAll(item.getTags());
                    score += shared.size() * 25;

                    // Encourage complementary color palettes if defined.
                    score += colorCompatibility(anchor.getColorHex(), item.getColorHex());

                    // Prioritize recency so newly uploaded items surface quickly.
                    score += recencyBoost(item.getCreatedAt(), newest, oldest);
                    return new ScoredItem(item, score);
                })
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .collect(Collectors.toList());
    }

    private static double computeOutfitScore(EnumMap<ClothingCategory, WardrobeItem> outfit) {
        double score = 0;
        for (WardrobeItem item : outfit.values()) {
            score += 10; // baseline value for having a category filled
        }

        // Pairwise harmony by comparing tags between all items.
        List<WardrobeItem> items = new ArrayList<>(outfit.values());
        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                Set<String> shared = new HashSet<>(items.get(i).getTags());
                shared.retainAll(items.get(j).getTags());
                score += shared.size() * 5;
                score += colorCompatibility(items.get(i).getColorHex(), items.get(j).getColorHex()) / 2.0;
            }
        }
        return score;
    }

    private static double colorCompatibility(String colorA, String colorB) {
        try {
            Color a = Color.decode(colorA);
            Color b = Color.decode(colorB);
            double distance = Math.sqrt(Math.pow(a.getRed() - b.getRed(), 2)
                    + Math.pow(a.getGreen() - b.getGreen(), 2)
                    + Math.pow(a.getBlue() - b.getBlue(), 2));
            double maxDistance = Math.sqrt(Math.pow(255, 2) * 3);
            return (1 - distance / maxDistance) * 20; // max bonus of 20 for matching colors
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static double recencyBoost(LocalDateTime createdAt, LocalDateTime newest, LocalDateTime oldest) {
        if (createdAt == null || newest == null || oldest == null) {
            return 0;
        }
        long totalSeconds = Math.max(1, ChronoUnit.SECONDS.between(oldest, newest));
        long offset = ChronoUnit.SECONDS.between(oldest, createdAt);
        double normalized = Math.min(1.0, Math.max(0.0, offset / (double) totalSeconds));
        // Baseline boost of 5 so recent pieces edge out older ones without overwhelming tag matches.
        return 5 + normalized * 10;
    }

    private record ScoredItem(WardrobeItem item, double score) {
    }

    public static final class Recommendation {
        private final EnumMap<ClothingCategory, WardrobeItem> outfit;
        private final double score;

        Recommendation(EnumMap<ClothingCategory, WardrobeItem> outfit, double score) {
            this.outfit = outfit;
            this.score = score;
        }

        public EnumMap<ClothingCategory, WardrobeItem> outfit() {
            return outfit;
        }

        public Map<ClothingCategory, UUID> toSelectionMap() {
            return outfit.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getId(), (a, b) -> a,
                            () -> new EnumMap<>(ClothingCategory.class)));
        }

        public double score() {
            return score;
        }
    }
}
