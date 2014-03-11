package map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modes.Mode;

import org.jsfml.system.Vector2i;
import org.jsfml.system.Vector3i;

import entities.Entity;
import entities.thinkers.Thinker;
import controls.Control;
import util.Constants;
import util.Line3D;

/**
 * <p>The game map! Everything in the game happens here.</p>
 * 
 * <p>This holds animations and entities for the map, as well as absolute times
 * for modes to run, and handles sending controls to the thinkers in it, as well
 * as collisions inside it..</p>
 * 
 *  Some units:
 *  <ul>
 *    <li>One second = 1000000 ticks (you still get 292471 in-game years, don't worry)</li>
 *  </ul>
 *  
 */
public class GameMap
{
    public static final long SECOND_TICKS   = 1000000;
    
    private Map<Entity, MapData>    map_entities;
    private Map<Vector2i, MapBlock> map_blocks;
    private String map_name;
    
    private long map_tick;
    private HashMap<Mode, Long>     map_modeTicks;

    public GameMap(String name)
    {
        this(name, null);
    }
    
    public GameMap(String name, Map<Entity, MapData> ents)
    {
        map_name = name;
        map_entities     = new HashMap<>();
        map_modeTicks    = new HashMap<>();
        map_blocks       = new HashMap<>();
        
        if (ents != null) { map_entities.putAll(ents); }
    }
    
    // ====
    // == GETTERS
    // ====
    
    /**
     * Get all the entities in the map.
     * @return I WONDER.
     */
    public List<Entity> getAllEntities()
    {
        return new ArrayList<Entity>(map_entities.keySet());
    }
    
    /**
     * Gets the position of the entity.
     * @param ent   The entity to get the position of.
     * @return Its position WOAH.
     */
    public Vector3i getPosition(Entity ent)
    {
        MapData data = map_entities.get(ent);
        if (data == null) { return null; }
        
        return new Vector3i(data.x, data.y, data.z);
    }

    /**
     * Gets the name of the map.
     * @return GUESS.
     */
    public String getMapName() { return map_name; }
    
    /**
     * Gets the tick of the map.
     * @return GUESS.
     */
    public long getTick() { return map_tick; }
    

    /**
     * Run some ticks on the map.
     * @param tickCount     How many ticks to run. If 0, runs to the next action.
     * @return  The tick the map ends on.
     */
    public long doTicks(long tickCount)
    {
        if (tickCount == 0) { thinkNext(); }
        else { thinkTicks(tickCount); }
        
        return map_tick;
    }

    // ====
    // == ENTITY CONTROL
    // ====
    
    /**
     * Add an entity to the map at the coordinates (0, 0, 0).
     * @param ent   The entity to add.
     * @return  Whether the entity was added to the map.
     */
    public boolean addToMap(Entity ent)
    {
        MapData pos = new MapData(0, 0, 0);
        return addToMap(ent, pos);
    }

    /**
     * Add an entity to the map.
     * @param ent   The entity to add.
     * @param pos   The position of the entity.
     * @return  Whether the entity was added to the map.
     */
    public boolean addToMap(Entity ent, MapData pos)
    {
        if (map_entities.containsKey(ent))
        {
            if (map_entities.get(ent) == null) { map_entities.remove(ent); }
            else { return false; }
        }
        
        map_entities.put(ent, new MapData(pos));
        if (ent instanceof Thinker) { registerThinkerModes((Thinker)ent); }
        addToBlockmap(ent);
        
        return true;
    }

    /**
     * Removes an entity from the map.
     * @param ent   The entity to remove.
     * @return  Whether the entity was removed from the map.
     */
    public boolean removeFromMap(Entity ent)
    {
        if (map_entities.containsKey(ent))
        {
            if (map_entities.get(ent) == null)
            {
                map_entities.remove(ent);
                return false;
            }

            map_entities.remove(ent);
            return true;
        }

        if (ent instanceof Thinker) { unregisterThinkerModes((Thinker)ent); }
        return false;
    }
    
    // ====
    // == MODE CONTROL
    // ====
    
    private void registerThinkerModes(Thinker t)
    {
        for (Map.Entry<Mode, Long> e: t.getModes().entrySet())
        {
            schedule(e.getKey(), e.getValue() + map_tick);
        }
    }
    
    private void unregisterThinkerModes(Thinker t)
    {
        for (Mode m: t.getModes().keySet())
        {
            unschedule(m);
        }
    }
    
