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
        double tick = m_controller.getMap().getTick();
        
        System.out.println("Tick " + tick + " is dumb");
        return 0.5;
    }
}
