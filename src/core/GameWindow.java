package core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.ContextActivationException;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

import util.Constants;
import util.OSHacks;


public class GameWindow
{
    private static native void XInitThreads();
    
    public static void main(String[] args) throws ContextActivationException
    {
        if (OSHacks.getOS() == OSHacks.OS_TYPES.LINUX)
        {
            Path hackpath = Paths.get("native/linuxhacks.so").toAbsolutePath();
            System.load(hackpath.toString());
            XInitThreads();
        }
        
        RenderWindow gamewin = new RenderWindow();
        VideoMode    gameres = new VideoMode(800, 600);
        Queue<Event> evQueue = new LinkedList<Event>();
        Lock evLock = new ReentrantLock();
        
        gamewin.create(gameres, "Borderhack: 0% of your recommended diet", RenderWindow.CLOSE | RenderWindow.TITLEBAR);
        gamewin.setFramerateLimit(Constants.FRAME_LIMIT);


        gamewin.setActive(false);
        @SuppressWarnings("unused")   // it is used you cuntwagon, just go with me
        Game   game = new Game(gamewin, evQueue, evLock);
        
        while (gamewin.isOpen())
        {
            Event nextEvent = gamewin.waitEvent();

            evLock.lock();
            if (nextEvent != null) { evQueue.add(nextEvent); }
            evLock.unlock();
            
            if (nextEvent.type == Event.Type.CLOSED) { break; }
        }
    }
    
}