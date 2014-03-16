package modes.player;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jsfml.system.Vector2i;

import controls.Control;
import entities.Entity;
import entities.player.MovementControl;
import entities.player.Player;
import entities.player.Player.PControl;
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
        long i = 0;

        pollMovement();
        doInvCheck(tick, map);
        
        i = doMovement(tick, map); if (i > 0) { return i; }
        i = doGrabbing(tick, map); if (i > 0) { return i; }
        
        return 0;
    }
    
    private void pollMovement()
    {
        if (pmm_movements == null) { pmm_movements = new ArrayDeque<>(); }
        
        for (Control c: m_controls)
        {
            if (!(c instanceof MovementControl)) { continue; }
            
            MovementControl mc = (MovementControl)c;
            pmm_movements.addLast(mc);
        }
        
        return;
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
    
    private long doGrabbing(Long tick, GameMap map)
    {
        if (hasControl(PControl.PICKUP.control()))
        {
            System.out.println("That's cool I guess");
        }
        return 0;
    }
    
    private void doInvCheck(Long tick, GameMap map)
    {
        if (hasControl(PControl.INVENTORY.control()))
        {
            System.out.println("what's an inventory");
        }
    }
}
