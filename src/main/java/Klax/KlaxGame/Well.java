package Klax.KlaxGame;

/**
 * @author Simon Anton Spitzer
 * Well represents the stack where all the bricks are dropped
 */

public class Well {
	
 	private static final int NUM = 5;
	private int well [][]  = new int[NUM][NUM];

	public Well (int[][] well){
		
		this.well = well;
	}
	
	public int[][] getWell() { return well;}

}
