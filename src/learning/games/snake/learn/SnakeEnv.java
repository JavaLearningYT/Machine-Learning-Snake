package learning.games.snake.learn;

import java.util.Random;

import javax.swing.JFrame;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import learning.core.Learn;
import learning.core.gui.LearningMonitorController;
import learning.games.snake.code.SnakeBoard;


public class SnakeEnv implements MDP<SnakeBox, Integer, DiscreteSpace>{
	private SnakeActionSpace actionSpace;
	private SnakeObservationSpace observationSpace;
	private SnakeObservationPolicy observationPolicy;
	private SnakeBoard snake;
	private SnakeBox last_observation;
	private int size=16;
	private LearningMonitorController mon = null;

	public SnakeEnv(SnakeActionSpace actionSpace2,SnakeObservationSpace observationSpace2, SnakeObservationPolicy obsPolicy) {
		this.actionSpace=actionSpace2;
		this.observationSpace=observationSpace2;
		this.observationPolicy=obsPolicy;
		
		}
	public SnakeEnv(SnakeActionSpace actionSpace2,SnakeObservationSpace observationSpace2, SnakeObservationPolicy obsPolicy,int size) {
		this.actionSpace=actionSpace2;
		this.observationSpace=observationSpace2;
		this.observationPolicy=obsPolicy;
		this.size=size;
		
		}
	public SnakeEnv(SnakeActionSpace actionSpace2, SnakeObservationSpace observationSpace2, SnakeObservationPolicy obsPolicy, int snakesize, LearningMonitorController mon) {
		this.actionSpace=actionSpace2;
		this.observationSpace=observationSpace2;
		this.observationPolicy=obsPolicy;
		this.size=snakesize;
		this.mon=mon;
		
	}
	public void setSize(int size) {
		this.size=size;
	}
	@Override
	public ObservationSpace<SnakeBox> getObservationSpace() {
		return observationSpace;
	}

	@Override
	public DiscreteSpace getActionSpace() {
		return actionSpace;
	}

	@Override
	public SnakeBox reset() {
		if(snake==null) {
			snake = new SnakeBoard(size);
			if(mon!=null) {
				mon.addVisualization(snake);
			}else if(Learn.display()) {
			Random rand = new Random();
			snake.visible();
			JFrame frame = new JFrame(rand.nextInt(2000)+"");
			frame.add(snake);
			frame.setVisible(true);
			frame.pack();
			}
			
		}
		snake.clear();
		return new SnakeBox(snake.info());
		
	}

	@Override
	public void close() {
		
	}

	@Override
	public boolean isDone() {
		return !snake.inGame();
	}

	@Override
	public StepReply<SnakeBox> step(Integer action) {
		snake.move(actionSpace.encode(action));
        //last_observation = observationSpace.getObservation(snake.getGrid());
        last_observation = observationSpace.getObservation(snake.info());
        double r = getReward();
		return new StepReply<SnakeBox>(last_observation, r, !snake.inGame(), null);
	}

	private double getReward() {
		if(!snake.inGame()) {
			if(snake.tooLong()) {
				return 0;
			}
			return -1;
		}
		if(snake.gotApple()) {
			return 5.0;//+.37*(snake.getLength());
		}
		return  0;//+(2-snake.getDistance()/17.0)*.00001;
		
		
		//MAKE IT SO NO SNAKE FOR 50 MOVES ONLY RETURNS 0 and game over instead -1
		
		
		/*
		if(!snake.inGame()) {
			return -100.2;
		}else {
			if(snake.gotApple()) {
				return 50.13;
			}/*else if(snake.good()) {
				return 0;
			}
			else {
				//return 0;
				return (25.0-snake.getDistance())*.1;
			}
		}*/
	}
	@Override
	public MDP<SnakeBox, Integer, DiscreteSpace> newInstance() {

		if(mon!=null) {
			return new SnakeEnv(actionSpace,observationSpace,observationPolicy,size,mon);
		}
		return new SnakeEnv(actionSpace,observationSpace,observationPolicy,size);
	}
}

