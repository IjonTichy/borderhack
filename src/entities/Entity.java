package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import map.GameMap;
import map.MapData;
import modes.Mode;
import modes.MovementMode;

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
    protected GameMap       ent_map;

    protected float         ent_health;

    protected Map<Mode, Double> ent_modes;
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
        ent_map         = null;
        
        defaults();
        init();
        setDefaultMode();
    }

    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        return false;
    }
    
    public GameMap getMap() { return ent_map; }
    
    
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
     * Associates this entity with a GameMap. If it isn't in the map, add itself
     * to the map at coordinates (0, 0, 0). If it is currently in a map, either
     * switch to that map or do nothing, depending on what force is set to.
     * 
     * @param map   The map to associate with.
     * @param force Force this entity to switch to that map?
     * @return Whether this entity is now on the map given.
     */
    public boolean associateWithMap(GameMap map, boolean force)
    {
        if (ent_map != null && ent_map != map)
        {
            if (force) { ent_map.remove(this); }
            else { return false; }
        }
        
        if (!map.hasEntity(this))
        {
            map.add(this, new MapData(0, 0, 0));
        }
        
        ent_map = map;
        return true;
    }
    
    /**
     * Disassociates this entity with the map it's currently associated with, and
     * removes it from said map.
     */
    public void disassociateFromMap()
    {
        if (ent_map == null) { return; }
        ent_map.remove(this);
        ent_map = null;
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
    public Map<Mode, Double> getModes()
    {
        return new HashMap<Mode, Double>(ent_modes);
    }

    /**
     * Adds a mode to an entity. Use this to apply debuffs or whatnot to things.
     * 
     * @param m     the mode to add.
     * @param tick  the tick the mode should next be called on. Should be absolute.
     * @return nothing.
     */
    public void updateMode(Mode m, double t)
    {
        ent_modes.put(m, t);
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
    public Map<Mode, Double> think(double tickDelta, GameMap map)
    {
        Map<Mode, Double> ret = new HashMap<>();
        double mapTick = map.getTick();
        double endTick = mapTick + tickDelta;
        
        tickDown(tickDelta);
        
        Set<Map.Entry<Mode, Double>> modes = ent_modes.entrySet();
        
        for (Map.Entry<Mode, Double> next: modes)
        {
            Mode   mode     = next.getKey();
            Double timeLeft = next.getValue();
            
            if (timeLeft == null || timeLeft > endTick) { continue; }
            double nextTick = mapTick + mode.act();
            
            updateMode(mode, nextTick);
            ret.put(mode, nextTick);
        }
        
        return ret;
    }

    /**
     * Only meant to be called by the GameMap. Ticks down every mode in the
     * entity by tickDelta ticks.
     * 
     * @param d how many ticks to tick off
     */
    public void tickDown(double d)
    {
        Set<Map.Entry<Mode, Double>> modes = ent_modes.entrySet();
        for (Map.Entry<Mode, Double> next: modes)
        {
            Mode   mode  = next.getKey();
            Double delay = next.getValue();
            
            double t = delay - d;
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
        
        if (inv.grab(this))
        {
            ent_backpack.add(inv);
            return true;
        }
        
        return false;
    }
    
    /**
     * Removes an inventory item from an entity's backpack.
     * @param inv the inventory item to remove
     * @return whether the inventory item WAS in the backpack.
     */
    public boolean removeInventory(Inventory inv)
    {
        if (inv.owner() != this) { return false; }
        
        if (inv.drop())
        {
            boolean inInv = ent_backpack.contains(inv);
            if (inInv) ent_backpack.remove(inv);
            return inInv;
        }
        
        return false;
    }
    
    /**
     * You want to know what's in an entity's backpack? Here you go.
     * @return A copy of the entity's backpack.
     */
    public List<Inventory> getBackpack()
    {
        return new ArrayList<Inventory>(ent_backpack);
    }
    
    /**
     * Check if a certain inventory item is in this entity's backpack.
     * @param inv The inventory item to check.
     * @return Whether said item is in this entity's backpack.
     */
    public boolean inBackpack(Inventory inv)
    {
        return ent_backpack.contains(inv);
    }

    public Vector3i getPosition()
    {
        if (ent_map == null) { return null; }
        return ent_map.getPosition(this);
    }

    /**
     * Do a 2D movement with a duration of 0 ticks.
     * @param delta     How far to move.
     * @return The MovementMode created from this.
     */
    public MovementMode move(Vector2i delta)
    {
        return move(new Vector3i(delta.x, delta.y, 0), 0);
    }

    /**
     * Do a 2D movement.
     * @param delta     How far to move.
     * @param time      How long the movement should take.
     * @return The MovementMode created from this.
     */
    public MovementMode move(Vector2i delta, double time)
    {
        return move(new Vector3i(delta.x, delta.y, 0), time);
    }

    /**
     * Do a movement with a duration of 0 ticks.
     * @param delta     How far to move.
     * @return The MovementMode created from this.
     */
    public MovementMode move(Vector3i delta)
    {
        return move(delta, 0);
    }
    
    /**
     * Do a movement.
     * @param delta     How far to move.
     * @param time      How long the movement should take.
     * @return The MovementMode created from this.
     */
    public MovementMode move(Vector3i delta, double time)
    {
        return new MovementMode(delta, time);
    }
}
