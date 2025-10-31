package com.marinne.wardrobe.ui;

import com.marinne.wardrobe.model.ClothingCategory;
import com.marinne.wardrobe.model.FriendProfile;
import com.marinne.wardrobe.model.WardrobeItem;
import com.marinne.wardrobe.util.ImageCache;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Renders the currently selected friend with layered wardrobe items.
 */
public final class AvatarPanel extends JPanel {

    private static final Map<ClothingCategory, Rectangle2D.Double> LAYER_FRAMES = Map.of(
            ClothingCategory.TOP, new Rectangle2D.Double(0.32, 0.22, 0.36, 0.28),
            ClothingCategory.BOTTOM, new Rectangle2D.Double(0.33, 0.48, 0.34, 0.32),
            ClothingCategory.DRESS, new Rectangle2D.Double(0.28, 0.22, 0.44, 0.58),
            ClothingCategory.SHOES, new Rectangle2D.Double(0.35, 0.80, 0.28, 0.14),
            ClothingCategory.ACCESSORY, new Rectangle2D.Double(0.38, 0.10, 0.22, 0.20),
            ClothingCategory.OUTERWEAR, new Rectangle2D.Double(0.24, 0.18, 0.50, 0.52),
            ClothingCategory.BAG, new Rectangle2D.Double(0.18, 0.55, 0.25, 0.28));

    private FriendProfile friend;
    private final EnumMap<ClothingCategory, WardrobeItem> outfit = new EnumMap<>(ClothingCategory.class);

    public AvatarPanel() {
        setOpaque(false);
    }

    public void setFriend(FriendProfile friend) {
        this.friend = friend;
        repaint();
    }

    public FriendProfile getFriend() {
        return friend;
    }

    public void setWardrobeItem(ClothingCategory category, WardrobeItem item) {
        if (item == null) {
            outfit.remove(category);
        } else {
            outfit.put(category, item);
        }
        repaint();
    }

    public void setOutfit(Map<ClothingCategory, WardrobeItem> selections) {
        outfit.clear();
        if (selections != null) {
            outfit.putAll(selections);
        }
        repaint();
    }

    public Map<ClothingCategory, WardrobeItem> getOutfitSnapshot() {
        return new EnumMap<>(outfit);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw subtle background frame.
        g2d.setColor(new Color(248, 245, 242));
        g2d.fillRoundRect(10, 10, width - 20, height - 20, 30, 30);
        g2d.setColor(new Color(220, 205, 190));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(10, 10, width - 20, height - 20, 30, 30);

        if (friend == null) {
            g2d.setColor(new Color(110, 100, 90));
            g2d.drawString("Choose a friend to preview outfits", 40, height / 2);
            g2d.dispose();
            return;
        }

        Image base = ImageCache.raw(friend.getAvatarImagePath()).getImage();
        if (base != null) {
            double aspect = (double) base.getWidth(null) / Math.max(1, base.getHeight(null));
            int avatarHeight = (int) (height * 0.82);
            int avatarWidth = (int) (avatarHeight * aspect);
            int avatarX = (width - avatarWidth) / 2;
            int avatarY = (height - avatarHeight) / 2 + 5;
            g2d.drawImage(base, avatarX, avatarY, avatarWidth, avatarHeight, this);

            // Draw clothing overlays.
            outfit.forEach((category, item) -> Optional.ofNullable(LAYER_FRAMES.get(category)).ifPresent(frame -> {
                Image layer = ImageCache.raw(item.getImagePath()).getImage();
                if (layer != null) {
                    int layerW = (int) (frame.width * avatarWidth);
                    int layerH = (int) (frame.height * avatarHeight);
                    int layerX = avatarX + (int) (frame.x * avatarWidth);
                    int layerY = avatarY + (int) (frame.y * avatarHeight);
                    g2d.drawImage(layer, layerX, layerY, layerW, layerH, this);
                }
            }));
        }

        // Draw legend boxes for currently applied items.
        int legendY = height - 70;
        int boxWidth = width / 6;
        int padding = 14;
        int index = 0;
        for (ClothingCategory category : ClothingCategory.values()) {
            WardrobeItem item = outfit.get(category);
            if (item == null) {
                continue;
            }
            int x = padding + index * (boxWidth + 8);
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(x, legendY, boxWidth, 50, 14, 14);
            g2d.setColor(new Color(150, 130, 120));
            g2d.drawRoundRect(x, legendY, boxWidth, 50, 14, 14);
            g2d.setColor(new Color(70, 60, 55));
            g2d.drawString(category.getDisplayName(), x + 8, legendY + 18);
            g2d.setColor(new Color(50, 45, 40));
            String name = item.getName();
            if (name.length() > 16) {
                name = name.substring(0, 15) + "...";
            }
            g2d.drawString(name, x + 8, legendY + 36);
            index++;
        }

        g2d.dispose();
    }
}
