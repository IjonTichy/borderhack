package entities.thinkers;

import modes.TestMode;
import anim.Animation;
import anim.ThinkerAnim;

public class TestThinker extends Thinker
{
    protected void init()
    {
        // TODO Auto-generated method stub

    }
    
    public int getID() { return 10; }

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
