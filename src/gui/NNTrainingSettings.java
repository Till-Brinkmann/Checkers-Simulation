package gui;

import javax.swing.JFrame;

import nn.NNTrainingManager;

public class NNTrainingSettings extends JFrame{
	/**
	 * 
	 */
	private NNTrainingManager manager;
	private static final long serialVersionUID = 1L;
	private GUI gui;
	public NNTrainingSettings(GUI pGui) {
		gui = pGui;
		manager = new NNTrainingManager(gui);
	}
	
}
