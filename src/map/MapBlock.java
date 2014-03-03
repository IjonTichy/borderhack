package map;

import java.util.ArrayList;

import entities.Entity;


public class MapBlock
{
    private ArrayList<Entity> entities;
    
    public MapBlock()
    {
        this(null);
    }
    
    public MapBlock(ArrayList<Entity> preEnts)
    {
        entities = new ArrayList<Entity>();
        if (preEnts != null) { entities.addAll(preEnts); }
    }
    
    
    /**
     * Registers an entity into a block.
     *
     * @param  newEnt   the entity to register in
     * @return          true if entity was added, false if entity wasn't (ie. was already in)
     */
    
    public boolean registerEntity(Entity newEnt)
    {
        if (entities.contains(newEnt)) { return false; }
        
        entities.add(newEnt);
        return true;
    }

    /**
     * Unregisters an entity from a block.
     *
     * @param  oldEnt   the entity to unregister
     * @return          true if entity was removed, false if entity wasn't (ie. not there)
     */
    
    public boolean unregisterEntity(Entity oldEnt)
    {
        if (!entities.contains(oldEnt)) { return false; }
        
        entities.remove(oldEnt);
        return true;
    }

    /**
     * Returns a copy of the list of all entities in a block.
     * (ain't this just the most fucking redundant doc ever)
     *
     * @return          ArrayList<Entity>, with all entities in block
     */
    public ArrayList<Entity> entsInBlock()
    {
        return new ArrayList<Entity>(entities);
    }
    
    /**
     * Returns how many items are in the block, as an integer.
     * 
     * @return          Guess.
     */
    public int entCount()
    {
        return entities.size();
    }
}