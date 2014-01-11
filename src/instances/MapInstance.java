package instances;

import map.GameMap;

/**
 * A game instance that runs a map. You'll typically spend your game in here.
 */
public class MapInstance implements I_Instance
{
    private GameMap my_map;
    
    public MapInstance(GameMap map)
    {
        my_map = map;
    }
    
    public I_Instance run()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
