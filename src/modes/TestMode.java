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
        System.out.println("Tick " + this.m_controller.getMap().getTick() + " is dumb");
        return 0.25;
    }
}
