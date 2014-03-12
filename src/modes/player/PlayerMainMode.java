package modes.player;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jsfml.system.Vector2i;

import controls.Control;
import entities.Entity;
import entities.player.MovementControl;
import map.GameMap;
import modes.Mode;

public class PlayerMainMode extends Mode
{
    private Deque<MovementControl> pmm_movements;
    
    public PlayerMainMode(Entity e)
    {
        super(e);
    }
    
    @Override
    public long defaultAction(Long tick, GameMap map)
    {
        // TODO Auto-generated method stub
        long ticksSpent = 0;

        ticksSpent += pollMovement();
        ticksSpent += doMovement(tick, map);
        
        return ticksSpent;
    }
    
    private long pollMovement()
    {
        if (pmm_movements == null) { pmm_movements = new ArrayDeque<>(); }
        
        for (Control c: m_controls)
        {
            if (!(c instanceof MovementControl)) { continue; }
            
            MovementControl mc = (MovementControl)c;
            pmm_movements.addLast(mc);
        }
        
        return 0;
    }
    
    private long doMovement(Long tick, GameMap map)
    {
        MovementControl nextMovement = pmm_movements.pollFirst();
        if (nextMovement == null) { return 0; }
        
        MovementControl.Direction nextDir = nextMovement.mc_direction;
        
        int xoff = nextDir.x;
        int yoff = nextDir.y;
        
        int blocksMoved = map.move(m_controller, new Vector2i(xoff, yoff));
        return blocksMoved;
    }
}
