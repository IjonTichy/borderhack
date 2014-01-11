package render;

import java.util.List;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.event.Event;

/**
 * Renderer interface. Any objects implementing this are expected to be renderers
 * that act in compliance with a RenderThread; that is, its render method shall
 * not loop. State can be saved inside the renderer itself.
 *
 */

public abstract class I_Renderer
{
    protected RenderThread r_thread;
    
    abstract void render(RenderWindow win, List<Event> arrayList);
    
    public void setRenderThread(RenderThread r)
    {
        r_thread = r;
    }
    
    public RenderThread detachRenderThread()
    {
        RenderThread ret = r_thread;
        r_thread = null;
        return ret;
    }
}
