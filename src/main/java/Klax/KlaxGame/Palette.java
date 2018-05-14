package Klax.KlaxGame;

/**
 * @author Simon Anton Spitzer
 * Palette allows the user to catch the bricks coming from the chute
 * and to drop them into the well
 */

public class Palette {
	
	private Brick[] paletteBlock;
	private int palettePosition;
	private int topBrick;
	
	public Palette (int palettePosition,int topBrick, Brick [] paletteBlock){
		this.palettePosition = palettePosition;
		this.topBrick = topBrick;
		this.paletteBlock = paletteBlock;
	}
	
	public int getPalettePosition() {
		return palettePosition;
	}

	public int getTopBrick() {
		return topBrick;
	}

	public Brick[] getPaletteBlock() {
		return paletteBlock;
	}

}
