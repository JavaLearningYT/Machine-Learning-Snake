import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscreteDense;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscrete;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscreteDense;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactoryCompGraphStdDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.rl4j.util.DataManagerTrainingListener;
import org.nd4j.linalg.learning.config.Adam;

import learning.core.MyListener;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Simple example for Malmo DQN w/ x,y,z position as input
 * @author howard-abrams (howard.abrams@ca.com) on 1/12/17.
 */
//Maybe see about making the action space only for the unchoosen spaces
public class Control {
    public static QLearning.QLConfiguration MALMO_QL = new QLearning.QLConfiguration(534, //Random seed
                    500, //Max step By epoch
                    500000,//000, //Max step
                    50000,//000, //Max size of experience replay
                    16, //size of batches
                    50,//50, //target update (hard)
                    10, //num step noop warmup
                    0.01, //reward scaling
                    0.99, //gamma
                    15.0, //td-error clipping
                    0.15f, //min epsilon
                    5000, //num step for eps greedy anneal
                    true //double DQN
    );
    public static AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration TOY_ASYNC_QL =
            new AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration(
                    123,        //Random seed
                    200,     //Max step By epoch
                    5000000,      //Max step
                    6,          //Number of threads
                    5,          //t_max
                    25,        //target update (hard)
                    0,          //num step noop warmup
                    0.05,        //reward scaling
                    0.95,       //gamma
                    10.0,       //td-error clipping
                    0.15f,       //min epsilon
                    3000        //num step for eps greedy anneal
            );
    public static DQNFactoryStdDense.Configuration MALMO_NET = DQNFactoryStdDense.Configuration.builder().l2(0.0001)
                    .updater(new Adam(0.0025)).numHiddenNodes(64).numLayer(3).build();
    private static A3CDiscrete.A3CConfiguration CARTPOLE_A3C =
            new A3CDiscrete.A3CConfiguration(
                    (new java.util.Random()).nextInt(),            //Random seed
                    500,            //Max step By epoch
                    8000000,         //Max step
                    16,              //Number of threads
                    25,              //t_max
                    500,             //num step noop warmup
                    0.01,           //reward scaling
                    0.99,           //gamma
                    10.0           //td-error clipping
            );
   // public static ActorCriticFactoryCompGraphStdDense.Configuration CARTPOLE_NET_A3C =  ActorCriticFactoryCompGraphStdDense.Configuration
    //	    .builder().updater(new Adam(.01)).l2(.001).numHiddenNodes(256).numLayer(3).build();

    private static MineEnv createMDP() {
        // Create action space comprised of just discrete north, south, east and west
    	Integer[] moves = new Integer[256];
    	for(int i=0;i<256;i++) {
    		moves[i]=i;
    	}
        MineSweeperActionSpaceDiscrete actionSpace =
                        new MineSweeperActionSpaceDiscrete(moves);

        // Create a basic observation space that simply contains the x, y, z world position
        MineSweeperObservationSpace observationSpace = new MineSweeperObservationSpacePosition();

        // Create a simple policy that just ensures the agent has moved and there is a reward
        MineSweeperDescretePositionPolicy obsPolicy = new MineSweeperDescretePositionPolicy();

        // Create the MDP with the above arguments, and load a mission using an XML file
        return new MineEnv(actionSpace, observationSpace, obsPolicy);
    }

    public static void minesweeperGame() throws  IOException {
        // record the training data in rl4j-data in a new folder (save)

        // Create the MDP complete with a Malmo mission
        MineEnv mdp = createMDP();

        // define the training
        QLearningDiscreteDense<MineBox> dql = new QLearningDiscreteDense<MineBox>(mdp, MALMO_NET, MALMO_QL);
        MyListener listener = new MyListener(50);
        // train
        dql.addListener(listener);
        dql.train();
        
        // get the final policy
        DQNPolicy<MineBox> pol = dql.getPolicy();

        // serialize and save (serialization showcase, but not required)
        pol.save("D:\\Austin\\Cool Projects\\MineSweeperLearn\\mineSweeper3.policy");
        

        // close the mdp
        mdp.close();
    }
    public static void toyAsyncNstep() throws IOException {

        //record the training data in rl4j-data in a new folder
        DataManager manager = new DataManager(true);

        //define the mdp
        MineEnv mdp = createMDP();
        //define the training
        AsyncNStepQLearningDiscreteDense<MineBox> dql = new AsyncNStepQLearningDiscreteDense<MineBox>(mdp, MALMO_NET, TOY_ASYNC_QL);
        MyListener listener = new MyListener(); 
        
        DataManagerTrainingListener p = new DataManagerTrainingListener(manager);
        //enable some logging for debug purposes on toy mdp
        dql.addListener(listener);
        //start the training
        dql.train();
        //useless on toy but good practice!
        mdp.close();

    }

    // showcase serialization by using the trained agent on a new similar mdp
    public static void loadMalmoCliffWalk() throws  IOException {
        // Create the MDP complete with a Malmo mission
        MineEnv mdp = createMDP();

        // load the previous agent
        DQNPolicy<MineBox> pol = DQNPolicy.load("D:\\Austin\\Cool Projects\\MineSweeperLearn\\cliffwalk.policy");
        System.out.println("what");
        // evaluate the agent 10 times
        double rewards = 0;
        for (int i = 0; i < 10; i++) {
        	System.out.println("Supposed to start");
            double reward = pol.play(mdp);
            System.out.println("did start");
            rewards += reward;
            Logger.getAnonymousLogger().info("Reward: " + reward);
        }

        // Clean up
        mdp.close();

        // Print average reward over 10 runs
        Logger.getAnonymousLogger().info("average: " + rewards / 10);
    }
    public static void A3CcartPole() throws IOException {

        //record the training data in rl4j-data in a new folder
        //DataManager manager = new DataManager(true);

        //define the mdp from gym (name, render)
        MineEnv mdp = createMDP();
        
        //StatsStorage s1 = new InMemoryStatsStorage();
        //StatsListener[] s = {new StatsListener(s1,5)};
        //UIServer uiServer = UIServer.getInstance();
        //uiServer.attach(s1);
        //ActorCriticFactorySeparateStdDense.Configuration CARTPOLE_NET_A3C =  ActorCriticFactorySeparateStdDense.Configuration
        	    //.builder().updater(new Adam(.0025)).l2(.02).numHiddenNodes(64).numLayer(3).useLSTM(true).listeners(s).build();
        

        
        //define the training
        /*A3CDiscreteDense<MineBox> a3c = new A3CDiscreteDense<MineBox>(mdp, CARTPOLE_NET_A3C, CARTPOLE_A3C);
        MyListener listener = new MyListener(); 
        a3c.addListener(listener);
        
        a3c.train();
        
        ACPolicy<MineBox> pol = a3c.getPolicy();

        pol.save("/tmp/val1/", "/tmp/pol1");

        //close the mdp (http connection)
        mdp.close();
        
        //reload the policy, will be equal to "pol", but without the randomness
        ACPolicy<MineBox> pol2 = ACPolicy.load("/tmp/val1/", "/tmp/pol1");*/
    }
    

}