package learning.games.snake.learn;

public class SnakeActionSpaceDiscrete extends SnakeActionSpace {
    /**
     * Construct an actions space from an array of action strings
     * @param actions Array of action strings
     */
    public SnakeActionSpaceDiscrete(Integer... actions) {
        super(actions.length);
        this.actions = actions;
    }
}