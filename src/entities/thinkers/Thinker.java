package entities.thinkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Entity;
import entities.thinkers.inventory.Inventory;
import map.GameMap;
import modes.Mode;

abstract public class Thinker extends Entity
{
    protected Map<Long, List<Mode>> t_modes;
    protected float                 t_shield;
    protected List<Inventory>       t_backpack;
    
    public Thinker()
    {
        t_modes = new HashMap<Long, List<Mode>>();
        setDefaultMode();
    }
    
    /**
     * Sets the default mode on the entity. 
     * Called by the entity on initialization, and by default does nothing.
     * If you want an entity to have any behaviour, adding an initial mode here
     * is the recommended way to do so.
     * 
     * <p>This is also called upon an entity on a map start (tick 0). If you wish
     * to have state be persistent, save a boolean in your subclass and check
     * that. Shit ain't hard, yo.</p>
     * 
     * @return nothing.
     */
    protected void setDefaultMode()
    {
        return;
    }
    
    /**
     * Adds a mode to an entity. Use this to apply debuffs or whatnot to things.
     * 
     * @param m     the mode to add.
     * @return nothing.
     */
    public void addMode(Mode m, Long tick)
    {
        List<Mode> tickmodes = t_modes.get(tick);
        
        if (tickmodes == null)
        {
            tickmodes = new ArrayList<Mode>();
            t_modes.put(tick,  tickmodes);
        }
        
        tickmodes.add(m);
    }

    public Long think(Long tick, GameMap map)
    {
        return null;
    }
}
