package learning.games.snake.learn;

interface SnakeObservationPolicy {
    boolean isObservationConsistant(double[][] world_state, double[][] original_world_state);
    boolean isObservationConsistant(double[] w1,double[] w2);
}
