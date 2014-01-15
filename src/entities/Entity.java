package entities;

import org.jsfml.system.Vector2i;

import anim.AnimData;
import anim.Animation;
import render.RenderQuad;
import util.Constants;

abstract public class Entity
{
    protected static long E_ID_Counter = 0;

    protected int       myLayer;
    protected int       mySizeX;
    protected int       mySizeY;
    protected Animation myAnim;
    
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
        return new Vector2i(this.mySizeX, this.mySizeY);
    }
    
    
    
    public Entity()
    {
        defaults();
        init();
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
        myLayer   = 0;
        mySizeX   = 1;
        mySizeY   = 1;
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
        if (myAnim == null)
        {
            myAnim = defaultAnimation();
            myAnim.setLooping(true);
        }
        AnimData animLayer = new AnimData(0, 0, myLayer);
        
        return myAnim.render(renderTick, animLayer, new Vector2i(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
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
