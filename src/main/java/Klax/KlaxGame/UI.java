package Klax.KlaxGame;

import java.applet.Applet;
import java.util.Timer;
import java.util.Date;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Game KLAX with Google Gueva Eventbus Architecture
 * by Simon Anton Spitzer
 * 
 * UI User Interface class to handle all user input and paint the UI
 * All parts marked with an (+) are adapted versions of Rushal Ajmera's Java implementation of this game
 * Available under: https://github.com/rushalajmera/Klax/blob/master/Klax/src/com/ritzsoftec/klax/Klax.java
 * 
 * Known bugs/prototype functionality:
 * Start/Pause functionality not implemented
 * Exit functionality that kills all threads not implemented; this version displays an exit message but the game continues in the background
 * Gameover functionality only shows "Gameover" in the console as soon as the maximum of allowed misses is reached, but it doesn't actively kill the game
 * Let's update some stuff
 */


public class UI extends Applet implements KeyListener {
	
	private static final long serialVersionUID = -87335033652168297L;
	
	EventBus eb = new EventBus();
    private EventBus bus;

    public UI ()
    {
     bus = eb;
     this.bus.register(this);
    }
	
 	public static final int PAUSED = 1;
 	public static final int RUNNING = 2;
	private int situation;
 	private Graphics g;
 	private Timer timer;
 	private Date startTime = null;
 	private long totalTime = 0;

	private int misses;
	private int palettePosition;
	private int score;
	private int topBrick;
	private Brick brick;
	private Brick [] paletteBlock;
	private int well[][];

 	@Override
 	public void init() {
 		
 		bus.register(this);
 		addKeyListener(this); 
 		this.setSize(APPLET_WIDTH, APPLET_HEIGHT);
        new GameStateLogic(bus);  
        new GameLogic(bus).start();
        bus.post(new RequestGameState());
        new Clock(bus).start();
 		palettePosition = 2;
 		situation = PAUSED;
 		
 		//Random initial bricks in the well

 		well[0][4] = 1;
 		well[0][3] = 1;
 		well[1][3] = 2;
 		well[1][4] = 3;
 		well[2][4] = 4;
 		well[4][4] = 1;
 		
 		repaint();
 		
 	}

    public void start()
    {
        new Thread() {
            public void run() { 
            	
            	startTime = new Date();
            	situation = RUNNING;
            	timer = new Timer(true);
            	bus.post(new RequestGameState()); 
            	repaint();
            	
            }
         
        }.start();
    }
    
 	@Override
 	public void stop() {
 		timer.cancel();
 	}

 	/**
 	 * Paint Method that gets called with 'repaint()' everytime a change happens in the game state values
 	 * (+)
 	 */
 	
 	@Override
 	public void paint(Graphics g) {
 		this.g = g;
 		g.setColor(Color.BLACK);

    	drawBackground();
    	drawFallingBrick(brick);
    	drawPaddle(palettePosition, paletteBlock, topBrick);
    	drawStackBricks(well);
    	drawScore(score);
    	drawMiss(misses);
    	drawTime();
    	drawMessage();
 	}
 	
    @Subscribe public void handleNotifyState(NotifyGameState e)
    {
    	
    	//Use the received Gamestate Values to print the GUI new
    	
    	this.misses = e.getmisses();
    	this.palettePosition = e.getPalettePosition();
    	this.score = e.getscore();
    	this.topBrick = e.gettopBrick();
    	this.brick = e.getBlock();
        this.paletteBlock = e.getpaletteBlock();
        this.well = e.getwell();
        repaint();
    	
    }
    
    @Subscribe void updateChute(Chute e){
    	this.brick = e.getBlock();
    			repaint();
    }
    
    @Subscribe void updatePalette (Palette e){
    	this.palettePosition = e.getPalettePosition();
    	this.topBrick = e.getTopBrick();
    	this.paletteBlock = e.getPaletteBlock();
    	repaint();
    }
    
    @Subscribe void updateStatus (Status e){
    	this.score = e.getscore();
    	this.misses = e.getmisses();
    	repaint();
    }
    
