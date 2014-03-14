package entities.inventory;

import java.util.List;

import org.jsfml.graphics.Color;

import entities.Entity;
import modes.inventory.InvMode;

abstract public class Inventory extends Entity
{
    /**
     * Inventory modes. They run when an inventory item is in your inventory.
     */
    protected List<InvMode>        inv_modes;
    protected List<InvMode>        inv_activemodes;
    
    public enum IStates { ONGROUND, INBACKPACK, EQUIPPED };
    
    protected IStates           inv_currentstate;
    protected IStates           inv_previousstate;
    
    public IStates getCurrentState()
    {
        return inv_currentstate;
    }
    
    abstract public String  getName();
    abstract public Color   getDisplayColor();
}