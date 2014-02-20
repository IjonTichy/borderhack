package controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsfml.window.event.*;

public class ControlMapper
{
    private static Map<KeyMapping, Control> km_s_keymap;
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
        if (km_s_keymap != null) { return; }
        km_s_keymap = new HashMap<KeyMapping, Control>();
    }
    
    public static void clearKeys()
    {
        km_s_keymap = null;
        initKeys();
    }
    
    public static void addKey(KeyMapping from, Control to)
    {
        initKeys();
        km_s_keymap.put(from, to);
    }
    
    public static void updateKeys(Map<KeyMapping, Control> newKeys)
    {
        if (newKeys == null) { return; }
        initKeys();
        km_s_keymap.putAll(newKeys);
    }
    
    public static void removeKey(KeyMapping noMore)
    {
        initKeys();
        km_s_keymap.remove(noMore);
    }
    
    public static void removeKeys(Collection<KeyMapping> noMore)
    {
        if (noMore == null) { return; }
        initKeys();
        
        for (KeyMapping km: noMore)
        {
            km_s_keymap.remove(km);
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
        if (km_s_keymap == null || km_enabled == null)
            { return new ArrayList<Control>(); }
        
        List<Control> outControls = new ArrayList<Control>();
        
        for (Event e: inEvents)
        {
            KeyEvent k = e.asKeyEvent();
            if (k == null) { continue; }
            
            for (Map.Entry<KeyMapping, Control> e2: km_s_keymap.entrySet())
            {
                KeyMapping km = e2.getKey();
                Control    c  = e2.getValue();
                
                if (km.equals(k) && km_enabled.contains(c))
                {
                    outControls.add(c);
                }
            }
        }
        
        return outControls;
    }
}
