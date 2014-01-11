package util;

import java.lang.reflect.Method;

public class DelayedAction
{
    public final long   tickCount;
    public final Method nextAction;
    
    public DelayedAction(long t, Method a)
    {
        tickCount  = t;
        nextAction = a;
    }
}