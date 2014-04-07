package modes.inventory;

import java.lang.reflect.Method;

import util.ActionUnavailableException;
import entities.inventory.Inventory;
import map.GameMap;
import modes.Mode;

abstract public class InvMode extends Mode
{
    protected Inventory im_controller;
    protected Method    im_nextbackpackaction;
    protected Method    im_nextequipaction;
    
    public InvMode(Inventory e) throws ActionUnavailableException
    {
        super(e);  // mostly unused
        
        im_controller           = e;
        m_nextaction            = getAction("defaultAction");
        im_nextbackpackaction   = getAction("defaultBackpackAction");
        im_nextequipaction      = getAction("defaultEquipAction");
    }
    
    public Method getCurrentAction()
    {
        switch (im_controller.getCurrentState())
        {
            case EQUIPPED:   return getActionDefault(im_nextequipaction,    "defaultEquipAction");
            case INBACKPACK: return getActionDefault(im_nextbackpackaction, "defaultBackpackAction");
            case ONGROUND:   return getActionDefault(m_nextaction,          "defaultAction");
        }
        
        return null;
    }
    
    public void handleStateSwitch(Inventory.IStates from, Inventory.IStates to)
    {
        
    }
    
    abstract public void mapToBackpack(long tick, GameMap map);
    abstract public void backpackToMap(long tick, GameMap map);
    
    abstract public void backpackToEquip(long tick, GameMap map);
    abstract public void equipToBackpack(long tick, GameMap map);
    
    abstract public void mapToEquip(long tick, GameMap map);
    abstract public void equipToMap(long tick, GameMap map);
}
