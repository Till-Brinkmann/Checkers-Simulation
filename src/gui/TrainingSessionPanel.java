package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.StackExecutor;
import task.Task;
import training.TrainingSession;

public class TrainingSessionPanel extends JPanel {
	
	protected TrainingSession ts;
	
	private StackExecutor executor;
	
	JLabel[] nnspecLabels;
	String[] nnspecLabelnames = {
			"NNQuantity: ",
			"NNSurviver: ",
			"Inputs: ",
			"#Hiddenlayer: ",
			"#Hiddenneurons: ",
			"Outputs: ",
			"Sigmoid Min: ",
			"Sigmoid Max: ",
			"Weight Min: ",
			"Weight Max: ",
			"Current Mutationpercentage: "
	};
	protected JButton startStop;
	protected JButton delete;
	
	public TrainingSessionPanel() {
		nnspecLabels = new JLabel[12];
		executor = new StackExecutor("TrainingSessionExecutor");
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(
				0,
				0,
				1,
				1,
				1,
				1,
				GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0,0,0,0),
				0,
				0);
		for(int i = 0; i < 11; i++) {
			add(new JLabel(nnspecLabelnames[i]), c);
			nnspecLabels[i] = new JLabel("No Value");
			c.gridx = 1;
			add(nnspecLabels[i], c);
			c.gridx = 0;
			c.gridy++;
		}
		startStop = new JButton("Start");
		startStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				toggleStartStopButton();
			}
			
		});
		startStop.setForeground(Color.GREEN);
		c.anchor = GridBagConstraints.NORTHEAST;
		c.gridx = 3;
		c.gridy = 0;
		add(startStop, c);
		delete = new JButton("DELETE");
		delete.setBackground(Color.RED);
		delete.setForeground(Color.LIGHT_GRAY);
		c.gridx = 4;
		add(delete, c);
	}
	
	public void update() {
		if(ts == null) {
			for(int i = 0; i < 11; i++) {
				nnspecLabels[i].setText("No Value");
			}
			startStop.setEnabled(false);
			return;
		}
		String[] specStrings = ts.nnspecs.toStringArray();
		for(int i = 0; i < 10; i++) {
			nnspecLabels[i].setText(specStrings[i]);
		}
		nnspecLabels[10].setText(Float.toString(ts.changePercentage));
		if(ts.isRunning()) {
			startStop.setForeground(Color.RED);
			startStop.setText("Stop");
		}
		else {
			startStop.setForeground(Color.GREEN);
			startStop.setText("Start");
		}
	}
	
	private void toggleStartStopButton() {
		if(ts.isRunning()) {
			//stop it.
			startStop.setText("Waiting...");
			ts.awaitStopping();
			startStop.setForeground(Color.GREEN);
			startStop.setText("Start");
			//update();
		}
		else {
			//start it.
			executor.execute(new Task() {

				@Override
				public void compute() {
					ts.train();
				}
				
			});
			startStop.setForeground(Color.RED);
			startStop.setText("Stop");
			
		}
	}
}
