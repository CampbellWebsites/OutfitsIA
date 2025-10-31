package other;

import characters.Aliki;
import characters.Caitlyn;
import characters.Campbell;
import characters.Jules;
import characters.Marinne;
import clothes.Accessory;
import clothes.Bottom;
import clothes.Clothes;
import clothes.Dress;
import clothes.Shoes;
import clothes.Top;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

public final class Game extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {

    private BufferedImage back;
    private int key, x, y;
    private ArrayList<Characters> charList;
    private String screen;
    private String level;
    private Characters player;
    private ImageIcon startBg;
    private ImageIcon chooseBg;
    private ImageIcon assignmentBg;
    private ImageIcon assignment1;
    private ImageIcon assignment2;
    private ImageIcon assignment3;
    private ImageIcon assignment4;
    private ImageIcon checkmark;
    private ImageIcon congrats;
    private String welcome;
    private double time;
    private int i;
    //private boolean collision;
    private Characters hoveredChar;
    private File saveFile;

    // Variables for dynamic scaling
    private int panelWidth;
    private int panelHeight;

    private ArrayList<Top> tops;
    private ArrayList<Bottom> bottoms;
    private ArrayList<Dress> dresses;
    private ArrayList<Accessory> accessories;
    private ArrayList<Shoes> shoes;
    private Clothes selectedItem;
    private int currentLevel;
    private int totalPoints;
    private HashMap<String, Clothes> currentOutfit;
    private int levelimageStartX;
    private int levelimageStartY;
    private int levelimageWidth;
    private int levelimageHeight;
    private int level1done;
    private int level2done;
    private int level3done;
    private int level4done;    
    private int level1score;
    private int level2score;
    private int level3score;
    private int level4score;
    private int totalscore;


    

    public Game() {
        new Thread(this).start();
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        key = -1;
        x = 0;
        y = 0;
        charList = setCharList();
        screen = "start";
        level = "";
        startBg = new ImageIcon("images/startbackground.png");
        chooseBg = new ImageIcon("images/classroom2.png");
        assignmentBg = new ImageIcon("images/assignmentBackground.png");
        assignment1 = new ImageIcon("images/assignment1.png");
        assignment2 = new ImageIcon("images/assignment2.png");
        assignment3 = new ImageIcon("images/assignment3.png");
        assignment4 = new ImageIcon("images/assignment4.png");
        checkmark = new ImageIcon("images/checkmark.png");
        congrats = new ImageIcon("images/congrats.gif");
        welcome = "Welcome to Campbell's Fashion School Game!";
        time = System.currentTimeMillis();
        //collision = false;
        saveFile = new File("saved_file2.0txt");

        tops = Top.createTops();
        bottoms = Bottom.createBottoms();
        dresses = Dress.createDresses();
        accessories = Accessory.createAccessories();
        shoes = Shoes.createShoes();

        currentOutfit = new HashMap<>();
        currentLevel = 1;
        totalPoints = 0;
    }

    private int calculateOutfitPoints() {
    int points = 0;
    for (Clothes item : currentOutfit.values()) {
        points += item.getPoints(currentLevel);
    }
    return points;
}

    // Method to adjust the size dynamically based on the panel size
    public void adjustSize() {
        panelWidth = getWidth();
        panelHeight = getHeight();
    }

    /*public void createFile() {
        try {
            if (saveFile.createNewFile()) {
                System.out.println("Successfully created file!");
            } else {
                System.out.println("File already exists!");
            }
        } catch (IOException ex) {
        }
    }

    public void writeToFile() {
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter(saveFile);
            if (charList.isEmpty()) {
                myWriter.write("win");
            } else {
                myWriter.write("You have " + charList.size() + " enemies left");
            }
        } catch (IOException ex) {
        } finally {
            try {
                if (myWriter != null) {
                    myWriter.close();
                }
            } catch (IOException ex) {
            }
        }

    public void readFile() {
        try {
            Scanner sc = new Scanner(saveFile);
            while (sc.hasNext()) {
                System.out.println(sc.nextLine());
            }
        } catch (FileNotFoundException ex) {
        }
    }*/

