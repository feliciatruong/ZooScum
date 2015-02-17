package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Class GameEnvironment - creates a JFrame environment to animate zoo animals 
 * 							and allows for a train to move around and collect
 * 							the animals to return to the zoo. Tracks number
 * 							of animals dropped off in a given amount of time.
 * 
 * @author	Jennifer Cryan
 * @author 	Jessica Huang
 * @author 	Jazarie Thach
 * @author 	Felica Truong
 * @author 	Josephine Vo
 * @version for CS48, Winter 2015, UCSB
 */
public class GameEnvironment extends JFrame {
	private final int 	DELAY = 20;		// time delay for animation thread

	Thread 				game;			// thread for game animation
	GamePanel 			gamePanel;		// main panel for game animation
	JFrame 				gameFrame;		// main frame for game panels/buttons
	private long 		timeStart;		// marks starting time
	private long 		timeEnd;		// marks ending time
	private long 		timePaused;		// stores amount of time paused
	private long 		pausetimeStart;	// marks start of pause time
	private long 		elapsedTime;	// elapsed playing time
	int 				maxX;			// max horizontal window bounds
	int 				maxY;			// max vertical window bounds
	int 				numAnimals;		// starting number of escaped animals
	private Image		trainImg;		// image icon for train image
	private Image		animalImg;		// image icon for animal image
	private boolean 	gameover;		// check for when game ends
	private int 		points;			// counts number of points earned
	Train 				train;			// train object
	ArrayList<Animal> 	animalArray;	// array of animals on screen
	{
		gamePanel 		= new GamePanel();
		gameFrame 		= new JFrame();
		timeStart 		= System.nanoTime() / 1000000000;
		timeEnd			= 0;
		timePaused		= 0;
		pausetimeStart  = 0;
		elapsedTime		= 0;
		maxX 			= 1024;
		maxY 			= 768;
		numAnimals 		= 3;
		gameover 		= false;
		points 			= 0;
		train 			= new Train();
		animalArray 	= new ArrayList<Animal>();
	}

	/**
	 * Method AddNewBoardAnimal - adds Animal to ArrayList and gives it an 
	 * 						 	  initial position on the board
	 */
	private void AddNewBoardAnimal() {
		Animal a = new Animal();
		a.setX((int) Math.round(Math.random() * maxX));
		a.setY((int) Math.round(Math.random() * maxY));
		animalArray.add(a);
	}// AddNewBoardAnimal

	/**
	 * Constructor GameEnvironment - creates JFrame and JPanel for game 
	 * 								 animation environment with a menu
	 */
	public GameEnvironment() {
		for (int i = 0; i < numAnimals; i++) {
			AddNewBoardAnimal();
		}
		train.getTA().add(new Animal()); // TODO check why adding animal to train

		// start new game animation and set up frame to display menu
		game = new Game();
		game.start();
		gameFrame.setFocusable(true);
		gameFrame.getContentPane().add(BorderLayout.CENTER, gamePanel);
		gameFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		gameFrame.setSize(maxX, maxY);
		gameFrame.setVisible(true);
		gamePanel.requestFocus();
		GameMenu gameMenu = new GameMenu();
	}// GameEnvironment

	/**
	 * Innerclass GamePanel - for JPanel animation, creates background image
	 * 						  and adds zoo, animals, and train to screen along 
	 * 						  with a timer and score output
	 */
	class GamePanel extends JPanel {
		/**
		 * Constructor GamePanel - adds keyboard listener and icons to panel
		 */
		public GamePanel() {
			KeyHandler handler = new KeyHandler();
			addKeyListener(handler);
			Icons();
		}// GamePanel

		// paint icons and text on screen
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			ShowIcons(g);
			ShowText(g);
		}// paintComponent

		/**
		 * Method Icons - load icon images from file 
		 */
		public void Icons() {
			// TODO check images, read from sprite pages?
			URL trainURL = getClass().getResource("/resources/a6c.png");
			URL animalURL = getClass().getResource("/resources/imgres.png");
			
			try {
				trainImg = new ImageIcon(trainURL).getImage();
				animalImg = new ImageIcon(animalURL).getImage();
			} catch(Exception e) { 
				e.printStackTrace();
				System.exit(1);
			}
		}// Icons

		/**
		 * Method ShowIcons - paint image icons onto screen for animation
		 * 		  @param g	- Graphics object to draw images on screen
		 */
		public void ShowIcons(Graphics g) {
			if (gameover == false) {
				for (int i = 0; i < 3; i++) {
					g.drawImage(animalImg, animalArray.get(i).getX(),
							animalArray.get(i).getY(), this);
				}
				g.drawImage(trainImg, train.getX(), train.getY(), this);
				for (int i = 0; i < train.getTA().size(); i++) {
					train.getTA().get(i).setY(train.getY());
					g.drawImage(animalImg, train.getTA().get(i).getX(), train
							.getTA().get(i).getY(), this);
				}
				Toolkit.getDefaultToolkit().sync();
			}
		}// ShowIcons

