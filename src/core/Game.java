package core;

import instances.MapInstance;

import java.util.concurrent.BlockingQueue;

import map.GameMap;
import map.MapData;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.event.Event;

import entities.Entity;
import entities.TestFloor;
import entities.TestWall;

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
        GameMap testmap = new GameMap("test map");
        
        
        int x, y;
        
        for (x = 0; x < 5; x++)
        {
            for (y = 0; y < 5; y++)
            {
                MapData testMD  = new MapData(x, y);
                Entity  testEnt = new TestWall();
                
                testmap.addToMap(testEnt, testMD);
                
                if (x == 0 || y == 0 || x == 4 || y == 4)
                {
                    testEnt = new TestFloor();
                    testmap.addToMap(testEnt, testMD);
                }
            }
        }
        
        MapInstance testInstance = new MapInstance(gameWindow, events, testmap);
        
        // END TESTS
        
        testInstance.run();
    }
}
