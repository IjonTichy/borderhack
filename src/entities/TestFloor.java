package entities;

public class TestFloor extends Entity
{
    protected void defaults()
    {
        super.defaults();
        myLayer   = -1;
    }
    
    public int getID() { return 1; }
    public String getTexturePath() { return "img/shitfloor.png"; }
    
    protected void init()
    {
        return;
    }
}
