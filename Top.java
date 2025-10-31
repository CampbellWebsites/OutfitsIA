package clothes;
import java.util.ArrayList;

public class Top extends Clothes {
    public static ArrayList<Top> createTops() {
        ArrayList<Top> tops = new ArrayList<>();
        
        // T-Shirt
        tops.add(new Top(200, 100, 80, 80, "images/top_tshirt.png", 1) {{
            levelPoints.put(1, 8);  // Field Day: Great for sports
            levelPoints.put(2, 5);  // Errands: Casual is okay
            levelPoints.put(3, 2);  // Presentation: Too casual
            levelPoints.put(4, 1);  // Ball: Inappropriate
            name = "T-Shirt";
            description = "A comfortable cotton t-shirt";
        }});
        
        // Blouse
        tops.add(new Top(300, 100, 80, 80, "images/top_blouse.png", 2) {{
            levelPoints.put(1, 3);  // Field Day: Too formal
            levelPoints.put(2, 7);  // Errands: Stylish choice
            levelPoints.put(3, 8);  // Presentation: Professional
            levelPoints.put(4, 6);  // Ball: Bit too casual
            name = "Blouse";
            description = "An elegant silk blouse";
        }});
        
        // Sweater
        tops.add(new Top(400, 100, 80, 80, "images/top_sweater.png", 3) {{
            levelPoints.put(1, 4);  // Field Day: Too warm
            levelPoints.put(2, 8);  // Errands: Cozy and appropriate
            levelPoints.put(3, 6);  // Presentation: Business casual
            levelPoints.put(4, 3);  // Ball: Too casual
            name = "Sweater";
            description = "A cozy knit sweater";
        }});
        
        // Tank Top
        tops.add(new Top(500, 100, 80, 80, "images/top_tank.png", 4) {{
            levelPoints.put(1, 9);  // Field Day: Perfect for sports
            levelPoints.put(2, 6);  // Errands: Casual
            levelPoints.put(3, 1);  // Presentation: Too casual
            levelPoints.put(4, 1);  // Ball: Inappropriate
            name = "Tank Top";
            description = "A sporty tank top";
        }});
        
        // Button-Up Shirt
        tops.add(new Top(600, 100, 80, 80, "images/top_button.png", 5) {{
            levelPoints.put(1, 2);  // Field Day: Too restrictive
            levelPoints.put(2, 7);  // Errands: Nice but casual
            levelPoints.put(3, 9);  // Presentation: Very professional
            levelPoints.put(4, 7);  // Ball: Acceptable formal wear
            name = "Button-Up Shirt";
            description = "A crisp button-up shirt";
        }});
        
        return tops;
    }

    private Top(int x, int y, int w, int h, String imagePath, int id) {
        super(x, y, w, h, imagePath, id);
    }
}