package entities;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import util.Constants;
import util.TexStorage;
import util.TextureAlreadyInException;
import util.RenderQuad;

public class Entity
{
    protected static long E_ID_Counter = 0;

    protected long    myID;
    protected Texture myTexture;
    protected String  myTexPath;
    protected int     myLayer;
    protected int     mySizeX;
    protected int     mySizeY;
    
    public Entity()
    {
        myID = E_ID_Counter++;
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
        myTexPath = "img/default2.png";
        myLayer   = 0;
        mySizeX   = 1;
        mySizeY   = 1;
    }

    
    /**
     * Any sub-entity logic should go here. super.init() is not necessary here;
     * subclasses might have it differently.
     * 
     * @param       nothing.
     * @return      nothing.
     */
    protected void init()
    {
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
        try { myTexture = TexStorage.loadTexturePath(myTexPath); }
        catch (IOException e) { myTexture = null; }
        catch (TextureAlreadyInException e) { myTexture = TexStorage.getTexture(myTexPath); }
        
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
        VertexArray retArray = new VertexArray();
        Vector2f    tmpCoord;
        Vector2f    texCoord;
        
        Vector2i    texSize = myTexture.getSize();
        Vector2f    center  = new Vector2f(Constants.TILE_WIDTH / 2, Constants.TILE_HEIGHT / 2);
        float xoff = texSize.x / 2;
        float yoff = texSize.y / 2;
        

        tmpCoord = new Vector2f(center.x - xoff, center.y - yoff);
        texCoord = new Vector2f(0, 0);
        retArray.add(new Vertex(tmpCoord, Color.WHITE, texCoord));
        
        tmpCoord = new Vector2f(center.x - xoff, center.y + yoff);
        texCoord = new Vector2f(0, texSize.y);
        retArray.add(new Vertex(tmpCoord, Color.WHITE, texCoord));
        
        tmpCoord = new Vector2f(center.x + xoff, center.y + yoff);
        texCoord = new Vector2f(texSize.x, texSize.y);
        retArray.add(new Vertex(tmpCoord, Color.WHITE, texCoord));
        
        tmpCoord = new Vector2f(center.x + xoff, center.y - yoff);
        texCoord = new Vector2f(texSize.x, 0);
        retArray.add(new Vertex(tmpCoord, Color.WHITE, texCoord));
        
        RenderQuad ret = new RenderQuad(myTexture, myLayer, retArray);
        return ret;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (myID ^ (myID >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        
        Entity other = (Entity) obj;
        
        if (myID != other.myID) { return false; }
        return true;
    }

    public Vector2i getSize()
    {
        return new Vector2i(this.mySizeX, this.mySizeY);
    }
}
