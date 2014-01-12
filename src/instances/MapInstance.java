package instances;


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
    
    public I_Instance run()
    {
        initRun();
        
        
        while (i_window.isOpen())
        {
            for(Event event : i_eventsForGame)
            {
                System.out.println("GAME EVENT: " + event);
                
                switch (event.type)
                {
                  case CLOSED:
                    i_window.close();
                    break; 
                  
                  default:
                    break;
                }
            }
            
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
        
        endRun();
        
        return null;
    }
}
