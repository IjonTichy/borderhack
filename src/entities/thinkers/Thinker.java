package entities.thinkers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import entities.Entity;
import entities.thinkers.inventory.Inventory;
import map.GameMap;
import modes.Mode;

abstract public class Thinker extends Entity
{
    // NOTE: The Long here is a delay until it can run again; while it is
    //       in map ticks, it it relative. The map makes it absolute.
    
    protected Map<Mode, Long>   t_modes;
    protected float             t_shield;
    protected List<Inventory>   t_backpack;
    
    public Thinker()
    {
        t_modes = new HashMap<Mode, Long>();
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
     * Gets a copy of the modes in a Thinker.
     * 
     * @return guess.
     */
    public Map<Mode, Long> getModes()
    {
        return new HashMap<Mode, Long>(t_modes);
    }
    
    /**
     * Adds a mode to an entity. Use this to apply debuffs or whatnot to things.
     * 
     * @param m     the mode to add.
     * @param tick  the tick the mode should next be called on. Should be relative,
     *              not absolute.
     * @return nothing.
     */
    public void updateMode(Mode m, Long delay)
    {
        t_modes.put(m, delay);
    }

    /**
     * Runs all thoughts scheduled to run in the next tickDelta ticks <i>once</i>.
     * It's meant to be called with tickDelta being the exact amount of ticks needed
     * to reach the next set of actions.
     * 
     * @param tickDelta the amount of ticks that have passed since the last think call.
     * @param map       the map that is being thought on.
     * 
     * @return a map corresponding to the next set of actions to call. The Long value
     *          in the map is an absolute tick value.
     */
    public Map<Mode, Long> think(Long tickDelta, GameMap map)
    {
        Map<Mode, Long> ret = new HashMap<>();
        long mapTick = map.getTick();
        long endTick = mapTick + tickDelta;
        
        tickDown(tickDelta);
        
        Set<Map.Entry<Mode, Long>> modes = t_modes.entrySet();
        
        for (Map.Entry<Mode, Long> next: modes)
        {
            Mode mode     = next.getKey();
            Long timeLeft = next.getValue();
            
            if (timeLeft > 0) { continue; }
            
            long runTick   = mapTick - timeLeft;
            long nextTick  = runTick + mode.act(runTick, map);
            long nextDelay = nextTick - endTick;
            
            updateMode(mode, nextDelay);
            ret.put(mode, nextTick);
        }
        
        return ret;
    }
    
    
    public void tickDown(Long tickDelta)
    {
        Set<Map.Entry<Mode, Long>> modes = t_modes.entrySet();
        for (Map.Entry<Mode, Long> next: modes)
        {
            Mode mode  = next.getKey();
            Long delay = next.getValue();
            
            long t = delay - tickDelta;
            updateMode(mode, t);
        }
    }
}
