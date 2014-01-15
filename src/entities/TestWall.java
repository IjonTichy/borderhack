package entities;

import anim.Animation;
import anim.WallAnim;

public class TestWall extends Entity
{
    protected void init()
    {
        // TODO Auto-generated method stub   
    }
    
    protected void defaults()
    {
        super.defaults();
        ent_layer = -10;
    }
    
    public int getID() { return 2; }
    public Animation defaultAnimation() { return new WallAnim(); }
    
}
