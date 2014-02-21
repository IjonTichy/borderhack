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
    
    public MovementControl() { this(null, Direction.NONE); }
    public MovementControl(Direction d) { this(null, d); }
    public MovementControl(String name) { this(name, Direction.NONE); }
    
    public MovementControl(String name, Direction d)
    {
        super(name);
        mc_direction = d;
    }
}
