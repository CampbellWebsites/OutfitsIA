package clothes;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.util.HashMap;

public abstract class Clothes {
    protected int x, y, w, h;
    protected String name;
    protected String description;
    protected ImageIcon image;
    protected HashMap<Integer, Integer> levelPoints; // Points for each level
    protected int id; // Unique identifier for each item

    public Clothes(int x, int y, int w, int h, String imagePath, int id) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.image = new ImageIcon(imagePath);
        this.id = id;
        this.levelPoints = new HashMap<>();
        initializeLevelPoints();
    }

    // Initializing points for each level
    protected void initializeLevelPoints() {
        // Default points (overridden in specific items)
        levelPoints.put(1, 0); // Field Day
        levelPoints.put(2, 0); // Errands with Friend
        levelPoints.put(3, 0); // Presentation Day
        levelPoints.put(4, 0); // Art Museum Ball
    }

    public int getPoints(int level) {
        return levelPoints.getOrDefault(level, 0);
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public ImageIcon getImage() { return image; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getId() { return id; }

    public void draw(Graphics g, int x, int y, int width, int height) {
        g.drawImage(image.getImage(), x, y, width, height, null);
    }
}
