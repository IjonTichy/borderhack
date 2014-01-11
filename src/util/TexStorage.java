package util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Texture;


public class TexStorage
{
    private static Map<String, Texture> textures_stored = new HashMap<String, Texture>();
    
    private TexStorage() {}
    
    public static Texture getTexture(String key)
    {
        return textures_stored.get(key);
    }
    
    public static void setTexture(String key, Texture newtex) throws TextureAlreadyInException
    {
        setTexture(key, newtex, false);
    }
    
    public static void setTexture(String key, Texture newtex, boolean overwrite) throws TextureAlreadyInException
    {
        if (getTexture(key) != null && !overwrite)
        {
            throw new TextureAlreadyInException();
        }
        
        textures_stored.put(key, newtex);
    }
    
    public static Texture loadTexturePath(String path) throws TextureAlreadyInException, IOException
    {
        return loadTexturePath(path, false);
    }
    
    public static Texture loadTexturePath(String path, boolean overwrite) throws TextureAlreadyInException, IOException
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
}
