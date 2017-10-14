package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

public class GameSettings extends JFrame{

	/**
	 * 
	 */
	private JCheckBox recordGame;
	private JButton okButton;
	private JTextField gameNameField;
	
	private boolean recordGameIsEnabled = false;
	private String gameName;
	private GUI gui;
	public GameSettings(GUI pGui) {
		super("Game Settings");
		gui = pGui;
		initialize();
		createWindow();
	}
	private void initialize() {
		recordGame = new JCheckBox("gameRecording");
		gameNameField = new JTextField(10);
		okButton  = new JButton("ok");
		okButton.setBackground(Color.WHITE);
		
		recordGame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	gui.getGameLogic().getPlayfield().enableGameRecording(recordGame.isSelected());
            	recordGameIsEnabled = recordGame.isSelected();
            }
            
        });
		okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	gameName = gameNameField.getText();
            	if(gameName.equals("") && recordGameIsEnabled){
            	    gameNameField.setBorder(BorderFactory.createLineBorder(Color.RED, 1));            	    
            		return;
            	}
            	if(!gameName.equals("")){
            		
            	}
            	gui.getGameLogic().startGame(gui.playfieldpanel, gui.playfieldpanel);
            	setAlwaysOnTop(false);
            	setVisible(false);
            	dispose();
            }
            
        });
	}
	private void createWindow() {
		setResizable(false);
		setLayout(new FlowLayout());
		setSize(350,450);
		setAlwaysOnTop (true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		add(recordGame);
		add(gameNameField);
		add(okButton);
		setVisible(true);
	}
}