    @Subscribe void updateWell (Well e) {
    	this.well = e.getWell();
    	repaint();
    }

	/**
	 * Checks the current position of a falling brick and draws it at the
	 * correct position.
	 * (+)
	 */
    
	private void drawFallingBrick(Brick brick) {

		g.setColor(findColor(brick.color));
		g.fillRect(PATH_X + brick.column * BRICK_WIDTH + 1, PATH_Y + brick.row
				* BRICK_HEIGHT + 1, BRICK_WIDTH - 1, BRICK_HEIGHT - 1);
		g.setColor(Color.BLACK);
	}


	/**
	 * Method to redraw the stacks according to the bricks contained.
	 * (+)
	 */
	
	private void drawStackBricks(int[][] w) {
		int len = 5;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				if (w[i][j] != EMPTY) {
					g.setColor(findColor(w[i][j]));
					g.fillRect(STACK_X + i * BRICK_WIDTH + 1, STACK_Y + j
							* BRICK_HEIGHT + 1, BRICK_WIDTH - 1,
							BRICK_HEIGHT - 1);
				}
			}
		}
		g.setColor(Color.BLACK);
	}

	/**
	 * Draws the paddle at its current position.
	 * (+)
	 */
	
	private void drawPaddle(int p, Brick[] pB, int t ) {
		g.fillRect(PADDLE_X + p * BRICK_WIDTH, PADDLE_Y,
				PADDLE_WIDTH, PADDLE_HEIGHT);

		int i = t;
		while (i >= 0) {
			if (pB[i] != null) {
				g.setColor(findColor(pB[i].color));
				g.fillRect(PADDLE_X + p * BRICK_WIDTH, PADDLE_Y
						- (i + 1) * 25, BRICK_WIDTH, BRICK_HEIGHT);
			}
			i--;
		}
		g.setColor(Color.BLACK);
	}

	/**
	 * Draws the score at the top left corner.
	 * (+)
	 */
	
	private void drawScore(int s) {
		g.drawString("Score: " + s, SCORE_X, SCORE_Y);
	}

	/**
	 * Draws the misses at the top right corner.
	 * (+)
	 */
	
	private void drawMiss(int m) {
		g.drawString("Misses: " + m, MISSES_X, MISSES_Y);
	}

	/**
	 * Draws the time elapsed at the bottom right corner.
	 * (+)
	 */
	private void drawTime() {
		Date currentTime = new Date();
		long total = 0;
		if (situation == PAUSED)
			total = totalTime;
		else
			total = totalTime + currentTime.getTime() - startTime.getTime();
		total /= 1000;
		int hours = (int) (total / (60 * 60));
		total = total % (60 * 60);
		int minutes = (int) (total / 60);
		total = total % 60;
		int seconds = (int) total;

		String time = hours + ":" + minutes + ":" + seconds;
		g.drawString("Time: " + time, TIME_X, TIME_Y);
	}

	/**
	 * Draws a help message to start / pause.
	 * (+)
	 */
	 private void drawMessage() {
	 	g.drawString("A/D for left/right, Spacebar = Drop.", 230,
	 			100);
	 }

	/**
	 * Displays a game over message.
	 * (+)
	 * @param msg
	 *            The message to be displayed.
	 */
	void gameOver(String msg) {
		timer.cancel();
		situation = PAUSED;
		msg += "\n" + "Your Score: " + score + ".";
		JOptionPane.showMessageDialog(this, msg);
		System.exit(0);
	}
	
	/**
	 * EventHandling for user keyboard input
	 */
	
	public void keyTyped(KeyEvent e) {
		char key = e.getKeyChar();

		if (situation == PAUSED) {
			if (key == '\n') {
				situation = RUNNING;
				startTime = new Date();
				repaint();
			} else if (key == '') {
				gameOver("Game exited.");
				repaint();
			}
		} else if (situation == RUNNING) {
			if (key == 'a' || key == 'A') {
				bus.post(new Palette (moveLeft(), topBrick, paletteBlock));
				bus.post(new RequestGameState());
				repaint();
			} else if (key == 'd' || key == 'D') {
				bus.post(new Palette (moveRight(), topBrick, paletteBlock));
				bus.post(new RequestGameState());
				repaint();
			} else if (key == ' ') {
				bus.post(new DropTheBrick());
				bus.post(new RequestGameState());
				repaint();
			} else if (key == '\n') {
				if (situation == PAUSED) {
					situation = RUNNING;
					startTime = new Date();
				} else {
					situation = PAUSED;
					totalTime += new Date().getTime() - startTime.getTime();
				}
				repaint();
			} else if (key == '') {
				gameOver("Game exited.");
				repaint();
			}
		}
		e.consume();
	}

	public void keyPressed(KeyEvent arg0) {}
	public void keyReleased(KeyEvent arg0) {}		
	
	 // Initialization of  UI layout

 	private static final int APPLET_WIDTH = 800; //Later size of Applet
 	private static final int APPLET_HEIGHT = 600;
 	
 	private static final int BRICK_WIDTH = 50; //Size of one brick
 	private static final int BRICK_HEIGHT = 20;
 	
 	private static final int STACK_X = 250;
 	private static final int STACK_Y = 480;
 	
 	private static final int PATH_X = 250;
 	private static final int PATH_Y = 150;
 	
 	private static final int PADDLE_X = 250;
 	private static final int PADDLE_Y = 450;
 	
 	private static final int PATH_HEIGHT = 200;
 	private static final int PATH_BRICKS = 10;
 	
 	private static final int PADDLE_HEIGHT = 10; //Size of paddle
 	private static final int PADDLE_WIDTH = BRICK_WIDTH;
 	
 	private static final int SCORE_X = 10;
 	private static final int SCORE_Y = 20;	
 	
 	private static final int MISSES_X = APPLET_WIDTH - 100;
 	private static final int MISSES_Y = 20;
 	
	private static final int TIME_X = APPLET_WIDTH - 150;
	private static final int TIME_Y = APPLET_HEIGHT;


 	private static final int EMPTY = 0;
	private static final int NUM = 5;
	
	/**
	 * Draws the background of the game. i.e. the stacks and the paths.
	 * (+)
	 */
	private void drawBackground() {
		g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		g.drawString("KLAX", 350, 50);

		// stacks
		for (int i = 0; i <= NUM; i++) {
			g.drawLine(STACK_X, STACK_Y + i * BRICK_HEIGHT, STACK_X + 250,
					STACK_Y + i * BRICK_HEIGHT);
		}

		for (int i = 0; i <= NUM; i++) {
			g.drawLine(STACK_X + i * BRICK_WIDTH, STACK_Y, STACK_X + i
					* BRICK_WIDTH, STACK_Y + 100);
		}

		// paths
		for (int i = 0; i <= NUM; i++) {
			g.drawLine(PATH_X + i * BRICK_WIDTH, PATH_Y, PATH_X + i
					* BRICK_WIDTH, PATH_Y + PATH_HEIGHT);
		}

		g.setColor(Color.LIGHT_GRAY);
		for (int i = 0; i <= PATH_BRICKS; i++) {
			g.drawLine(PATH_X, PATH_Y + i * BRICK_HEIGHT, PATH_X + 250, PATH_Y
					+ i * BRICK_HEIGHT);
		}
		g.setColor(Color.BLACK);
	}

	/**
	 * Converts int representation of color to Color object.
	 * (+)
	 */
	
	private Color findColor(int color) {
		switch (color) {
		case 1:
			return Color.RED;
		case 2:
			return Color.GREEN;
		case 3:
			return Color.BLUE;
		case 4:
			return Color.YELLOW;
		default:
			return Color.BLACK;
		}
	}

	/**
	 * Moves the paddle to the left.
	 * (+)
	 */
	
	public int moveLeft() { 
		if (palettePosition == 0){
			palettePosition = 0;}
		else{
			palettePosition--;}
		
		return palettePosition;
	}

	/**
	 * Moves the paddle to the right.
	 * (+)
	 */
	
	public int moveRight() { 
		if (palettePosition == 4){
			palettePosition = 4;}
		else {
			palettePosition++;}
		
		return palettePosition;
	}	
}