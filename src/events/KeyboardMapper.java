package events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsfml.window.event.*;

public class KeyboardMapper
{
    private static Map<KeyMapping, Control> km_keymap;
    
    public KeyboardMapper()
    {
        
    }
    
    public List<Control> interpretKeys(List<Event> inEvents)
    {
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
