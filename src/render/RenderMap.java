package render;

import map.GameMap;

import org.jsfml.graphics.*;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.MouseWheelEvent;

import entities.Entity;
import util.Constants;
import util.RenderQuad;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RenderMap extends I_Renderer
{
    private long        render_tick;
    private Clock       render_clock;
    private List<Time>  render_times;
    private GameMap     render_map;
    private static Font render_fps_font;
    
    private Vector2f    render_center;
    private float       render_zoom;
    private float       zoom_velocity;

    public long getRenderTick() { return render_tick; }
    
    public RenderMap(GameMap map)
    {
        render_clock = new Clock();
        render_tick  = 0;
        render_times = new ArrayList<Time>();
        render_map   = map;
        
        if (render_fps_font == null)
        {
            Path fontPath = Paths.get("fonts/VeraMoBd.ttf");
            render_fps_font = new Font();
            
            try
            {
                render_fps_font.loadFromFile(fontPath);
            }
            catch (IOException e)
            {
                System.err.println("Could not find font \"" + fontPath.toString() + "\"");
            }
        }

        render_center = new Vector2f(0, 0);
        render_zoom   = 1;
    }
    
    // ====
    // == RENDERING
    // ====
    
    private Map<Integer, Map<Texture, VertexArray>> buildVertTree(GameMap map)
    {
        Map<Integer, Map<Texture, VertexArray>> ret = new TreeMap<Integer, Map<Texture, VertexArray>>();
        
        for (Entity ent: map.getAllEntities())
        {
            if (ent == null) { continue; }  // dunno how that happened but okay
            
            Vector2i position = map.getPosition(ent);
            int x = position.x * Constants.TILE_WIDTH;
            int y = position.y * Constants.TILE_HEIGHT;
                
            RenderQuad toRender = ent.render(render_tick);
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
    private static final float SCROLL_SECTION = 0.25f;
    private static final float ZOOM_MIN       = 0.25f;
    private static final float ZOOM_MAX       = 10.0f;
    
    private void handleEvents(RenderWindow rWindow, List<Event> newEvents)
    {
        Vector2i mousePos = Mouse.getPosition(rWindow);
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
            float sideMult   =  SCROLL_FACTOR / SCROLL_SECTION;
            
            leftShift  = Math.max(leftShift  - sideAdjust, 0) * sideMult * render_zoom;
            rightShift = Math.max(rightShift - sideAdjust, 0) * sideMult * render_zoom;
            topShift   = Math.max(topShift   - sideAdjust, 0) * sideMult * render_zoom;
            botShift   = Math.max(botShift   - sideAdjust, 0) * sideMult * render_zoom;
            
            Vector2f newCenter = new Vector2f(render_center.x - leftShift + rightShift,
                                              render_center.y - topShift + botShift);
            
            render_center = newCenter;
        }
        
        for (Event e: newEvents)
        {
            switch (e.type)
            {
                case MOUSE_WHEEL_MOVED:
                    MouseWheelEvent m = e.asMouseWheelEvent();
                    zoom_velocity += m.delta * ZOOM_FACTOR;
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
    
    // TODO: When a map format is made, have this use it
    // as of right now, map detection is completely temporary
    public void render(RenderWindow rWindow, List<Event> newEvents)
    {
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
        
        Time curTime = render_clock.getElapsedTime();
        float tickDiff_f;
        
        if (render_times.size() > 0)
        {
            Time prevTime = render_times.get(render_times.size()-1);
            tickDiff_f = curTime.asMilliseconds() - prevTime.asMilliseconds();
        }
        else
        {
            tickDiff_f = curTime.asMilliseconds();
        }
        
        int tickDiff = (int)tickDiff_f;
        
        if (render_times.size() == Constants.MAX_RENDERTIMES)
        {
            int i;
            
            for (i = 1; i < Constants.MAX_RENDERTIMES; i++)
            {
                render_times.set(i-1, render_times.get(i));
            }
            
            render_times.set(Constants.MAX_RENDERTIMES-1, curTime);
        }
        else
        {
            render_times.add(curTime);
        }
        
        int sizeOver = render_times.size() - Constants.MAX_RENDERTIMES;
        if (sizeOver > 0) { render_times = render_times.subList(sizeOver, render_times.size()); }
        
        // This is purely for display; the actual time between frames is below
        if (Constants.DRAW_FPS && render_fps_font != null && render_times.size() > 0)
        {
            float curFPS = -1, prevFPS, frameTime = 0;
            int left  = Math.max(0, render_times.size() - (Constants.FPS_TIMESUSED + 1));
            int right = render_times.size();
            int fpsCount = 0;
            
            List<Time> fpsSlice = render_times.subList(left, right);
            
            for (Time t: fpsSlice)
            {
                if (t == null) { continue; }
                
                prevFPS = curFPS;
                curFPS = t.asSeconds();
                    
                if (prevFPS != -1)
                {
                    frameTime += (curFPS - prevFPS);
                    fpsCount++;
                }
            }
            
            frameTime /= fpsCount;
            
            rWindow.setView(rWindow.getDefaultView());
            
            Text fps = new Text(Integer.toString((int)(1 / frameTime)) + " (" + Long.toString(render_tick) + ")", render_fps_font);
            fps.setPosition(new Vector2f(10, 10));
            
            rWindow.draw(fps);
        }
        
        render_tick += tickDiff;
        rWindow.setView(oldView); // avoid side effects if possible
    }
}
