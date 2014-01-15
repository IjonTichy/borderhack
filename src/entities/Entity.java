package entities;

import java.util.List;

import org.jsfml.system.Vector2i;

import anim.AnimData;
import anim.Animation;
import modes.Mode;
import render.RenderQuad;
import util.Constants;

abstract public class Entity
{
    protected static long E_ID_Counter = 0;

    protected int           ent_layer;
    protected int           ent_size_x;
    protected int           ent_size_y;
    protected Animation     ent_anim;
    protected List<Mode>    ent_modes;
    
    /**
     * Any sub-entity logic should go here. super.init() is not necessary here;
     * subclasses might have it differently.
     * 
     * @param       nothing.
     * @return      nothing.
     */
    abstract protected void init();
    
    /**
     * Entity ID goes here.
     * If this does not return a constant value, you have only yourself to blame
     * when everything starts breaking horribly.
     * 
     * @return entity ID, as integer
     */
    abstract public int getID();
    
    /**
     * Default animation. If myAnim is not set upon calling render, the result of this is used.
     * @return
     */
    public abstract Animation defaultAnimation();


    public Vector2i getSize()
    {
        return new Vector2i(this.ent_size_x, this.ent_size_y);
    }
    
    
    
    public Entity()
    {
        defaults();
        init();
        setDefaultMode();
    }

    /**
     * Set defaults for an entity here. 
     * ALWAYS PUT super.defaults() BEFORE YOUR STUFF.
     * This is to ensure that defaults always propagate if undefined, which
     * leads to Bad Things being avoided.
     * 
     * @param       nothing.
     * @return      nothing.
     */
    protected void defaults()
    {
        ent_layer   = 0;
        ent_size_x  = 1;
        ent_size_y  = 1;
    }
    
    /**
     * Sets the default mode on the entity. 
     * Called by the entity on initialization, and by default does nothing.
     * If you want an entity to have any behaviour, adding an initial mode here
     * is the recommended way to do so.
     * 
     * @return nothing.
     */
    protected void setDefaultMode()
    {
        return;
    }

    
    /**
     * Called by RenderMap; returns a RenderQuad containing the data necessary
     * for rendering the entity on the map. Offsets according to position are handled
     * in the map itself. Should not change the state of the Entity; if it does, I
     * am not responsible for anything that breaks horribly.
     * 
     * By default, makes sure that there's an animation available, and tells it
     * to render. Unless you have very good reasons to do otherwise, leave this
     * as is.
     *
     * <p><b>TODO:</b>
     *      When scripting API is done, give it a simpler rendering method that only requires
     *      returning a texture plus an offset from the center. It shouldn't have to care
     *      about the underlying engine.</p>
     *
     * @param renderTick The tick in RenderMap that this was called in. Useful for animations.
     * @return          a RenderQuad containing a texture, a layer to render on, and a VertexArray to render with
     */
    
    public RenderQuad render(long renderTick)
    {
        if (ent_anim == null)
        {
            ent_anim = defaultAnimation();
            ent_anim.setLooping(true);
        }
        AnimData animLayer = new AnimData(0, 0, ent_layer);
        
        return ent_anim.render(renderTick, animLayer, new Vector2i(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
    }
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (getID() ^ (getID() >>> 32));
        return result;
    }

    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        return false;
    }
}
