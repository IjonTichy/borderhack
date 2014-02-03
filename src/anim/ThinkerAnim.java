package anim;

import org.jsfml.system.Vector2i;

public class ThinkerAnim extends Animation
{
    public Vector2i getAnimSize() { return new Vector2i(24, 24); }
    public String getFrameSource() { return "img/pillowshadingsin.png"; }
    public int getID() { return 3; }
}
