package learning.core;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.deeplearning4j.rl4j.learning.IEpochTrainer;
import org.deeplearning4j.rl4j.learning.ILearning;
import org.deeplearning4j.rl4j.learning.listener.TrainingListener;
import org.deeplearning4j.rl4j.util.IDataManager.StatEntry;
import learning.core.gui.LearningMonitorController;

/**
 * 
 * 
 * @author James
 *
 */
public class MyListener implements TrainingListener {
	private Lock lock = new ReentrantLock();
	private int iterations;
	private double[] scores;
	private int on = 0;
	private double count = 0;
	private LearningMonitorController monitorL;
	private int iUse;

	public MyListener() {
		this(200);
	}

	public MyListener(int iterations) {
		this.iterations = iterations;
		iUse = iterations-1;
		scores = new double[iterations];
	}

	public MyListener(int iterations, String title) {
		this(iterations);
	}

	public MyListener(String title) {
		this();
	}
	public double getCurrentAvg(){
		lock.lock();
		double curAvg = count / ((double) iterations);
		lock.unlock();
		return curAvg;
	}
	public void addMonitor(LearningMonitorController learningMonitorController) {
		monitorL = learningMonitorController;
	}

	@Override
	public ListenerResponse onTrainingStart() {

		return ListenerResponse.CONTINUE;
	}

	@Override
	public void onTrainingEnd() {
		// TODO Auto-generated method stub

	}


	@Override
	public ListenerResponse onNewEpoch(IEpochTrainer trainer) {
		// TODO Auto-generated method stub

		return ListenerResponse.CONTINUE;
	}

	@Override
	/**
	 * fastest possible way of logging I could figure out 
	 */
	public ListenerResponse onEpochTrainingResult(IEpochTrainer trainer, StatEntry statEntry) {
		int score = (int) (statEntry.getReward()) / 5 + ((int) statEntry.getReward() % 5 > 0 ? 1 : 0);
		lock.lock();
		count = count - scores[on] + score;
		scores[on] = score;
		if (on == iUse) {
			monitorL.notifyGraph();
			on = 0;
			
		} else {
			on++;
		}
		lock.unlock();
		return ListenerResponse.CONTINUE;
	}

	@Override
	public ListenerResponse onTrainingProgress(ILearning learning) {
		return ListenerResponse.CONTINUE;
	}

}
