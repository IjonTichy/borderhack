package modes.player;

import controls.Control;
import entities.thinkers.Thinker;
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
        return 0;
    }
    
}
