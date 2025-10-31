package characters;

import clothes.Clothes;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import other.Characters;

public class Aliki extends Characters {
    public Aliki() {
        super();
    }



    public Aliki(int x, int y){
        super(x,y, 200, 250, 0, new ImageIcon("images/Aliki.png"),new ImageIcon("images/AlikiHead.png"), setListAliki(), "Aliki", "Aliki is a basketball star. She was the first draft pick and has a nutrition foundation.");
    }

    public String toString(){
        return "Aliki";
    }


    public static ArrayList <Clothes> setListAliki(){
        ArrayList <Clothes> temp = new ArrayList <Clothes> ();
        return temp;
    }
}
//asd