		/**
		 * Method ShowText  - paint text onto screen for timer and score
		 * 		  @param g	- Graphics object to draw text on screen
		 */
		public void ShowText(Graphics g) {
			g.setFont(new Font("Cracked", Font.PLAIN, 24));
			g.setColor(Color.BLACK);
			String score = "Score: " + points;
			g.drawString(score, 0, 5);
			// TODO may want to change to time remaining
			String elapsed = "Time elapsed: " + elapsedTime;
			g.drawString(elapsed, 0, 35);
		}// ShowText
	}// GamePanel (JPanel)

	/**
	 * Innerclass Game - thread to animate game on screen with moving icons
	 */
	class Game extends Thread {

		/**
		 * Constructor Game - starts game animation and checks for interruptions
		 */
		public Game() {
			try {
				while (true) {
					GameLogic(DELAY);
				}
			} catch (Exception e) {
				if (e instanceof InterruptedException) {
				} else {
					System.out.println(e);
					System.exit(1);
				}
			}
		}// Game

		/**
		 * Method GameLogic - determines what to do as train moves around screen
		 * 		@param pauseDelay
		 * 		@throws InterruptedException
		 */
		void GameLogic(int pauseDelay) throws InterruptedException {
			if(!gameover) {
	
				// checks if train crosses paths with animals
				for(int i = 0; i < animalArray.size(); i++) {
					if((train.getX() == animalArray.get(i).getX()) && 
							(train.getY() == animalArray.get(i).getY())) {
						train.getTA().add(train.getTA().size(), animalArray.get(i));
						animalArray.remove(i);
					}
				}
				
				// checks if train crosses paths with tail & stops game
				for(int i = 0; i < train.getTA().size(); i++) {
					if((train.getX() == train.getTA().get(i).getX()) && 
							(train.getY() == train.getTA().get(i).getY())) {
						gameover = true;
					}
				}
				
				// TODO **** increment points after dropping animals off ****
				// checks if train crosses paths with zoo and clears tailArray
				if(train.getX() == 0 || train.getY() == 0) {
					train.getTA().clear();
				}
				
				// checks if train goes beyond screen boundaries 
				if(train.getX() > maxX || train.getX() < 0 || 
						train.getY() > maxY || train.getY() < 0) {
					gameover = true;
				}
			}
			
			if(!gameover) {
				timeEnd = System.nanoTime() / 1000000000 - timePaused;
				elapsedTime = (int)(timeEnd - timeStart); /* + previous load time */
			}
        		
			train.Move();
			gamePanel.repaint();
			
			if(Thread.currentThread().interrupted()) {
				throw(new InterruptedException());
			}
			Thread.currentThread().sleep(DELAY);
		} // GameLogic
	}// Game (Thread)

	
	/**
	 * Class KeyHandler - handles keyboard events to move train up, down, left,
	 * 					  or right based on user input
	 */
	public class KeyHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if ((key == KeyEvent.VK_LEFT) && (!train.getRight())) {
				train.setLeft(true);
				train.setUp(false);
				train.setDown(false);
			}
			if ((key == KeyEvent.VK_RIGHT) && (!train.getLeft())) {
				train.setRight(true);
				train.setUp(false);
				train.setDown(false);
			}
			if ((key == KeyEvent.VK_UP) && (!train.getDown())) {
				train.setUp(true);
				train.setRight(false);
				train.setLeft(false);
			}
			if ((key == KeyEvent.VK_DOWN) && (!train.getUp())) {
				train.setDown(true);
				train.setRight(false);
				train.setLeft(false);
			}
		} // keyPressed
	} // KeyHandler (KeyAdapter)


	/**
	 * Class GameMenu - displays in-game menu with options to pause or exit game
	 */
	class GameMenu implements ActionListener {
		JButton Pause = new JButton("Pause");
		JButton Exit = new JButton("Exit");

		/**
		 * Constructor GameMenu - initializes game panel with buttons across bottom
		 */
		public GameMenu() {
			Pause.addActionListener(this);
			Pause.setBackground(Color.GREEN);
			//Pause.setBorderPainted(false);
			Pause.setOpaque(true);
			Exit.addActionListener(this);
			Exit.setBackground(Color.GREEN);
			//Exit.setBorderPainted(false);
			Exit.setOpaque(true);


			JPanel gameButtons = new JPanel(new BorderLayout());
			gameFrame.getContentPane().add(BorderLayout.SOUTH, gameButtons);
			gameButtons.add(BorderLayout.EAST, Pause);
			// gameButtons.add(BorderLayout.CENTER, Save);
			gameButtons.add(BorderLayout.WEST, Exit);
			gameFrame.setVisible(true);
		}// GameMenu

		/**
		 * Override actionPerformed - sets actions for when buttons are pressed
		 */
		@Override
		public void actionPerformed(ActionEvent buttonPress) {
			if (buttonPress.getSource() == Exit) {
				System.exit(0);
			}
			// TODO pull up dialogue for user options
			if (buttonPress.getSource() == Pause) {
				if (gameover == false) {
					gameover = true;
					pausetimeStart = System.nanoTime() / 1000000000;
				} else {
					gameover = false;
					timePaused += (System.nanoTime() / 1000000000 - pausetimeStart);
				}
			}
		}// actionPerformed
	}// GameMenu (ActionListener)
	
	// Initialize program to display main menu on screen
    public static void main(String [] args) {
       	GameEnvironment gameEnv = new GameEnvironment();
    }// main
    
    
}// GameEnvironment (Class)
