package core;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

import map.GameMap;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2i;
import org.jsfml.window.ContextActivationException;
import org.jsfml.window.Keyboard;
import org.jsfml.window.event.Event;

import render.RenderMap;
import entities.Entity;
import entities.TestFloor;
import util.MapData;

public class Game implements Runnable
{
    private          Thread       myThread;
    private volatile RenderWindow gameWindow;
    private volatile Queue<Event> events;
    private volatile Lock         eventLock;
    
    public Game(RenderWindow gamewin, Queue<Event> evQueue, Lock evLock)
    {
        gameWindow = gamewin;
        myThread   = new Thread(this, "gameThread");
        events     = evQueue;
        eventLock  = evLock;
        myThread.start();
    }
    
    public void run()
    {
        GameMap testmap = new GameMap("test map");
        RenderMap mapRender = new RenderMap(testmap);
        List<Event> newEvents = new ArrayList<Event>();
        
        
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
        
        // END TESTS
        
        
        while(gameWindow.isOpen())
        {
            newEvents.clear();
            eventLock.lock();
            
            try
            {
                for(Event event : events)
                {
                    switch (event.type)
                    {
                      case CLOSED:
                        gameWindow.close();
                        break; 
                      
                      default:
                        newEvents.add(event);
                        break;
                    }
                }
                
                events.clear();
            }
            finally
            {
                eventLock.unlock();
            }

            setActive(true);
            
            gameWindow.clear(Color.BLACK);
            mapRender.render(gameWindow, newEvents);

            gameWindow.display();
            
            setActive(false);
        }
    }
    
    private void setActive(boolean mode)
    {
        try { gameWindow.setActive(mode); }
        catch (ContextActivationException e) {}
    }
}
