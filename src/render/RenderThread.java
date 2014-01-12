package render;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Clock;
import org.jsfml.window.ContextActivationException;
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
    private volatile boolean    keep_rendering;
    private          long       r_tick;
    
    private RenderWindow                            r_window;
    private SortedMap<Integer, List<I_Renderer>>    r_renderers;
    private Queue<Event>                            r_newevents;
    
    public RenderThread(RenderWindow window, Queue<Event> events)
    {
        r_window   = window;
        r_newevents = events;
        r_renderers = new TreeMap<Integer, List<I_Renderer>>();
    }
    
    public long getTick() { return r_tick; }
    
    public void addRenderer(I_Renderer r)
    {
        removeRenderer(r);
        
        int layer = r.getLayer();
        
        List<I_Renderer> layerArray = r_renderers.get(layer);
        
        if (layerArray == null)
        {
            layerArray = new ArrayList<I_Renderer>();
            r_renderers.put(layer, layerArray);
        }
        
        layerArray.add(r);
    }
    
    public boolean removeRenderer(I_Renderer r)
    {
        boolean removed = false;
        
        for (List<I_Renderer> layer: r_renderers.values())
        {
            removed |= layer.remove(r);
        }
        
        return removed;
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
                
            for (List<I_Renderer> rends: r_renderers.values())
            {
                for (I_Renderer r: rends)
                {
                    try
                    {
                        r.render(r_window, new ArrayList<Event>(newEvents));
                    }
                    catch (ContextActivationException e)
                    {
                        e.printStackTrace();
                    }
                }
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
