package anim;

import org.jsfml.system.Vector2i;

public class WallAnim extends Animation
{
    public Vector2i getAnimSize() { return new Vector2i(16, 16); }
    public String getFrameSource() { return "img/testanim.png"; }
    public int getID() { return 0; }
}
