package entities;

import modes.TestMode;
import anim.Animation;
import anim.ThinkerAnim;

public class TestThinker extends Entity
{
    protected void init()
    {
        // TODO Auto-generated method stub

    }
    
    @Override
    public void setDefaultMode()
    {
        updateMode(new TestMode(this), 0l);
    }

    public Animation defaultAnimation()
    {
        return new ThinkerAnim();
    }
}
