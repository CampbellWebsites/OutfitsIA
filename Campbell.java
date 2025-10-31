package characters;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import clothes.Clothes;
import other.Characters;

public class Campbell extends Characters {
    public Campbell() {
        super();
    }

    public Campbell(int x, int y){
        super(x,y, 200, 250, 2, new ImageIcon("images/campbell.png"), new ImageIcon("images/campbellHead.png"), setListCampbell(), "Campbell", "Campbell is a medical researcher and a fashion designer in NYC. She won a Nobel Prize.");
        //getClothes().setX(super.getX()+super.getW());
        //setClothes(setListCampbell());
    }

    public String toString(){
        return "Campbell";
    }
        
    public static ArrayList <Clothes> setListCampbell(){
        ArrayList <Clothes> temp = new ArrayList <Clothes> ();
        return temp;
    }



}
 