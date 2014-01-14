package anim;

import java.io.IOException;
import java.util.List;

import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2i;

import render.RenderQuad;
import util.NegativeTickException;
import util.TexStorage;
import util.TextureAlreadyInException;

abstract public class Animation
{
    protected long              a_starttick;
    protected List<Texture>     a_frames;
    protected int               a_frameticks;
    protected boolean           a_loop;
    
    public Animation()
    {
        defaults();
    }
    
    abstract public Vector2i getAnimSize();
    abstract public String   getFrameSource();
    abstract public int      getID();
    
    protected void defaults()
    {
        a_starttick     = 0;
        a_frameticks    = 250;
        a_loop          = false;
    }
    
    protected int getAnimTicks()
    {
        return a_frameticks * a_frames.size();
    }
    
    protected void setStartTick(long tick) throws NegativeTickException
    {
        if (tick < 0) { throw new NegativeTickException(); }
        a_starttick = tick;
    }
    
    protected void loadFrames()
    {
        List<Texture> frames = null;
        
        try
        {
            frames = TexStorage.loadAnimPath(getFrameSource(), getAnimSize());
        }
        catch (TextureAlreadyInException e) { frames = TexStorage.getAnimation(getFrameSource()); }
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
        long actualTick = tick - a_starttick;
        
        if (actualTick >= getAnimTicks() && !a_loop)
        {
            return null;
        }
        
        Vector2i size   = getAnimSize();
        return RenderQuad.renderAnchored(a_frames.get(0), size, RenderQuad.anchors.CENTER, a.layer);
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
