import javax.swing.JFrame;
import javax.swing.JLabel;

import org.deeplearning4j.rl4j.learning.IEpochTrainer;
import org.deeplearning4j.rl4j.learning.ILearning;
import org.deeplearning4j.rl4j.learning.listener.TrainingListener;
import org.deeplearning4j.rl4j.util.IDataManager.StatEntry;

public class MyListener implements TrainingListener{
	private long TOTALSCORE = 0;
	private long TOTALINPUT = 0;
	private double[] scores = new double[200];
	private int on=0;
	private JFrame frame = new JFrame();
	private JLabel totalAverage = new JLabel();
	private JLabel AVG = new JLabel();

	@Override
	public ListenerResponse onTrainingStart() {
		//frame.add(totalAverage);
		frame.add(AVG);
		frame.setVisible(true);
		frame.pack();
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
	public ListenerResponse onEpochTrainingResult(IEpochTrainer trainer, StatEntry statEntry) {
		//TOTALSCORE+=(long) statEntry.getReward();
		if(statEntry.getReward()!=0) {
		scores[on]=statEntry.getReward();
		if(on==199) {
			on=0;
		}else {
			on++;
		}
		double count = 0;
		for(double a:scores) {
			count+=a;
		}
		
		//TOTALINPUT++;
		//totalAverage.setText("AVG: "+TOTALSCORE/TOTALINPUT);
		AVG.setText("Short AVG:"+count/200.0);
		}
		return ListenerResponse.CONTINUE;
	}

	@Override
	public ListenerResponse onTrainingProgress(ILearning learning) {
		// TODO Auto-generated method stub
		return ListenerResponse.CONTINUE;
	}
	
}
