package learning.games.snake.learn;

import org.deeplearning4j.rl4j.space.DiscreteSpace;

public class SnakeActionSpace extends DiscreteSpace{
	    /**
	     * Array of action strings that will be sent to Malmo
	     */
	    protected Integer[] actions;

	    /**
	     * Protected constructor
	     * @param size number of discrete actions in this space
	     */
	    protected SnakeActionSpace(int size) {
	        super(size);
	    }

	    @Override
	    public Integer encode(Integer action) {
	        return actions[action];
	    }

	    @Override
	    public Integer noOp() {
	        return 1;
	    }
	
}
