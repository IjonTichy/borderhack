package entities;

import anim.Animation;
import anim.FloorAnim;

public class TestFloor extends Entity
{
    protected void defaults()
    {
        super.defaults();
        ent_layer   = 10;
    }
    
    public int getID() { return 1; }
    
    protected void init()
    {
        return;
    }

    public Animation defaultAnimation()
    {
        return new FloorAnim();
    }
}
