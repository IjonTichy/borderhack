package util;

import org.jsfml.graphics.Texture;
import org.jsfml.graphics.VertexArray;

public class RenderQuad
{
    public final Texture     texture;
    public final int         layer;
    public final VertexArray points;
    
    public RenderQuad(Texture t, int l, VertexArray p)
    {
        this.texture = t;
        this.layer = l;
        this.points = p;
    }
}