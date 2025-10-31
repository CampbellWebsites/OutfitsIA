package clothes;
import java.util.ArrayList;

public class Bottom extends Clothes {
    public static ArrayList<Bottom> createBottoms() {
        ArrayList<Bottom> bottoms = new ArrayList<>();
        
        // Jeans
        bottoms.add(new Bottom(200, 200, 80, 80, "images/bottom_jeans.png", 1) {{
            levelPoints.put(1, 6);
            levelPoints.put(2, 8);
            levelPoints.put(3, 4);
            levelPoints.put(4, 2);
            name = "Jeans";
            description = "Classic blue jeans";
        }});
        
        // Slacks
        bottoms.add(new Bottom(300, 200, 80, 80, "images/bottom_slacks.png", 2) {{
            levelPoints.put(1, 3);
            levelPoints.put(2, 5);
            levelPoints.put(3, 9);
            levelPoints.put(4, 7);
            name = "Slacks";
            description = "Professional dress pants";
        }});
        
        // Athletic Shorts
        bottoms.add(new Bottom(400, 200, 80, 80, "images/bottom_shorts.png", 3) {{
            levelPoints.put(1, 10);
            levelPoints.put(2, 4);
            levelPoints.put(3, 1);
            levelPoints.put(4, 1);
            name = "Athletic Shorts";
            description = "Comfortable sports shorts";
        }});
        
        // Skirt
        bottoms.add(new Bottom(500, 200, 80, 80, "images/bottom_skirt.png", 4) {{
            levelPoints.put(1, 4);
            levelPoints.put(2, 7);
            levelPoints.put(3, 8);
            levelPoints.put(4, 8);
            name = "Skirt";
            description = "A-line midi skirt";
        }});
        
        // Leggings
        bottoms.add(new Bottom(600, 200, 80, 80, "images/bottom_leggings.png", 5) {{
            levelPoints.put(1, 8);
            levelPoints.put(2, 6);
            levelPoints.put(3, 3);
            levelPoints.put(4, 2);
            name = "Leggings";
            description = "Stretchy athletic leggings";
        }});
        
        return bottoms;
    }

    private Bottom(int x, int y, int w, int h, String imagePath, int id) {
        super(x, y, w, h, imagePath, id);
    }
}