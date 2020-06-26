import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import learning.core.Learn;

public class MineEnv implements MDP<MineBox, Integer, DiscreteSpace>{
    private MineSweeperActionSpace actionSpace;

	private MineSweeperObservationPolicy observationPolicy;
	private Board board;
	private int count=0;

	private MineSweeperObservationSpace observationSpace;
	private MineBox last_observation;
	
	private double[] previous;

	private Integer[] last_world_state;
	public MineEnv(MineSweeperActionSpace actionSpace2,MineSweeperObservationSpace observationSpace2, MineSweeperObservationPolicy obsPolicy) {
	this.actionSpace=actionSpace2;
	this.observationSpace=observationSpace2;
	this.observationPolicy=obsPolicy;
	
	}

	@Override
	public ObservationSpace<MineBox> getObservationSpace() {
		// TODO Auto-generated method stub
		return observationSpace;
	}

	@Override
	public DiscreteSpace getActionSpace() {
		return actionSpace;
	}

	@Override
	public MineBox reset() {
		
		count++;
		previous=null;
		last_observation=null;
		last_world_state=null;
		if(board==null) {
		board= new Board(new JLabel("HI"));
		if(Learn.display()) {
		JFrame f = new JFrame();
		f.add(board);
		f.pack();
		f.setVisible(true);
		f.setTitle(count+"");
		}
		}else{
			board.newGame();
		}
		
		return new MineBox(board.getField());
	}

	@Override
	public void close() {
		if(board==null) {
			return;
		}
		board.close();
		board=null;
	}

	@Override
	public StepReply<MineBox> step(Integer action) {
		if(last_observation==null) {
			previous=new double[1];
		}else {
			previous=last_observation.toArray();
		}
		Integer choice = (Integer)actionSpace.encode(action);
		board.play(choice);
		last_world_state = board.getField();
        last_observation = observationSpace.getObservation(last_world_state);
        double r = getReward();
		return new StepReply<MineBox>(last_observation, r, !board.inGame(), null);
	}
	
	private double getReward() {
		
		if(board.isGameWon()) {
			return 10;
		}else if(!board.inGame()) {
			if(board.firstMove()) {
				return 0;
			}
			return -1;
		}
		if(Arrays.equals(last_observation.toArray(),previous)) {
			return 0;
		}else {
			//reward+=.5;
			return 1;
		}
		
	}

	@Override
	public boolean isDone() {
		return !board.inGame();
	}

	@Override
	public MDP<MineBox, Integer, DiscreteSpace> newInstance() {
		System.out.println("new MDP");
		return new MineEnv(actionSpace,observationSpace,observationPolicy);
	}
}