    private void schedule(Mode m, long tick)
    {
        Long curTime  = map_modeTicks.get(m);
        long nextTime;
        
        if (curTime == null || curTime <= map_tick) { nextTime = tick; }
        else { nextTime = Math.min(tick, curTime); }
        
        map_modeTicks.put(m, nextTime);
    }
    
    private void unschedule(Mode m)
    {
        map_modeTicks.remove(m);
    }
    
    private void thinkTicks(long ticks)
    {
        long endTick = ticks + map_tick;
        
        while (map_tick < endTick)
        {
           thinkNext();
        }
    }
    
    private long getNextThoughtTick()
    {
        if (map_modeTicks.size() == 0) { return 0; }
        
        long ret = Long.MAX_VALUE;
        for (long t: map_modeTicks.values()) { ret = Math.min(t, ret); }
        return ret;
    }
    
    private long thinkNext()
    {
        if (map_modeTicks.size() == 0) { return map_tick; }
        
        Map<Mode, Long> newModes = new HashMap<>();
        long next = getNextThoughtTick();
        long shortest = Long.MAX_VALUE;
        
        for (Entity e: map_entities.keySet())
        {
            if (e instanceof Thinker)
            {
                Thinker t = (Thinker)e;
                newModes.putAll(t.think(next - map_tick, this));
            }
        }
        
        for (Map.Entry<Mode, Long> e: newModes.entrySet())
        {
            Mode m = e.getKey();
            Long l = e.getValue();
            schedule(m, l);
            shortest = Math.min(l, shortest);
        }
        
        if (shortest == Long.MAX_VALUE) { shortest = next; }

        for (Entity e: map_entities.keySet())
        {
            if (e instanceof Thinker)
            {
                Thinker t = (Thinker)e;
                t.tickDown(shortest - map_tick);
            }
        }

        map_tick = shortest;
        return map_tick;
    }
    
    // ====
    // == CONTROL... CONTROL
    // ====
    
    /**
     * Sends the given controls to every thinker on the map.
     * @param controls  The controls to send.
     */
    public void sendControls(List<Control> controls)
    {
        for (Entity e: map_entities.keySet())
        {
            if (!(e instanceof Thinker)) { continue; }
            Thinker t = (Thinker)e;
            
            for (Mode m: t.getModes().keySet())
            {
                m.giveControls(controls);
            }
        }
    }
    
    // ====
    // == BLOCKMAP CONTROL
    // ====

    private Vector2i blockPos(int x, int y)
    {
        return new Vector2i(x / Constants.BLOCK_SIZE, y / Constants.BLOCK_SIZE);
    }
    
    private void addToBlock(Entity ent, int blockX, int blockY)
    {
        Vector2i blockpos = new Vector2i(blockX, blockY);
        MapBlock block = map_blocks.get(blockpos);
        
        if (block == null)
        {
            block = new MapBlock();
            map_blocks.put(blockpos, block);
        }
        
        block.registerEntity(ent);
    }
    
    private void removeFromBlock(Entity ent, MapBlock block)
    {
        block.unregisterEntity(ent);
        if (block.entCount() == 0) { map_blocks.remove(block); }
    }
    
    private void removeFromBlockmap(Entity ent)
    {
        Collection<MapBlock> blocks = map_blocks.values();
        for (MapBlock block: blocks) { removeFromBlock(ent, block); }
    }
    
    private void addToBlockmap(Entity ent)
    {
        MapData entPos = map_entities.get(ent);
        if (entPos == null) { return; }
        
        Vector3i entSize = ent.getSize();

        // Subtract 1 from size because a dimension of 1 means "starts and ends on same tile"
        Vector2i top = blockPos(entPos.x,                 entPos.y);
        Vector2i bot = blockPos(entPos.x + entSize.x - 1, entPos.y + entSize.y - 1);
        
        int x, y;
        
        for (x = top.x; x <= bot.x; x++)
        {
            for (y = top.y; y <= bot.y; y++)
            {
                addToBlock(ent, x, y);
            }
        }
    }
    
    private void recalcBlocks(Entity ent)
    {
        if (!map_entities.containsKey(ent)) { return; }
        
        removeFromBlockmap(ent);
        addToBlockmap(ent);
    }
    
    // ====
    // == COLLISION AND MOVEMENT
    // ====

