package anim;

import org.jsfml.system.Vector2i;

public class FloorAnim extends Animation
{
    public Vector2i getAnimSize() { return new Vector2i(32, 32); }
    public String getFrameSource() { return "img/default2.png"; }
    public int getID() { return 1; }
}
