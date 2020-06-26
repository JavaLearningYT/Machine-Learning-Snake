package learning.games.snake.learn;

import org.deeplearning4j.rl4j.space.ObservationSpace;

public abstract class SnakeObservationSpace implements ObservationSpace<SnakeBox> {
    public abstract SnakeBox getObservation(double[][] world_state);

	public abstract SnakeBox getObservation(double[] world_state) ;
}