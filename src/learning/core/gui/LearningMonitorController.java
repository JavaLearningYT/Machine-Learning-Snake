package learning.core.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;
import org.deeplearning4j.rl4j.learning.configuration.LearningConfiguration;
import org.deeplearning4j.rl4j.network.configuration.NetworkConfiguration;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import learning.core.LinearRegression;
import learning.core.MyListener;

public class LearningMonitorController {
	
	private int visualDelay = 20;
	private int displayCount = 3;
	private boolean run = true;
	private ReentrantLock lock = new ReentrantLock();
	private ArrayList<JPanel> visuals = new ArrayList<JPanel>();
	private int count =0;
	private MyListener listener;
	private boolean graphReady = false;
	private Series<Integer, Double> scores = new Series<Integer, Double>();
	//Trend Line
	private Series<Integer, Double> line = new Series<Integer, Double>();
	
	private LinearRegression regression = new LinearRegression();
	public LearningMonitorController(LearningConfiguration LearningConfig, NetworkConfiguration NetworkSetup, MyListener listener) {
		this.listener=listener;
		listener.addMonitor(this);
		scores.setName("AVG");
		line.setName("TrendLine");
	}
	private Thread repaintThread = new Thread(new Runnable() {

		@Override
		public void run() {
			Thread.currentThread().setName("Repaint Thread");
			while(run) {
				try {
					Thread.sleep(visualDelay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(displayCount>0&&visuals.size()>0) {
					repaintVisuals();
					
				}
			}
			
		}

		
		
	});
	private Thread graphThread = new Thread(new Runnable() {

		@Override
		public void run() {
			Thread.currentThread().setName("Graph Thread");
			while(run) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(graphReady) {
					graphReady=false;
					Platform.runLater(new Runnable() {
					      @Override public void run() {
					    	  updateGraph();
					      }
					    });
					
				}
			}
			
		}
		
	});
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private LineChart<Integer, Double> lineChart;

    @FXML
    private GridPane instanceGrid;

    @FXML
    private Slider displaySlider;

    @FXML
    private Label displayCountLabel;

    @FXML
    private Slider timeDelaySlider;
    
    @FXML
    private Label timeDelayLabel;

    public void updateGraph() {
    	
    	double curAvg = listener.getCurrentAvg();
    	count++;
    	scores.getData().add(new XYChart.Data<Integer, Double>(count, curAvg));
    	regression.add(count, curAvg);
    	regression.regress();
    	if(count>1) {
    	line.getData().clear();
    	line.getData().add(new XYChart.Data<Integer, Double>(0,regression.predict(0)));
    	line.getData().add(new XYChart.Data<Integer, Double>(count,regression.predict(count)));
    	}

    	
    }
    
    public void addVisualization(JPanel networkVisualization) {
		lock.lock();
		if(visuals.size()<displayCount) {
			networkVisualization.setVisible(true);
		}	
		
		visuals.add(networkVisualization);
		int size = visuals.size();
		lock.unlock();
		
		SwingNode node = new SwingNode();
		Platform.runLater(new Runnable() {
		      @Override public void run() {
		    	Thread.currentThread().setName("Javafx Thread");
		    	instanceGrid.add(node, (size-1)%2, (size-1)/2);
		    	node.setContent(networkVisualization);
		    	  updateVisuals();
		    	  displaySlider.setMax(visuals.size());
		      }
		    });		
		
		
	}
    
    private void updateVisuals() {
    	for(int i=0;i<visuals.size();i++) {
			visuals.get(i).setVisible(i<displayCount);
		}
	}

    private void repaintVisuals() {
		for(int i=0;i<displayCount&&i!=visuals.size();i++) {
			visuals.get(i).repaint();
		}
	}
	public void notifyGraph() {
		graphReady = true;
	}
	/**
	 * stops all runnables
	 */
	public void close() {
		run=false;
		visuals.clear();
	}

    @FXML
    void initialize() {
    	assert timeDelaySlider != null : "fx:id=\"timeDelaySlider\" was not injected: check your FXML file 'LearningMonitor.fxml'.";
        assert lineChart != null : "fx:id=\"lineChart\" was not injected: check your FXML file 'LearningMonitor.fxml'.";
        assert instanceGrid != null : "fx:id=\"instanceGrid\" was not injected: check your FXML file 'LearningMonitor.fxml'.";
        assert displaySlider != null : "fx:id=\"displaySlider\" was not injected: check your FXML file 'LearningMonitor.fxml'.";
        assert displayCountLabel != null : "fx:id=\"displayCountLabel\" was not injected: check your FXML file 'LearningMonitor.fxml'.";
        assert timeDelayLabel != null : "fx:id=\"timeDelayLabel\" was not injected: check your FXML file 'LearningMonitor.fxml'.";
        lineChart.getData().add(scores);
    	lineChart.getData().add(line);
    	timeDelaySlider.valueProperty().addListener(
    	        (observable, oldvalue, newvalue) ->
    	        {
    	        	visualDelay=(int)timeDelaySlider.getValue()+20;
    	        	timeDelayLabel.setText(visualDelay+" Milliseconds");
    	        } );
    	displaySlider.valueProperty().addListener(
    	        (observable, oldvalue, newvalue) ->
    	        {
    	        	displayCount=(int)displaySlider.getValue();
    	        	displayCountLabel.setText( displayCount+" Instances");
    	        	updateVisuals();
    	        } );
        repaintThread.start();
        graphThread.start();

    }
}
