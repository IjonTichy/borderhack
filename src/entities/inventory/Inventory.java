package entities.inventory;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector3i;

import entities.Entity;
import map.MapData;
import modes.Mode;
import modes.inventory.InvMode;

abstract public class Inventory extends Entity
{   
    public enum IStates { ONGROUND, INBACKPACK, EQUIPPED };
    
    protected IStates           inv_currentstate;
    protected IStates           inv_previousstate;
    protected Entity            inv_owner;
    
    public IStates getCurrentState()
    {
        return inv_currentstate;
    }
    
    abstract public String  getName();
    abstract public Color   getDisplayColor();
    
    public Entity owner()
    {
        return inv_owner;
    }
    
    protected boolean setState(IStates state)
    {
        boolean changing = inv_currentstate != state;
        
        if (changing)
        {
            inv_previousstate = inv_currentstate;
            inv_currentstate  = state;
            
            for (Mode m: ent_modes.keySet())
            {
                if (!(m instanceof InvMode)) { continue; }
                ((InvMode)m).handleStateSwitch(inv_previousstate, inv_currentstate);
            }
        }
        
        return changing;
    }
    
    /**
     * Sets this inventory to be owned by the entity given.
     * If grabbed, this is removed from the map.
     * 
     * @param grabbing  Who's getting it tonight
     * @return Whether this inventory was grabbable or not
     */
    public boolean grab(Entity grabbing)
    {
        if (grabbing == null) { return false; }
        if (inv_owner != null) { return false; }
        
        inv_owner = grabbing;
        setState(IStates.INBACKPACK);
        ent_map.remove(this);
        return true;
    }
    
    /**
     * Disowns this inventory item.
     * @return Whether this inventory was owned by 
     */
    public boolean drop()
    {
        return drop(new Vector3i(0,0,0));
    }
    
    public boolean drop(Vector3i dropOffset)
    {
        if (inv_owner == null) { return false; }
        
        inv_owner = null;
        setState(IStates.ONGROUND);
        
        Vector3i ownPos = inv_owner.getPosition();
        
        if (ownPos != null && dropOffset != null)
        {
            MapData newPos = new MapData(ownPos.x + dropOffset.x,
                                         ownPos.y + dropOffset.y,
                                         ownPos.z + dropOffset.z);
            
            inv_owner.getMap().add(this, newPos);
        }
        return true;
    }
}