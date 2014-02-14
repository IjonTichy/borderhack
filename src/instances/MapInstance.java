package instances;


import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.event.Event;

import render.RenderMap;
import map.GameMap;

/**
 * A game instance that runs a map. You'll typically spend your game in here.
 */
public class MapInstance extends I_Instance
{
    private GameMap my_map;
    
    public MapInstance(RenderWindow window, BlockingQueue<Event> events, GameMap map)
    {
        initVars(window, events);
        my_map = map;
        
        RenderMap mapRenderer = new RenderMap(map);
        addRenderer(mapRenderer);
        addFPSCounter();
    }
    
    public I_Instance tick(List<Event> newEvents)
    {
        my_map.doTicks(0);
        return this;
    }
}
