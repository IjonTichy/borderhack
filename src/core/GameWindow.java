package core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
        // XInitThreads is not called, causes the event queue to break horribly
        //  as well as random freezing; it ain't pretty
        if (OSHacks.getOS() == OSHacks.OS_TYPES.LINUX)
        {
            Path hackpath = Paths.get("native/linuxhacks.so").toAbsolutePath();
            System.load(hackpath.toString());
            XInitThreads();
        }
        
        RenderWindow gamewin            = new RenderWindow();
        VideoMode    gameres            = new VideoMode(1024, 768);
        BlockingQueue<Event> evQueue    = new LinkedBlockingQueue<Event>();
        
        gamewin.create(gameres, "Borderhack: 8% of your recommended diet", RenderWindow.CLOSE | RenderWindow.TITLEBAR);
        gamewin.setFramerateLimit(Constants.FRAME_LIMIT);


        gamewin.setActive(false);
        Game   game = new Game(gamewin, evQueue);
        
        while (!game.endedGame())
        {
            Event nextEvent = gamewin.pollEvent();

            if (nextEvent != null) { evQueue.add(nextEvent); }
            
            try { Thread.sleep(0, 1000); }
            catch (InterruptedException e) { /* I don't give a shit */ }
        }
        
        gamewin.close();
    }
    
}