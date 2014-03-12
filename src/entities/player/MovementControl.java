package entities.player;

import controls.Control;

public class MovementControl extends Control
{   
    public static enum Direction
    {
        NORTHWEST(-1, -1), NORTH(0, -1), NORTHEAST(1, -1),
        WEST(-1, 0),       NONE(0, 0),   EAST(1, 0),
        SOUTHWEST(-1, 1),  SOUTH(0, 1),  SOUTHEAST(1, 1);
        
        public final int x;
        public final int y;
        
        private Direction(int xOff, int yOff)
        {
            x = xOff;
            y = yOff;
        }
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
    
    public String toString()
    {
        StringBuilder ret = new StringBuilder();
        
        ret.append(this.getClass().getSimpleName());
        ret.append("(\"");
        ret.append(c_name);
        ret.append("\", ");
        ret.append(mc_direction.name());
        ret.append(")");
        
        return ret.toString();
    }
}
