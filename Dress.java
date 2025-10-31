package clothes;
import java.util.ArrayList;

public class Dress extends Clothes {
    public static ArrayList<Dress> createDresses() {
        ArrayList<Dress> dresses = new ArrayList<>();
        
        // Casual Dress
        dresses.add(new Dress(200, 300, 80, 80, "images/dress_casual.png", 1) {{
            levelPoints.put(1, 5);
            levelPoints.put(2, 8);
            levelPoints.put(3, 6);
            levelPoints.put(4, 4);
            name = "Casual Dress";
            description = "Comfortable everyday dress";
        }});
        
        // Evening Gown
        dresses.add(new Dress(300, 300, 80, 80, "images/dress_formal.png", 2) {{
            levelPoints.put(1, 2);
            levelPoints.put(2, 4);
            levelPoints.put(3, 8);
            levelPoints.put(4, 10);
            name = "Evening Gown";
            description = "Elegant formal dress";
        }});
        
        // Sundress
        dresses.add(new Dress(400, 300, 80, 80, "images/dress_sun.png", 3) {{
            levelPoints.put(1, 7);
            levelPoints.put(2, 9);
            levelPoints.put(3, 5);
            levelPoints.put(4, 3);
            name = "Sundress";
            description = "Light and breezy sundress";
        }});
        
        // Business Dress
        dresses.add(new Dress(500, 300, 80, 80, "images/dress_business.png", 4) {{
            levelPoints.put(1, 3);
            levelPoints.put(2, 6);
            levelPoints.put(3, 10);
            levelPoints.put(4, 7);
            name = "Business Dress";
            description = "Professional sheath dress";
        }});
        
        // Cocktail Dress
        dresses.add(new Dress(600, 300, 80, 80, "images/dress_cocktail.png", 5) {{
            levelPoints.put(1, 2);
            levelPoints.put(2, 5);
            levelPoints.put(3, 7);
            levelPoints.put(4, 9);
            name = "Cocktail Dress";
            description = "Stylish cocktail dress";
        }});
        
        return dresses;
    }   
    
    private Dress(int x, int y, int w, int h, String imagePath, int id) {
        super(x, y, w, h, imagePath, id);
    }
}
