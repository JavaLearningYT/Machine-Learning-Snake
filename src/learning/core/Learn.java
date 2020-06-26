package learning.core;

import java.util.concurrent.locks.ReentrantLock;

import org.nd4j.linalg.factory.Nd4j;

import javafx.application.Application;
import javafx.stage.Stage;
import learning.games.snake.learn.SnakeControl;

public class Learn extends Application{
	private static int THREADCOUNT=0;
	private static final int MAXCOUNT=4;
	private static ReentrantLock lock = new ReentrantLock();
	public static void main(String[] args) {
		launch();
	}
	public static boolean display() {
		//return false;
		
		lock.lock();
		THREADCOUNT++;
		boolean a = (THREADCOUNT<=MAXCOUNT);
		lock.unlock();
		return a;
	}
	@Override
	public void start(Stage arg0) throws Exception {
		//Native.setProtected(true);
				//System.out.println(Native.isProtected());
				/*new Thread(new Runnable()->{

					@Override
					public void run() {
						while(true) {
							
						}
						
					}
					
				});*/
				//Nd4j.getEnvironment().allowHelpers(true);
				System.setProperty("sun.java2d.opengl","True");
				//Nd4j.getMemoryManager().togglePeriodicGc(enabled);
				Nd4j.getMemoryManager().setAutoGcWindow(10000);             //Set to 10 seconds (10000ms) between System.gc() calls
				Nd4j.getMemoryManager().togglePeriodicGc(false);   
				try {
					
					
					 //Nd4j.getMemoryManager().setAutoGcWindow(1000);
					//CudaEnvironment.getInstance().getConfiguration().setMaximumDeviceCacheableLength(1024 * 1024 * 1024L).setMaximumDeviceCache( 2 * 1024 * 1024 * 1024L).setMaximumHostCacheableLength(1024 * 1024 * 1024L).setMaximumHostCache(2 * 1024 * 1024 * 1024L);
					//SnakeControl.A3CcartPole();
					//SnakeControl.minesweeperGame();
					SnakeControl.toyAsyncNstep();
					//SnakeControl.AsyncTest();
					//SnakeControl.optimalTmax();
					//SnakeControl.optimalSettings();
					//SnakeControl.optimalScaling();
					//SnakeControl.optimalTd();
					} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
}
