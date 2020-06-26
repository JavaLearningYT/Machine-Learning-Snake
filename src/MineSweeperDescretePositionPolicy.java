import java.util.Arrays;

public class MineSweeperDescretePositionPolicy implements MineSweeperObservationPolicy{
	MineSweeperObservationSpacePosition observationSpace = new MineSweeperObservationSpacePosition();

    @Override
    public boolean isObservationConsistant(Integer[] world_state, Integer[] original_world_state) {
        MineBox last_observation = observationSpace.getObservation(world_state);
        MineBox old_observation = observationSpace.getObservation(original_world_state);

        double[] newvalues = old_observation == null ? null : old_observation.toArray();
        double[] oldvalues = last_observation == null ? null : last_observation.toArray();

        return !(world_state==null  || Arrays.equals(oldvalues, newvalues));
    }
}
