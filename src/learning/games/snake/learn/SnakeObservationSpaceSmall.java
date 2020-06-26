package learning.games.snake.learn;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class SnakeObservationSpaceSmall extends SnakeObservationSpace{
	int size;


    /**
     * Construct observation space from a bitmap size. Assumes 3 color channels.
     * 
     * @param xSize total x size of bitmap
     * @param ySize total y size of bitmap
     */
    public SnakeObservationSpaceSmall(int size) {
    	this.size=size;
    }

    @Override
    public String getName() {
        return "Box(" + size+")";
    }

    @Override
    public int[] getShape() {
        return new int[] {size};
    }

    @Override
    public INDArray getLow() {
        //INDArray low = Nd4j.create(getShape());
    	INDArray low = Nd4j.linspace(-1, -1, size).reshape(getShape());
        return low;
    }

    @Override
    public INDArray getHigh() {
        INDArray high = Nd4j.linspace(10, 10, size).reshape(getShape());
        return high;
    }

	@Override
	public SnakeBox getObservation(double[] world_state) {
		// TODO Auto-generated method stub
		return new SnakeBox(world_state);
	}

	@Override
	public SnakeBox getObservation(double[][] world_state) {
		// TODO Auto-generated method stub
		return null;
	}
}
