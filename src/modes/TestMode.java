package modes;

import entities.thinkers.Thinker;
import map.GameMap;

public class TestMode extends Mode
{
    public TestMode(Thinker e)
    {
        super(e);
    }
    
    @Override
    public long defaultAction(Long tick, GameMap map)
    {
        System.out.println("Tick " + tick + " is dumb");
        return 100;
    }
}
