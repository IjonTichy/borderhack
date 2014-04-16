package util;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3f;
import org.jsfml.system.Vector3i;

public class Line3D
{   
    public final Vector3f start;
    public final Vector3f end;
    
    public Line3D(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        this(new Vector3f(x1, y1, z1), new Vector3f(x2, y2, z2));
    }
    
    public Line3D(Vector3f start, Vector3f end)
    {
        this.start = start;
        this.end   = end;
    }
    
    public List<Vector3i> bresenham()
    {
        return bresenham(start, end);
    }
    
    public static List<Vector3i> bresenham(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        return bresenham(new Vector3i(Math.round(x1), Math.round(y1), Math.round(z1)),
                         new Vector3i(Math.round(x2), Math.round(y2), Math.round(z2)));
    }
    
    public static List<Vector3i> bresenham(Vector3f start, Vector3f end)
    {
        return bresenham(new Vector3i(Math.round(start.x), Math.round(start.y), Math.round(start.z)),
                         new Vector3i(Math.round(end.x),   Math.round(end.y),   Math.round(end.z)));
    }
    
    public static List<Vector3i> bresenham(Vector3f end)
    {
        return bresenham(new Vector3i(0, 0, 0),
                         new Vector3i(Math.round(end.x),   Math.round(end.y),   Math.round(end.z)));
    }
    
    public static List<Vector3i> bresenham(Vector3i end)
    {
        return bresenham(new Vector3i(0, 0, 0), end);
    }
    
    public static List<Vector3i> bresenham(Vector3i start, Vector3i end)
    {
        List<Vector3i> ret = new ArrayList<>();
        
        List<Vector2i> xy_bresenham = Line2D.bresenham(start.x, start.y, end.x, end.y);
        int pointCount = xy_bresenham.size();
        List<Vector2i> z_bresenham  = Line2D.bresenham(0, start.z, pointCount - 1, end.z);
        
        for (Vector2i zpos: z_bresenham)
        {
            int xyindex = zpos.x;
            
            Vector2i xypos = xy_bresenham.get(xyindex);
            Vector3i nextPos = new Vector3i(xypos.x, xypos.y, zpos.y);
            ret.add(nextPos);
        }
        
        return ret;
    }
}
