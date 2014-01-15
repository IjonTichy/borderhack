package modes;

import java.lang.reflect.Method;

import map.GameMap;
import entities.Entity;

abstract public class Mode
{
    protected Entity m_controller;
    protected Method m_nextaction;
    
    public Mode(Entity e) throws ActionUnavailableException
    {
        m_controller = e;
        m_nextaction = getAction("defaultAction");
    }
    
    private Method getAction(String actionName) throws ActionUnavailableException
    {
        @SuppressWarnings("rawtypes")
        Class[] args = {Long.class, GameMap.class};
        Method ret;
        
        try
        {
            ret = this.getClass().getMethod(actionName, args);
        }
        catch (NoSuchMethodException e)
        {
            System.err.println("Requested action \"" + actionName + "\" on mode"
                    + this.getClass().getSimpleName() + ", and it doesn't exist");
            e.printStackTrace();
            throw(new ActionUnavailableException());
        }
        catch (SecurityException e)
        {
            System.err.println("Requested action \"" + actionName + "\" on mode"
                    + this.getClass().getSimpleName() + ", and it is inaccessible");
            e.printStackTrace();
            throw(new ActionUnavailableException());
        }
        
        return ret;   
    }
    
    public Method getCurrentAction()
    {
        return m_nextaction;
    }
    
    abstract public Method defaultAction(long tick, GameMap map);
}
