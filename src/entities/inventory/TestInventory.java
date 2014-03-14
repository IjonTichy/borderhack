package entities.inventory;

import org.jsfml.graphics.Color;

import anim.Animation;

public class TestInventory extends Inventory
{
    private static final Color myColor = new Color(255, 255, 255);
    
    @Override
    public String getName()
    {
        return "Some Test Inventory";
    }
    
    @Override
    public Color getDisplayColor()
    {
        return myColor;
    }
    
    @Override
    protected void init()
    {
    }
    
    @Override
    public Animation defaultAnimation()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
