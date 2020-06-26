package learning.games.snake.learn;

import java.util.Arrays;

public class SnakeDiscretePositionPolicy implements SnakeObservationPolicy {
    ///SnakeObservationSpaceGrid observationSpace = new SnakeObservationSpaceGrid(SnakeControl.SNAKESIZE,SnakeControl.SNAKESIZE);

    @Override
    public boolean isObservationConsistant(double[][] world_state, double[][] original_world_state) {
    	return Arrays.deepEquals(world_state,original_world_state);

    }

	@Override
	public boolean isObservationConsistant(double[] w1, double[] w2) {
		return Arrays.equals(w1, w2);
	}

}