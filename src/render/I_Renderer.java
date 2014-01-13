package render;

import java.util.List;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.ContextActivationException;
import org.jsfml.window.event.Event;

/**
 * Renderer interface. Any objects implementing this are expected to be renderers
 * that act in compliance with a RenderThread; that is, its render method shall
 * not loop. State can be saved inside the renderer itself.
 * 
 * <p><b>DO NOT TOUCH GAME STATE.</b> That is a can of worms no one wants to open.</p>
 */

public abstract class I_Renderer
{
    protected RenderThread  r_thread;
    protected int           r_layer = 0;
    
    abstract void render(RenderWindow win, List<Event> events) throws ContextActivationException;
    
    public void attachRenderThread(RenderThread r)
    {
        r_thread = r;
    }
    
    public RenderThread detachRenderThread()
    {
        RenderThread ret = r_thread;
        r_thread = null;
        return ret;
    }
    
    public int getLayer()
    {
        return r_layer;
    }
}
