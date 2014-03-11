package instances;


import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.event.Event;

import controls.Control;
import controls.ControlMapper;
import controls.KeyMapping;
import entities.thinkers.player.Player;
import render.RenderMap;
import map.GameMap;

/**
 * A game instance that runs a map. You'll typically spend your game in here.
 */
public class MapInstance extends I_Instance
{
    private GameMap mi_map;
    private static ControlMapper s_mi_controlmapper;
    
    public MapInstance(RenderWindow window, BlockingQueue<Event> events, GameMap map)
    {
        initVars(window, events);
        mi_map = map;
        
        RenderMap mapRenderer = new RenderMap(map);
        addRenderer(mapRenderer);
        addFPSCounter();
        setupControlMapper();
    }
    
    private static void setupControlMapper()
    {
        if (s_mi_controlmapper != null) { return; }
        
        s_mi_controlmapper = new ControlMapper();  
        
        for (Player.PControl p: Player.PControl.values())
        {
            Control    c = p.control();
            KeyMapping k = p.key();
            
            s_mi_controlmapper.enableControl(c);
            ControlMapper.addKey(k, c);
        }
    }
    
    public I_Instance tick(List<Event> newEvents)
    {
        List<Control> outEvents = s_mi_controlmapper.interpretKeys(newEvents);
        mi_map.sendControls(outEvents);
        
        mi_map.doTicks(0);
        return this;
    }
}
