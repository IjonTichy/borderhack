package modes;

import entities.Entity;

public class TestMode extends Mode
{
    public TestMode(Entity e)
    {
        super(e);
    }
    
    @Override
    public long defaultAction()
    {
        long tick = m_controller.getMap().getTick();
        
        System.out.println("Tick " + tick + " is dumb");
        return 100;
    }
}
