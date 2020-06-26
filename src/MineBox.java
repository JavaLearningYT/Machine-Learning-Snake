import java.util.Arrays;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Encodable state as a simple value array similar to Gym Box model, but without a JSON constructor
 * @author howard-abrams (howard.abrams@ca.com) on 1/12/17.
 */
public class MineBox implements Encodable {
    private Integer[] board;

    /**
     * Construct state from an array of doubles
     * @param value state values
     */
    //TODO: If this constructor was added to "Box", we wouldn't need this class at all.
    public MineBox(Integer[] board) {
        this.board = board;
    }

    @Override
    public double[] toArray() {
    	
        double[] b = new double[board.length];
        
        for(int i =0;i<board.length;i++) {
        	b[i]=board[i];
        }
        return b;
    }

    @Override
    public String toString() {
        return Arrays.toString(board);
    }

	@Override
	public boolean isSkipped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public INDArray getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Encodable dup() {
		// TODO Auto-generated method stub
		return null;
	}
}