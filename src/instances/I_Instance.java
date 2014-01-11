package instances;

/**
 * <p>A game instance. The game is handled through instances running one after the
 * other, each one having control of the game during its run. Instances, once they
 * finish or defer execution, return a new instance. Cleanup is expected to be 
 * done by the instance as it exits; it is not enforced, due to instances
 * potentially being used for things like entering a menu during gameplay.
 * Reconstructing the entire game instance just for entering and leaving a menu
 * is flat-out ridiculous.</p>
 * 
 * <p>Right now, the only thing an instance needs to do is define a run method,
 * which returns either a new instance, or null. Null is interpreted as "end
 * the game". More functions might be required later.</p>
 *
 */
public interface I_Instance
{
    public I_Instance run();
}