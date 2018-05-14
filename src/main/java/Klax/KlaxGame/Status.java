package Klax.KlaxGame;

/**
 * @author Simon Anton Spitzer
 * Represents the game state that mainly matters to the user regarding his score and misses
 * Both values are displayed at the UI as an orientation how well the user is playing
 */

public class Status {
	
	private int score;
	private int misses;
	
	public Status (int score, int misses){
		
		this.score = score;
		this.misses = misses;
	}

	public int getscore() { return score;}
	public int getmisses() { return misses;}
}
