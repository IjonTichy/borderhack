package modes;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.system.Vector3i;

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
        
        mm_interpositions   = new ArrayList<>();
        mm_intertimes       = new ArrayList<>();
    }
    
    @Override
    public double defaultAction()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
