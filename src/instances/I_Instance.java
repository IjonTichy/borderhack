package instances;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.ContextActivationException;
import org.jsfml.window.event.Event;

import render.FPSRenderer;
import render.I_Renderer;
import render.RenderThread;
import util.Constants;
import util.QueueSplitter;

/**
 * <p>A game instance. The game is handled through instances running one after the
 * other, each one having control of the game during its run. Instances, once they
 * finish or defer execution, return a new instance. Cleanup is expected to be 
 * done by the instance as it exits; it is not enforced, due to instances
 * potentially being used for things like entering a menu during gameplay.
 * Reconstructing the entire game instance just for entering and leaving a menu
 * is flat-out ridiculous.</p>
 * 
 * <p>Right now, the only thing an instance needs to do is define a run method,
 * which returns either a new instance, or null. Null is interpreted as "end
 * the game". More functions might be required later.</p>
 *
 */


abstract public class I_Instance
{
    protected RenderWindow i_window;
    protected RenderThread i_renderer;
    protected QueueSplitter<Event> i_eventTee;
    protected BlockingQueue<Event> i_eventsIn;
    protected BlockingQueue<Event> i_eventsForGame;
    protected BlockingQueue<Event> i_eventsForRenderer;
    
    protected void initVars(RenderWindow window, BlockingQueue<Event> events)
    {
        i_window            = window;
        i_eventsIn          = events;
        i_eventTee          = new QueueSplitter<Event>(events, 2, 1);
        i_eventsForGame     = i_eventTee.getQueue(0);
        i_eventsForRenderer = i_eventTee.getQueue(1);
                
        i_renderer          = new RenderThread(window, i_eventsForRenderer);
    }
    
    protected void initRun()
    {
        setActive(false);
        i_eventsIn.clear();
        i_renderer.start();
        i_eventTee.start();
    }
    
    protected void endRun()
    {
        i_renderer.end();
        i_eventTee.end();
    }
    
    protected void addRenderer(I_Renderer i)
    {
        i_renderer.addRenderer(i);
        i.attachRenderThread(i_renderer);
    }
    
    protected void addFPSCounter()
    {
        if (Constants.DRAW_FPS)
        {
            addRenderer(new FPSRenderer());
        }
    }
    
    private void setActive(boolean mode)
    {
        try { i_window.setActive(mode); }
        catch (ContextActivationException e) {}
    }
    
    /**
     * Run an instance.
     * @return the next I_Instance that should be run.
     */
    
    public I_Instance run()
    {
        ArrayList<Event> newEvents = new ArrayList<Event>();
        I_Instance nextInstance = null;
        
        initRun();
        
        while (i_window.isOpen())
        {
            newEvents.clear();

            for(Event event : i_eventsForGame)
            {
                switch (event.type)
                {
                  case CLOSED:
                    i_window.close();
                    break;
                  
                  default:
                    newEvents.add(event);
                    break;
                }
            }
            
            i_eventsForGame.clear();
            
            nextInstance = tick(newEvents);
        }
        
        endRun();
        
        return nextInstance;
    }
    
    /**
     * Instance tick. Basic event handling is already handled in the run loop;
     * this may concern itself only with its own behaviour.
     * It should not loop; it gets called periodically.
     * 
     * @param newEvents     New events to handle. Independent of the renderer's events.
     * 
     * @return an I_Instance, which is either null (to end the game), itself
     *          (to continue execution), or a new instance (to use instead)
     */
    abstract protected I_Instance tick(List<Event> newEvents);
}