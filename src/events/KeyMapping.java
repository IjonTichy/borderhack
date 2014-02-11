package events;

import org.jsfml.window.Keyboard;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

public class KeyMapping
{
    public final Keyboard.Key   k_key;
    public final boolean        k_state;
    public final boolean        k_shift;
    public final boolean        k_control;
    
    public KeyMapping(Keyboard.Key key, boolean upDown)
    {
        this(key, upDown, false, false);
    }
    
    public KeyMapping(Keyboard.Key key, boolean upDown, boolean withShift, boolean withControl)
    {
        k_key       = key;
        k_state     = upDown;
        k_shift     = withShift;
        k_control   = withControl;
    }
    
    public boolean matches(Event other)
    {
        KeyEvent k = other.asKeyEvent();
        if (k == null) { return false; }
        return matches(k);
    }
    
    private static boolean eventState(KeyEvent k)
    {
        if (k.type == Event.Type.KEY_PRESSED) { return true; }
        return false;
    }
    
    public int hashCode()
    {
        int ret = 0;
        int ctrl = k_control ? 1 : 0;
        int shft = k_shift   ? 1 : 0;
        int stte = k_state   ? 1 : 0;
        
        ret |= ctrl;
        ret |= shft << 1;
        ret |= stte << 2;
        ret |= k_key.ordinal() << 3;
        
        return ret;
    }
    
    public boolean equals(KeyEvent other)
    {
        if (k_key     != other.key
         || k_state   != eventState(other)
         || k_shift   != other.shift
         || k_control != other.control) { return false; }
        
        return true;
    }
    
    public boolean equals(KeyMapping other)
    {
        if (k_key     != other.k_key
         || k_state   != other.k_state
         || k_shift   != other.k_shift
         || k_control != other.k_control) { return false; }
               
        return true;
    }
}
