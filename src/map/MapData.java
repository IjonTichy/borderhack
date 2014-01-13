package map;

/**
 * Stores the X and Y position of an Entity.
 * 
 * Right now, it looks like a less featureful version of Vector2i.
 * This is here in case the map needs to store more information about an entity
 * that is not part of the entity itself. Makes life easier.
 *
 */
public class MapData
{
    public int x;
    public int y;
    
    public MapData()
    {
        this(0, 0);
    }
    
    public MapData(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public MapData(MapData m)
    {
        this.x = m.x;
        this.y = m.y;
    }
}
