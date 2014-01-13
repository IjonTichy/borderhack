package anim;

public class AnimData
{
    public float x;
    public float y;
    public int   layer;
    
    public AnimData()
    {
        this(0f, 0f, 0);
    }
    
    public AnimData(float x, float y, int l)
    {
        this.x      = x;
        this.y      = y;
        this.layer  = l;
    }
    
    public AnimData(AnimData a)
    {
        this.x      = a.x;
        this.y      = a.y;
        this.layer  = a.layer;
    }
}
