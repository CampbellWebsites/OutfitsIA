package com.marinne.wardrobe.ui;

import com.marinne.wardrobe.model.ClothingCategory;
import com.marinne.wardrobe.model.WardrobeItem;
import com.marinne.wardrobe.model.WardrobeState;
import com.marinne.wardrobe.util.ImageCache;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Provides upload, categorization, and metadata editing for wardrobe items.
 */
public final class WardrobePanel extends JPanel implements WardrobeState.StateListener {

    private final WardrobeState state;
    private final Map<ClothingCategory, DefaultListModel<WardrobeItem>> models = new EnumMap<>(ClothingCategory.class);
    private final Map<ClothingCategory, JList<WardrobeItem>> lists = new EnumMap<>(ClothingCategory.class);
    private final JComboBox<ClothingCategory> categoryFilter;

    private final JTextField nameField = new JTextField();
    private final JComboBox<ClothingCategory> categoryField = new JComboBox<>(ClothingCategory.values());
    private final DefaultListModel<String> tagsModel = new DefaultListModel<>();
    private final JList<String> tagsList = new JList<>(tagsModel);
    private final JTextField tagInput = new JTextField();
    private final JTextField colorField = new JTextField("#ffffff");

    private final BiConsumer<ClothingCategory, WardrobeItem> applyToAvatar;

    private WardrobeItem selectedItem;

    public WardrobePanel(WardrobeState state, BiConsumer<ClothingCategory, WardrobeItem> applyToAvatar) {
        super(new BorderLayout(12, 12));
        this.state = state;
        this.applyToAvatar = applyToAvatar;
        this.categoryFilter = new JComboBox<>(ClothingCategory.values());
        state.registerListener(this);
        initModels();
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        refreshItems();
    }

