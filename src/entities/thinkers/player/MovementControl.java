package entities.thinkers.player;

import controls.Control;

public class MovementControl extends Control
{   
    public static enum Direction
    {
        NORTHWEST, NORTH, NORTHEAST,
        WEST,      NONE,  EAST,
        SOUTHWEST, SOUTH, SOUTHEAST
    };
    
    public final Direction mc_direction;
    
    public MovementControl()
    {
        this(Direction.NONE);
    }
    
    public MovementControl(Direction d)
    {
        mc_direction = d;
    }
}
