package entities;

import java.io.IOException;

import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2i;

import render.RenderQuad;
import util.Constants;
import util.TexStorage;
import util.TextureAlreadyInException;

abstract public class Entity
{
    protected static long E_ID_Counter = 0;

    protected Texture myTexture;
    protected int     myLayer;
    protected int     mySizeX;
    protected int     mySizeY;
    
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
     * Store texture path here.
     * @return
     */
    public abstract String  getTexturePath();


    public Vector2i getSize()
    {
        return new Vector2i(this.mySizeX, this.mySizeY);
    }
    
    
    
    public Entity()
    {
        defaults();
        loadTexture(true);
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
     * Grabs and loads the entity's texture according to its texture path. Refers
     * to TexStorage to make sure texture is not redundantly loaded. It's up to
     * the entity to keep track of its texture usage if it dynamically generates
     * textures for whatever reason.
     * 
     * @param       nothing.
     * @return      nothing.
     */
    
    protected void loadTexture(boolean smooth)
    {
        try { myTexture = TexStorage.loadTexturePath(getTexturePath()); }
        catch (IOException e)
        {
            System.err.println("ERROR: Could not load image \"" + getTexturePath() + "\"");
        }
        catch (TextureAlreadyInException e) { myTexture = TexStorage.getTexture(getTexturePath()); }
        
        if (myTexture != null)
        {
            myTexture.setSmooth(smooth);
        }
    }

    
    /**
     * Called by RenderMap; returns a RenderQuad containing the data necessary
     * for rendering the entity on the map. Offsets according to position are handled
     * in the map itself. Should not change the state of the Entity; if it does, I
     * am not responsible for anything that breaks horribly.
     *
     * <p><b>TODO:</b>
     *      When scripting API is done, give it a simpler rendering method that only requires
     *      returning a texture, and potentially an offset from the center. It shouldn't have
     *      to care about the underlying engine.</p>
     *
     * @param renderTick The tick in RenderMap that this was called in. Useful for animations.
     * @return          a RenderQuad containing a texture, a layer to render on, and a VertexArray to render with
     */
    
    public RenderQuad render(long renderTick)
    {
        int      sizeX = Constants.TILE_WIDTH * mySizeX;
        int      sizeY = Constants.TILE_HEIGHT * mySizeY;
        Vector2i size  = new Vector2i(sizeX, sizeY);
        return RenderQuad.renderAnchored(myTexture, size, RenderQuad.anchors.CENTER, myLayer);
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
