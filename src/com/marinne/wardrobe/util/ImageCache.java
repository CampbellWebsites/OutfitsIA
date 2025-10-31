package com.marinne.wardrobe.util;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads images from the workspace with fallback search paths and caches them for reuse.
 */
public final class ImageCache {

    private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();
    private static final ImageIcon EMPTY_ICON = new ImageIcon();

    private ImageCache() {
    }

    public static ImageIcon load(String relativePath, int width, int height) {
        if (relativePath == null || relativePath.isBlank()) {
            return EMPTY_ICON;
        }
        String key = relativePath + "::" + width + "x" + height;
        return CACHE.computeIfAbsent(key, k -> {
            ImageIcon icon = resolveIcon(relativePath);
            if (icon.getIconWidth() <= 0 || icon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
                return EMPTY_ICON;
            }
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        });
    }

    public static ImageIcon raw(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return EMPTY_ICON;
        }
        return CACHE.computeIfAbsent(relativePath, ImageCache::resolveIcon);
    }

    private static ImageIcon resolveIcon(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return EMPTY_ICON;
        }
        Path workspace = Path.of(System.getProperty("user.dir"));
        Path direct = workspace.resolve(relativePath);
        if (Files.exists(direct)) {
            return new ImageIcon(direct.toString());
        }

        Path imagesDir = workspace.resolve("images");
        Path alt = imagesDir.resolve(relativePath);
        if (Files.exists(alt)) {
            return new ImageIcon(alt.toString());
        }

        // Attempt within uploads directory.
        Path uploads = workspace.resolve("data").resolve("uploads").resolve(relativePath);
        if (Files.exists(uploads)) {
            return new ImageIcon(uploads.toString());
        }

        try {
            if (Files.exists(Path.of(relativePath))) {
                return new ImageIcon(Path.of(relativePath).toRealPath().toString());
            }
        } catch (IOException ignored) {
        }

        return new ImageIcon();
    }
}
