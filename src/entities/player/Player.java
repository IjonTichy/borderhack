package entities.player;

import org.jsfml.window.Keyboard.Key;

import modes.player.PlayerMainMode;
import anim.Animation;
import anim.PlayerAnim;
import controls.Control;
import controls.KeyMapping;
import entities.Entity;

public class Player extends Entity
{
    public static enum PControl
    {
        MOVE_NORTHWEST(new MovementControl("Move NW", MovementControl.Direction.NORTHWEST),
                       new KeyMapping(Key.NUMPAD7, true)),
                        
        MOVE_NORTH(    new MovementControl("Move N",  MovementControl.Direction.NORTH),
                       new KeyMapping(Key.NUMPAD8, true)),
                
        MOVE_NORTHEAST(new MovementControl("Move NE", MovementControl.Direction.NORTHEAST),
                       new KeyMapping(Key.NUMPAD9, true)),
                
        MOVE_WEST(     new MovementControl("Move W",  MovementControl.Direction.WEST),
                       new KeyMapping(Key.NUMPAD4, true)),
                
        MOVE_EAST(     new MovementControl("Move E",  MovementControl.Direction.EAST),
                       new KeyMapping(Key.NUMPAD6, true)),
                
        MOVE_SOUTHWEST(new MovementControl("Move SW", MovementControl.Direction.SOUTHWEST),
                       new KeyMapping(Key.NUMPAD1, true)),
                
        MOVE_SOUTH(    new MovementControl("Move S",  MovementControl.Direction.SOUTH),
                       new KeyMapping(Key.NUMPAD2, true)),
                
        MOVE_SOUTHEAST(new MovementControl("Move SE", MovementControl.Direction.SOUTHEAST),
                       new KeyMapping(Key.NUMPAD3, true)),
                
        
        WAIT(new MovementControl("Wait", MovementControl.Direction.NONE),
             new KeyMapping(Key.NUMPAD5, true)),
             
        PICKUP(new Control("Pick up item"), new KeyMapping(Key.G, true)),
        
        INVENTORY(new Control("Show inventory"), new KeyMapping(Key.I, true)),
        
        ;
        
        private Control c;
        private KeyMapping k;
        
        private PControl(Control c)
        {
            this(c, null);
        }
        
        private PControl(Control c, KeyMapping k)
        {
            this.c = c;
            this.k = k;
        }
        
        public Control    control() { return c; }
        public KeyMapping key()     { return k; }
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
        updateMode(new PlayerMainMode(this), 0);
    }
}
