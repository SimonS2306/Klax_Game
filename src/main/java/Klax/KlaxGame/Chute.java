package Klax.KlaxGame;

/**
 * @author Simon Anton Spitzer
 * The chute represent the top part of the game where all the bricks fall down.
 * It has a maximum of 10 fields and every bricks has to be caught with the palette.
 */
  
public class Chute {

	Brick brick;
	
	public Chute (Brick brick){
		
		this.brick = brick;
	}
	
	public Brick getBlock() {

		return brick;
	}

}
