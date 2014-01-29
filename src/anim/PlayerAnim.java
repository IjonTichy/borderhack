package anim;

import org.jsfml.system.Vector2i;

public class PlayerAnim extends Animation
{
    public Vector2i getAnimSize() { return new Vector2i(24, 24); }
    public String getFrameSource() { return "img/testplayer.png"; }
    public int getID() { return 0; }
}
