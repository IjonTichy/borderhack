package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import map.GameMap;
import modes.Mode;

import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3i;

import entities.inventory.Inventory;
import anim.AnimData;
import anim.Animation;
import render.RenderQuad;
import util.Constants;

abstract public class Entity
{
    protected static long E_ID_Counter = 0;

    protected int           ent_layer;
    protected int           ent_size_x;
    protected int           ent_size_y;
    protected int           ent_size_z;
    protected Animation     ent_anim;

    protected float         ent_health;

    protected Map<Mode, Long> ent_modes;
    protected List<Inventory> ent_backpack;
    
    
    /**
     * Constructs an Entity. True defaults are set here. You should <b>never</b>
     * override this method, as it contains many important things necessary for
     * an Entity to not break horribly.
     */
    public Entity()
    {
        ent_layer       = 0;
        ent_size_x      = 1;
        ent_size_y      = 1;
        ent_size_z      = 1;
        ent_health      = 1000;
        ent_modes       = new HashMap<>();
        ent_backpack    = new ArrayList<>();
        
        defaults();
        init();
        setDefaultMode();
    }

    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        return false;
    }
    
    
    /**
     * Any sub-entity logic should go here. super.init() is not necessary here;
     * subclasses might have it differently.
     * 
     * @param       nothing.
     * @return      nothing.
     */
    abstract protected void init();
    
    /**
     * Default animation. If myAnim is not set upon calling render, the result of this is used.
     * @return
     */
    public abstract Animation defaultAnimation();


    public Vector3i getSize()
    {
        return new Vector3i(this.ent_size_x, this.ent_size_y, this.ent_size_z);
    }

    /**
     * Set defaults for an entity here.
     * 
     * @param       nothing.
     * @return      nothing.
     */
    protected void defaults()
    {
    }
    
    /**
     * Called by RenderMap; returns a RenderQuad containing the data necessary
     * for rendering the entity on the map. Offsets according to position are handled
     * in the map itself. Should not change the state of the Entity; if it does, I
     * am not responsible for anything that breaks horribly.
     * 
     * <p>By default, makes sure that there's an animation available, and tells it
     * to render. Unless you have very good reasons to do otherwise, leave this
     * as is.</p>
     *
     * @param renderTick The tick in RenderMap that this was called in. Useful for animations.
     * @return          a RenderQuad containing a texture, a layer to render on, and a VertexArray to render with
     */
    
    public RenderQuad render(long renderTick)
    {
        if (ent_anim == null)
        {
            ent_anim = defaultAnimation();
            ent_anim.setLooping(true);
        }
        
        AnimData animLayer = new AnimData(0, 0, ent_layer);
        return ent_anim.render(renderTick, animLayer, new Vector2i(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
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
        return new HashMap<Mode, Long>(ent_modes);
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
        ent_modes.put(m, delay);
    }

    /**
     * Runs all thoughts scheduled to run in the next tickDelta ticks <i>once</i>.
     * It's meant to be called with tickDelta being the exact amount of ticks needed
     * to reach the next set of actions. Only the GameMap should call this.
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
        
        Set<Map.Entry<Mode, Long>> modes = ent_modes.entrySet();
        
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

    /**
     * Only meant to be called by the GameMap. Ticks down every mode in the
     * entity by tickDelta ticks.
     * 
     * @param tickDelta how many ticks to tick off
     */
    public void tickDown(Long tickDelta)
    {
        Set<Map.Entry<Mode, Long>> modes = ent_modes.entrySet();
        for (Map.Entry<Mode, Long> next: modes)
        {
            Mode mode  = next.getKey();
            Long delay = next.getValue();
            
            long t = delay - tickDelta;
            updateMode(mode, t);
        }
    }
    
    /**
     * Clears an entity's backpack.
     * @return how many items WERE in the backpack.
     */
    public int clearBackpack()
    {
        int ret = ent_backpack.size();
        ent_backpack.clear();
        
        return ret;
    }
    
    /**
     * Adds an inventory item to an entity's backpack. 
     * @param inv the inventory item to add
     * @return whether the inventory item is in the backpack.
     */
    public boolean addInventory(Inventory inv)
    {
        if (ent_backpack.contains(inv)) { return true; }
        
        ent_backpack.add(inv);
        return true;
    }
    
    /**
     * Removes an inventory item from an entity's backpack.
     * @param inv the inventory item to remove
     * @return whether the inventory item WAS in the backpack.
     */
    public boolean removeInventory(Inventory inv)
    {
        boolean ret = ent_backpack.contains(inv);
        ent_backpack.remove(inv);
        return ret;
    }
}