    private void initModels() {
        for (ClothingCategory category : ClothingCategory.values()) {
            DefaultListModel<WardrobeItem> model = new DefaultListModel<>();
            models.put(category, model);
            JList<WardrobeItem> list = new JList<>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setCellRenderer(new WardrobeCellRenderer());
            list.addListSelectionListener(evt -> {
                if (!evt.getValueIsAdjusting()) {
                    setSelectedItem(list.getSelectedValue());
                }
            });
            list.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2 && !list.isSelectionEmpty()) {
                        WardrobeItem item = list.getSelectedValue();
                        applyToAvatar.accept(item.getCategory(), item);
                    }
                }
            });
            lists.put(category, list);
        }
    }

    private Component buildToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.add(categoryFilter);
        categoryFilter.addActionListener(e -> switchCategory((ClothingCategory) categoryFilter.getSelectedItem()));

        JButton uploadButton = new JButton("Upload Item");
        uploadButton.addActionListener(e -> openUploadDialog());
        toolbar.add(uploadButton);

        JButton removeButton = new JButton("Remove Item");
        removeButton.addActionListener(e -> removeSelectedItem());
        toolbar.add(removeButton);

        JButton applyButton = new JButton("Apply to Avatar");
        applyButton.addActionListener(e -> {
            if (selectedItem != null) {
                applyToAvatar.accept(selectedItem.getCategory(), selectedItem);
            }
        });
        toolbar.add(applyButton);

        return toolbar;
    }

    private Component buildContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.55);

        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setBorder(new TitledBorder("Wardrobe Items"));
        listContainer.add(new JScrollPane(lists.get(ClothingCategory.TOP)), BorderLayout.CENTER);
        splitPane.setLeftComponent(listContainer);

        JPanel editor = buildEditorPanel();
        splitPane.setRightComponent(editor);
        return splitPane;
    }

    private JPanel buildEditorPanel() {
        JPanel editor = new JPanel(new GridBagLayout());
        editor.setBorder(new TitledBorder("Item Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        editor.add(new javax.swing.JLabel("Name"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        nameField.setPreferredSize(new Dimension(180, 28));
        editor.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        editor.add(new javax.swing.JLabel("Category"), gbc);
        gbc.gridx = 1;
        editor.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        editor.add(new javax.swing.JLabel("Accent Color"), gbc);
        gbc.gridx = 1;
        editor.add(colorField, gbc);

        JButton colorPicker = new JButton("Choose...");
        colorPicker.addActionListener(e -> openColorPicker());
        gbc.gridx = 2;
        editor.add(colorPicker, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.gridwidth = 3;
        editor.add(new javax.swing.JLabel("Tags"), gbc);

        gbc.gridy++;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        tagsList.setBorder(BorderFactory.createLineBorder(new Color(210, 200, 190)));
        editor.add(new JScrollPane(tagsList), gbc);

        JPanel tagControls = new JPanel(new BorderLayout(6, 6));
        tagControls.add(tagInput, BorderLayout.CENTER);
        JButton addTagButton = new JButton("Add Tag");
        addTagButton.addActionListener(e -> addTag());
        tagControls.add(addTagButton, BorderLayout.EAST);
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        editor.add(tagControls, gbc);

        JButton removeTagButton = new JButton("Remove Selected Tag");
        removeTagButton.addActionListener(e -> removeSelectedTag());
        gbc.gridy++;
        editor.add(removeTagButton, gbc);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> persistChanges());
        gbc.gridy++;
        editor.add(saveButton, gbc);

        return editor;
    }

    private void setSelectedItem(WardrobeItem item) {
        this.selectedItem = item;
        tagsModel.clear();
        if (item == null) {
            nameField.setText("");
            colorField.setText("#ffffff");
            return;
        }
        nameField.setText(item.getName());
        categoryField.setSelectedItem(item.getCategory());
        colorField.setText(item.getColorHex());
        item.getTags().forEach(tagsModel::addElement);
    }

    private void switchCategory(ClothingCategory category) {
        JPanel listContainer = (JPanel) ((JSplitPane) getComponent(1)).getLeftComponent();
        listContainer.removeAll();
        listContainer.add(new JScrollPane(lists.get(category)), BorderLayout.CENTER);
        listContainer.revalidate();
        listContainer.repaint();
    }

    private void openUploadDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selected = chooser.getSelectedFile();
        if (selected == null || !selected.exists()) {
            return;
        }

        ClothingCategory guess = guessCategory(selected.getName());
        JTextField nameSuggestion = new JTextField(suggestName(selected.getName()));
        JComboBox<ClothingCategory> categoryChooser = new JComboBox<>(ClothingCategory.values());
        categoryChooser.setSelectedItem(guess);
        JTextField tagsField = new JTextField(String.join(", ", defaultTagsFor(guess)));
        JTextField colorChooser = new JTextField("#ffffff");

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(new javax.swing.JLabel("Name"), gbc);
        gbc.gridx = 1;
        form.add(nameSuggestion, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new javax.swing.JLabel("Category"), gbc);
        gbc.gridx = 1;
        form.add(categoryChooser, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new javax.swing.JLabel("Tags (comma-separated)"), gbc);
        gbc.gridx = 1;
        form.add(tagsField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new javax.swing.JLabel("Accent Color (#RRGGBB)"), gbc);
        gbc.gridx = 1;
        form.add(colorChooser, gbc);

        int choice = JOptionPane.showConfirmDialog(this, form, "Describe Your Item", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Path savedPath = copyToUploads(selected.toPath());
            Set<String> tags = parseTags(tagsField.getText());
            WardrobeItem item = WardrobeItem.create(nameSuggestion.getText(),
                    (ClothingCategory) categoryChooser.getSelectedItem(),
                    tags,
                    colorChooser.getText(),
                    savedPath);
            state.addItem(item);
            categoryFilter.setSelectedItem(item.getCategory());
            SwingUtilities.invokeLater(() -> lists.get(item.getCategory()).setSelectedValue(item, true));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not copy the file: " + ex.getMessage(),
                    "Upload Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedItem() {
        if (selectedItem == null) {
            return;
        }
        UUID id = selectedItem.getId();
        int choice = JOptionPane.showConfirmDialog(this, "Remove " + selectedItem.getName() + " from the wardrobe?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            state.removeItem(id);
            selectedItem = null;
        }
    }

    private void openColorPicker() {
        Color color = JColorChooser.showDialog(this, "Choose Accent Color", Color.decode(colorField.getText()));
        if (color != null) {
            colorField.setText(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
        }
    }

    private void addTag() {
        String tag = tagInput.getText().trim().toLowerCase();
        if (!tag.isEmpty() && !tagsModel.contains(tag)) {
            tagsModel.addElement(tag);
        }
        tagInput.setText("");
    }

    private void removeSelectedTag() {
        int index = tagsList.getSelectedIndex();
        if (index >= 0) {
            tagsModel.remove(index);
        }
    }

    private void persistChanges() {
        if (selectedItem == null) {
            return;
        }
        Set<String> tags = new TreeSet<>();
        for (int i = 0; i < tagsModel.size(); i++) {
            tags.add(tagsModel.getElementAt(i));
        }
        WardrobeItem updated = new WardrobeItem(selectedItem.getId(),
                nameField.getText(),
                (ClothingCategory) categoryField.getSelectedItem(),
                tags,
                colorField.getText(),
                selectedItem.getImagePath(),
                selectedItem.getCreatedAt());
        state.updateItem(updated);
    }

    private void refreshItems() {
        models.values().forEach(DefaultListModel::clear);
        state.getItems().forEach(item -> models.get(item.getCategory()).addElement(item));
        switchCategory(ClothingCategory.TOP);
    }

    private Path copyToUploads(Path source) throws IOException {
        String sanitized = source.getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");
        Path target = state.getUploadDir().resolve(System.currentTimeMillis() + "_" + sanitized);
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        return state.getUploadDir().relativize(target);
    }

    private ClothingCategory guessCategory(String filename) {
        String lower = filename.toLowerCase();
        if (lower.contains("dress")) {
            return ClothingCategory.DRESS;
        }
        if (lower.contains("shoe") || lower.contains("heel") || lower.contains("boot") || lower.contains("sandal")) {
            return ClothingCategory.SHOES;
        }
        if (lower.contains("skirt")) {
            return ClothingCategory.BOTTOM;
        }
        if (lower.contains("pant") || lower.contains("jean") || lower.contains("short") || lower.contains("legging")) {
            return ClothingCategory.BOTTOM;
        }
        if (lower.contains("coat") || lower.contains("jacket") || lower.contains("blazer")) {
            return ClothingCategory.OUTERWEAR;
        }
        if (lower.contains("bag") || lower.contains("purse")) {
            return ClothingCategory.BAG;
        }
        if (lower.contains("hat") || lower.contains("scarf") || lower.contains("headband") || lower.contains("necklace")) {
            return ClothingCategory.ACCESSORY;
        }
        return ClothingCategory.TOP;
    }

    private String suggestName(String filename) {
        String base = filename.replaceAll("\\.[^.]+$", "");
        return base.replace('_', ' ');
    }

    private Set<String> defaultTagsFor(ClothingCategory category) {
        return switch (category) {
            case TOP -> Set.of("casual", "layering");
            case BOTTOM -> Set.of("versatile");
            case DRESS -> Set.of("one-piece");
            case SHOES -> Set.of("footwear");
            case ACCESSORY -> Set.of("accent");
            case OUTERWEAR -> Set.of("layering", "outerwear");
            case BAG -> Set.of("carryall");
        };
    }

    private Set<String> parseTags(String input) {
        if (input == null || input.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public void onItemsChanged() {
        SwingUtilities.invokeLater(() -> {
            models.values().forEach(DefaultListModel::clear);
            state.getItems().forEach(item -> models.get(item.getCategory()).addElement(item));
            if (selectedItem != null) {
                state.findItem(selectedItem.getId()).ifPresentOrElse(this::setSelectedItem, () -> setSelectedItem(null));
            }
        });
    }

    @Override
    public void onOutfitsChanged() {
        // not needed
    }

    @Override
    public void onPlannerChanged() {
        // not needed
    }

    private static final class WardrobeCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (component instanceof javax.swing.JLabel label && value instanceof WardrobeItem item) {
                label.setText(item.getName());
                label.setIcon(ImageCache.load(item.getImagePath(), 48, 48));
                label.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
                label.setIconTextGap(12);
            }
            return component;
        }
    }
}
