package controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsfml.window.event.*;

/**
 * Maps keys to controls.
 *
 */
public class ControlMapper
{
    private static Map<KeyMapping, Set<Control>> s_km_keymap;
    private Set<Control> km_enabled;
    
    
    public ControlMapper()
    {
        this(null);
    }
    
    public ControlMapper(List<Control> payAttentionToThese)
    {
        initKeys();
        enableControls(payAttentionToThese);
    }
    
    
    
    private static void initKeys()
    {
        if (s_km_keymap != null) { return; }
        s_km_keymap = new HashMap<KeyMapping, Set<Control>>();
    }
    
    public static void clearKeys()
    {
        s_km_keymap = null;
        initKeys();
    }
    
    public static void addKey(KeyMapping from, Control to)
    {
        initKeys();
        
        Set<Control> cs = s_km_keymap.get(from);
        
        if (cs == null)
        {
            cs = new HashSet<Control>();
            s_km_keymap.put(from, cs);
        }
        
        cs.add(to);
    }
    
    public static void updateKeys(Map<KeyMapping, Control> newKeys)
    {
        if (newKeys == null) { return; }
        initKeys();

        for (Map.Entry<KeyMapping, Control> e: newKeys.entrySet())
        {
            addKey(e.getKey(), e.getValue());
        }
    }
    
    public static void removeKeyControl(KeyMapping at, Control noMore)
    {
        initKeys();
        
        Set<Control> cs = s_km_keymap.get(at);
        if (cs == null) { return; }
        cs.remove(noMore);
    }
    
    public static void removeKeys(Map<KeyMapping, Control> noMore)
    {
        if (noMore == null) { return; }
        initKeys();
        
        for (Map.Entry<KeyMapping, Control> e: noMore.entrySet())
        {
            removeKeyControl(e.getKey(), e.getValue());
        }
    }
    
    
    
    private void initEnabled()
    {
        if (km_enabled != null) { return; }
        km_enabled = new HashSet<Control>();
    }
    
    public void clearEnabled()
    {
        km_enabled = null;
        initEnabled();
    }
    
    public void enableControl(Control payAttentionToThis)
    {
        initEnabled();
        km_enabled.add(payAttentionToThis);
    }
    
    public void enableControls(Collection<Control> payAttentionToThis)
    {
        if (payAttentionToThis == null) { return; }
        initEnabled();
        km_enabled.addAll(payAttentionToThis);
    }
    
    public void disableControl(Control youDontMatter)
    {
        initEnabled();
        km_enabled.remove(youDontMatter);
    }
    
    public void disableControls(Collection<Control> youDontMatter)
    {
        if (youDontMatter == null) { return; }
        initEnabled();
        km_enabled.removeAll(youDontMatter);
    }
    
    
    
    public List<Control> interpretKeys(List<Event> inEvents)
    {
        if (s_km_keymap == null || km_enabled == null)
            { return new ArrayList<Control>(); }
        
        List<Control> outControls = new ArrayList<>();
        
        for (Event e: inEvents)
        {
            KeyEvent k = e.asKeyEvent();
            if (k == null) { continue; }
            
            Set<Control> c = matchedControls(k);
            outControls.addAll(c);
        }
        
        return outControls;
    }
    
    private Set<Control> matchedControls(KeyEvent key)
    {
        Set<Control> controls = s_km_keymap.get(key);
        if (controls == null) { controls = new HashSet<>(); }
        
        Set<Control> out = new HashSet<>(km_enabled);
        out.retainAll(controls);
        return out;
    }
}
