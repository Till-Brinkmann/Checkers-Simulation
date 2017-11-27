package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import nn.NNTrainingManager;

public class NNTrainingSettings extends JFrame{
	/**
	 * 
	 */
	private JButton confirm;
	private JSpinner epochsS;
	private JSpinner quantityS;
	private JSpinner surviver;
	
	private NNTrainingManager manager;
	private static final long serialVersionUID = 1L;
	private GUI gui;
	public NNTrainingSettings(GUI pGui) {
		gui = pGui;
		createWindow();	
	}
	private void createWindow() {
		setSize(300,400);
		setLayout(new FlowLayout());
		
		epochsS = new JSpinner();
		
		quantityS = new JSpinner();
		
		surviver = new JSpinner();

		
		confirm = new JButton("Confirm");
        confirm.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	new Thread(new Runnable(){
            		public void run(){
            			manager = new NNTrainingManager(gui, (int)epochsS.getValue(), (int)quantityS.getValue(), Math.min((int)surviver.getValue(), (int)quantityS.getValue()));
            		}
            	}).start();
            	dispose();
            }
        });
		confirm.setBackground(Color.WHITE);
		add(new JLabel("Epochs:"));
		add(epochsS);
		add(new JLabel("Quantity:"));
		add(quantityS);
		add(new JLabel("Surviver:"));
		add(surviver);
		add(confirm);
		setVisible(true);
	}
	
}
