package modes;

import entities.Entity;

public class TestMode extends Mode
{
    public TestMode(Entity e)
    {
        super(e);
    }
    
    @Override
    public double defaultAction()
    {
        return 0.25;
    }
}
