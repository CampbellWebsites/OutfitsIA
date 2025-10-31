package com.marinne.wardrobe.ui;

import com.marinne.wardrobe.model.ClothingCategory;
import com.marinne.wardrobe.model.Outfit;
import com.marinne.wardrobe.model.WardrobeItem;
import com.marinne.wardrobe.model.WardrobeState;
import com.marinne.wardrobe.util.ImageCache;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Enables saving, editing, and applying complete looks from the virtual wardrobe.
 */
public final class SavedOutfitsPanel extends JPanel implements WardrobeState.StateListener {

    private final WardrobeState state;
    private final Supplier<Map<ClothingCategory, WardrobeItem>> outfitSupplier;
    private final Consumer<Map<ClothingCategory, WardrobeItem>> outfitApplier;

    private final DefaultListModel<Outfit> model = new DefaultListModel<>();
    private final JList<Outfit> list = new JList<>(model);
    private final JPanel detailPanel = new JPanel(new GridBagLayout());

    public SavedOutfitsPanel(WardrobeState state,
                             Supplier<Map<ClothingCategory, WardrobeItem>> outfitSupplier,
                             Consumer<Map<ClothingCategory, WardrobeItem>> outfitApplier) {
        super(new BorderLayout(12, 12));
        this.state = state;
        this.outfitSupplier = outfitSupplier;
        this.outfitApplier = outfitApplier;
        state.registerListener(this);
        add(buildListPanel(), BorderLayout.WEST);
        add(buildDetailPanel(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        refreshOutfits();
    }

    private JPanel buildListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(240, 400));
        panel.setBorder(BorderFactory.createTitledBorder("Saved Looks"));
        list.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showOutfitDetails(list.getSelectedValue());
            }
        });
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildDetailPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createTitledBorder("Look Details"));
        container.add(new JScrollPane(detailPanel), BorderLayout.CENTER);
        return container;
    }

    private JPanel buildButtons() {
        JPanel buttons = new JPanel();
        JButton saveCurrent = new JButton("Save Current Outfit");
        saveCurrent.addActionListener(e -> saveCurrentOutfit());
        buttons.add(saveCurrent);

        JButton apply = new JButton("Load on Avatar");
        apply.addActionListener(e -> {
            Outfit outfit = list.getSelectedValue();
            if (outfit != null) {
                outfitApplier.accept(resolveOutfit(outfit));
            }
        });
        buttons.add(apply);

        JButton rename = new JButton("Rename / Tag");
        rename.addActionListener(e -> renameOutfit());
        buttons.add(rename);

        JButton delete = new JButton("Delete");
        delete.addActionListener(e -> deleteOutfit());
        buttons.add(delete);
        return buttons;
    }

    private void saveCurrentOutfit() {
        Map<ClothingCategory, WardrobeItem> current = outfitSupplier.get();
        if (current.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select clothing layers on the avatar before saving.",
                    "Outfit Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField("Unnamed Look");
        JTextField tagsField = new JTextField("favorite, everyday");
        JTextArea notesField = new JTextArea(3, 20);
        notesField.setLineWrap(true);
        notesField.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(new JLabel("Outfit Name"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Tags (comma-separated)"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(tagsField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Notes"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        form.add(new JScrollPane(notesField), gbc);

        int choice = JOptionPane.showConfirmDialog(this, form, "Save Outfit", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        EnumMap<ClothingCategory, UUID> selections = new EnumMap<>(ClothingCategory.class);
        current.forEach((category, item) -> selections.put(category, item.getId()));
        Set<String> tags = parseTags(tagsField.getText());
        Outfit outfit = new Outfit(UUID.randomUUID(), nameField.getText(), selections, tags,
                notesField.getText(), LocalDateTime.now());
        state.saveOutfit(outfit);
        list.setSelectedValue(outfit, true);
    }

    private void renameOutfit() {
        Outfit outfit = list.getSelectedValue();
        if (outfit == null) {
            return;
        }

        JTextField nameField = new JTextField(outfit.getName());
        JTextField tagsField = new JTextField(String.join(", ", outfit.getTags()));
        JTextArea notesArea = new JTextArea(outfit.getNotes(), 3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(new JLabel("Outfit Name"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Tags"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(tagsField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Notes"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        form.add(new JScrollPane(notesArea), gbc);

        int choice = JOptionPane.showConfirmDialog(this, form, "Edit Outfit", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        Outfit updated = new Outfit(outfit.getId(),
                nameField.getText(),
                outfit.getSelections(),
                parseTags(tagsField.getText()),
                notesArea.getText(),
                outfit.getCreatedAt());
        state.saveOutfit(updated);
        list.setSelectedValue(updated, true);
    }

    private void deleteOutfit() {
        Outfit outfit = list.getSelectedValue();
        if (outfit == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete " + outfit.getName() + "?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            state.deleteOutfit(outfit.getId());
        }
    }

    private void showOutfitDetails(Outfit outfit) {
        detailPanel.removeAll();
        if (outfit == null) {
            detailPanel.add(new JLabel("Select a look to preview its pieces."));
        } else {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(4, 4, 4, 4);

            JLabel name = new JLabel(outfit.getName());
            name.setFont(name.getFont().deriveFont(16f));
            detailPanel.add(name, gbc);

            gbc.gridy++;
            String meta = String.format("Tags: %s", outfit.getTags().isEmpty() ? "none" : String.join(", ", outfit.getTags()));
            detailPanel.add(new JLabel(meta), gbc);

            gbc.gridy++;
            detailPanel.add(new JLabel("Created on: " + outfit.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))), gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            detailPanel.add(new JLabel("Notes:"), gbc);
            gbc.gridy++;
            JTextArea notes = new JTextArea(outfit.getNotes());
            notes.setEditable(false);
            notes.setLineWrap(true);
            notes.setWrapStyleWord(true);
            JScrollPane notesScroll = new JScrollPane(notes);
            notesScroll.setPreferredSize(new Dimension(200, 80));
            detailPanel.add(notesScroll, gbc);

            gbc.gridy++;
            detailPanel.add(new JLabel("Pieces:"), gbc);

            gbc.gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            detailPanel.add(buildPiecesPanel(outfit), gbc);
        }
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private JPanel buildPiecesPanel(Outfit outfit) {
        JPanel pieces = new JPanel(new GridBagLayout());
        pieces.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        outfit.getSelections().forEach((category, itemId) -> state.findItem(itemId).ifPresent(item -> {
            JLabel icon = new JLabel(ImageCache.load(item.getImagePath(), 48, 48));
            pieces.add(icon, gbc);

            gbc.gridx = 1;
            JPanel text = new JPanel(new BorderLayout());
            text.add(new JLabel(category.getDisplayName()), BorderLayout.NORTH);
            text.add(new JLabel(item.getName()), BorderLayout.CENTER);
            pieces.add(text, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
        }));

        return pieces;
    }

    private Map<ClothingCategory, WardrobeItem> resolveOutfit(Outfit outfit) {
        EnumMap<ClothingCategory, WardrobeItem> selections = new EnumMap<>(ClothingCategory.class);
        outfit.getSelections().forEach((category, itemId) -> state.findItem(itemId)
                .ifPresent(item -> selections.put(category, item)));
        return selections;
    }

    private Set<String> parseTags(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }
        Set<String> tags = new TreeSet<>();
        for (String token : text.split(",")) {
            String trimmed = token.trim().toLowerCase();
            if (!trimmed.isEmpty()) {
                tags.add(trimmed);
            }
        }
        return tags;
    }

    private void refreshOutfits() {
        model.clear();
        List<Outfit> outfits = state.getOutfits();
        outfits.forEach(model::addElement);
        if (!model.isEmpty()) {
            list.setSelectedIndex(0);
        }
    }

    @Override
    public void onItemsChanged() {
        SwingUtilities.invokeLater(() -> showOutfitDetails(list.getSelectedValue()));
    }

    @Override
    public void onOutfitsChanged() {
        SwingUtilities.invokeLater(() -> {
            refreshOutfits();
            showOutfitDetails(list.getSelectedValue());
        });
    }

    @Override
    public void onPlannerChanged() {
        // not needed here
    }
}
