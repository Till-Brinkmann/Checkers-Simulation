package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import json.JSONObject;
import training.NNSpecification;
import training.TrainingSession;
import training.TrainingSession.TrainingMode;
/**
 * The panel that holds the comboBox for choosing the trainingsession and a TrainingSessionPanel to display information.
 * @author Till
 *
 */
public class TrainingPanel extends JPanel {

	public static final File tsDirsDir = new File("resources/Trainingsessions");
	
	JComboBox<TrainingSession> sessions;
	JButton newSession;
	TrainingSessionPanel tsPanel;
	
	public TrainingPanel() {
		initComponents();
		//listen for presses of the delete button
		tsPanel.delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO(?) maybe add an "Do you REALLY want this" window
				//remove from available sessions
				TrainingSession sessionToDelete = tsPanel.ts;
				sessions.removeItem(sessionToDelete);
				//remove the trainingSession (place a delete file flag in the folder)
				try {
					System.out.println(new File(tsDirsDir.getAbsolutePath() + "/" + sessionToDelete.name).getAbsolutePath());
					if(!(new File(tsDirsDir.getAbsolutePath() + "/" + sessionToDelete.name).exists())) {
						return;
					}
					new File(tsDirsDir.getAbsolutePath() + "/" + sessionToDelete.name + "/.delete").createNewFile();
				} catch (IOException e) {
					NNGUI.console.printError("Could not set the delete flag. Please delete the files of " + sessionToDelete.name + " manually", "TrainingPanel");
					e.printStackTrace();
				}
				tsPanel.ts = null;
				tsPanel.setEnabled(false);
			}
			
		});
		//loadTrainingSessions();
		/*tsPanel.setEnabled(true);
		//TODO this is only temporarily until you can add trainingsessions via the gui
		sessions.addItem(
				new TrainingSession(
						"MinMax", TrainingMode.MINMAX, new NNSpecification(64, 10, 64, 64, -1, 1, -10, 10, 100, 15), 100, 100, 0.0001f, 0));
		sessions.addItem(
				new TrainingSession(
						"Complete self learning", TrainingMode.NORMAL, new NNSpecification(64, 10, 64, 64, -1, 1, -10, 10, 150, 20), 60, 60, 0.00001f, 0));
		sessions.addItem(
				new TrainingSession(
						"RandomAI", TrainingMode.RANDOMAI, new NNSpecification(64, 10, 64, 64, -1, 1, -10, 10, 100, 15), 100, 100, 0.0001f, 0));
		*/
	}

	private void initComponents(){
		setLayout(new GridBagLayout());
//		JButton b = new JButton("New Trainingsession");
//		b.setAlignmentX(LEFT_ALIGNMENT);
//		b.setAlignmentY(CENTER_ALIGNMENT);
//		b.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				//TODO Open a window with a lot of setup possibilities
//				//and call a method that returns a new TrainingSession to add 
//				NNGUI.console.printInfo("Not implemented yet.");
//				return;
//				TrainingSession tmp = new TSSetupWindow().get();
//				sessions.addItem(tmp);
//				sessions.setSelectedItem(tmp);
//			}
//			
//		});
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
//		add(b, c);
		sessions = new JComboBox<TrainingSession>();
		sessions.setAlignmentX(RIGHT_ALIGNMENT);
		sessions.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				//TODO remove again
				//for now it is only possible to start one session at a time
				if(tsPanel.ts != null) tsPanel.ts.awaitStopping();
				tsPanel.ts = (TrainingSession) sessions.getSelectedItem();
				tsPanel.update();
			}
			
		});
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 2;
		c.gridwidth = 1;
		add(sessions, c);
		newSession = new JButton("Create new session");
		newSession.setBackground(Color.WHITE);
		newSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		c.gridx = 3;
		add(newSession,c);
		
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
	
	public void loadTrainingSessions() {
		if(tsDirsDir.listFiles() == null) {
			NNGUI.console.printWarning("No Trainingsessions found.");
			return;
		}
		for(File tsDir : tsDirsDir.listFiles()) {
			File[] propfiles = tsDir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File file, String name) {
					if(name.equals("Session.json")) return true;
					return false;
				}
			});
			String name;
			TrainingMode mode;
			NNSpecification nnspecs;
			float defaultChangePercentage;
			float changePercentage;
			float learnrate;
			int epoch;
			try {
				FileReader fileReader = new FileReader(propfiles[0]);
				char[] chars = new char[(int)propfiles[0].length()];
				fileReader.read(chars);
				fileReader.close();
				JSONObject object = new JSONObject(String.valueOf(chars));
				name = object.getString("Name");
				mode = TrainingMode.valueOf(object.getString("Mode"));
				JSONObject nnspecsobject = object.getJSONObject("NNSpecs");
				nnspecs = new NNSpecification(
						nnspecsobject.getInt("Inputs"),
						nnspecsobject.getInt("Hiddenlayer Count"),
						nnspecsobject.getInt("Hiddenneuron Count"),
						nnspecsobject.getInt("Outputs"),
						(float)nnspecsobject.getDouble("Sigmoid Min"),
						(float)nnspecsobject.getDouble("Sigmoid Max"),
						(float)nnspecsobject.getDouble("Weight Min"),
						(float)nnspecsobject.getDouble("Weight Max"),
						nnspecsobject.getInt("NN Quantity"),
						nnspecsobject.getInt("NN Surviver"));
				defaultChangePercentage = (float)object.getDouble("Default Changepercentage");
				changePercentage = (float)object.getDouble("Current Changepercentage");
				learnrate = (float)object.getDouble("Learnrate");
				epoch = object.getInt("Epoch");
				sessions.addItem(new TrainingSession(name, mode, nnspecs, defaultChangePercentage, changePercentage, learnrate, epoch));
			} catch (Exception e) {
				//do nothing
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void saveAll() {
		if(tsDirsDir.listFiles() == null) {
			NNGUI.console.printWarning("No Trainingsessions found.");
			return;
		}
		for(File tsDir : tsDirsDir.listFiles()) {
			if(tsDir.isDirectory() && tsDir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if(name.equals(".delete")) return true;
						return false;
					}
					
				}).length != 0) {
				try {
					Files.walkFileTree(tsDir.toPath(), new FileVisitor<Path>(){

						@Override
						public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
							arg0.toFile().delete();
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) throws IOException {
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
							arg0.toFile().delete();
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
							return FileVisitResult.CONTINUE;
						}
						
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		for(int i = 0; i < sessions.getItemCount(); i++) {
			//saves automatically when stopped
			sessions.getItemAt(i).awaitStopping();
		}
	}

}
