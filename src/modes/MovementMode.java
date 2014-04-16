package modes;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.system.Vector3i;

import util.Line3D;

public class MovementMode extends Mode
{
    private final Vector3i mm_delta;
    private final double   mm_time;
    private final List<Vector3i>    mm_interpositions;
    private final List<Double>      mm_intertimes;

    public MovementMode()
    {
        this(new Vector3i(0, 0, 0), 0);
    }
    
    public MovementMode(Vector3i delta, double time)
    {
        mm_delta = delta;
        mm_time  = time;
        
        mm_interpositions   = Line3D.bresenham(delta);
        mm_intertimes       = new ArrayList<>();
    }
    
    public List<Vector3i> positions()
    {
        return new ArrayList<Vector3i>(mm_interpositions);
    }
    
    public List<Double> times()
    {
        return new ArrayList<Double>(mm_intertimes);
    }
    
    @Override
    public double defaultAction()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
