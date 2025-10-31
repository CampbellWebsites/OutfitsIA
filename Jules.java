package characters;
import clothes.Clothes;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import other.Characters;

public class Jules extends Characters {
    public Jules() {
        super();
    }

    public Jules(int x, int y){
        super(x,y, 200, 250, 2, new ImageIcon("images/jules.png"), new ImageIcon("images/julesHead.png"), setListJules(), "Jules", "Jules is a college English professor. In her free time, she teaches English to prisoners.");
    }

    public String toString(){
        return "Jules";
    }
        public static ArrayList <Clothes> setListJules(){
        ArrayList <Clothes> temp = new ArrayList <Clothes> ();
        return temp;
    }
}
