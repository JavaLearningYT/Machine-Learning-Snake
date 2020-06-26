package learning.games.snake.learn;


  
/*******************************************************************************
 * Copyright (c) 2015-2019 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/


import org.deeplearning4j.rl4j.learning.HistoryProcessor;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteConv;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdConv;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.rl4j.util.DataManagerTrainingListener;

import com.microsoft.msr.malmo.MissionSpec;


import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

/**
 * More complex example for Malmo DQN w/ screen pixels as input. After the network learns how to find the reward
 * on a simple open plane, the mission is made more complex by putting lava in the way.
 * @author howard-abrams (howard.abrams@ca.com) on 1/12/17.
 */
public class SnakeControlGrid {
    public static QLearning.QLConfiguration MALMO_QL = new QLearning.QLConfiguration(123, //Random seed
                    200, //Max step By epoch
                    100000, //Max step
                    50000, //Max size of experience replay
                    32, //size of batches
                    500, //target update (hard)
                    10, //num step noop warmup
                    0.01, //reward scaling
                    0.99, //gamma
                    1.0, //td-error clipping
                    0.1f, //min epsilon
                    10000, //num step for eps greedy anneal
                    true //double DQN
    );

    public static DQNFactoryStdConv.Configuration MALMO_NET = new DQNFactoryStdConv.Configuration(
    				0.01, //learning rate
                    0.00, //l2 regularization
                    null, // updater
                    null // Listeners
    );

    /*
     * The pixel input is 320x240, but using the history processor we scale that to 160x120
     * and then crop out a 160x80 segment to remove pixels that aren't needed
     */
    public static HistoryProcessor.Configuration MALMO_HPROC = new HistoryProcessor.Configuration(1, // Number of frames
                    32, // Scaled width
                    32, // Scaled height
                    16, // Cropped width
                    16, // Cropped height
                    0, // X offset
                    0, // Y offset
                    0 // Number of frames to skip
    );


    private static SnakeEnv createMDP() {
        // Create action space comprised of just discrete north, south, east and west
    	Integer[] moves = {1,2,3};
        SnakeActionSpaceDiscrete actionSpace =
                        new SnakeActionSpaceDiscrete( moves);

        // Create a basic observation space that simply contains the x, y, z world position
        SnakeObservationSpace observationSpace = new SnakeObservationSpaceGrid(16, 16);

        // Create a simple policy that just ensures the agent has moved and there is a reward
       SnakeDiscretePositionPolicy obsPolicy = new SnakeDiscretePositionPolicy();

        // Create the MDP with the above arguments, and load a mission using an XML file
        return new SnakeEnv(actionSpace, observationSpace, obsPolicy);
    }

    public static void minesweeperGame() throws  IOException {
        // record the training data in rl4j-data in a new folder (save)
        DataManager manager = new DataManager(true);

        // Create the MDP complete with a Malmo mission
        SnakeEnv mdp = createMDP();

        // define the training
        System.out.println(Arrays.toString(MALMO_HPROC.getShape()));
        QLearningDiscreteConv<SnakeBox> dql =
                new QLearningDiscreteConv<SnakeBox>(mdp, MALMO_NET, MALMO_HPROC, MALMO_QL, manager);
        DataManagerTrainingListener p = new DataManagerTrainingListener(manager);
        dql.addListener(p);
        // train
        dql.train();
        
        // get the final policy
        DQNPolicy<SnakeBox> pol = dql.getPolicy();

        // serialize and save (serialization showcase, but not required)
        pol.save("D:\\Austin\\Cool Projects\\MineSweeperLearn\\mineSweeper3.policy");
        

        // close the mdp
        mdp.close();
    }
/*
    //showcase serialization by using the trained agent on a new similar mdp
    public static void loadMalmoCliffWalk() throws MalmoConnectionError, IOException {
        MalmoEnv mdp = createMDP(10000);

        //load the previous agent
        DQNPolicy<MalmoBox> pol = DQNPolicy.load("cliffwalk_pixel.policy");

        //evaluate the agent
        double rewards = 0;
        for (int i = 0; i < 10; i++) {
            double reward = pol.play(mdp, new HistoryProcessor(MALMO_HPROC));
            rewards += reward;
            Logger.getAnonymousLogger().info("Reward: " + reward);
        }

        // Clean up
        mdp.close();

        Logger.getAnonymousLogger().info("average: " + rewards / 10);
    }*/
}

