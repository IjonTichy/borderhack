package map;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import modes.Mode;

import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import render.Animation;
import entities.Entity;
import util.Constants;
import util.DelayedAction;
import util.MapData;

public class GameMap
{
    private Map<Entity, MapData>    map_entities;
    private Map<Vector2i, MapBlock> map_blocks;
    private Map<Animation, Vector2f> render_animations;
    private String map_name;
    
    private long map_tick;
    private SortedMap<Long, Map<Mode, Method>> map_actions;
    
    public GameMap(String name)
    {
        map_name = name;
        map_entities = new HashMap<Entity, MapData>();
    }
    
    public GameMap(String name, Map<Entity, MapData> ents)
    {
        map_name = name;
        map_entities = new HashMap<Entity, MapData>();
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

    public String getMapName()
    {
        return map_name;
    }

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
        
        map_entities.put(ent, pos.copy());
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
       
        return false;
    }
    
    // ====
    // == MODE CONTROL
    // ====
    
    public long scheduleNextAction(Mode scheduling, Method nextAction, long delay)
    {
        long runTic = map_tick + delay;
        Map<Mode, Method> actionsOnTic = map_actions.get(runTic);
        
        if (actionsOnTic == null)
        {
            actionsOnTic = new HashMap<Mode, Method>();
            map_actions.put(runTic, actionsOnTic);
        }

        actionsOnTic.put(scheduling, nextAction);
        return runTic;
    }
    
    public long runNextActions()
    {
        if (map_actions.size() == 0) { return map_tick; }
        
        long ticDelay = map_actions.firstKey();
        map_tick += ticDelay;
        
        runActionsOnTick(map_tick);
        
        return map_tick;
    }
    
    public long runTicks(long ticCount)
    {
        long i;
        long ticToReach = map_tick + ticCount;
        
        for (i = map_tick; i < ticToReach; i++)
        {
            runActionsOnTick(i);
            map_tick++;
        }
        
        return map_tick;
    }
    
    private void runActionsOnTick(long tick)
    {
        Map<Mode, Method> ticActions = map_actions.get(tick);
        
        for (Map.Entry<Mode, Method> nextActionSet: ticActions.entrySet())
        {
            Mode   nextMode   = nextActionSet.getKey();
            Method nextAction = nextActionSet.getValue();

            runAction(nextMode, nextAction);
        }
    }
    
    private void runAction(Mode mode, Method action)
    {
        try
        {
            DelayedAction result = (DelayedAction)action.invoke(mode, map_tick, this);
            
            if (result != null)
            {
                scheduleNextAction(mode, result.nextAction, map_tick + result.tickCount);
            }
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("Mode " + mode.getClass().getSimpleName() + ", action " + action.getName() + " either accepted the wrong arguments,");
            System.err.println("or it returned the wrong arguments.");
            System.err.println("(actions should take arguments (long mapTick, GameMap map), and return a DelayedAction)");
            System.err.println("Stack trace:");
            e.printStackTrace();
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            System.err.println("Mode " + mode.getClass().getSimpleName() + ", action " + action.getName() + " fucked up:");
            e.printStackTrace();
        }
    }

    // ====
    // == ANIMATION CONTROL
    // ====
    
    public void addAnimation(Animation anim)
    {
        addAnimation(anim, new Vector2f(0, 0));
    }
    
    public void addAnimation(Animation anim, Vector2i coord)
    {
        addAnimation(anim, new Vector2f(coord.x, coord.y));
    }
    
    public void addAnimation(Animation anim, Vector2f coord)
    {
        if (render_animations.get(anim) != null) { return; }
        
        render_animations.put(anim, coord);
    }
    
    public void removeAnimation(Animation anim)
    {
        render_animations.remove(anim);
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
    
    private void removeFromBlocks(Entity ent)
    {
        Collection<MapBlock> blocks = map_blocks.values();
        for (MapBlock block: blocks) { removeFromBlock(ent, block); }
    }
    
    private void calcBlocks(Entity ent)
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
    
    private void recalcBlocks(Entity ent)
    {
        if (!map_entities.containsKey(ent)) { return; }
        
        removeFromBlocks(ent);
        calcBlocks(ent);
    }
}