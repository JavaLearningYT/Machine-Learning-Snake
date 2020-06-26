import org.deeplearning4j.rl4j.space.DiscreteSpace;

/**
 * Abstract base class for all Malmo-specific action spaces
 * @author howard-abrams (howard.abrams@ca.com) on 1/12/17.
 */
public abstract class MineSweeperActionSpace extends DiscreteSpace {
    /**
     * Array of action strings that will be sent to Malmo
     */
    protected Integer[] actions;

    /**
     * Protected constructor
     * @param size number of discrete actions in this space
     */
    protected MineSweeperActionSpace(int size) {
        super(size);
    }

    @Override
    public Object encode(Integer action) {
        return actions[action];
    }

    @Override
    public Integer noOp() {
        return -1;
    }
}
