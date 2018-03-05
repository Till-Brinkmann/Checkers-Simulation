package gui;

import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import training.TrainingSession;
/**
 * Main frame of the program.
 */
public class NNGUI extends JFrame{

	public static Console console = new Console();
	public static LineChart chart = new LineChart();
	//public static BarChart barChart = new BarChart();
	private static TrainingPanel tp;
	public NNGUI() {
		super("Checker Simulation NN Training");
		initComponents();
		resize();
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) {}
			@Override
			public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {
				console.printInfo("Saving your data. Please wait a moment.");
				//need to call invokeLater, otherwise you wont see the message above.
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						tp.saveAll();
						System.exit(0);
					}
					
				});
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowOpened(WindowEvent arg0) {}
			
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		console.printInfo("Loading Trainingsessions...");
		tp.loadTrainingSessions();
		console.print("...Finished");
	}
	
	private void initComponents(){
		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());
		tp = new TrainingPanel();
		GridBagConstraints c = new GridBagConstraints(
				0,
				0,
				6,
				1,
				1,
				1,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.BOTH,
				new Insets(0,0,0,0),
				0,
				0);
		cp.add(tp, c);
		c.gridx = 7;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 4;
		cp.add(console.panel, c);
	}
	
	private void resize(){
		DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		this.setBounds(
				mode.getWidth()/10,
				mode.getHeight()/10,
				(int) Math.round((mode.getHeight()/2.3f)*1.777),
				(int) Math.round(mode.getHeight()/2.3f));
	}
	
	public static void main(String[] args){
		boolean noGUI = false;
		boolean startTraining = false;
		String tsName = "";
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("--noGUI")) noGUI = true;
			if(args[i].equals("--train")) {
				if(i + 1 >= args.length || (!args[i+1].startsWith("\"") && !args[i+1].endsWith("\""))) {
					System.out.println(
							"Please add the name of the trainingsession"
							+ " you want to start in quotation marks(\") behind the --train parameter.");
				}
				else {
					startTraining = true;
					tsName = args[i+1].substring(1,args[i+1].length()-1);
				}
			}
		}
		
		if(noGUI && startTraining) {
			tp = new TrainingPanel();
			TrainingSession ts = null;
			tp.loadTrainingSessions();
			for(int i = 0, max = tp.sessions.getItemCount(); i < max; i++) {
				if(tp.sessions.getItemAt(i).name == tsName) {
					ts = tp.sessions.getItemAt(i);
					break;
				}
			}
			if(ts == null) {
				System.out.println("Session could not be found or loaded.");
				return;
			}
			//this will loop until the console is closed
			ts.train();
		}
		else {
			new NNGUI();
		}
	}

}
