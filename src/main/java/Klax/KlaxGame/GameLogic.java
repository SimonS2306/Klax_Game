package Klax.KlaxGame;

import java.awt.event.KeyEvent;
//import java.util.Date;
import java.util.Random;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * @author Simon Anton Spitzer
 * GameLogic takes care of of applying all Klax game rules
 */
public class GameLogic {
	
    private EventBus bus;

 	private static final int MAX = 10;
 	private static final int CAUGHT_BRICK_SCORE = 10;
 	private static final int VERTICAL_KLAX_SCORE = 50;
 	private static final int HORIZONTAL_KLAX_SCORE = 100;
 	private static final int DIAGONAL_KLAX_SCORE = 1000;
 	private static final int NUM_PADDLE_BRICKS = 3;
 	private static final int MAX_MISSES = 3;
 	private static final int EMPTY = 0;
 	private static final int NUM = 5;
//	private static final int PAUSED = 1;
//	private static final int RUNNING = 2;
//	private int situation;

 	private final Random rand = new Random();
 	
	private  int well[][];
	private Brick brick;
	private Brick[] paletteBlock;
	private int score;
	private int misses;
	private int palettePosition;
	private int topBrick;
	
    public GameLogic(EventBus eb) {
    	
        bus = eb;
        bus.register(this);
    }
    
    //Used to initialize all values for the game 

    public void start() {
    	
    	//situation = PAUSED;
        bus.post(new RequestGameState());
    }
 	
    //GameLogic Subscribe Methods
    
    @Subscribe public void handleTick (Tick e) {
    	
    	this.bus.post(new RequestGameState());
    	this.trytocatch();
    	this.bus.post(new RequestGameState());
    	this.checkforklax();
    }
    
    @Subscribe public void handleNotifyState(NotifyGameState e) {
    	
    	this.misses = e.getmisses();
    	this.palettePosition = e.getPalettePosition();
    	this.score = e.getscore();
    	this.topBrick = e.gettopBrick();
    	this.brick = e.getBlock();
        this.paletteBlock = e.getpaletteBlock();
        this.well = e.getwell();

    }

    @Subscribe void updateDrop(DropTheBrick e){
    	this.unloadPalette();
    }   
    
    /**
	 * Adds a miss to the misses count.
	 * (+)
	 */
	public void addMiss() {
		misses++;
	if (misses == MAX_MISSES){
			System.out.println("Gameover");;
	}
	}
    /**
	 * Checks if there is space on the paddle.
	 * (+)
	 * @return
	 */
	private boolean spaceOnPaddle() {
		if (topBrick < NUM_PADDLE_BRICKS - 1)
			return true;
		else
			return false;
	}
    
	/**
	 * Creates Brick of none is on the Chute, otherwise checks if Palette catches the Brick.
	 * Every change is reported posted on the bus so the GameStateLogic and GUI can update themselves.
	 * (+)
	 */
	private void trytocatch() {
		
		if (brick == null) {
			brick = new Brick(getRandomColumn(), getRandomColor());
		}
		if (brick.row < MAX && brick != null) {
			this.brick.row++;}
		else {
		if (brick.row == MAX) {
			if (palettePosition != brick.column) {
				addMiss();
			} else {
				if (spaceOnPaddle()) {
					
					// Palette catches the brick
					
					paletteBlock[++topBrick] = brick;
					score += CAUGHT_BRICK_SCORE;
					
				} else {
					addMiss();
				}
			}
			brick = new Brick(getRandomColumn(), getRandomColor());
			
		}}
		bus.post(new Chute(brick));
		bus.post(new Palette (palettePosition, topBrick, paletteBlock));
		bus.post(new Status (score, misses));
	}

	/**
	 * Checks the well for any vertical, horizontal or diagonal formations of 3 bricks with the same color.
	 * If found the involved bricks are removed and the remaining bricks drop down.
	 * (+)
	 */
	
