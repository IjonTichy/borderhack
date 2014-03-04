package core;

import instances.I_Instance;
import instances.MapInstance;

import java.util.concurrent.BlockingQueue;

import map.GameMap;
import map.MapData;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.event.Event;

import entities.Entity;
import entities.TestFloor;
import entities.TestWall;
import entities.thinkers.TestThinker;
import entities.thinkers.player.Player;

public class Game implements Runnable
{
    private          Thread               myThread;
    private volatile RenderWindow         gameWindow;
    private volatile BlockingQueue<Event> events;
    private volatile boolean              endedGame;
    
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
        Player  testply = new Player();
        
        int x, y;
        
        for (x = 0; x < 16; x++)
        {
            for (y = 0; y < 16; y++)
            {
                MapData testMD  = new MapData(x, y);
                Entity  testEnt = new TestWall();
                
                testmap.addToMap(testEnt, testMD);
                
                if (x == 0 || y == 0 || x == 15 || y == 15)
                {
                    testEnt = new TestFloor();
                    testmap.addToMap(testEnt, testMD);
                }
            }
        }
        
        testmap.addToMap(new TestThinker(), new MapData(1, 1));
        testmap.addToMap(testply, new MapData(2, 2));
        
        I_Instance gameInstance = new MapInstance(gameWindow, events, testmap);
        
        // END TESTS
        
        while (gameInstance != null)
        {
            gameInstance = gameInstance.run();
        }
        
        endedGame = true;
    }
    
    public boolean endedGame() { return this.endedGame; }
}
