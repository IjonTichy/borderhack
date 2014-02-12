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
    public long defaultAction(long tick, GameMap map, List<Control> controls)
    {
        System.out.println("Tick " + tick + " is dumb");
        return 100;
    }
}
