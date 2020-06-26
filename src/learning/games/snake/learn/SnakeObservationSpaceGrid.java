package learning.games.snake.learn;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class SnakeObservationSpaceGrid extends SnakeObservationSpace {
    int xSize;
    int ySize;


    /**
     * Construct observation space from a bitmap size. Assumes 3 color channels.
     * 
     * @param xSize total x size of bitmap
     * @param ySize total y size of bitmap
     */
    public SnakeObservationSpaceGrid(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
    }

    @Override
    public String getName() {
        return "Box(" + ySize + "," + xSize +")";
    }

    @Override
    public int[] getShape() {
        return new int[] {xSize*ySize};
    }

    @Override
    public INDArray getLow() {
        //INDArray low = Nd4j.create(getShape());
    	INDArray low = Nd4j.linspace(0, 0, xSize * ySize).reshape(getShape());
        return low;
    }

    @Override
    public INDArray getHigh() {
        INDArray high = Nd4j.linspace(255, 255, xSize * ySize).reshape(getShape());
        return high;
    }

    public SnakeBox getObservation(double[][] world_state) {

        double[] values = new double[xSize*ySize];
        for(int i=0;i<xSize;i++) {
        	for(int a=0;a<ySize;a++) {
        		values[i+a]=world_state[i][a];
        	}
        }

       

        return new SnakeBox(values);
    }

	@Override
	public SnakeBox getObservation(double[] world_state) {
		// TODO Auto-generated method stub
		return null;
	}

}

