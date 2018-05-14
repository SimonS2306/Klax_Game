package Klax.KlaxGame;

/**
 * @author Simon Anton Spitzer
 * Brick represent a tile in the game which falls down from the chute.
 * Gets initialized every time with the row 0 to start at the top of the chute.
 * Column and color get random values.
 */


public class Brick {
	
	int row;
	int column; 
	int color;

	public Brick(int column, int color) {

		this.row = 0;
		this.column = column;
		this.color = color;
	}
	
}