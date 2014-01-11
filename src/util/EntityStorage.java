package util;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import entities.Entity;

/**
 * The entire point of this class is to be able to get a reference to an entity
 * under mostly any circumstances. Given an ID (long), this should get it to you.
 * Should the entity not exist anymore, well, whaddya expect.
 */
public class EntityStorage
{
    private static HashMap<Long, WeakReference<Entity>> entities_stored = new HashMap<Long, WeakReference<Entity>>();
    private EntityStorage() {}
    
    public static void addEntity(long id, Entity ent) throws EntityIDUsedException
    {
        if (getEntity(id) != null)
        {
            throw new EntityIDUsedException();
        }
        
        entities_stored.put(id, new WeakReference<Entity>(ent));
    }
    
    public static Entity getEntity(long id)
    {
        WeakReference<Entity> ref = entities_stored.get(id);
        if (ref == null) { return null; }
        
        Entity retEnt = ref.get();
        
        if (retEnt == null)
        {
            entities_stored.remove(id);
            return null;
        }
        
        return retEnt;
    }
    
    public static HashMap<Long, Entity> getAllEntities()
    {
        HashMap<Long, Entity> ret = new HashMap<Long, Entity>();
        
        for (long entKey: entities_stored.keySet())
        {
            Entity ent = getEntity(entKey);
            
            if (ent != null) { ret.put(entKey, ent); }
        }
        
        return ret;
    }
}