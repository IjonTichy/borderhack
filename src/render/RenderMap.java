package render;

import map.GameMap;

import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.ContextActivationException;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.MouseWheelEvent;

import entities.Entity;
import util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RenderMap extends I_Renderer
{
    private GameMap     render_map;
    
    private Vector2f    render_center;
    private float       render_zoom;
    private float       zoom_velocity;
    private float       render_xsize;
    private float       render_ysize;
    private float       render_xstart;
    private float       render_ystart;
    
    private List<Class<? extends Entity>> null_warned;
    
    public RenderMap(GameMap map)
    {
        render_map    = map;
        render_center = new Vector2f(0, 0);
        render_zoom   = 1;
        null_warned   = new ArrayList<Class<? extends Entity>>();
        
        r_layer = Integer.MIN_VALUE;
        
        render_xsize = 0;
        render_ysize = 0;
        render_xstart = 0;
        render_ystart = 0;
    }
    
    public Vector2f getRenderedSize()
    {
        return new Vector2f(render_xsize - render_xstart, render_ysize - render_ystart);
    }
    
    /**
     * Returns a rectangle in which you can move around the center of the view
     * without edges showing, unless it's too small.
     * @return
     */
    private FloatRect getScrollRect(RenderWindow w)
    {
        Vector2i winSize = w.getSize();
        Vector2f mapSize = getRenderedSize();
        
        if (render_zoom == 0) // this should never happen
        {
            return new FloatRect(mapSize.x/2, mapSize.y/2, 0, 0);
        }
        
        float leftBuffer = (winSize.x * render_zoom) / 2f;
        float bufWidth   = mapSize.x - (leftBuffer * 2);
        float topBuffer  = (winSize.y * render_zoom) / 2f;
        float bufHeight  = mapSize.y - (topBuffer * 2);
        
        if (bufWidth < 0)
        {
            leftBuffer  = mapSize.x / 2;
            bufWidth = 0;
        }
        
        if (bufHeight < 0)
        {
            topBuffer = mapSize.y / 2;
            bufHeight = 0;
        }
        
        leftBuffer += render_xstart;
        topBuffer  += render_ystart;
        
        return new FloatRect(leftBuffer, topBuffer, bufWidth, bufHeight);
    }
    
    // ====
    // == RENDERING
    // ====
    
    private Map<Integer, Map<Texture, VertexArray>> buildVertTree(GameMap map)
    {
        Map<Integer, Map<Texture, VertexArray>> ret = new TreeMap<Integer, Map<Texture, VertexArray>>();
        
        render_xsize  = 0;
        render_ysize  = 0;
        render_xstart = Float.MAX_VALUE;
        render_ystart = Float.MAX_VALUE;

        long rtick = r_thread == null ? 0 : r_thread.getTick();
        
        for (Entity ent: map.getAllEntities())
        {   
            if (ent == null) { continue; }  // dunno how that happened but okay
            
            Vector2i position = map.getPosition(ent);
            int x = position.x * Constants.TILE_WIDTH;
            int y = position.y * Constants.TILE_HEIGHT;
                
            RenderQuad toRender = ent.render(rtick);
            VertexArray newPoints = new VertexArray();
            
            if (toRender == null)
            {
                if (!null_warned.contains(ent.getClass()))
                {
                    System.err.println("WARNING: Got nothing to render for " + ent.getClass().getSimpleName());
                    System.err.println("If it was rendering, its animation died.");
                    System.err.println("If you meant for this to be invisible, set ent_invisible to true.");
                    null_warned.add(ent.getClass());
                }    
                
                continue;
            }
            
            for (Vertex v: toRender.points)
            {
                Vector2f point = v.position;
                float px = point.x + x;
                float py = point.y + y;

                render_xstart = Math.min(render_xstart, px);
                render_ystart = Math.min(render_ystart, py);
                render_xsize = Math.max(render_xsize, px);
                render_ysize = Math.max(render_ysize, py);
                
                Vector2f newPoint = new Vector2f(px, py);
                
                Vertex newV = new Vertex(newPoint, v.color, v.texCoords);
                newPoints.add(newV);
            }
            
            Map<Texture, VertexArray> layerMap = ret.get(toRender.layer);
            
            if (layerMap == null)
            {
                layerMap = new HashMap<Texture, VertexArray>();
                ret.put(toRender.layer, layerMap);
            }
            
            VertexArray texVerts = (VertexArray)layerMap.get(toRender.texture);
            
            if (texVerts == null)
            {
                texVerts = new VertexArray();
                texVerts.setPrimitiveType(PrimitiveType.QUADS);
                layerMap.put(toRender.texture, texVerts);
            }
            
            texVerts.addAll(newPoints);
        }
        
        return ret;
    }
    
    private View mapView(RenderWindow w)
    {
        RenderView r = new RenderView();
        
        Vector2i winSize = w.getSize();
        r.setSize(winSize.x, winSize.y);
        r.setCenter(render_center);
        
        r.zoom(render_zoom);
        
        return r;
    }
    
    private static final int   SCROLL_FACTOR  = 8;
    private static final float ZOOM_FACTOR    = 0.1f;
    private static final float ZOOM_DECAY     = 0.5f;
    private static final float SCROLL_SECTION = 0.4f;
    private static final float ZOOM_MIN       = 0.1f;
    private static final float ZOOM_MAX       = 10.0f;
    
    
    private void handlePosition(RenderWindow rWindow, List<Event> newEvents)
    {
        Vector2i mousePos   = Mouse.getPosition(rWindow);
        Vector2i winSize    = rWindow.getSize();
        Vector2i winCenter  = new Vector2i(winSize.x / 2, winSize.y / 2);
        
        if (!(mousePos.x < 0 || mousePos.x > winSize.x || mousePos.y < 0 || mousePos.y > winSize.y))
        {
            float leftShift  = (float)(winCenter.x - mousePos.x) / winCenter.x;
            float rightShift = (float)(mousePos.x - winCenter.x) / winCenter.x;
            float topShift   = (float)(winCenter.y - mousePos.y) / winCenter.y;
            float botShift   = (float)(mousePos.y - winCenter.y) / winCenter.y;
            
            // Restrict to outer 25% of screen, scale to 1, then scale to SCROLL_FACTOR
    
            float sideAdjust = 1 - SCROLL_SECTION;
            float sideMult   =     SCROLL_FACTOR / SCROLL_SECTION;
            
            leftShift  = Math.max(leftShift  - sideAdjust, 0) * sideMult;
            rightShift = Math.max(rightShift - sideAdjust, 0) * sideMult;
            topShift   = Math.max(topShift   - sideAdjust, 0) * sideMult;
            botShift   = Math.max(botShift   - sideAdjust, 0) * sideMult;
            
            float xShift = leftShift - rightShift;
            float yShift = topShift  - botShift;
            
            Vector2f newCenter = new Vector2f(render_center.x - (xShift * render_zoom),
                                              render_center.y - (yShift * render_zoom));
            
            render_center = newCenter;
        }
        
        for (Event e: newEvents)
        {
            switch (e.type)
            {
                case MOUSE_WHEEL_MOVED:
                    MouseWheelEvent m = e.asMouseWheelEvent();
                    zoom_velocity -= m.delta * ZOOM_FACTOR;
                    break;
                 
                default:
                    break;
            }
        }
        
        View oldView = mapView(rWindow);
        Vector2f oldMouse = rWindow.mapPixelToCoords(mousePos, oldView);
        
        float zoommod;
        
        if (zoom_velocity < 0) { zoommod = 1 / (1 - zoom_velocity); }
        else { zoommod = 1 + zoom_velocity; }
        
        render_zoom *= zoommod;
        render_zoom = Math.min(ZOOM_MAX, Math.max(ZOOM_MIN, render_zoom));

        View curView = mapView(rWindow);
        Vector2f newMouse = rWindow.mapPixelToCoords(mousePos, curView);
        
        Vector2f mouseShift = new Vector2f(oldMouse.x - newMouse.x, oldMouse.y - newMouse.y);
        render_center = new Vector2f(render_center.x + mouseShift.x, render_center.y + mouseShift.y);
        
        zoom_velocity *= ZOOM_DECAY;
        if (Math.abs(zoom_velocity) < 0.01) { zoom_velocity = 0; }
        
        if (Math.abs(render_zoom - 1) < 0.01) { render_zoom = 1; }
        

        // Cap panning area so you can't see beyond edges if at all possible
        // Don't move this up, it makes the zooming jittery and nasty
        
        FloatRect validCoords = getScrollRect(rWindow);
        
        float rx = Math.max(validCoords.left, Math.min(validCoords.left + validCoords.width, render_center.x));
        float ry = Math.max(validCoords.top,  Math.min(validCoords.top + validCoords.height, render_center.y));
        
        render_center = new Vector2f(rx, ry);
    }
    
    
    public void render(RenderWindow rWindow, List<Event> newEvents)
    {
        try { rWindow.setActive(true); }
        catch (ContextActivationException e) { return; } // window closed
        
        View rView = mapView(rWindow);
        handlePosition(rWindow, newEvents);
        
        ConstView oldView = rWindow.getView();
        Map<Integer, Map<Texture, VertexArray>> renderMap = buildVertTree(render_map);
        
        rWindow.setView(rView);
        rWindow.clear(Color.BLUE);
        
        
        for (Map.Entry<Integer, Map<Texture, VertexArray>> layerEntry: renderMap.entrySet())
        {
            Map<Texture, VertexArray> texVerts = layerEntry.getValue();
            
            for (Map.Entry<Texture, VertexArray> vertEntry: texVerts.entrySet())
            {
                Texture vertTex    = vertEntry.getKey();
                VertexArray verts  = vertEntry.getValue();
                RenderStates state = new RenderStates(vertTex);
                
                
                rWindow.draw(verts, state);
            }
        }
        
        rWindow.setView(oldView); // avoid side effects if possible
        
        try { rWindow.setActive(false); }
        catch (ContextActivationException e) { return; } // window closed
    }
}
