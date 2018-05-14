package Klax.KlaxGame;
import com.google.common.eventbus.EventBus;

/**
 * @author Simon Anton Spitzer
 * Clock simulates ongoing time in the game and sends out Ticks 
 * every 300 milliseconds to initiate the beginning of a new round.
 */

public class Clock
{
    EventBus bus;

    public Clock(EventBus eb)
    {
        bus = eb;
    }

    public void start()
    {
        new Thread() {
            public void run() {
                while (true) {
                    try { Thread.sleep(300); }
                    catch (InterruptedException ie) {}
                    bus.post(new Tick());
                }
            }
        }.start();
    }                
}
