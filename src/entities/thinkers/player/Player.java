package entities.thinkers.player;

import modes.player.PlayerMainMode;
import anim.Animation;
import anim.PlayerAnim;
import entities.thinkers.Thinker;

public class Player extends Thinker
{

    @Override
    protected void init()
    {
        // TODO Auto-generated method stub

    }

    /**
     * Player ID is 0. This should be assumed.
     */
    public int getID()
    {
        return 0;
    }

    @Override
    public Animation defaultAnimation()
    {
        return new PlayerAnim();
    }

    @Override
    protected void setDefaultMode()
    {
        updateMode(new PlayerMainMode(this), 0l);
    }
}
