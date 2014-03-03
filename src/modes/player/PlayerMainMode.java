package modes.player;

import org.jsfml.system.Vector2i;

import controls.Control;
import entities.thinkers.Thinker;
import entities.thinkers.player.MovementControl;
import map.GameMap;
import modes.Mode;

public class PlayerMainMode extends Mode
{
    public PlayerMainMode(Thinker e)
    {
        super(e);
    }
    
    @Override
    public long defaultAction(Long tick, GameMap map)
    {
        // TODO Auto-generated method stub
        
        if (m_controls.size() > 0)
        {
            System.out.println("\nGot controls:");
            
            for (Control c: m_controls)
            {
                System.out.println("* " + c);
            }
        }
        
        doMovement(tick, map);
        
        return 0;
    }
    
    private void doMovement(Long tick, GameMap map)
    {
        int xoff = 0;
        int yoff = 0;
        
        for (Control c: m_controls)
        {
            if (!(c instanceof MovementControl)) { continue; }
            
            MovementControl mc = (MovementControl)c;
            MovementControl.Direction md = mc.mc_direction;
            
            xoff += md.x;
            yoff += md.y;
        }
        
        if (xoff != 0 || yoff != 0) { map.move(m_controller, new Vector2i(xoff, yoff)); }
    }
}
