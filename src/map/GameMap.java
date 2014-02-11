package map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modes.Mode;

import org.jsfml.system.Vector2i;

import entities.Entity;
import entities.thinkers.Thinker;
import util.Constants;

public class GameMap
{
    private Map<Entity, MapData>    map_entities;
    private Map<Vector2i, MapBlock> map_blocks;
    private String map_name;
    
    private long map_tick;
    private HashMap<Mode, Long> map_actions;
    
    public GameMap(String name)
    {
        map_name = name;
        map_entities = new HashMap<Entity, MapData>();
        map_actions  = new HashMap<Mode, Long>();
    }
    
    public GameMap(String name, Map<Entity, MapData> ents)
    {
        this(name);
        map_entities.putAll(ents);
    }
    
    // ====
    // == GETTERS
    // ====
    
    public List<Entity> getAllEntities()
    {
        return new ArrayList<Entity>(map_entities.keySet());
    }
    
    public Vector2i getPosition(Entity ent)
    {
        MapData data = map_entities.get(ent);
        if (data == null) { return null; }
        
        return new Vector2i(data.x, data.y);
    }

    public String getMapName() { return map_name; }
    public long getTick() { return map_tick; }

    // ====
    // == ENTITY CONTROL
    // ====
    
    public boolean addToMap(Entity ent)
    {
        MapData pos = new MapData(0, 0);
        return addToMap(ent, pos);
    }
    
    public boolean addToMap(Entity ent, MapData pos)
    {
        if (map_entities.containsKey(ent))
        {
            if (map_entities.get(ent) == null) { map_entities.remove(ent); }
            else { return false; }
        }
        
        map_entities.put(ent, new MapData(pos));
        if (ent instanceof Thinker) { registerThinkerModes((Thinker)ent); }
        
        return true;
    }
    
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
        Long curTime  = map_actions.get(m);
        long nextTime;
        
        if (curTime == null || curTime < map_tick) { nextTime = tick; }
        else { nextTime = Math.min(tick, curTime); }
        
        map_actions.put(m, nextTime);
    }
    
    private void unschedule(Mode m)
    {
        map_actions.remove(m);
    }
    
    private void thinkTicks(long ticks)
    {
        long nextTick = ticks + map_tick;
        
        while (map_tick < nextTick)
        {
           thinkNext();
        }
    }
    
    private long getNextThoughtTick()
    {
        long ret = Long.MAX_VALUE;
        for (long t: map_actions.values()) { ret = Math.min(t, ret); }
        return ret;
    }
    
    private long thinkNext()
    {
        Map<Mode, Long> newModes = new HashMap<>();
        long next = getNextThoughtTick();
        long delta = next - map_tick;
        
        for (Entity e: map_entities.keySet())
        {
            if (e instanceof Thinker)
            {
                Thinker t = (Thinker)e;
                newModes.putAll(t.think(delta, this));
            }
        }
        
        for (Map.Entry<Mode, Long> m: newModes.entrySet())
        {
            schedule(m.getKey(), m.getValue());
        }
        
        map_tick = next;
        return map_tick;
    }
    
    // ====
    // == BLOCKMAP CONTROL
    // ====
    
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
        
        Vector2i entSize = ent.getSize();

        // Subtract 1 from size because a dimension of 1 means "starts and ends on same time"
        int topX = entPos.x / Constants.BLOCK_SIZE;
        int topY = entPos.y / Constants.BLOCK_SIZE;
        int botX = (topX + entSize.x - 1) / Constants.BLOCK_SIZE;
        int botY = (topY + entSize.y - 1) / Constants.BLOCK_SIZE;
        
        int x, y;
        
        for (x = topX; x <= botX; x++)
        {
            for (y = topY; y <= botY; y++)
            {
                addToBlock(ent, x, y);
            }
        }
    }
    
    @SuppressWarnings("unused")
    private void recalcBlocks(Entity ent)
    {
        if (!map_entities.containsKey(ent)) { return; }
        
        removeFromBlockmap(ent);
        addToBlockmap(ent);
    }
}