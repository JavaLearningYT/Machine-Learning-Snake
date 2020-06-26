package learning.games.snake.learn;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class SnakeBox implements Encodable {
	private final INDArray data;

    /**
     * Construct state from an array of doubles
     * @param value state values
     */
    //TODO: If this constructor was added to "Box", we wouldn't need this class at all.
    public SnakeBox(double... value) {
        this.data = Nd4j.create(value);
    }
    public SnakeBox(double[][] values) {
    	double[] value = new double[values.length*values[0].length];
    	for(int i=0;i<values.length;i++) {
        	for(int a=0;a<values[0].length;a++) {
        		value[i+a]=values[i][a];
        	}
        }
    	this.data = Nd4j.create(value);
    	
    }
    private SnakeBox(INDArray toDup) {
        data = toDup.dup();
    }
	@Override
    public double[] toArray() {
        return data.data().asDouble();
    }
	@Override
	public boolean isSkipped() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public INDArray getData() {
		//return Nd4j.create(value,1,value.length,1);
		//System.out.println(a.rank());
		return data;
	
	}
	@Override
	public Encodable dup() {
		// TODO Auto-generated method stub
		return new SnakeBox(data);
	}
}
