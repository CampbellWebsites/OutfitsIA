package characters;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import clothes.Clothes;
import other.Characters;

public class Marinne extends Characters {
    public Marinne() {
        super();
    }

    public Marinne(int x, int y){
        super(x,y, 200, 250, 2, new ImageIcon("images/marinne.png"), new ImageIcon("images/marinneHead.png"), setListMarinne(), "Marinne", "She has a husband and four kids. She drives her pink Porsche to pilates daily.");
    }

    public String toString(){
        return "Marinne";
    }

    public static ArrayList <Clothes> setListMarinne(){
    ArrayList <Clothes> temp = new ArrayList <Clothes> ();

        return temp;
    }
}
