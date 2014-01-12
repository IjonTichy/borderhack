package render;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

import util.Constants;

public class FPSRenderer extends I_Renderer
{
    private List<Time>  render_times;
    private Clock       render_clock;
    private static Font render_fps_font;
    
    public FPSRenderer()
    {
        render_times = new ArrayList<Time>();
        render_clock = new Clock();
        r_layer      = Integer.MAX_VALUE;
        
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
    }
    
    
    void render(RenderWindow win, List<Event> events)
    {
        Time curTime = render_clock.getElapsedTime();
        
        long tick = r_thread == null ? -1 : r_thread.getTick();
        
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
            
            win.setView(win.getDefaultView());
            
            Text fps = new Text(Integer.toString((int)(1 / frameTime)) + " (" + Long.toString(tick) + ")", render_fps_font);
            fps.setPosition(new Vector2f(10, 10));
            
            win.draw(fps);
        }
    }
    
}
