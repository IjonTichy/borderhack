package modes;

import java.util.List;

import entities.thinkers.Thinker;
import events.Control;
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
