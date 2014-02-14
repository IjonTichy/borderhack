package modes.player;

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
        return 0;
    }
    
}
