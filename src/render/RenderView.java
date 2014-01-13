package render;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import anim.Animation;

public class RenderView extends View
{
    private SortedMap<Integer, List<Animation>> v_animations;
    
    public RenderView()
    {
        super();
        init();
    }
    
    public RenderView(FloatRect r)
    {
        super(r);
        init();
    }
    
    public RenderView(Vector2f c, Vector2f s)
    {
        super(c, s);
        init();
    }
    
    private void init()
    {
        v_animations = new TreeMap<Integer, List<Animation>>();
    }
    
    public static RenderView fromView(View v)
    {
        RenderView ret = new RenderView();
        ret.setCenter(v.getCenter());
        ret.setSize(v.getSize());
        ret.setViewport(v.getViewport());
        ret.setRotation(v.getRotation());
        
        return ret;
    }
    
    public boolean hasAnimation(Animation a)
    {
        for (List<Animation> l: v_animations.values())
        {
            if (l.contains(a)) { return true; }
        }
        
        return false;
    }
    
    public void addAnimation(Animation a, int l)
    {
        if (hasAnimation(a))
        {
            removeAnimation(a);
        }
        
        List<Animation> layer = v_animations.get(l);
        
        if (layer == null)
        {
            layer = new ArrayList<Animation>();
            v_animations.put(l, layer);
        }
        
        layer.add(a);
    }
    
    public void removeAnimation(Animation a)
    {
        for (List<Animation> l: v_animations.values())
        {
            l.remove(a);
        }
    }
    
    // TODO: renderAnimations
}