        // Method to create and save data in a text file
    public void saveDataToFile(String fileName, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(data);
            writer.newLine();
            System.out.println("Data saved successfully to " + fileName);
        } catch (IOException e) {
            System.err.println("An error occurred while saving data: " + e.getMessage());
        }
    }

    public ArrayList<Characters> setCharList() {
        ArrayList<Characters> temp = new ArrayList<>();
        temp.add(new Caitlyn(100, 500));
        temp.add(new Marinne(300, 500));
        temp.add(new Campbell(500, 500));
        temp.add(new Jules(700, 500));
        temp.add(new Aliki(900, 500));
        return temp;
    }

    public void run() {
        try {
            while (true) {
                Thread.currentThread().sleep(5);
                repaint();
            }
        } catch (Exception e) {
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        adjustSize();  // Call adjustSize() here to update panel dimensions
        Graphics2D twoDgraph = (Graphics2D) g;
        if (back == null)
            back = (BufferedImage) (createImage(getWidth(), getHeight()));

        Graphics g2d = back.createGraphics();
        g2d.clearRect(0, 0, getWidth(), getHeight());

        int fontSize = Math.min(getWidth(), getHeight()) / 25; // Adjust font size dynamically
        g2d.setFont(new Font("Broadway", Font.BOLD, fontSize));

        drawScreens(g2d);

        twoDgraph.drawImage(back, null, 0, 0);
    }

    public void drawStartScreen(Graphics g2d) {
        g2d.drawImage(startBg.getImage(), 0, 0, getWidth(), getHeight(), this);
    
        int fontSize = Math.min(getWidth(), getHeight()) / 20;
        g2d.setFont(new Font("Broadway", Font.BOLD, fontSize));
    
        // Split welcome message into two lines
        String welcomeLine1 = "Welcome to Campbell's";
        String welcomeLine2 = "Fashion School Game!";
    
        // Calculate positions for centered text
        FontMetrics fm = g2d.getFontMetrics();
        int line1Width = fm.stringWidth(welcomeLine1);
        int line2Width = fm.stringWidth(welcomeLine2);
        
        // Draw first line of welcome message
        g2d.drawString(welcomeLine1.substring(0, Math.min(i, welcomeLine1.length())), 
                       getWidth() / 2 - line1Width / 2, 
                       getHeight() / 4);
    
        // Draw second line of welcome message
        if (i > welcomeLine1.length()) {
            g2d.drawString(welcomeLine2.substring(0, Math.min(i - welcomeLine1.length(), welcomeLine2.length())), 
                           getWidth() / 2 - line2Width / 2, 
                           getHeight() / 4 + fontSize);
        }
    
        // Draw "Press a to Start!" lower on the screen
        g2d.drawString("Press a to Start!", 
                       getWidth() / 2 - fm.stringWidth("Press a to Start!") / 2, 
                       getHeight() / 2);
    
        // Update animation counter
        if (i < (welcomeLine1.length() + welcomeLine2.length())) {
            if (System.currentTimeMillis() - time > 100) {
                time = System.currentTimeMillis();
                i++;
            }

        }
        }
        public void drawChooseScreen(Graphics g2d) {
            g2d.drawImage(chooseBg.getImage(), 0, 0, getWidth(), getHeight(), this);
            for (Characters c : charList) {
                if (c instanceof Characters) {
                    int charWidth = getWidth() / 10;
                    int charHeight = getHeight() / 10;
                    int charX = c.getX() * getWidth() / 1100;
                    int charY = c.getY() * getHeight() / 1100;
    
                    c.drawChar(g2d, charX, charY, charWidth, charHeight);
                }
            }
    
            // Update hover detection to match the new scaling
            if (hoveredChar != null) {
                g2d.setFont(new Font("Arial", Font.PLAIN, getWidth() / 50));
                g2d.setColor(Color.WHITE);
                g2d.drawString("Name: " + hoveredChar.getName(), getWidth() / 20, getHeight() / 10);
                g2d.drawString("Description: " + hoveredChar.getDescription(), getWidth() / 20, getHeight() / 10 + 30);
            }
        }

    /**
     * @param g2d
     */
    public void drawSelectScreen(Graphics g2d) {
        g2d.drawImage(assignmentBg.getImage(), 0, 0, getWidth(), getHeight(), this);

		    // Calculate the size and positions for the images
			this.levelimageWidth = getWidth() / 5; // Scale image width relative to the screen
			this.levelimageHeight = getHeight() / 5; // Scale image height relative to the screen
			int gridSize = 2; // 2x2 grid of images (4 images total)
			
			// Calculate the total width and height of the grid area
			int totalWidth = levelimageWidth * gridSize + (gridSize - 1) * getWidth() / 10;
			int totalHeight = levelimageHeight * gridSize + (gridSize - 1) * getHeight() / 10;
			
			// Calculate the starting position (top-left corner) to center the grid on the screen
			this.levelimageStartX = (getWidth() - totalWidth) / 2;
			this.levelimageStartY = (getHeight() - totalHeight) / 2;

		g2d.drawImage(assignment1.getImage(), levelimageStartX, levelimageStartY-getHeight() / 20, levelimageWidth, levelimageHeight, this);
        if(level1done == 1){
            g2d.drawImage(checkmark.getImage(), levelimageStartX+levelimageWidth/2, levelimageStartY-getHeight() / 20+levelimageWidth/2, levelimageWidth/4, levelimageHeight/4, this); 
        }
    	g2d.drawImage(assignment2.getImage(), levelimageStartX + levelimageWidth + getWidth() / 15, levelimageStartY-getHeight() / 20, levelimageWidth, levelimageHeight, this);
        if(level2done == 1){
            g2d.drawImage(checkmark.getImage(), levelimageStartX + levelimageWidth + getWidth() / 15+levelimageWidth/2, levelimageStartY-getHeight() / 20+levelimageWidth/2, levelimageWidth/4, levelimageHeight/4, this);
            }
    	g2d.drawImage(assignment3.getImage(), levelimageStartX, levelimageStartY + levelimageHeight + getHeight() / 10, levelimageWidth, levelimageHeight, this);
    	if(level3done ==1){
            g2d.drawImage(checkmark.getImage(), levelimageStartX+levelimageWidth/2, levelimageStartY + levelimageHeight + getHeight() / 10+levelimageWidth/2, levelimageWidth/4, levelimageHeight/4, this);
        }
        g2d.drawImage(assignment4.getImage(), levelimageStartX + levelimageWidth + getWidth() / 15, levelimageStartY + levelimageHeight + getHeight() / 10, levelimageWidth, levelimageHeight, this);
        if(level4done==1){
            g2d.drawImage(checkmark.getImage(), levelimageStartX + levelimageWidth + getWidth() / 15+levelimageWidth/2, levelimageStartY + levelimageHeight + getHeight() / 10+levelimageWidth/2, levelimageWidth/4, levelimageHeight/4, this);
        }

    	// Dynamically adjust font size based on screen size for text
    	g2d.setFont(new Font("Arial", Font.PLAIN, getWidth() / 40));
    	g2d.setColor(Color.black);

		    // Display text messages and dynamically position them
		if (player != null) {
			g2d.drawString("You picked " + player.toString(), getWidth() / 20, getHeight() / 18);

        g2d.drawString("Please select a level.", getWidth() / 20, getHeight() / 10 + 10);
        }

        // Add See Result Button
        if (level1done ==1 && level2done ==1 &&level3done==1 && level4done ==1){
            g2d.setColor(Color.GREEN);
        }
        else{
            g2d.setColor(Color.GRAY);
        }
        g2d.fillRect(getWidth()-getWidth()/4, getHeight()-getHeight()/4, getWidth()/4, getHeight()/10); // Position and size of the Another Function button
        g2d.setColor(Color.WHITE);
        g2d.drawString("Check your Results", getWidth()-getWidth()/4, getHeight()-getHeight()/5); // Button label
}

public void drawAssignmentScreen(Graphics g2d) {
    // Draw background
    g2d.setColor(Color.BLUE);
    //g2d.fillRect(0, 0, getWidth(), getHeight());
    if("level1".equals(level)){
        setBackground(Color.WHITE);
        g2d.setFont(new Font("Garamond", Font.BOLD, getWidth() / 40));
        g2d.drawString("Field Day", 50, 50);
        g2d.drawString("You will be participating in the annual school field day.", 50, 100);
        g2d.drawString("You will compete in events such as the 3 legged race and tug of war. Select an outfit that is", 50, 150);
        g2d.drawString("comfortable and appropriate for this event. Remember to follow the school dress code!", 50, 200);
        //g2d.drawString("Press SPACE to Begin!", 800, 800);
    }

    else if("level2".equals(level)){
        setBackground(Color.lightGray);
        g2d.setFont(new Font("Garamond", Font.BOLD, getWidth() / 40));
        g2d.drawString("Errands with a Friend", 50, 50);
        g2d.drawString("It’s a Sunday morning and you’re on the way to run errands with a friend.", 50, 100);
        g2d.drawString("You’re going to a coffee shop and then shopping for new clothes.", 50, 150);
        g2d.drawString("Wear something chic but comfortable.", 50, 200);
        //g2d.drawString("Press SPACE to Begin!", 800, 800);
    }

    else if("level3".equals(level)){
        setBackground(Color.white);
        g2d.setFont(new Font("Garamond", Font.BOLD, getWidth() / 40));
        g2d.drawString("Presentation Day", 50, 50);
        g2d.drawString("You have worked hard to prepare a demonstration for your high school research class.", 50, 100);
        g2d.drawString("Now, you need to decide what to wear on your presentation day.", 50, 150);
        //g2d.drawString("Press SPACE to Begin!", 800, 800);
    }

    else if("level4".equals(level)){
        setBackground(Color.white);
        g2d.setFont(new Font("Garamond", Font.BOLD, getWidth() / 40));
        g2d.drawString("Art Museum’s Annual Ball", 50, 50);
        g2d.drawString("The Art Museum’s Annual Ball is upcoming. You need to select an outfit for this", 50, 100);
        g2d.drawString("special occasion. Ladies should wear a formal dress.  Choose something elegant and stylish.", 50, 150);
        //g2d.drawString("Press SPACE to Begin!", 800, 800);
    }
    
    // Draw character and current outfit
    if (player != null) {
        // Draw character head 
        int HeadWidth = getWidth() / 15;
        int HeadHeight = getHeight() / 15;
        int HeadX = 100 * getWidth() / 1000;
        int HeadY = 250 * getHeight() / 1000;

        player.drawHead(g2d, HeadX, HeadY, HeadWidth, HeadHeight);    
        
        // Draw current outfit items centered under head
        int itemWidth = getWidth() / 20;  // Width of each clothing item
        int itemHeight = getHeight() / 20; // Height of each clothing item
        int startX = HeadX + (HeadWidth - itemWidth) / 2 + getWidth() / 40;  // Adjust center position slightly right
        int startY = HeadY + HeadHeight + getHeight() / 5; // Slightly less spacing than before
        int spacingY = itemHeight + getHeight() / 60;  // Vertical spacing between items
        
        int currentY = startY;
        for (Clothes item : currentOutfit.values()) {
            item.draw(g2d, startX, currentY, itemWidth, itemHeight);
            currentY += spacingY;
        }
    }


    // Initialized starting positions using screen dimensions, does this work??
    int startX = getWidth() / 3;
    int startY = (int)(getHeight() * 0.285);
    int itemWidth = getWidth() / 13;
    int itemHeight = getHeight() / 11;
    int horizontalSpacing = getWidth() / 11;
    int verticalSpacing = getHeight() / 10;
    int labelOffset = 25;  

    // Draw tops section
    g2d.setFont(new Font("Arial", Font.BOLD, getWidth() / 70));
    g2d.drawString("Tops", startX, startY);  
    startY += labelOffset; 
    for (Top top : tops) {
        top.draw(g2d, startX, startY, itemWidth, itemHeight);
        startX += horizontalSpacing;
    }

    // Draw bottoms section
    startY += verticalSpacing;
    startX = getWidth() / 3;
    g2d.drawString("Bottoms", startX, startY);
    startY += labelOffset;
    for (Bottom bottom : bottoms) {
        bottom.draw(g2d, startX, startY, itemWidth, itemHeight);
        startX += horizontalSpacing;
    }

    // Draw dresses section
    startY += verticalSpacing;
    startX = getWidth() / 3;
    g2d.drawString("Dresses", startX, startY);
    startY += labelOffset;
    for (Dress dress : dresses) {
        dress.draw(g2d, startX, startY, itemWidth, itemHeight);
        startX += horizontalSpacing;
    }

    // Draw accessories section
    startY += verticalSpacing;
    startX = getWidth() / 3;
    g2d.drawString("Accessories", startX, startY);
    startY += labelOffset;
    for (Accessory accessory : accessories) {
        accessory.draw(g2d, startX, startY, itemWidth, itemHeight);
        startX += horizontalSpacing;
    }

    // Draw shoes section
    startY += verticalSpacing;
    startX = getWidth() / 3;
    g2d.drawString("Shoes", startX, startY);
    startY += labelOffset;
    for (Shoes shoe : shoes) {
        shoe.draw(g2d, startX, startY, itemWidth, itemHeight);
        startX += horizontalSpacing;
    }


    // Draw score
    g2d.drawString("Current Score: " + totalPoints, 50, getHeight() - 50);
    g2d.drawString(level +  " Points: " + calculateOutfitPoints(), 50, getHeight() - 30);


    // Add Save Result Button
    g2d.setColor(Color.GRAY);
    g2d.fillRect(getWidth()-getWidth()/7, getHeight()-getHeight()/7, 120, 40); // Position and size of the Save Result button
    g2d.setColor(Color.WHITE);
    g2d.drawString("Back", getWidth()-getWidth()/8, getHeight()-getHeight()/9); // Button label    

        // Add Back Button
    g2d.setColor(Color.GRAY);
    g2d.fillRect(getWidth()-getWidth()/7, getHeight()-getHeight()/4, 120, 40); // Position and size of the Another Function button
    g2d.setColor(Color.WHITE);
    g2d.drawString("Save Result", getWidth()-getWidth()/7, getHeight()-getHeight()/5); // Button label

    }

    public void drawResultsScreen(Graphics g2d) {
        //color is not changing to red
        setBackground(Color.RED);
        int fontSize = Math.min(getWidth(), getHeight()) / 20;
        g2d.setFont(new Font("Broadway", Font.BOLD, fontSize));
         
        String level1scorestring = "Level 1 : " + level1score;
        String level2scorestring = "Level 2 : " + level2score;
        String level3scorestring = "Level 3 : " + level3score;
        String level4scorestring = "Level 4 : " + level4score;
        String totalscorestring = "Total : " + totalscore;
        int verticalSpacing = getHeight() / 8;  //1
        g2d.drawString("Congratulation", getWidth()/2, getHeight()/10); // Congratulation
        g2d.drawString("You picked " + player.toString(), getWidth()/2, getHeight()/10+ (verticalSpacing));
        g2d.drawString(level1scorestring, getWidth()/2, getHeight()/10+ (2*verticalSpacing)); // Level 1 Score
        g2d.drawString(level2scorestring, getWidth()/2, getHeight()/10+(3*verticalSpacing)); // Level 2 Score
        g2d.drawString(level3scorestring, getWidth()/2, getHeight()/10+(4*verticalSpacing)); // Level 3 Score
        g2d.drawString(level4scorestring, getWidth()/2, getHeight()/10+(5*verticalSpacing)); // Level 4 Score
        g2d.drawString(totalscorestring, getWidth()/2, getHeight()/10+(6*verticalSpacing)); // Total Score

        g2d.drawString("Congratulation", getWidth()/2, getHeight()/10); // 



        g2d.drawImage(congrats.getImage(), 0, 0, getWidth()/2, getHeight(), this);
        //We need to add final score but its not working
        //And we need to add a finish or view score button
    
    }

    private void drawScreens(Graphics g2d) {
        switch (screen) {
            case "start":
                drawStartScreen(g2d);
                break;
            case "choose":
                drawChooseScreen(g2d);
                break;
            case "selection":
                drawSelectScreen(g2d);
                break;
            case "assignments":
                drawAssignmentScreen(g2d);
                break;
            case "result":
                drawResultsScreen(g2d);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        key = e.getKeyCode();

        //click a
        if (key == 65) {
            screen = "choose";
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        x = arg0.getX();  // Remove the offset
        y = arg0.getY();  // Remove the offset

        hoveredChar = null;
        for (Characters c : charList) {
            // Scale the detection area the same way as drawing
            int scaledX = c.getX() * getWidth() / 1100;
            int scaledY = c.getY() * getHeight() / 1100;
            int scaledWidth = getWidth() / 10;
            int scaledHeight = getHeight() / 10;

            if (x >= scaledX && x <= scaledX + scaledWidth &&
                y >= scaledY && y <= scaledY + scaledHeight) {
                hoveredChar = c;
                break;
            }
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        int x = arg0.getX();
        int y = arg0.getY();

        if (screen.equals("choose")) {
            // Scale the character positions the same way they're drawn
            for (Characters c : charList) {
                int scaledX = c.getX() * getWidth() / 1100;
                int scaledY = c.getY() * getHeight() / 1100;
                int scaledWidth = getWidth() / 10;
                int scaledHeight = getHeight() / 10;

                if (x >= scaledX && x <= scaledX + scaledWidth &&
                    y >= scaledY && y <= scaledY + scaledHeight) {
                    player = c;
                    screen = "selection";
                    break;
                }
            }
        }

        else if (screen.equals("selection")) {
            if (x >= levelimageStartX && x <= levelimageStartX + levelimageWidth &&
                y >= levelimageStartY-getHeight() / 20 && y <= levelimageStartY-getHeight() / 20 + levelimageHeight) {
                level = "level1";
                screen = "assignments";
            }
            else if (x >= levelimageStartX+levelimageWidth+ getWidth() / 15 && x <= levelimageStartX+levelimageWidth+ getWidth() / 15 + levelimageWidth &&
                y >= levelimageStartY-getHeight() / 20 && y <= levelimageStartY-getHeight() / 20 + levelimageHeight) {
                level = "level2";
                screen = "assignments";
            }
            else if (x >= levelimageStartX && x <= levelimageStartX+ levelimageWidth &&
                y >= levelimageStartY + levelimageHeight + getHeight() / 10 && y <= levelimageStartY + levelimageHeight + getHeight() / 10 + levelimageHeight) {
                level = "level3";
                screen = "assignments";
            }
            else if (x >= levelimageStartX+levelimageWidth+ getWidth() / 15 && x <= levelimageStartX+levelimageWidth+ getWidth() / 15 + levelimageWidth &&
                y >= levelimageStartY + levelimageHeight + getHeight() / 10 && y <= levelimageStartY + levelimageHeight + getHeight() / 10 + levelimageHeight) {
                level = "level4";
                screen = "assignments";
            }        
            else if(level1done==1 && level2done == 1 && level3done==1 && level4done == 1) {
                if (x >= getWidth()-getWidth()/4 && x <= getWidth() &&
                    y >= getHeight()-getHeight()/4 && y <= getHeight()-getHeight()/4 + getHeight()/10) {
                    screen = "result"; 
                }
            }    
        }

        else if (screen.equals("assignments")) {
            // Save file button check
            if (x >= getWidth()-getWidth()/7 && x <= getWidth()-getWidth()/7 + 120 &&
                y >= getHeight()-getHeight()/4 && y <= getHeight()-getHeight()/4 + 40) {
                saveDataToFile("test.txt", "Level " + level + "   Result :" + calculateOutfitPoints());
                if ("level1".equals(level)) {
                    level1score = calculateOutfitPoints();
                    level1done = 1;
                }
                else if ("level2".equals(level)) {
                    level2score = calculateOutfitPoints();
                    level2done = 1;
                }
                else if ("level3".equals(level)) {
                    level3score = calculateOutfitPoints();
                    level3done = 1;
                }
                else if ("level4".equals(level)) {
                    level4score = calculateOutfitPoints();
                    level4done = 1;
                    totalscore = level1score + level2score + level3score + level4score;
                    saveDataToFile("test.txt", "Total Score is " + totalscore + "!");
                }
                screen = "selection";
                currentOutfit.clear();
                return;
            }
            
            // Back button check - fixed coordinates
            if (x >= getWidth()-getWidth()/7 && x <= getWidth()-getWidth()/7 + 120 &&
                y >= getHeight()-getHeight()/7 && y <= getHeight()-getHeight()/7 + 40) {
                currentOutfit.clear();
                screen = "selection";
                return;
            }

            // Click handling for clothing items
            int startX = getWidth() / 3;
            int startY = (int)(getHeight() * 0.285);
            int itemWidth = getWidth() / 13;
            int itemHeight = getHeight() / 11;
            int horizontalSpacing = getWidth() / 11;
            int verticalSpacing = getHeight() / 10;
            int labelOffset = 25;

            // Check tops
            int currentY = startY + labelOffset;
            int currentX = startX;
            for (Top top : tops) {
                if (x >= currentX && x <= currentX + itemWidth &&
                    y >= currentY && y <= currentY + itemHeight) {
                    currentOutfit.put("top", top);
                    currentOutfit.remove("dress");
                    repaint();
                    return;
                }
                currentX += horizontalSpacing;
            }

            // Check bottoms
            currentY += verticalSpacing;
            currentX = startX;
            for (Bottom bottom : bottoms) {
                if (x >= currentX && x <= currentX + itemWidth &&
                    y >= currentY && y <= currentY + itemHeight) {
                    currentOutfit.put("bottom", bottom);
                    currentOutfit.remove("dress");
                    repaint();
                    return;
                }
                currentX += horizontalSpacing;
            }

            // Check dresses
            currentY += verticalSpacing;
            currentX = startX;
            for (Dress dress : dresses) {
                if (x >= currentX && x <= currentX + itemWidth &&
                    y >= currentY && y <= currentY + itemHeight) {
                    currentOutfit.put("dress", dress);
                    currentOutfit.remove("top");
                    currentOutfit.remove("bottom");
                    repaint();
                    return;
                }
                currentX += horizontalSpacing;
            }

            // Check accessories
            currentY += verticalSpacing;
            currentX = startX;
            for (Accessory accessory : accessories) {
                if (x >= currentX && x <= currentX + itemWidth &&
                    y >= currentY && y <= currentY + itemHeight) {
                    currentOutfit.put("accessory", accessory);
                    repaint();
                    return;
                }
                currentX += horizontalSpacing;
            }

            // Check shoes
            currentY += verticalSpacing;
            currentX = startX;
            for (Shoes shoe : shoes) {
                if (x >= currentX && x <= currentX + itemWidth &&
                    y >= currentY && y <= currentY + itemHeight) {
                    currentOutfit.put("shoes", shoe);
                    repaint();
                    return;
                }
                currentX += horizontalSpacing;
            }
        }
    }
	
    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }
}
