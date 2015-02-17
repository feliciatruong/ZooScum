package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * Class MainEnvironment - creates a JFrame environment to display a menu to
 * 							the user to play game, read instructions, or exit
 * 
 * @author	Jennifer Cryan
 * @author 	Jessica Huang
 * @author 	Jazarie Thach
 * @author 	Felica Truong
 * @author 	Josephine Vo
 * @version for CS48, Winter 2015, UCSB
 */
class MainEnvironment extends JFrame implements ActionListener {
	int			maxX;				// max horizontal window bounds
	int			maxY;				// max vertical window bounds
    JFrame 		menuFrame;			// main frame for main menu options
    JLayeredPane menuPane;			// layered pane for different options
    //JFrame 	instructionsFrame;
    JPanel  	menuPanel;			// panel to display menu screen
    JPanel		buttonsPanel;		// panel to display buttons
    JPanel 		instructionsPanel;	// panel to display instructions
    JPanel		backPanel;			// panel to display buttons to go back
    JEditorPane instructionsPane; 	// pane to display text instructions
    JButton 	Play;				// button to play game
    JButton 	Instructions;		// button to read instructions
    JButton 	Exit;				// button to exit program
    JButton 	Back;				// button to return to main menu
    URL			instructionsURL;	// URL to retrieve instructions text
    URL 		menuBackgroundURL;	// URL to retrieve background image
    Image 		menuImg;			// image icon for background image
    {
    	maxX				= 1024;
    	maxY				= 768;
    	menuFrame 			= new JFrame("Main Menu");
    	menuPane			= new JLayeredPane();
    	menuPanel			= new JPanel();
    	buttonsPanel 		= new JPanel();
    	instructionsPanel 	= new JPanel();
    	backPanel			= new JPanel();
    	Play 				= new JButton("Play Game");
    	Instructions 		= new JButton("How to Play");
    	Exit 				= new JButton("Exit");
    	Back				= new JButton("Back to Menu");
    	instructionsPane	= new JEditorPane();
    	instructionsURL		= getClass().getClassLoader()
    							.getResource("/resources/instructions.txt");
    	menuBackgroundURL 	= getClass().getResource("/resources/madagascar_1672003c.jpg");
    	//menuImg 			= new ImageIcon(menuBackgroundURL).getImage();
    }
    
    // Initialize program to display main menu on screen
    public static void main(String [] args) {
       	MainEnvironment mainMenu = new MainEnvironment();
        mainMenu.DisplayMenu();
    }// main
    
    
    /**
     * Method DisplayMenu - sets up frame, pane and panels to display menu options
     */
    public void DisplayMenu() {
    	//TODO make buttons pretty w/ button panel overlay background
    	
    	// init panels sizes, visibility
        buttonsPanel.setPreferredSize(new Dimension(200, 200));
        buttonsPanel.setMinimumSize(new Dimension(200, 200));
        buttonsPanel.setOpaque(true);
        instructionsPanel.setPreferredSize(new Dimension(500, 500));
        instructionsPanel.setMinimumSize(new Dimension(400, 400));
        instructionsPanel.setOpaque(false);
        backPanel.setPreferredSize(new Dimension(100, 70));
        backPanel.setOpaque(false);
        
        // init buttons sizes, listeners, backgrounds
        Play.setPreferredSize(new Dimension(100, 50));
        Play.addActionListener(this);
		Play.setBackground(Color.GREEN);	
        Instructions.setPreferredSize(new Dimension(100, 50));
        Instructions.addActionListener(this);
		Instructions.setBackground(Color.GREEN);
        Exit.setPreferredSize(new Dimension(100, 50));  
        Exit.addActionListener(this);
		Exit.setBackground(Color.GREEN);		
		Back.setPreferredSize(new Dimension(100, 50));
        Back.addActionListener(this);
		Back.setBackground(Color.GREEN);

		// add buttons to panel
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
		buttonsPanel.add(Play);
		buttonsPanel.add(Instructions);
		buttonsPanel.add(Exit);
		buttonsPanel.setVisible(true);
        
		// add text & button to instructions panel
		try {
			instructionsPane.setPage(instructionsURL);
		} catch(IOException e) { 
			e.printStackTrace();
			System.exit(1);
		}
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        instructionsPanel.add(instructionsPane);
        instructionsPanel.add(Back);
        instructionsPanel.setVisible(false);
        menuPanel.setVisible(true);
        
     // add panel components to layered frame
        menuPane.add(menuPanel, JLayeredPane.DEFAULT_LAYER);
        menuPane.add(buttonsPanel, JLayeredPane.PALETTE_LAYER);
        menuPane.add(instructionsPanel, JLayeredPane.POPUP_LAYER);
        menuPane.setVisible(true);
        
     // add layered pane to frame
        menuFrame.setFocusable(true);
        menuFrame.getContentPane().add(BorderLayout.CENTER, menuPane);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setMinimumSize(new Dimension(maxX, maxY));
        menuFrame.setSize(maxX, maxY);
        menuFrame.setVisible(true);
        
    }// DisplayMenu
    
    
    /**
     * Method MenuPanel - draws background image on menu panel
     */
	class MenuPanel extends JPanel {
		public void paintComponent(Graphics g) {
			// TODO make work with background image
			try {
				menuImg = new ImageIcon(menuBackgroundURL).getImage();
			} catch(Exception e) { 
				e.printStackTrace();
				System.exit(1);
			}
		
		    super.paintComponent(g);
		    g.drawImage(menuImg, 0, 0, this);  
	    }// paintComponent
	}// MenuPanel (JPanel)
	
	/**
	 * Override actionPerformed - sets actions when menu buttons are pressed
	 */
    @Override
    public void actionPerformed(ActionEvent event) {
        if(event.getSource() == Play) {
            dispose();
            GameEnvironment game = new GameEnvironment();
        }
        if(event.getSource() == Instructions) {
        	menuPane.moveToBack(buttonsPanel);
        	buttonsPanel.setOpaque(false);
        	instructionsPanel.setVisible(true);
        	instructionsPanel.setOpaque(true);
        	menuPane.moveToFront(instructionsPanel);
        }
        if(event.getSource() == Exit) {
        	dispose();
            System.exit(0);
        }
        if(event.getSource() == Back) {
        	menuPane.moveToBack(instructionsPanel);
        	menuPane.moveToFront(buttonsPanel);
        	buttonsPanel.setOpaque(true);
        	instructionsPanel.setOpaque(false);
        	
        }
    }// actionPerformed  
}// MainEnvironment

    