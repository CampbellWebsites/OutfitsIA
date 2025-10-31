package clothes;
import java.util.ArrayList;

public class Accessory extends Clothes {
    public static ArrayList<Accessory> createAccessories() {
        ArrayList<Accessory> accessories = new ArrayList<>();
        
        // Pearl Necklace
        accessories.add(new Accessory(200, 400, 80, 80, "images/acc_necklace.png", 1) {{
            levelPoints.put(1, 2);
            levelPoints.put(2, 6);
            levelPoints.put(3, 8);
            levelPoints.put(4, 9);
            name = "Pearl Necklace";
            description = "Classic pearl necklace";
        }});
        
        // Baseball Cap
        accessories.add(new Accessory(300, 400, 80, 80, "images/acc_hat.png", 2) {{
            levelPoints.put(1, 9);
            levelPoints.put(2, 7);
            levelPoints.put(3, 2);
            levelPoints.put(4, 1);
            name = "Baseball Cap";
            description = "Sporty baseball cap";
        }});
        
        // Scarf
        accessories.add(new Accessory(400, 400, 80, 80, "images/acc_scarf.png", 3) {{
            levelPoints.put(1, 3);
            levelPoints.put(2, 8);
            levelPoints.put(3, 7);
            levelPoints.put(4, 6);
            name = "Scarf";
            description = "Stylish silk scarf";
        }});
        
        // Headband
        accessories.add(new Accessory(500, 400, 80, 80, "images/acc_headband.png", 4) {{
            levelPoints.put(1, 8);
            levelPoints.put(2, 6);
            levelPoints.put(3, 5);
            levelPoints.put(4, 4);
            name = "Headband";
            description = "Athletic headband";
        }});
        
        // Statement Necklace
        accessories.add(new Accessory(600, 400, 80, 80, "images/acc_statement.png", 5) {{
            levelPoints.put(1, 1);
            levelPoints.put(2, 5);
            levelPoints.put(3, 7);
            levelPoints.put(4, 10);
            name = "Statement Necklace";
            description = "Bold statement necklace";
        }});
        
        return accessories;
    }

    private Accessory(int x, int y, int w, int h, String imagePath, int id) {
        super(x, y, w, h, imagePath, id);
    }
}