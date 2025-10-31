package other;
import java.awt.Graphics;
import javax.swing.ImageIcon;

import clothes.Clothes;

import java.util.ArrayList;

//add get weapon

public class Characters {

    private int x,y,w,h, grade, dx, dy;
    private ImageIcon pic;
    private ImageIcon Headpic;
    private Clothes cloth;
    private ArrayList<Clothes> ClothesList;
    private String name;
    private String description;
    //add arraylist

    public Characters() {
        x=0;
        y=0;
        w=0;
        h=0;
        grade=0;
        ClothesList=new ArrayList<Clothes>();
        
        dx=0;
        dy=0;
        pic = new ImageIcon();
        Headpic = new ImageIcon();
        name = "";
        description = "";
    }

    public Characters(int xV, int yV, int width, int height, int gr, ImageIcon p,ImageIcon Headp, ArrayList<Clothes> list, String name, String description){
        x=xV;
        y=yV;
        w=width;
        h=height;
        grade=gr;
        dx=0;
        dy=0;
        pic = p;
        Headpic=Headp;
        this.cloth=cloth;
        ClothesList=list;
        this.name=name;
        this.description=description;
    }

    // public Characters(int xV, int yV, int width, int height, int gr, ImageIcon p, Clothes we, ArrayList<Clothes> list, String name, String description){
    //     x=xV;
    //     y=yV;
    //     w=width;
    //     h=height;
    //     grade=gr;
    //     dx=0;
    //     dy=0;
    //     pic = p;
    //     this.cloth=cloth;
    //     ClothesList=list;
    //     this.name=name;
    //     this.description=description;
    // }

    // public Characters(int xV, int yV, int width, int height, int sp, int hea, int dam, int st, ImageIcon p, String name, String description){
    //     x=xV;
    //     y=yV;
    //     w=width;
    //     h=height;
    //     speed=sp;
    //     health=hea;
    //     damage=dam;
    //     stam=st;
    //     dx=0;
    //     dy=0;
    //     pic = p;
    //     this.name=name;
    //     this.description=description;
    // }

    public void drawChar(Graphics g2d, int x, int y, int width, int height){
        g2d.drawImage(pic.getImage(),x,y, w,h,null);
    }

    public void drawHead(Graphics g2d, int x, int y, int width, int height){
        g2d.drawImage(Headpic.getImage(),x,y, w,h,null);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getW() {
        return this.w;
    }

    public int getH() {
        return this.h;
    }

    public int getGR() {
        return this.grade;
    }

    public int getDX() {
        return this.dx;
    }

    public int getDY() {
        return this.dy;
    }

    public ImageIcon getPic() {
        return this.pic;
    }

    public Clothes getClothes(){
        return this.cloth;
    }

    public ImageIcon getHeadpic() {
        return this.Headpic;
    }

    public void setClothes(ArrayList <Clothes> list){
        this.ClothesList=list;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setW(int w) {
        this.w = w;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void setGR(int grade) {
        this.grade = grade;
    }

    public void setDX(int dx) {
        this.dx = dx;
    }

    public void setDY(int dy) {
        this.dy = dy;
    }

    public void setPIC(ImageIcon pic) {
        this.pic = pic;
    }

    public void setHEADPIC(ImageIcon Headpic) {
        this.Headpic = Headpic;
    }
    
    public Characters grade(int grade){
        setGR(grade);
        return this;
    }

    public Characters pic(ImageIcon pic) {
        setPIC(pic);
        return this;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    // public boolean Collisions(int x,int y) {
	// 	return getX()+getW()>=b.getX()&&getX()<=b.getX()+b.getW()&&
	// 			getY()+getH()>=b.getY()&&getY()<=b.getY()+b.getH();
	// }

    //add collison
    //if collision is true is mouseMoved have a boolean that flipped to true
    

}