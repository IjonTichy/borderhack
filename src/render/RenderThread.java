package render;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Clock;
import org.jsfml.window.event.Event;

/**
 * <p>A thread for rendering. Itself, it does no rendering; instead, it is given
 * various objects implementing I_Renderer, which, when asked to, will attempt
 * to render whatever it is they're meant to render. Every Instance typically
 * has their own RenderThread; it is started when a Game runs the Instance,
 * and stopped when the Instance returns.</p>
 * 
 * <p><b>NOTE:</b> The queue passed to the RenderThread should NOT be the one
 *      coming from the event thread. It should be a queue populated with new
 *      events from said event thread, but it should not be that very queue
 *      from the event thread.</p>
 */

public class RenderThread extends Thread
{
    private boolean             keep_rendering;
    private long                r_tick;
    
    private RenderWindow        r_window;
    private List<I_Renderer>    r_renderers;
    private Queue<Event>        r_newevents;
    
    public RenderThread(RenderWindow window, Queue<Event> events)
    {
        r_window   = window;
        r_newevents = events;
    }
    
    public void run()
    {
        if (keep_rendering) { return; }
        
        r_tick = 0;
        keep_rendering = true;
        List<Event> newEvents = new ArrayList<Event>();
        Clock renderClock = new Clock();
        
        while (keep_rendering)
        {
            newEvents.clear();
            
            while (r_newevents.size() > 0)
            {
                newEvents.add(r_newevents.poll());
            }
                
            for (I_Renderer r: r_renderers)
            {
                r.render(r_window, new ArrayList<Event>(newEvents));
            }
            
            r_window.display();
            r_tick = (long)(renderClock.getElapsedTime().asMilliseconds());
        }
    }
    
    // Would be named stop, but that's a thread method
    public void end()
    {
        keep_rendering = false;
    }
}
