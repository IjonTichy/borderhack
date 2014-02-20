package events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.window.event.*;

public class ControlMapper
{
    private Map<KeyMapping, Control> km_keymap;
    
    public ControlMapper()
    {
        this(null);
    }
    
    public ControlMapper(Map<KeyMapping, Control> keys)
    {
        updateKeys(keys);
    }
    
    private void initKeys()
    {
        km_keymap = new HashMap<KeyMapping, Control>();
    }
    
    public void clearKeys()
    {
        initKeys();
    }
    
    public void addKey(KeyMapping from, Control to)
    {
        if (km_keymap == null) { initKeys(); }
        km_keymap.put(from, to);
    }
    
    public void updateKeys(Map<KeyMapping, Control> newKeys)
    {
        if (newKeys == null) { return; }
        if (km_keymap == null) { initKeys(); }
        km_keymap.putAll(newKeys);
    }
    
    public List<Control> interpretKeys(List<Event> inEvents)
    {
        if (km_keymap == null) { return new ArrayList<Control>(); }
        
        List<Control> outControls = new ArrayList<Control>();
        
        for (Event e: inEvents)
        {
            KeyEvent k = e.asKeyEvent();
            if (k == null) { continue; }
            
            for (Map.Entry<KeyMapping, Control> e2: km_keymap.entrySet())
            {
                KeyMapping km = e2.getKey();
                Control    c  = e2.getValue();
                
                if (km.equals(k))
                {
                    outControls.add(c);
                }
            }
        }
        
        return outControls;
    }
}
