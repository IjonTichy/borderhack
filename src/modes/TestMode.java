package modes;

import entities.Entity;
import map.GameMap;

public class TestMode extends Mode
{
    public TestMode(Entity e)
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
