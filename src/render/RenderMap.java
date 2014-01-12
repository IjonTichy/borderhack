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
import util.RenderQuad;

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
    
    public RenderMap(GameMap map)
    {
        render_map   = map;
        render_center = new Vector2f(0, 0);
        render_zoom   = 1;
        
        r_layer = Integer.MIN_VALUE;
    }
    
    // ====
    // == RENDERING
    // ====
    
    private Map<Integer, Map<Texture, VertexArray>> buildVertTree(GameMap map)
    {
        Map<Integer, Map<Texture, VertexArray>> ret = new TreeMap<Integer, Map<Texture, VertexArray>>();

        long rtick = r_thread == null ? 0 : r_thread.getTick();
        
        for (Entity ent: map.getAllEntities())
        {
            if (ent == null) { continue; }  // dunno how that happened but okay
            
            Vector2i position = map.getPosition(ent);
            int x = position.x * Constants.TILE_WIDTH;
            int y = position.y * Constants.TILE_HEIGHT;
                
            RenderQuad toRender = ent.render(rtick);
            VertexArray newPoints = new VertexArray();
            
            for (Vertex v: toRender.points)
            {
                Vector2f point = v.position;
                Vector2f newPoint = new Vector2f(point.x + x, point.y + y);
                
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
        View r = new View();
        
        Vector2i winSize = w.getSize();
        r.setSize(winSize.x, winSize.y);
        r.setCenter(winSize.x / 2, winSize.y / 2);
        
        r.move(render_center);
        r.zoom(render_zoom);
        
        return r;
    }
    
    private static final int   SCROLL_FACTOR  = 8;
    private static final float ZOOM_FACTOR    = 0.1f;
    private static final float ZOOM_DECAY     = 0.5f;
    private static final float SCROLL_SECTION = 0.4f;
    private static final float ZOOM_MIN       = 0.25f;
    private static final float ZOOM_MAX       = 10.0f;
    
    
    private void handleEvents(RenderWindow rWindow, List<Event> newEvents)
    {
        Vector2i mousePos     = Mouse.getPosition(rWindow);
        Vector2i winSize = rWindow.getSize();
        Vector2i winCenter = new Vector2i(winSize.x / 2, winSize.y / 2);
        
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
        
        render_zoom *= (1 + zoom_velocity);
        render_zoom = Math.min(ZOOM_MAX, Math.max(ZOOM_MIN, render_zoom));

        View curView = mapView(rWindow);
        Vector2f newMouse = rWindow.mapPixelToCoords(mousePos, curView);
        
        Vector2f mouseShift = new Vector2f(oldMouse.x - newMouse.x, oldMouse.y - newMouse.y);
        render_center = new Vector2f(render_center.x + mouseShift.x, render_center.y + mouseShift.y);
        
        zoom_velocity *= ZOOM_DECAY;
        if (Math.abs(zoom_velocity) < 0.01) { zoom_velocity = 0; }
    }
    
    
    public void render(RenderWindow rWindow, List<Event> newEvents) throws ContextActivationException
    {
        rWindow.setActive(true);
        
        View rView = mapView(rWindow);
        handleEvents(rWindow, newEvents);
        
        ConstView oldView = rWindow.getView();
        Map<Integer, Map<Texture, VertexArray>> renderMap = buildVertTree(render_map);
        
        rWindow.setView(rView);
        rWindow.clear(Color.BLUE);
        
        
        for (Map.Entry<Integer, Map<Texture, VertexArray>> layerEntry: renderMap.entrySet())
        {
            Map<Texture, VertexArray> texVerts = layerEntry.getValue();
            
            for (Map.Entry<Texture, VertexArray> vertEntry: texVerts.entrySet())
            {
                Texture vertTex   = vertEntry.getKey();
                VertexArray verts = vertEntry.getValue();
                RenderStates state = new RenderStates(vertTex);
                
                rWindow.draw(verts, state);
            }
        }
        
        rWindow.setView(oldView); // avoid side effects if possible
        rWindow.setActive(false);
    }
}
