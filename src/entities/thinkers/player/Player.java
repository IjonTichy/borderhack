package entities.thinkers.player;

import modes.player.PlayerMainMode;
import anim.Animation;
import anim.PlayerAnim;
import events.Control;
import entities.thinkers.Thinker;

public class Player extends Thinker
{
    public static enum PControl
    {
        MOVE_NORTHWEST(new MovementControl(MovementControl.Direction.NORTHWEST)),
        MOVE_NORTH(    new MovementControl(MovementControl.Direction.NORTH)),
        MOVE_NORTHEAST(new MovementControl(MovementControl.Direction.NORTHEAST)),
        MOVE_WEST(     new MovementControl(MovementControl.Direction.WEST)),
        MOVE_EAST(     new MovementControl(MovementControl.Direction.EAST)),
        MOVE_SOUTHWEST(new MovementControl(MovementControl.Direction.SOUTHWEST)),
        MOVE_SOUTH(    new MovementControl(MovementControl.Direction.SOUTH)),
        MOVE_SOUTHEAST(new MovementControl(MovementControl.Direction.SOUTHEAST)),
        
        WAIT(new MovementControl(MovementControl.Direction.NONE)),
        
        ;
        
        private Control c;
        
        private PControl(Control c)
        {
            this.c = c;
        }
        
        public Control control() { return c; }
    };

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
