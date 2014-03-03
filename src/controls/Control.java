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
    
    
    public String toString()
    {
        StringBuilder ret = new StringBuilder();
        
        ret.append(this.getClass().getSimpleName());
        ret.append("(\"");
        ret.append(c_name);
        ret.append("\")");
        
        return ret.toString();
    }
}
