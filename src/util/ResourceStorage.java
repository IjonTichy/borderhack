package util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2i;


public class ResourceStorage
{
    private static Map<String, Texture> textures_stored     = new HashMap<String, Texture>();
    
    private ResourceStorage() {}
    
    public static Texture getTexture(String key)
    {
        return textures_stored.get(key);
    }
    
    public static void setTexture(String key, Texture newtex)
                throws TextureAlreadyInException
    {
        setTexture(key, newtex, false);
    }
    
    public static void setTexture(String key, Texture newtex, boolean overwrite)
                throws TextureAlreadyInException
    {
        if (getTexture(key) != null && !overwrite)
        {
            throw new TextureAlreadyInException();
        }
        
        textures_stored.put(key, newtex);
    }
    
    public static Texture loadTexturePath(String path)
                throws TextureAlreadyInException, IOException
    {
        return loadTexturePath(path, false);
    }
    
    public static Texture loadTexturePath(String path, boolean overwrite)
                throws TextureAlreadyInException, IOException
    {
        if (getTexture(path) != null && !overwrite)
        {
            throw new TextureAlreadyInException();
        }
        
        Texture newtex = new Texture();
        newtex.loadFromFile(Paths.get(path));

        textures_stored.put(path, newtex);
        return newtex;
    }

    private static Map<String, List<Texture>> anims_stored  = new HashMap<String, List<Texture>>();
    
    
    
    // ====
    // == ANIMATIONS
    // ====
    
    public static List<Texture> getAnimation(String key)
    {
        return anims_stored.get(key);
    }
    
    public static void setAnimation(String key, List<Texture> newanim, boolean overwrite)
                throws TextureAlreadyInException
    {
        if (getAnimation(key) != null && !overwrite)
        {
            throw new TextureAlreadyInException();
        }
        
        anims_stored.put(key, newanim);
    }
    
    public static List<Texture> loadAnimPath(String path, Vector2i frameSize)
                throws TextureAlreadyInException, IOException, TextureCreationException
    {
        return loadAnimPath(path, frameSize, false);
    }
    
    public static List<Texture> loadAnimPath(String path, Vector2i frameSize, boolean overwrite)
                throws TextureAlreadyInException, IOException, TextureCreationException
    {
        if (getAnimation(path) != null && !overwrite)
        {
            throw new TextureAlreadyInException();
        }
        
        Image rawimg = new Image();
        List<Texture> frames = new ArrayList<Texture>();
        
        rawimg.loadFromFile(Paths.get(path));
        Vector2i imgSize = rawimg.getSize();
        
        // have at least one frame get made
        frameSize = new Vector2i(Math.min(imgSize.x, frameSize.x),
                                 Math.min(imgSize.y, frameSize.y));
        
        int xframes = imgSize.x / frameSize.x;
        int yframes = imgSize.y / frameSize.y;
        
        int x, y;
        
        for (y = 0; y < yframes; y++)
        {
            for (x = 0; x < xframes; x++)
            {
                Image paddedFrame = new Image();
                IntRect rect = new IntRect(x * frameSize.x, y * frameSize.y, frameSize.x, frameSize.y);
                
                paddedFrame.create(frameSize.x + 2, frameSize.y + 2, Color.TRANSPARENT);
                paddedFrame.copy(rawimg, 1, 1, rect, false);
                
                Texture next = new Texture();
                next.loadFromImage(paddedFrame);
                frames.add(next);
            }
        }

        anims_stored.put(path, frames);
        return frames;
    }
}
