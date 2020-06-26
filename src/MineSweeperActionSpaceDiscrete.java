

/**
 * Action space that allows for a fixed set of specific Malmo actions
 * @author howard-abrams (howard.abrams@ca.com) on 1/12/17.
 */
public class MineSweeperActionSpaceDiscrete extends MineSweeperActionSpace {
    /**
     * Construct an actions space from an array of action strings
     * @param actions Array of action strings
     */
	//possible update action space dynamically to only available spaces andd see how that works out
    public MineSweeperActionSpaceDiscrete(Integer... actions) {
        super(actions.length);
        this.actions = actions;
    }
}