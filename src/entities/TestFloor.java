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
    
    protected void init()
    {
        return;
    }

    public Animation defaultAnimation()
    {
        return new FloorAnim();
    }
}
