package render;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

public class RenderQuad
{
    public final Texture     texture;
    public final int         layer;
    public final VertexArray points;
    
    public static enum anchors
    {
        TOP_LEFT,
        TOP,
        TOP_RIGHT,
        LEFT,
        CENTER,
        RIGHT,
        BOTTOM_LEFT,
        BOTTOM,
        BOTTOM_RIGHT
    };
    
    public RenderQuad(Texture t, int l, VertexArray p)
    {
        this.texture = t;
        this.layer = l;
        this.points = p;
    }

    /**
     * Returns a RenderQuad with the texture positioned in its box according to its anchor.
     * The placement of this is a bit esoteric, but it makes the most sense here
     * right now.
     * 
     * @param tex    <i>(Texture)</i> the texture to center
     * @param center <i>(Vector2f)</i> the center point
     * @param layer  <i>(int)</i> the layer to put this on
     * @return an anchored RenderQuad, suitable for moving around.
     */
    public static RenderQuad renderAnchored(Texture tex, Vector2i size, anchors anchor, int layer)
    {
        float centerX;
        float centerY;
        
        Vector2i texSize = tex.getSize();
        float texCenterX = (float)texSize.x / 2;
        float texCenterY = (float)texSize.y / 2;
        
        // This is split so code doesn't get AS duplicated
        switch (anchor)
        {
            case TOP_LEFT:
            case LEFT:
            case BOTTOM_LEFT:
                centerX = texCenterX;
                break;
            
            case TOP_RIGHT:
            case RIGHT:
            case BOTTOM_RIGHT:
            default:
                centerX = (float)size.x - texCenterX;
                break;
            
            case TOP:
            case CENTER:
            case BOTTOM:
                centerX = (float)size.x / 2;
                break;
        }
        
        switch (anchor)
        {
            case TOP_LEFT:
            case TOP:
            case TOP_RIGHT:
                centerY = texCenterY;
                break;
                
            case LEFT:
            case CENTER:
            case RIGHT:
            default:
                centerY = (float)size.y / 2;
                break;
                
            case BOTTOM_LEFT:
            case BOTTOM:
            case BOTTOM_RIGHT:
                centerY = (float)size.y - texCenterY;
                break;
        }
        
        return renderAnchored(tex, new Vector2f(centerX, centerY), layer);
    }


    /**
     * Returns a RenderQuad with the texture centered around the point given.
     * The placement of this is a bit esoteric, but it makes the most sense here
     * right now.
     * 
     * @param tex    <i>(Texture)</i> the texture to center
     * @param center <i>(Vector2f)</i> the center point
     * @param layer  <i>(int)</i> the layer to put this on
     * @return a centered RenderQuad, suitable for moving around.
     */
    public static RenderQuad renderAnchored(Texture tex, Vector2f center, int layer)
    {
        VertexArray retArray = new VertexArray();
        Vector2f    tmpCoord;
        Vector2f    texCoord;
        
        Vector2i    texSize = tex.getSize();
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
        
        RenderQuad ret = new RenderQuad(tex, layer, retArray);
        return ret;
    }
}