package learning.games.snake.learn;

import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscreteDense;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscreteDense;
import org.deeplearning4j.rl4j.learning.configuration.A3CLearningConfiguration;
import org.deeplearning4j.rl4j.learning.configuration.AsyncQLearningConfiguration;
import org.deeplearning4j.rl4j.network.configuration.ActorCriticDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;

import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.nd4j.linalg.learning.config.AMSGrad;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.learning.config.Sgd;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import learning.core.MyListener;
import learning.core.gui.LearningMonitorController;

import java.io.File;
import java.io.IOException;

/**
 * Simple example for Malmo DQN w/ x,y,z position as input
 * 
 * @author howard-abrams (howard.abrams@ca.com) on 1/12/17.
 */
//Maybe see about making the action space only for the unchoosen spaces

/*
 * PROBLEM THOUGHT The program might be crashing to poor performance because bad
 * games finish faster meaning they have a larger effect on the network because
 * more of them occur
 */
public class SnakeControl {

	public static final int SNAKESIZE = 12;

	public static ActorCriticDenseNetworkConfiguration NET_A3C = ActorCriticDenseNetworkConfiguration.builder()
			.l2(.000001).numHiddenNodes(16).numLayers(5).updater(new AMSGrad(.0025)).useLSTM(true).build();
	private static A3CLearningConfiguration SETTINGS_A3C = A3CLearningConfiguration.builder().numThreads(16)
			.maxEpochStep(500).nStep(8).gamma(.99).maxStep(2000000).rewardFactor(.05).learnerUpdateFrequency(-1)
			.build();

	public static AsyncQLearningConfiguration ASYNC_QL = AsyncQLearningConfiguration.builder().numThreads(16)
			.updateStart(5000).epsilonNbStep(250000).maxEpochStep(1000).gamma(.99).rewardFactor(.05).maxStep(20000000)
			.nStep(8).minEpsilon(.005f).build();
	public static DQNDenseNetworkConfiguration MALMO_NET = DQNDenseNetworkConfiguration.builder().l2(.000001)
			.numHiddenNodes(64).numLayers(5).updater(new AMSGrad(.00025)).build();


	private static SnakeEnv createMDP() {
		// Create action space comprised of just discrete north, south, east and west

		Integer[] moves = { 1, 2, 3 };
		SnakeActionSpaceDiscrete actionSpace = new SnakeActionSpaceDiscrete(moves);
		// Create a basic observation space that simply contains the x, y, z world
		// position
		SnakeObservationSpace observationSpace = new SnakeObservationSpaceGrid(SNAKESIZE, SNAKESIZE);

		// Create a simple policy that just ensures the agent has moved and there is a
		// reward
		SnakeDiscretePositionPolicy obsPolicy = new SnakeDiscretePositionPolicy();

		// Create the MDP with the above arguments, and load a mission using an XML file
		return new SnakeEnv(actionSpace, observationSpace, obsPolicy, SNAKESIZE);
	}

	private static SnakeEnv createMDP(LearningMonitorController mon) {
		// Create action space comprised of just discrete north, south, east and west

		Integer[] moves = { 1, 2, 3 };
		SnakeActionSpaceDiscrete actionSpace = new SnakeActionSpaceDiscrete(moves);
		// Create a basic observation space that simply contains the x, y, z world
		// position
		SnakeObservationSpace observationSpace = new SnakeObservationSpaceGrid(SNAKESIZE, SNAKESIZE);

		// Create a simple policy that just ensures the agent has moved and there is a
		// reward
		SnakeDiscretePositionPolicy obsPolicy = new SnakeDiscretePositionPolicy();

		// Create the MDP with the above arguments, and load a mission using an XML file
		return new SnakeEnv(actionSpace, observationSpace, obsPolicy, SNAKESIZE, mon);
	}
	
	private static SnakeEnv createMDP2(LearningMonitorController controller) {
		Integer[] moves = { 1, 2, 3 };
		SnakeActionSpaceDiscrete actionSpace = new SnakeActionSpaceDiscrete(moves);
		// Create a basic observation space that simply contains the x, y, z world
		// position
		SnakeObservationSpace observationSpace = new SnakeObservationSpaceSmall(27);

		// Create a simple policy that just ensures the agent has moved and there is a
		// reward
		SnakeDiscretePositionPolicy obsPolicy = new SnakeDiscretePositionPolicy();

		// Create the MDP with the above arguments, and load a mission using an XML file
		return new SnakeEnv(actionSpace, observationSpace, obsPolicy, SNAKESIZE, controller);
	}

