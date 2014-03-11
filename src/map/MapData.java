package map;

import org.jsfml.system.Vector3i;

/**
 * Stores the position of an Entity.
 * 
 * Right now, it looks like a less featureful version of Vector3i.
 * This is here in case the map needs to store more information about an entity
 * that is not part of the entity itself. Makes life easier.
 *
 */
public class MapData
{
    public int x;
    public int y;
    public int z;
    
    public MapData()
    {
        this(0, 0, 0);
    }
    
    public MapData(int x, int y)
    {
        this(x, y, 0);
    }
    
    public MapData(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public MapData(MapData m)
    {
        this.x = m.x;
        this.y = m.y;
        this.z = m.z;
    }

    public Vector3i toVector3i()
    {
        return new Vector3i(x, y, z);
    }
}
