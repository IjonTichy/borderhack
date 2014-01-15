package anim;

import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2i;

import render.RenderQuad;
import util.NegativeTickException;
import util.ResourceStorage;
import util.TextureAlreadyInException;

abstract public class Animation
{
    protected long              a_starttick;
    protected List<Texture>     a_frames;
    protected int               a_frametime;
    protected boolean           a_loop;
    
    public Animation()
    {
        defaults();
        loadFrames();
    }
    
    abstract public Vector2i getAnimSize();
    abstract public String   getFrameSource();
    abstract public int      getID();
    
    protected void defaults()
    {
        a_starttick = 0;
        a_frametime = 250;
        a_loop      = false;
    }
    
    public int getAnimTicks()
    {
        return a_frametime * a_frames.size();
    }
    
    public int getAnimFrameCount()
    {
        return a_frames.size();
    }
    
    protected void setStartTick(long tick) throws NegativeTickException
    {
        if (tick < 0) { throw new NegativeTickException(); }
        a_starttick = tick;
    }
    
    public void setLooping(boolean l)
    {
        a_loop = l;
    }
    
    protected void loadFrames()
    {
        List<Texture> frames = null;
        
        try
        {
            frames = ResourceStorage.loadAnimPath(getFrameSource(), getAnimSize());
        }
        catch (TextureAlreadyInException e)
        {
            frames = ResourceStorage.getAnimation(getFrameSource());
        }
        catch (IOException e)
        {
            System.err.println("ERROR: Could not find file \"" + getFrameSource() + "\"");
            e.printStackTrace();
        }
        catch (TextureCreationException e)
        {
            System.err.println("ERROR: Could not create texture, no clue why");
            e.printStackTrace();
        }
        
        a_frames = frames;
    }
    
    public RenderQuad render(long tick, AnimData a)
    {
        return render(tick, a, getAnimSize());
    }
    
    public RenderQuad render(long tick, AnimData a, Vector2i size)
    {
        long actualTick = tick - a_starttick;
        int  wrappedFrame;
        
        if (a_frametime == 0)
        {
            wrappedFrame = 0;
        }
        else
        {
            if (actualTick >= getAnimTicks() && !a_loop)
            {
                return null;
            }
            
            long unwrappedFrame = actualTick / a_frametime;
            wrappedFrame = (int)(unwrappedFrame % getAnimFrameCount());
        }
        
        return RenderQuad.renderAnchored(a_frames.get(wrappedFrame), size, RenderQuad.anchors.CENTER, a.layer);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (getID() ^ (getID() >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Animation other = (Animation) obj;
        if (a_starttick != other.a_starttick)
        {
            return false;
        }
        return true;
    }
}
