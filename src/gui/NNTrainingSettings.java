package gui;

import java.awt.Color;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.filechooser.FileFilter;

import nn.NNTrainingManager;

public class NNTrainingSettings extends JFrame{
	/**
	 * 
	 */
	private boolean continueT = false;;
	private JButton confirm;
	private JSpinner epochsS;
	private JSpinner quantityS;
	private JSpinner surviver;
	private JSpinner inputNeurons;
	private JSpinner outputNeurons;
	private JSpinner hiddenNeurons;
	private JSpinner hiddenLayer;
	private JSpinner sigmin;
	private JSpinner sigmax;
	private JSpinner weightsmin;
	private JSpinner weightsmax;
	private JCheckBox continueTraining;
	
	private NNTrainingManager manager;
	private static final long serialVersionUID = 1L;
	private GUI gui;
	private Console console;
	public NNTrainingSettings(GUI pGui, Console console) {
		super("NN training settings");
		gui = pGui;
		this.console = console;
		createWindow();	
	}
	private void createWindow() {
		setIconImage(new ImageIcon("resources/Icons/options.png").getImage());

		setSize(300,450);
		setLayout(new FlowLayout());
		
		continueTraining = new JCheckBox("continue training");
        continueTraining.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent event)
            {
            	if(continueTraining.isSelected()) {
            		
            		int availableNNs = nnAvailability();
            		if(availableNNs == 0) {
            			continueTraining.setSelected(false);
            			console.printWarning("No nn was found in the folder /resources/NNSave", "NNTrainingsSettings");
            		}
            		else {
            			continueT = true;
            			if(availableNNs == 1) {
            				console.printInfo(" 1 NN was found.", "NNTrainingsSettings");
            			}
            			else {
            				String available = Integer.toString(availableNNs);
            				console.printInfo(available + " NNs were found. Please do not lower the NNquanity below " + available + ".", "NNTrainingsSettings");      
            			}           			
	            		try {
							setNNinfos();
							console.printInfo("NNSettings changed values to existing NNs", "NNTrainingsSettings");
						} catch (NumberFormatException | IOException e) {
							console.printWarning("Error in setNNinfos.","NNTraingsSettings");
							e.printStackTrace();
						}
	            		enableNNSettings(false);		            		
        			}
        		}           	
            	else {
            		continueT = false;
            		enableNNSettings(true);
            	}
            }
        });
		epochsS = new JSpinner();
		epochsS.setValue(new Integer(20));
		quantityS = new JSpinner();
		quantityS.setValue(new Integer(20));
		surviver = new JSpinner();
		surviver.setValue(new Integer(10));
		inputNeurons = new JSpinner();
		inputNeurons.setValue(new Integer(64));
		outputNeurons = new JSpinner();
		outputNeurons.setValue(new Integer(64));
		hiddenNeurons = new JSpinner();
		hiddenNeurons.setValue(new Integer(64));
		hiddenLayer = new JSpinner();
		hiddenLayer.setValue(new Integer(10));
		sigmin = new JSpinner();
		sigmin.setValue(new Integer(-1));
		sigmax = new JSpinner();
		sigmax.setValue(new Integer(1));
		weightsmin = new JSpinner();
		weightsmin.setValue(new Integer(-10));
		weightsmax = new JSpinner();
		weightsmax.setValue(new Integer(10));
		
		
		confirm = new JButton("     Confirm     ");
        confirm.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	new Thread(new Runnable(){
            		public void run(){
            			manager = new NNTrainingManager(continueT, gui, (int)epochsS.getValue(), (int)quantityS.getValue(), Math.min((int)surviver.getValue(),
            					(int)quantityS.getValue()), (int)inputNeurons.getValue(), (int)outputNeurons.getValue(), (int)hiddenNeurons.getValue(), 
            					(int)hiddenLayer.getValue(), Double.parseDouble(sigmin.getValue().toString()), Double.parseDouble(sigmax.getValue().toString()), 
            					 Double.parseDouble(weightsmin.getValue().toString()), Double.parseDouble(weightsmax.getValue().toString()));
            			
            		}
            	}).start();
            	dispose();
            }
        });
		confirm.setBackground(Color.WHITE);
		add(new JLabel("----------general settings----------"));
		add(new JLabel("Epochs:                             "));
		add(epochsS);
		add(new JLabel("Quantity:                           "));
		add(quantityS);
		add(new JLabel("Surviver:                           "));
		add(surviver);
		add(continueTraining);
		add(new JLabel("------------NN settings-------------"));
		add(new JLabel("inputNeurons:                       "));
		add(inputNeurons);
		add(new JLabel("outputNeurons:                      "));
		add(outputNeurons);
		add(new JLabel("hiddenNeurons:                      "));
		add(hiddenNeurons);
		add(new JLabel("hiddenLayer:                        "));
		add(hiddenLayer);
		add(new JLabel("sigmoidMin:                         "));
		add(sigmin);
		add(new JLabel("sigmoidMax:                         "));
		add(sigmax);
		add(new JLabel("weigthsMin:                         "));
		add(weightsmin);
		add(new JLabel("weightsMax:                         "));
		add(weightsmax);
		add(confirm);
		setVisible(true);
	}
	private void enableNNSettings(boolean a) {
		inputNeurons.setEnabled(a);
		outputNeurons.setEnabled(a);
		hiddenNeurons.setEnabled(a);
		hiddenLayer.setEnabled(a);
		sigmin.setEnabled(a);
		sigmax.setEnabled(a);
		weightsmin.setEnabled(a);
		weightsmax.setEnabled(a);
	}
	private int nnAvailability() {
		File directory = new File("resources/NNSave");
        FilenameFilter filter = new FilenameFilter() {
        	public boolean accept(File directory, String fileName) {
        		return fileName.endsWith(".nni");
        	}
        };
		String[] list = directory.list(filter); 
		return list.length;
	}
	private void setNNinfos() throws NumberFormatException, IOException {
		File directory = new File("resources/NNSave");
		String[] nns = directory.list();
		File file = new File("resources/NNSave/" + nns[0]);
		FileReader reader;
		reader = new FileReader(file);
		@SuppressWarnings("resource")
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;
		int lineNumber = 1;
		while ((line = bufferedReader.readLine()) != null) {
			switch(lineNumber) {
			case 3:
				weightsmax.setValue(Double.parseDouble(line));
				break;
			case 5:
				weightsmin.setValue(Double.parseDouble(line));
				break;		    	  
			case 7:
				sigmax.setValue(Double.parseDouble(line));
				break;		    	  
			case 9:
				sigmin.setValue(Double.parseDouble(line));
				break;				  
			case 11:
				inputNeurons.setValue((int) Double.parseDouble(line));
				break;				  
			case 13:
				outputNeurons.setValue((int) Double.parseDouble(line));
				break;				  
			case 15:
				hiddenNeurons.setValue((int) Double.parseDouble(line));
				break;				  
			case 17:
				hiddenLayer.setValue((int) Double.parseDouble(line));
		  	break;	
			}
		lineNumber++;
		}
	}
}
