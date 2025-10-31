package com.marinne.wardrobe;

import com.marinne.wardrobe.model.ClothingCategory;
import com.marinne.wardrobe.model.FriendProfile;
import com.marinne.wardrobe.model.WardrobeItem;
import com.marinne.wardrobe.model.WardrobeState;
import com.marinne.wardrobe.ui.AvatarPanel;
import com.marinne.wardrobe.ui.MixAndMatchPanel;
import com.marinne.wardrobe.ui.PlannerPanel;
import com.marinne.wardrobe.ui.SavedOutfitsPanel;
import com.marinne.wardrobe.ui.WardrobePanel;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

/**
 * Main frame for Marinne's virtual wardrobe application.
 */
public final class WardrobeAppFrame extends JFrame {

    private final WardrobeState state;
    private final AvatarPanel avatarPanel = new AvatarPanel();
    private final EnumMap<ClothingCategory, WardrobeItem> currentOutfit = new EnumMap<>(ClothingCategory.class);

    public WardrobeAppFrame(Path workspaceRoot) {
        super("Marinne's Virtual Wardrobe Studio");
        this.state = new WardrobeState(workspaceRoot);
        configureWindow();
        buildLayout();
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 780));
        setLocationRelativeTo(null);
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildMainContent(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("Marinne's Virtual Wardrobe Studio");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);

        JComboBox<FriendProfile> friendSelector = new JComboBox<>(state.getFriends().toArray(new FriendProfile[0]));
        friendSelector.addActionListener(e -> {
            FriendProfile friend = (FriendProfile) friendSelector.getSelectedItem();
            avatarPanel.setFriend(friend);
        });
        if (!state.getFriends().isEmpty()) {
            avatarPanel.setFriend(state.getFriends().get(0));
            friendSelector.setSelectedIndex(0);
        }

        JPanel friendPanel = new JPanel(new BorderLayout(6, 6));
        friendPanel.add(new JLabel("Style for:"), BorderLayout.WEST);
        friendPanel.add(friendSelector, BorderLayout.CENTER);
        header.add(friendPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout(16, 16));

        avatarPanel.setPreferredSize(new Dimension(420, 0));
        main.add(avatarPanel, BorderLayout.WEST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Wardrobe", new WardrobePanel(state, this::applyItem));
        tabs.addTab("Mix & Match", new MixAndMatchPanel(state, this::applyOutfit));
        tabs.addTab("Saved Looks", new SavedOutfitsPanel(state, this::snapshotOutfit, this::applyOutfit));
        tabs.addTab("Planner", new PlannerPanel(state));
        main.add(tabs, BorderLayout.CENTER);

        return main;
    }

    private JPanel buildStatusBar() {
        JPanel status = new JPanel(new GridLayout(1, 1));
        status.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 215, 205)));
        JLabel note = new JLabel("Tip: Double-click wardrobe pieces or mix suggestions to layer them on the avatar.");
        note.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        note.setForeground(new Color(110, 100, 95));
        status.add(note);
        return status;
    }

    private void applyItem(ClothingCategory category, WardrobeItem item) {
        if (item == null) {
            currentOutfit.remove(category);
        } else {
            currentOutfit.put(category, item);
        }
        avatarPanel.setWardrobeItem(category, item);
    }

    private void applyOutfit(Map<ClothingCategory, WardrobeItem> selections) {
        currentOutfit.clear();
        currentOutfit.putAll(selections);
        avatarPanel.setOutfit(currentOutfit);
    }

    private Map<ClothingCategory, WardrobeItem> snapshotOutfit() {
        return new EnumMap<>(currentOutfit);
    }

    public static void launch(Path workspaceRoot) {
        SwingUtilities.invokeLater(() -> {
            WardrobeAppFrame frame = new WardrobeAppFrame(workspaceRoot);
            frame.setVisible(true);
        });
    }
}
