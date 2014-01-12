package core;

import instances.MapInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import map.GameMap;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.ContextActivationException;
import org.jsfml.window.event.Event;

import render.RenderMap;
import entities.Entity;
import entities.TestFloor;
import util.MapData;

public class Game implements Runnable
{
    private          Thread               myThread;
    private volatile RenderWindow         gameWindow;
    private volatile BlockingQueue<Event> events;
    
    public Game(RenderWindow gamewin, BlockingQueue<Event> evQueue)
    {
        gameWindow = gamewin;
        myThread   = new Thread(this, "gameThread");
        events     = evQueue;
        myThread.start();
    }
    
    public void run()
    {
        List<Event> newEvents = new ArrayList<Event>();
        
        // TESTING
        
        GameMap testmap = new GameMap("test map");
        
        
        int x, y;
        
        for (x = 0; x < 5; x++)
        {
            for (y = 0; y < 5; y++)
            {
                MapData testMD  = new MapData(x, y);
                Entity  testEnt = new Entity();
                
                testmap.addToMap(testEnt, testMD);
                
                if (x == 0 || y == 0 || x == 4 || y == 4)
                {
                    testEnt = new TestFloor();
                    testmap.addToMap(testEnt, testMD);
                }
            }
        }
        
        RenderMap mapRender = new RenderMap(testmap);
        
        MapInstance testInstance = new MapInstance(gameWindow, events, testmap);
        
        // END TESTS
        
        testInstance.run();
    }
    
    private void setActive(boolean mode)
    {
        try { gameWindow.setActive(mode); }
        catch (ContextActivationException e) {}
    }
}
