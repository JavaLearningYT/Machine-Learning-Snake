import java.util.Arrays;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.microsoft.msr.malmo.TimestampedStringVector;
import com.microsoft.msr.malmo.WorldState;

/**
 * Basic observation space that contains just the X,Y,Z location triplet, plus Yaw and Pitch 
 * @author howard-abrams (howard.abrams@ca.com) on 1/12/17.
 */
public class MineSweeperObservationSpacePosition extends MineSweeperObservationSpace {
    @Override
    public String getName() {
        return "Grid(256)";
    }

    @Override
    public int[] getShape() {
        return new int[] {256};
    }

    @Override
    public INDArray getLow() {
    	float[] a = new float[256];
    	Arrays.fill(a, -1);
        INDArray low = Nd4j.create(a);
        return low;
    }

    @Override
    public INDArray getHigh() {
    	float[] a = new float[256];
    	Arrays.fill(a, 9);
        INDArray high = Nd4j.create(a);
        return high;
    }

    public MineBox getObservation(Integer[] world_state) {
        return new MineBox(world_state);
    }
}