    public void checkforklax() {
		// Check for upward diagonal Klax.
		for (int col = 0; col < NUM - 2; col++) {
			// Iterates over each column starting from the left, except the last
			// 2 columns

			for (int row = 2; row < NUM; row++) {
				// Iterates over each 3-brick diagonal set except the first 2
				// bricks

				if (well[col][row] == EMPTY) {
					continue;
				} else if ((well[col][row] == well[col + 1][row - 1])
						&& (well[col][row] == well[col + 2][row - 2])) {

					// 1. Remove those bricks.
					well[col][row] = EMPTY;
					well[col + 1][row - 1] = EMPTY;
					well[col + 2][row - 2] = EMPTY;

					// 2. Shift the upper bricks down.

					// Column 1
					for (int i = row - 1; i >= 0; i--) {
						well[col][i + 1] = well[col][i];
					}

					// Column 2
					for (int i = row - 2; i >= 0; i--) {
						well[col + 1][i + 1] = well[col + 1][i];
					}

					// Column 3
					for (int i = row - 3; i >= 0; i--) {
						well[col + 2][i + 1] = well[col + 2][i];
					}

					// 3. Update the score.
					score += DIAGONAL_KLAX_SCORE;
				}
			}
		}

		// Check for downward diagonal Klax.
		for (int col = 0; col < NUM - 2; col++) {
			// Iterates over each column starting from the left, except the last
			// 2 columns

			for (int row = 0; row < NUM - 2; row++) {
				// Iterates over each 3-brick diagonal set except the first 2
				// bricks

				if (well[col][row] == EMPTY) {
					continue;
				} else if ((well[col][row] == well[col + 1][row + 1])
						&& (well[col][row] == well[col + 2][row + 2])) {

					// 1. Remove those bricks.
					well[col][row] = EMPTY;
					well[col + 1][row + 1] = EMPTY;
					well[col + 2][row + 2] = EMPTY;

					// 2. Shift the upper bricks down.

					// Column 1
					for (int i = row - 1; i >= 0; i--) {
						well[col][i + 1] = well[col][i];
					}

					// Column 2
					for (int i = row; i >= 0; i--) {
						well[col + 1][i + 1] = well[col + 1][i];
					}

					// Column 3
					for (int i = row + 1; i >= 0; i--) {
						well[col + 2][i + 1] = well[col + 2][i];
					}

					// 3. Update the score.
					score += DIAGONAL_KLAX_SCORE;

				}
			}
		}

		// Check for horizontal Klax
		for (int row = NUM - 1; row >= 0; row--) {
			// Iterates over each row, starting from the bottom.

			for (int col = 0; col < NUM - 2; col++) {
				// Iterates over each 3-brick set starting from the left.

				if (well[col][row] == EMPTY) {
					continue;
				} else if ((well[col][row] == well[col + 1][row])
						&& (well[col][row] == well[col + 2][row])) {

					// 1. Remove those bricks.
					well[col][row] = EMPTY;
					well[col + 1][row] = EMPTY;
					well[col + 2][row] = EMPTY;

					// 2. Shift the upper bricks down.
					for (int i = row - 1; i >= 0; i--) {
						well[col][i + 1] = well[col][i];
						well[col + 1][i + 1] = well[col + 1][i];
						well[col + 2][i + 1] = well[col + 2][i];
					}

					// 3. Update the score.
					score += HORIZONTAL_KLAX_SCORE;
				}
			}
		}

		// Check for vertical Klax
		for (int col = 0; col < NUM; col++) {
			// Iterates over each column.

			for (int row = 0; row < NUM - 2; row++) {
				// Iterates over each 3-brick set starting from the top.

				if (well[col][row] == EMPTY) {
					continue;
				} else if ((well[col][row] == well[col][row + 1])
						&& (well[col][row] == well[col][row + 2])) {

					// 1. Remove those bricks
					well[col][row] = EMPTY;
					well[col][row + 1] = EMPTY;
					well[col][row + 2] = EMPTY;

					// 2. Shift the upper bricks down.
					for (int i = row - 1; i >= 0; i--) {
						well[col][i + 3] = well[col][i];
						well[col][i] = EMPTY;
					}

					// 3. Update the score.
					score += VERTICAL_KLAX_SCORE;
					
				}
			}
		}
		bus.post(new Status (score, misses));
		bus.post(new Well (well));
	}
    
	
	 // Unload the top most brick from the paddle.
    // If the well is full on the spot the user wants to drop the brick with the palette, the game is over
    
	public void unloadPalette() {
		if (topBrick >= 0 && paletteBlock[topBrick] != null) {
			int i = 4;
			for (i = 4; i >= 0; i--) {
				if (well[palettePosition][i] == EMPTY) {
					well[palettePosition][i] = paletteBlock[topBrick].color;
					paletteBlock[topBrick] = null;
					topBrick--;
					break;
				}
			}
			if (i < 0) {
				//gameOver("Stack Full. Game Over!");
			}
		}
		bus.post(new Palette (palettePosition, topBrick, paletteBlock));
		bus.post(new Well(well));
	}

	
	/**
	 * Gets random column number from 0 to 4.
	 * (+)
	 * @return Column number.
	 */
	private int getRandomColumn() {
		return rand.nextInt(5);
	}

	/**
	 * Gets random color from 1 to 4.
	 * (+)
	 * @return Random color number.
	 */
	private int getRandomColor() {
		// Gets colors from 1 to 4.
		return rand.nextInt(4) + 1;
	}

	public void keyPressed(KeyEvent arg0) {
		// Unused
		
	}

	public void keyReleased(KeyEvent arg0) {
		// Unused
		
	}
    
	}

	