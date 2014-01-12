package instances;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    
    abstract public I_Instance run();
    
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
    
}