    /**
     * Gets all the entities that could collide at the given position.
     * @param x     The X coordinate to check collisions against.
     * @param y     The Y coordinate to check collisions against.
     * @param z     The Z coordinate to check collisions against.
     * 
     * @return  All the entities on that point.
     */
    public List<Entity> collisions(int x, int y, int z)
    {
        return collisions(new Vector3i(x, y, z));
    }
    
    /**
     * Gets all the entities that could collide at the given position.
     * @param position  The point to check collisions against.
     * @return  All the entities on that point.
     */
    public List<Entity> collisions(Vector3i position)
    {
        List<Entity> colliding = new ArrayList<>();
        Vector2i checkBlockPos = blockPos(position.x, position.y);
        MapBlock checkBlock = map_blocks.get(checkBlockPos);
        
        System.out.println("checkBlock is " + checkBlock);
        if (checkBlock == null) { return colliding; }
        
        for (Entity e: checkBlock.entsInBlock())
        {
            MapData  entData = map_entities.get(e);
            Vector3i entSize = e.getSize();
            
            Vector3i entTop = new Vector3i(entData.x, entData.y, entData.z);
            Vector3i entBot = new Vector3i(entData.x + entSize.x - 1,
                                           entData.y + entSize.y - 1,
                                           entData.z + entSize.z - 1);
            
            if (position.x < entTop.x || position.y < entTop.y || position.z < entTop.z
             || position.x > entBot.x || position.y > entBot.y || position.z > entBot.z)
            {
                continue;
            }
            
            colliding.add(e);
        }
        
        return colliding;
    }
    
    /**
     * Moves an entity on the XY plane.
     * @param toMove    The entity to move.
     * @param delta     How much to move the entity.
     * @return the new position of the entity.
     */
    public Vector3i move(Entity toMove, Vector2i delta) { return move(toMove, delta, true); }

    /**
     * Moves an entity on all three dimensions.
     * @param toMove    The entity to move.
     * @param delta     How much to move the entity.
     * @return the new position of the entity.
     */
    public Vector3i move(Entity toMove, Vector3i delta) { return move(toMove, delta, true); }

    /**
     * Moves an entity on the XY plane.
     * @param toMove    The entity to move.
     * @param delta     How much to move the entity.
     * @param collide   Whether to do collision or not.
     * @return the new position of the entity.
     */
    public Vector3i move(Entity toMove, Vector2i delta, boolean collide)
    {
        return move(toMove, new Vector3i(delta.x, delta.y, 0), collide);
    }

    /**
     * Moves an entity on all three dimensions.
     * @param toMove    The entity to move.
     * @param delta     How much to move the entity.
     * @param collide   Whether to do collision or not.
     * @return the new position of the entity.
     */
    public Vector3i move(Entity toMove, Vector3i delta, boolean collide)
    {
        MapData curPos = map_entities.get(toMove);
        return put(toMove, new Vector3i(curPos.x + delta.x, curPos.y + delta.y, delta.z + curPos.z), collide);
    }

    /**
     * Puts an entity at a specific coordinate.
     * @param toPut     The entity to move.
     * @param newPos    Where to move the entity.
     * @return the new position of the entity.
     */
    public Vector3i put(Entity toPut, Vector3i newPos)
    {
        return put(toPut, newPos, false);
    }

    /**
     * Puts an entity at a specific coordinate.
     * @param toPut     The entity to move.
     * @param newPos    Where to move the entity.
     * @param collide   Whether to do collision or not.
     * @return the new position of the entity.
     */
    public Vector3i put(Entity toPut, Vector3i newPos, boolean collide)
    {
        MapData  curPos = map_entities.get(toPut);
        Vector3i endPos = null;
        if (curPos == null) { return null; }

        if (collide == false)
        {
            endPos = newPos;
        }
        else
        {
            List<Vector3i> midpoints = Line3D.bresenham(curPos.toVector3i(), newPos);
            
            Vector3i prevPos = midpoints.get(0);
            
            for (Vector3i pos: midpoints.subList(1, midpoints.size()))
            {
                List<Entity> collides = collisions(pos);
                if (collides.size() > 0) { endPos = prevPos; }
                prevPos = pos;
            }
            
            if (endPos == null) { endPos = prevPos; }
        }
        
        curPos.x = endPos.x;
        curPos.y = endPos.y;
        curPos.z = endPos.z;
        recalcBlocks(toPut);
        
        return endPos;
    }
}