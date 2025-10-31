package characters;
import clothes.Clothes;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import other.Characters;

public final class Caitlyn extends Characters {
    public Caitlyn() {
        super();
    }

    public Caitlyn(int x, int y){
    
        super(x,y, 200, 250, 2, new ImageIcon("images/caitlyn.png"), new ImageIcon("images/caitlynHead.png"), setList() ,"Caitlyn","Caitlyn is a famous actress. She attends many red carpet events and has won 2 Oscars.");
                //getClothes().setX(super.getX()+super.getW());
                //setClothes(setList());
            }
        
            public String toString(){
                return "Caitlyn";
            }
        
            public static ArrayList <Clothes> setList(){
        ArrayList <Clothes> temp = new ArrayList <Clothes> ();
        return temp;
    }

}
//new Lolly(x,y)