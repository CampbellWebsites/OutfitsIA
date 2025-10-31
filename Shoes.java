package clothes;
import java.util.ArrayList;

public class Shoes extends Clothes {
    public static ArrayList<Shoes> createShoes() {
        ArrayList<Shoes> shoes = new ArrayList<>();
        
        // Sneakers
        shoes.add(new Shoes(200, 500, 80, 80, "images/shoes_sneakers.png", 1) {{
            levelPoints.put(1, 10);
            levelPoints.put(2, 8);
            levelPoints.put(3, 3);
            levelPoints.put(4, 1);
            name = "Sneakers";
            description = "Athletic sneakers";
        }});
        
        // High Heels
        shoes.add(new Shoes(300, 500, 80, 80, "images/shoes_heels.png", 2) {{
            levelPoints.put(1, 2);
            levelPoints.put(2, 5);
            levelPoints.put(3, 9);
            levelPoints.put(4, 10);
            name = "High Heels";
            description = "Elegant high heels";
        }});
        
        // Flats
        shoes.add(new Shoes(400, 500, 80, 80, "images/shoes_flats.png", 3) {{
            levelPoints.put(1, 5);
            levelPoints.put(2, 9);
            levelPoints.put(3, 8);
            levelPoints.put(4, 7);
            name = "Flats";
            description = "Comfortable ballet flats";
        }});
        
        // Boots
        shoes.add(new Shoes(500, 500, 80, 80, "images/shoes_boots.png", 4) {{
            levelPoints.put(1, 4);
            levelPoints.put(2, 7);
            levelPoints.put(3, 7);
            levelPoints.put(4, 6);
            name = "Boots";
            description = "Stylish ankle boots";
        }});
        
        // Sandals
        shoes.add(new Shoes(600, 500, 80, 80, "images/shoes_sandals.png", 5) {{
            levelPoints.put(1, 7);
            levelPoints.put(2, 6);
            levelPoints.put(3, 2);
            levelPoints.put(4, 2);
            name = "Sandals";
            description = "Casual sandals";
        }});
        
        return shoes;
    }

    private Shoes(int x, int y, int w, int h, String imagePath, int id) {
        super(x, y, w, h, imagePath, id);
    }
}