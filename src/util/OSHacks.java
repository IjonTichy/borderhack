package util;

public class OSHacks
{
    private OSHacks() {}
    
    public static enum OS_TYPES
    {
        WINDOWS,
        LINUX,
        MAC
    };
    
    private static String   os_name;
    private static OS_TYPES os_type;
    
    private static void determineOS()
    {
        os_name = System.getProperty("os.name");
        os_name = os_name.toLowerCase();
        
        if (os_name.indexOf("win") >= 0)
        {
            os_type = OS_TYPES.WINDOWS;
        }
        
        if (os_name.indexOf("nix") >= 0 || os_name.indexOf("nux") >= 0 || os_name.indexOf("aix") > 0)
        {
            os_type = OS_TYPES.LINUX;
        }
        
        if (os_name.indexOf("mac") >= 0)
        {
            os_type = OS_TYPES.MAC;
        }
    }
    
    public static OS_TYPES getOS()
    {
        if (os_name == null)
        {
            determineOS();
        }
        
        return os_type;
    }
}