	public static void toyAsyncNstep() throws IOException {
		Thread.currentThread().setName("Main Thread");
		MyListener listener = new MyListener(1000);
		// record the training data in rl4j-data in a new folder
		FXMLLoader loader = new FXMLLoader((new File("D:\\Austin\\Cool Projects\\MineSweeperLearn\\src\\resources\\fxml\\LearningMonitor.fxml")).toURI().toURL());
		LearningMonitorController controller = new LearningMonitorController(ASYNC_QL, MALMO_NET, listener);
		loader.setController(controller);
		Parent root = loader.load();
        
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

		// define the mdp
		SnakeEnv mdp = createMDP2(controller);
		// mdp.setSize(SNAKESIZE);
		// define the training

		AsyncNStepQLearningDiscreteDense<SnakeBox> dql = new AsyncNStepQLearningDiscreteDense<SnakeBox>(mdp, MALMO_NET,
				ASYNC_QL);

		// enable some logging for debug purposes on toy mdp

		dql.addListener(listener);
		// start the training
		new Thread(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName("Learning Thread");
		try {
			dql.train();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// useless on toy but good practice!
		mdp.close();
		System.out.println("finished async");}}).start();;
		

	}
	/*
	public static void AsyncTest() throws IOException {
		IUpdater[] updaters = {new Adam(.0025),new Nadam(.0025), new Sgd(.0025), new AMSGrad(.0025)};
		int i=1;
		for(IUpdater a:updaters) {
			MyListener listener = new MyListener(1000);
			// record the training data in rl4j-data in a new folder
			MALMO_NET.setUpdater(a);
			LearningMonitor monitor = new LearningMonitor(ASYNC_QL, MALMO_NET, listener);

			// define the mdp
			SnakeEnv mdp = createMDP2(monitor);
			// mdp.setSize(SNAKESIZE);
			// define the training

			AsyncNStepQLearningDiscreteDense<SnakeBox> dql = new AsyncNStepQLearningDiscreteDense<SnakeBox>(mdp, MALMO_NET,
					ASYNC_QL);

			// enable some logging for debug purposes on toy mdp

			dql.addListener(listener);
			// start the training
			try {
				dql.train();
			} catch (Exception e) {
				e.printStackTrace();
			}
			monitor.saveData(""+i);
			i++;
			monitor.dispose();
			// useless on toy but good practice!
			mdp.close();
		}
	}
	public static void A3CcartPole() throws IOException {

		MyListener listener = new MyListener();
		// record the training data in rl4j-data in a new folder
		LearningMonitor monitor = new LearningMonitor(ASYNC_QL, MALMO_NET, listener);

		// define the mdp
		SnakeEnv mdp = createMDP2(monitor);

		// define the training
		A3CDiscreteDense<SnakeBox> a3c = new A3CDiscreteDense<SnakeBox>(mdp, NET_A3C, SETTINGS_A3C);
		a3c.addListener(listener);

		a3c.train();

		ACPolicy<SnakeBox> pol = a3c.getPolicy();

		// pol.save("/tmp/val1/", "/tmp/pol1");

		// close the mdp (http connection)
		mdp.close();
		System.out.println("Done");
	}
	/*
	 * public static void optimalTmax() throws IOException { String value = "tmax";
	 * String path = new String("D:\\QLearning\\Async\\optimalcompare2\\"+value);
	 * File file = new File(path);
	 * 
	 * file.mkdirs(); FileWriter writer = new FileWriter(path+"\\settings.txt");
	 * writer.write(MALMO_NET.toString()+"\n");
	 * writer.write(TOY_ASYNC_QL.toString()); writer.flush(); writer.close();
	 * for(int i=20;i<=10240;i=i*2){ SnakeEnv mdp = createMDP(); Stopwatch watch =
	 * Stopwatch.createStarted(); //watch.start();
	 * AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration newSettings =
	 * TOY_ASYNC_QL; newSettings.setNstep(i);//.setTargetDqnUpdateFreq(i);
	 * AsyncNStepQLearningDiscreteDense<SnakeBox> dql = new
	 * AsyncNStepQLearningDiscreteDense<SnakeBox>(mdp, MALMO_NET, newSettings);
	 * MyListener listener = new MyListener(50,value+":"+i); //enable some logging
	 * for debug purposes on toy mdp
	 * 
	 * dql.addListener(listener); //start the training try { dql.train();
	 * }catch(Exception e){ PrintWriter write = new
	 * PrintWriter(path+"\\"+value+"-"+i+"-ERROR.txt"); try {
	 * e.printStackTrace(write); }catch(Exception a) { e.printStackTrace();
	 * a.printStackTrace(); }finally { write.close(); }
	 * 
	 * }
	 * 
	 * watch.stop(); ImageIO.write(listener.getGraph(), ImageFormat.PNG, new
	 * File(path+"\\"+value+"-"+i+"-time-"+watch.elapsed(TimeUnit.MINUTES)+".png"));
	 * dql=null; //ImageWriter writer = new ImageWriter(); mdp.close(); } } public
	 */
}