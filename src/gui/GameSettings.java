package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.ClassLoader;
import javax.swing.*;


@SuppressWarnings("serial")
public class GameSettings extends JFrame{

	/**
	 *
	 */
	private ImageIcon gameSettingsIcon;
	String[] playerNameList;
	private JCheckBox recordGame;
	private JButton okButton;
	private JTextField gameNameField;
	private JComboBox<String> player1ComboBox;
	private JComboBox<String> player2ComboBox;

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
		gameSettingsIcon = new ImageIcon("resources/Icons/options.png");
		setIconImage(gameSettingsIcon.getImage());
		recordGame = new JCheckBox("gameRecording");
		gameNameField = new JTextField(10);
		okButton  = new JButton("ok");
		okButton.setBackground(Color.WHITE);
		player1ComboBox = new JComboBox<String>(createList());
		player2ComboBox = new JComboBox<String>(createList());

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
            	    gui.console.printInfo("When game recording is selected, you have to enter a game name!", "Gamesettings");
            		return;
            	}
//            	try {
					gui.getGameLogic().startGame(gui.playfieldpanel, gui.playfieldpanel, recordGameIsEnabled, gameName);
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//					gui.console.printWarning("Can not find the AI.class url!\n ClassNotFoundException: " + e);
//				}
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
		add(player1ComboBox);
		add(player2ComboBox);
		setVisible(true);
	}
	private String[] createList() {
		File[] files = new File("resources/AI").listFiles();
		int listLength = 1;
		if(files != null) {
			for(int i = 0; i < files.length; i++) {
				if(files[i].getName().substring(files[i].getName().length()-5,files[i].getName().length()).equals(".class")){
					listLength++;
				}
			}
		}
		playerNameList = new String[listLength];
		if(files != null) {
			int i = 0;
			int j = 0;
			while(j < listLength-1) {
				if(files[j].getName().substring(files[j].getName().length()-5,files[j].getName().length()).equals(".class")){
					playerNameList[i] = files[j].getName();
					i++;
				}
				j++;
			}
		}
		playerNameList[listLength-1] = "player";
		return playerNameList;
	}
	public Class<?> getPlayer1Class() throws ClassNotFoundException{
		if(player1ComboBox.getSelectedItem().equals("player")) {
			return null;
		}
		ClassLoader loader = getClass().getClassLoader();
		loader.getResource((String) player1ComboBox.getSelectedItem());
		return loader.loadClass((String) player1ComboBox.getSelectedItem()); 
	}
	public Class<?> getPlayer2Class() throws ClassNotFoundException{
		if(player2ComboBox.getSelectedItem().equals("player")) {
			return null;
		}
		ClassLoader loader = getClass().getClassLoader();
		loader.getResource((String) player2ComboBox.getSelectedItem());
		return loader.loadClass((String) player2ComboBox.getSelectedItem());
	}
}
