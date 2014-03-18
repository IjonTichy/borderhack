package entities.inventory;

import org.jsfml.graphics.Color;

import entities.Entity;
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
     * @param grabbing  Who's getting it tonight
     * @return Whether this inventory was grabbable or not
     */
    public boolean grab(Entity grabbing)
    {
        if (inv_owner != null) { return false; }
        
        inv_owner = grabbing;
        setState(IStates.INBACKPACK);
        return true;
    }
    
    /**
     * Disowns this inventory item.
     * @return Whether this inventory was owned by 
     */
    public boolean drop()
    {
        if (inv_owner == null) { return false; }
        
        inv_owner = null;
        setState(IStates.ONGROUND);
        return true;
    }
}