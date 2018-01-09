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

public class NNGUI extends JFrame{

	public static Console console = new Console();
	private TrainingPanel tp;
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
			public void windowOpened(WindowEvent arg0) {
				//TODO load sessions
			}
			
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
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
				Math.round(mode.getWidth()/2.5f),
				Math.round(mode.getHeight()/2.5f));
	}
	
	public static void main(String[] args){
		new NNGUI();
	}

}
