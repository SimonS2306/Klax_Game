package Klax.KlaxGame;

/**
 * @author Simon Anton Spitzer
 * Accumulated storage of all game state values
 */

public class NotifyGameState {
		
	private int misses;
	private int palettePosition;
	private int score;
	private int topBrick;
	private Brick brick;
	private Brick[] paletteBlock;
	private int well [][];
	
	
    public NotifyGameState(int misses,int palettePosition,int score, int topBrick,Brick brick, Brick[] paletteBlock, int [][] well )
    {
    	
        this.misses = misses;
        this.palettePosition = palettePosition;
        this.score = score;
    	this.topBrick = topBrick;
    	this.brick = brick;
    	this.paletteBlock = paletteBlock;
    	this.well = well;

    }

	public int getmisses() { return misses;}
	public int getPalettePosition() { return palettePosition;}
	public int getscore() { return score;}
	public int gettopBrick() { return topBrick;}
	public Brick getBlock() { return brick;}
	public Brick[] getpaletteBlock() { return paletteBlock;}
	public int[][] getwell() { return well;}
	
	
}