package modes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import util.ActionUnavailableException;
import map.GameMap;
import entities.thinkers.Thinker;
import events.Control;

abstract public class Mode
{
    protected Thinker       m_controller;
    protected Method        m_nextaction;
    private   List<Control> m_controls;
    
    public Mode()
    {
        this(null);
    }
    
    public Mode(Thinker e)
    {
        m_controller = e;
        m_nextaction = null;
        m_controls   = new ArrayList<>();
    }
    
    protected Method getAction(String actionName) throws ActionUnavailableException
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
            System.err.println("Requested action \"" + actionName + "\" on mode "
                    + this.getClass().getSimpleName() + ", and it doesn't exist");
            throw(new ActionUnavailableException());
        }
        catch (SecurityException e)
        {
            System.err.println("Requested action \"" + actionName + "\" on mode "
                    + this.getClass().getSimpleName() + ", and it is inaccessible");
            throw(new ActionUnavailableException());
        }
        
        return ret;   
    }
    
    public Thinker getController()
    {
        return m_controller;
    }
    
    public Method getCurrentAction()
    {
        if (m_nextaction == null)
        {
            try
            {
                return getAction("defaultAction");
            }
            catch (ActionUnavailableException e)
            {
                System.err.println("Oh jesus christ mode \"" + this.getClass().getSimpleName()
                                    + "\" doesn't have a default action jesus christ this is bad");
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        return m_nextaction;
    }
    
    public Long act(long tick, GameMap map)
    {
        return doaction(getCurrentAction(), tick, map);
    }
    
    protected Long doaction(Method action, long tick, GameMap map)
    {
        Long result = null;
        
        try
        {
            result = (Long)action.invoke(this, tick, map);
            clearControls();
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("Mode " + getClass().getSimpleName() + ", action " + action.getName() + " either accepted the wrong arguments,");
            System.err.println("or it returned the wrong arguments.");
            System.err.println("(actions should take arguments (long mapTick, GameMap map), and return a DelayedAction)");
            System.err.println("Stack trace:");
            e.printStackTrace();
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            System.err.println("Mode " + getClass().getSimpleName() + ", action " + action.getName() + " fucked up:");
            e.printStackTrace();
        }
        
        return result;
    }
    
    public void giveControl(Control c)
    {
        m_controls.add(c);
    }
    
    public void giveControls(List<Control> c)
    {
        m_controls.addAll(c);
    }
    
    private void clearControls()
    {
        m_controls.clear();
    }
    
    /**
     * Default action.
     * @param tick  the tick the map is on
     * @param map   the map itself
     * @return the amount of ticks until the map should call this mode again
     */
    abstract public long defaultAction(Long tick, GameMap map);
}
