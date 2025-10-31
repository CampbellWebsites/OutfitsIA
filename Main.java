import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import other.Game;

public class Main extends JFrame{
	private static final int WINDOW_WIDTH;
	private static final int WINDOW_EIGHT;
    static {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WINDOW_WIDTH = (int) (screenSize.width ); // 100% of screen width
        WINDOW_EIGHT = (int) (screenSize.height ); // 100% of screen height
    }
	
	public Main () {
		super("Campbell's Fashion School Game");
		setSize(WINDOW_WIDTH, WINDOW_EIGHT);		
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure proper close behavior
		Game play = new Game();
		((Component) play).setFocusable(true);
				
		getContentPane().add(play);
		
		setVisible(true);

		addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				//play.createFile();
				//play.readFile();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				//play.writetoFile();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}

		});	

        // Resize the game content when the window size changes
        // Add the ComponentListener to the JFrame itself
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Adjust the game size based on the new window size
                play.adjustSize();
            }
        });


	}
	

	public static void main(String[] args) {
		Main instance = new Main();
		instance.setTitle("My Application");
		}

}
