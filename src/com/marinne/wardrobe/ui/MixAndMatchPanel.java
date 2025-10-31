package com.marinne.wardrobe.ui;

import com.marinne.wardrobe.model.ClothingCategory;
import com.marinne.wardrobe.model.WardrobeItem;
import com.marinne.wardrobe.model.WardrobeState;
import com.marinne.wardrobe.service.RecommendationEngine;
import com.marinne.wardrobe.util.ImageCache;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Provides mix-and-match outfit recommendations from a selected anchor item.
 */
public final class MixAndMatchPanel extends JPanel implements WardrobeState.StateListener {

    private final WardrobeState state;
    private final JComboBox<ClothingCategory> categoryFilter = new JComboBox<>(ClothingCategory.values());
    private final DefaultListModel<WardrobeItem> itemModel = new DefaultListModel<>();
    private final JList<WardrobeItem> itemList = new JList<>(itemModel);
    private final JPanel recommendationsPanel = new JPanel(new GridBagLayout());
    private final Consumer<Map<ClothingCategory, WardrobeItem>> applyOutfit;

    public MixAndMatchPanel(WardrobeState state, Consumer<Map<ClothingCategory, WardrobeItem>> applyOutfit) {
        super(new BorderLayout(12, 12));
        this.state = state;
        this.applyOutfit = applyOutfit;
        state.registerListener(this);
        add(buildLeftPanel(), BorderLayout.WEST);
        add(new JScrollPane(recommendationsPanel), BorderLayout.CENTER);
        refreshItemList();
    }

    private JPanel buildLeftPanel() {
        JPanel container = new JPanel(new BorderLayout(8, 8));
        container.setPreferredSize(new Dimension(260, 400));
        container.setBorder(BorderFactory.createTitledBorder("Choose a starting piece"));

        categoryFilter.addActionListener(e -> refreshItemList());
        container.add(categoryFilter, BorderLayout.NORTH);

        itemList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                WardrobeItem selected = itemList.getSelectedValue();
                if (selected != null) {
                    generateRecommendations(selected);
                }
            }
        });
        container.add(new JScrollPane(itemList), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Suggestions");
        refreshButton.addActionListener(e -> {
            WardrobeItem selected = itemList.getSelectedValue();
            if (selected != null) {
                generateRecommendations(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Select an item first.",
                        "No Anchor Selected", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        container.add(refreshButton, BorderLayout.SOUTH);
        return container;
    }

    private void refreshItemList() {
        itemModel.clear();
        ClothingCategory category = (ClothingCategory) categoryFilter.getSelectedItem();
        if (category == null) {
            return;
        }
        state.getItems().stream()
                .filter(item -> item.getCategory() == category)
                .forEach(itemModel::addElement);
        if (!itemModel.isEmpty()) {
            itemList.setSelectedIndex(0);
        }
    }

    private void generateRecommendations(WardrobeItem anchor) {
        recommendationsPanel.removeAll();
        List<RecommendationEngine.Recommendation> recommendations =
                RecommendationEngine.generate(anchor, state.getItems());

        if (recommendations.isEmpty()) {
            recommendationsPanel.add(new JLabel("Add more items in other categories to see outfit ideas."));
        } else {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            for (RecommendationEngine.Recommendation recommendation : recommendations) {
                JPanel card = buildRecommendationCard(recommendation);
                recommendationsPanel.add(card, gbc);
                gbc.gridy++;
            }
            gbc.weighty = 1;
            recommendationsPanel.add(new JPanel(), gbc);
        }
        recommendationsPanel.revalidate();
        recommendationsPanel.repaint();
    }

    private JPanel buildRecommendationCard(RecommendationEngine.Recommendation recommendation) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 200, 190)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel scoreLabel = new JLabel(String.format("Score: %.1f", recommendation.score()));
        scoreLabel.setForeground(new Color(90, 80, 70));
        card.add(scoreLabel, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        EnumMap<ClothingCategory, WardrobeItem> outfit = recommendation.outfit();
        for (Map.Entry<ClothingCategory, WardrobeItem> entry : outfit.entrySet()) {
            JPanel row = new JPanel(new BorderLayout(6, 6));
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(237, 230, 225)));
            JLabel icon = new JLabel(ImageCache.load(entry.getValue().getImagePath(), 52, 52));
            row.add(icon, BorderLayout.WEST);
            JLabel text = new JLabel(entry.getKey().getDisplayName() + ": " + entry.getValue().getName());
            row.add(text, BorderLayout.CENTER);
            card.add(row, gbc);
            gbc.gridy++;
        }

        JButton applyButton = new JButton("Try This Look");
        applyButton.addActionListener(e -> applyOutfit.accept(new EnumMap<>(outfit)));
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        card.add(applyButton, gbc);

        return card;
    }

    @Override
    public void onItemsChanged() {
        SwingUtilities.invokeLater(this::refreshItemList);
    }

    @Override
    public void onOutfitsChanged() {
        // not needed
    }

    @Override
    public void onPlannerChanged() {
        // not needed
    }
}
