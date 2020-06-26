interface MineSweeperObservationPolicy {
    boolean isObservationConsistant(Integer[] world_state, Integer[] original_world_state);
}