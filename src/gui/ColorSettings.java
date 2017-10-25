package gui;
import javax.swing.event.*;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class ColorSettings extends JFrame{

	private ImageIcon colorSettingsIcon;
	private JPanel foregroundPanel;
    private JSlider[] foregroundSlider;
    private JLabel labelBg;
    private JPanel backgroundPanel;
    private JSlider[] backgroundSlider;
    private JLabel labelFg;
    private JCheckBox officialFigureColor;
    private JButton okButton;
    GUI gui;

	public ColorSettings(GUI pGui,boolean random) {
		super("Color Settings");
		gui = pGui;
		initialize(random);
		createWindow();
		updateColors();
	}
	private void initialize(boolean random) {
		colorSettingsIcon = new ImageIcon("resources/Icons/colorChanger.png");
		setIconImage(colorSettingsIcon.getImage());
		backgroundSlider = new JSlider[3];
		foregroundSlider = new JSlider[3];
		if(random){
			for(int i = 0; i < 3; i++){
				backgroundSlider[i] = new JSlider(0,255,(int)(Math.random()*256));
				backgroundSlider[i].setBackground(Color.WHITE);
				foregroundSlider[i] = new JSlider(0,255,(int)(Math.random()*256));
				foregroundSlider[i].setBackground(Color.WHITE);
			}
		}
		else{
			//TODO richtige Farbwerte eintragen!
		}
		officialFigureColor = new JCheckBox("official figure colors");
		officialFigureColor.setBackground(Color.WHITE);
		okButton = new JButton("confirm");
		okButton.setBackground(Color.WHITE);
		for(int i = 0; i < 3; i++){
	        backgroundSlider[i].addChangeListener(new ChangeListener()
	        {
	        	public void stateChanged(ChangeEvent evt){
	        		updateColors();
	            }
	        });
	        foregroundSlider[i].addChangeListener(new ChangeListener()
	        {
	        	public void stateChanged(ChangeEvent evt){
	        		updateColors();
	            }
	        });
		}
		officialFigureColor.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {

            }

        });
		okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	setVisible(false);
            }

        });
	}

	private void createWindow() {
		setResizable(false);
		setBackground(Color.WHITE);
		setSize(350,450);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		//Background slider
		backgroundPanel = new JPanel();
		backgroundPanel.setBackground(Color.WHITE);
		backgroundPanel.setLayout(new GridLayout(4,1));
		labelBg = new JLabel("Background");
		backgroundPanel.add(labelBg);
		for(int i = 0; i < 3; i++){
			backgroundPanel.add(backgroundSlider[i]);
		}
		add(backgroundPanel);
		//Foreground slider
		foregroundPanel = new JPanel();
		foregroundPanel.setLayout(new GridLayout(4,1));
		labelFg = new JLabel("Foreground");
		foregroundPanel.add(labelFg);
		for(int i = 0; i < 3; i++){
			foregroundPanel.add(foregroundSlider[i]);
		}
		add(foregroundPanel);
		add(officialFigureColor);
		add(okButton);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
	}
	public Color sliderMixer(JSlider[] rgb){
		Color color = new Color(rgb[0].getValue(),rgb[1].getValue(),rgb[2].getValue());
		return color;
	}
	private void updateColors(){
		gui.updateBackground(sliderMixer(backgroundSlider));
		gui.updateForeground(sliderMixer(foregroundSlider));
		backgroundPanel.setBackground(sliderMixer(backgroundSlider));
		foregroundPanel.setBackground(sliderMixer(foregroundSlider));
	}

}
