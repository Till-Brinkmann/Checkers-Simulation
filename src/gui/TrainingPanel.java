package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import training.NNSpecification;
import training.TrainingSession;
import training.TrainingSession.TrainingMode;

public class TrainingPanel extends JPanel {

	public static final File tsDir = new File("resources/Trainingsessions");
	
	JComboBox<TrainingSession> sessions;
	//TODO do we really need a noEntry message?
	private static final TrainingSession noEntry = new TrainingSession("No Entries", TrainingMode.NORMAL, new NNSpecification(1,1,1,1,1,1,1,1,1,1,1,1));
	
	TrainingSessionPanel tsPanel;
	
	public TrainingPanel() {
		initComponents();
		//listen for presses of the delete button
		tsPanel.delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO(?) maybe add an do you REALLY want this window
				//remove from available sessions
				sessions.removeItem(tsPanel.ts);
				//remove the trainingSession (place a delete file flag in the folder)
				try {
					new File("resources/" + tsPanel.ts.name + ".delete").createNewFile();
				} catch (IOException e) {
					NNGUI.console.printError("Could not set the delete flag. Please delete the files of " + tsPanel.ts.name + " manually", "TrainingPanel");
					e.printStackTrace();
				}
				tsPanel.ts = null;
				//sessions.addItem(noEntry);
				tsPanel.setEnabled(false);
			}
			
		});
		//TODO load saved trainingsessions
		//sessions.addItem(new TrainingSession("__TEST__", TrainingMode.NORMAL, new NNSpecification(64, 10, 64, 64, -1, 1, -10, 10, 50, 10, 80, 0.0001f)));
		//sessions.addItem(new TrainingSession("__TEST2__", TrainingMode.NORMAL, new NNSpecification(64, 10, 64, 64, -1, 1, -5, 5, 50, 5, 60, 0.0001f)));
		sessions.addItem(new TrainingSession("MinMax", TrainingMode.MINMAX, new NNSpecification(64, 10, 64, 64, -1, 1, -10, 10, 100, 15, 80, 0.0001f)));
		sessions.addItem(new TrainingSession("Complete self learning", TrainingMode.NORMAL, new NNSpecification(64, 10, 64, 64, -1, 1, -10, 10, 150, 20, 60, 0.0001f)));
		tsPanel.setEnabled(true);
		//if no sessions available load default message
		if(sessions.getItemCount() == 0) sessions.addItem(noEntry);
	}

	private void initComponents(){
		setLayout(new GridBagLayout());
		//setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JButton b = new JButton("New Trainingsession");
		b.setAlignmentX(LEFT_ALIGNMENT);
		b.setAlignmentY(CENTER_ALIGNMENT);
		b.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO Open a window with a lot of setup possibilities
				//and call a method that returns a new TrainingSession to add 
				NNGUI.console.printInfo("Not implemented yet.");
				return;
//				TrainingSession tmp = new TSSetupWindow().get();
//				sessions.addItem(tmp);
//				sessions.setSelectedItem(tmp);
			}
			
		});
		GridBagConstraints c = new GridBagConstraints(
				0,
				0,
				2,
				1,
				1,
				1,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(0,0,0,0),
				0,
				0);
		add(b, c);
		sessions = new JComboBox<TrainingSession>();
		sessions.setAlignmentX(RIGHT_ALIGNMENT);
		sessions.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getItem() == noEntry) {
					tsPanel.ts = null;
				}
				else {
					tsPanel.ts = (TrainingSession) sessions.getSelectedItem();
				}
				// TODO if a new TrainingSession was chosen display its information
				//NNGUI.console.printInfo("action happened");
				tsPanel.update();
			}
			
		});
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 2;
		c.gridwidth = 3;
		add(sessions, c);
		
		tsPanel = new TrainingSessionPanel();
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 6;
		c.gridheight = 9;
		c.fill = GridBagConstraints.BOTH;
		tsPanel.setEnabled(false);
		add(tsPanel, c);
	}
	
	public void saveAll() {
		//TODO sachen löschen
		for(int i = 0; i < sessions.getItemCount(); i++) {
			sessions.getItemAt(i).awaitStopping();
			sessions.getItemAt(i).save();;
		}
	}

}
