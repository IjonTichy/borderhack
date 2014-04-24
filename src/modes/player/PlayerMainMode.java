package modes.player;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jsfml.system.Vector2i;

import controls.Control;
import entities.Entity;
import entities.player.MovementControl;
import entities.player.Player.PControl;
import map.GameMap;
import modes.Mode;
import modes.MovementMode;

public class PlayerMainMode extends Mode
{
    private Deque<MovementControl> pmm_movements;
    
    public PlayerMainMode(Entity e)
    {
        super(e);
    }
    
    @Override
    public double defaultAction()
    {
        double i = 0;
        
        GameMap map = m_controller.getMap();
        double tick;
        
        if (map == null) { tick = 0; }
        else { tick = map.getTick(); }

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
    
    private double doMovement(Double tick, GameMap map)
    {
        MovementControl nextMovement = pmm_movements.pollFirst();
        if (nextMovement == null) { return 0; }
        
        MovementControl.Direction nextDir = nextMovement.mc_direction;
        
        int xoff = nextDir.x;
        int yoff = nextDir.y;
        
        MovementMode blocksMoved = m_controller.move(new Vector2i(xoff, yoff));
        if (blocksMoved == null) { return 0; }
        return Math.max(0, blocksMoved.positions().size() - 1);
    }
    
    private double doGrabbing(Double tick, GameMap map)
    {
        if (hasControl(PControl.PICKUP.control()))
        {
            System.out.println("That's cool I guess");
        }
        return 0;
    }
    
    private void doInvCheck(Double tick, GameMap map)
    {
        if (hasControl(PControl.INVENTORY.control()))
        {
            System.out.println("what's an inventory");
        }
    }
}
