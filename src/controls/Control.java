package controls;

public class Control
{
    protected String c_name;
    
    public Control()
    {
        this(null);
    }
    
    public Control(String name)
    {
        c_name = name;
    }
    
    public String getName() { return c_name; }
}
