package util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import entities.Entity;

/**
 * The entire point of this class is to be able to get a reference to an entity
 * under mostly any circumstances. Given an ID (long), this should get it to you.
 * Should the entity not exist anymore, well, whaddya expect.
 */
public class ClassStorage
{
    private static HashMap<Long, WeakReference<Entity>> entities_stored = new HashMap<>();
    private static Set<Class<? extends Entity>> entity_types            = new HashSet<>();
    
    // if you can make 9223372036854775808 entities in a reasonable amount of time,
    // get the hell out of my engine
    private static long nextIDFree = 0;
    
    private ClassStorage() {}
    
    private static void addEntity(Entity ent, long id) throws IDAlreadyUsedException
    {
        if (getEntity(id) != null)
        {
            throw new IDAlreadyUsedException();
        }
        
        entities_stored.put(id, new WeakReference<Entity>(ent));
    }
    
    /**
     * Get an entity by its storage ID. If it no longer exists, it will be removed 
     * from storage. You cannot remove entities explicitly.
     * 
     * @param id  storage ID for Entity
     * @return the Entity stored at that ID
     */
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
    
    /**
     * Get EVERY entity stored in the ClassStorage. Dunno WHY you would want this,
     * but hey, it's here for you.
     * @return A clean version of entities_stored.
     */
    public static Map<Long, Entity> getAllEntities()
    {
        HashMap<Long, Entity> ret = new HashMap<Long, Entity>();
        
        for (long entKey: entities_stored.keySet())
        {
            Entity ent = getEntity(entKey);
            
            if (ent != null) { ret.put(entKey, ent); }
        }
        
        return ret;
    }
    
    public static long registerEntity(Entity e) throws IDAlreadyUsedException
    {
        long id = nextIDFree++;
        
        try
        {
            addEntity(e, id);
        } catch (IDAlreadyUsedException e1)
        {
            System.err.println("ERROR: Registered an entity, ID wasn't free; this should never happen");
            e1.printStackTrace();
        }
        
        Class<? extends Entity> e_class = e.getClass();
        entity_types.add(e_class);
        
        return id;
    }
}