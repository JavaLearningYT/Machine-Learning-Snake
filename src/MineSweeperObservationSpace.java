import org.deeplearning4j.rl4j.space.ObservationSpace;

public abstract class MineSweeperObservationSpace implements ObservationSpace<MineBox> {
    public abstract MineBox getObservation(Integer[] world_state);
}