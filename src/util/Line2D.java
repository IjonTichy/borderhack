package util;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

public class Line2D
{   
    public final Vector2f start;
    public final Vector2f end;
    
    public Line2D(int x1, int y1, int x2, int y2)
    {
        this(new Vector2f(x1, y1), new Vector2f(x2, y2));
    }
    
    public Line2D(Vector2f start, Vector2f end)
    {
        this.start = start;
        this.end   = end;
    }
    
    public List<Vector2i> bresenham()
    {
        return bresenham(start, end);
    }
    
    public static List<Vector2i> bresenham(int x1, int y1, int x2, int y2)
    {
        return bresenham(new Vector2i(x1, y1), new Vector2i(x2, y2));
    }
    
    public static List<Vector2i> bresenham(Vector2f start, Vector2f end)
    {
        return bresenham(new Vector2i(Math.round(start.x), Math.round(start.y)),
                         new Vector2i(Math.round(end.x),   Math.round(end.y)  ));
    }
    
    public static List<Vector2i> bresenham(Vector2f end)
    {
        return bresenham(new Vector2i(0, 0),
                         new Vector2i(Math.round(end.x),   Math.round(end.y)  ));
    }
    
    public static List<Vector2i> bresenham(Vector2i start, Vector2i end)
    {
        List<Vector2i> ret = new ArrayList<>();
        
        boolean steep = Math.abs(end.y - start.y) > Math.abs(end.x - start.x);
        if (steep)
        {
            start = new Vector2i(start.y, start.x);
            end   = new Vector2i(end.y,   end.x);
        }
        
        boolean reverse = end.x < start.x;
        if (reverse) { Vector2i x = end; end = start; start = x; }
        
        Vector2i delta = new Vector2i(end.x - start.x, Math.abs(end.y - start.y));
        
        int y = start.y;
        int error = delta.x / 2;
        
        int ystep = 1;
        if (start.y > end.y) { ystep = -1; }
        
        
        for (int x = start.x; x <= end.x; x++)
        {
            Vector2i next;
            if (steep) { next = new Vector2i(y, x); }
            else       { next = new Vector2i(x, y); }
            
            if (reverse) { ret.add(0, next); }
            else         { ret.add(next); }
            
            error -= delta.y;
            if (error < 0) { y += ystep; error += delta.x; }
        }
        
        return ret;
    }
}
