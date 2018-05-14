package Klax.KlaxGame;

import com.google.common.eventbus.*;

/**
 * @author Simon Anton Spitzer
 * GameStateLogic handles all the update it receives 
 * from the Gamelogic or GUI to keep NotifyGameState (gamestate) up-to-date
 */

public class GameStateLogic {


	private EventBus bus;
    
	private int misses = 0;
	private int palettePosition = 2;
	private int score = 0;
	private int topBrick = -1;
	private Brick brick = new Brick(2,2);
	private Brick [] paletteBlock = new Brick [3];
	private int well[][] = new int[5][5];
//	private static final int PAUSED = 1;
//	private static final int RUNNING = 2;

    public GameStateLogic(EventBus eb)
    {
        bus = eb;
        bus.register(this);        
    	
    	palettePosition = 2;
    }
    
    @Subscribe void updateChute(Chute e){
    	this.brick = e.getBlock();
    }
    
    @Subscribe void updatePalette (Palette e){
    	this.palettePosition = e.getPalettePosition();
    	this.topBrick = e.getTopBrick();
    	this.paletteBlock = e.getPaletteBlock();
    }

    @Subscribe void updateWell (Well e) {
    	this.well = e.getWell();
    } 
    
    @Subscribe void updateStatus (Status e){
    	this.score = e.getscore();
    	this.misses = e.getmisses();
    }
    
    @Subscribe public void updateGet(RequestGameState e)
    {
        notifyGameState();       
    }

    private void notifyGameState()
    {   
        bus.post(new NotifyGameState(misses, palettePosition, score, topBrick, brick, paletteBlock, well));  
    }
}	