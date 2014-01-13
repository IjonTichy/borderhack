package util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueSplitter<T> extends Thread
{
    private BlockingQueue<T> recieving_queue;
    private BlockingQueue<T>[]  split_queues;
    private boolean keep_running;
    private int     poll_delay;
    
    @SuppressWarnings("unchecked") // unavoidable warning
    public QueueSplitter(BlockingQueue<T> inQueue, int queueCount, int pollTime)
    {
        if (queueCount < 1)
        {
            throw(new NegativeArraySizeException());
        }
        
        recieving_queue = inQueue;
        split_queues    = new BlockingQueue[queueCount];
        poll_delay      = Math.max(0, pollTime);
        
        int i;
        for (i = 0; i < queueCount; i++)
        {
            split_queues[i] = new LinkedBlockingQueue<T>();
        }
    }
    
    public void run()
    {
        keep_running = true;
        
        while (keep_running)
        {
            T nextItem = recieving_queue.poll();
            
            if (nextItem != null)
            {
                for (BlockingQueue<T> q: split_queues)
                {
                    q.add(nextItem);
                }
            }
            
            try
            {
                Thread.sleep(poll_delay);
            }
            catch (InterruptedException e)
            {
                keep_running = false;
            }
        }
    }
    
    public void end()
    {
        keep_running = false;
    }
    
    public BlockingQueue<T> getQueue(int index)
    {
        if (index < 0 || index >= split_queues.length)
        {
            return null;
        }
        
        return split_queues[index];
    }
}
