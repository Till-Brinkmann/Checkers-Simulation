package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.*;

import checkers.GameLogic;
import checkers.Player;


@SuppressWarnings("serial")
public class GameSettings extends JFrame{

	/**
	 *
	 */
	Class<?> ai;
	URL url;
	URLClassLoader loader;
	private ImageIcon gameSettingsIcon;
	String[] playerNameList;
	private JCheckBox recordGame;
	private JButton okButton;
	private JTextField gameNameField;
	private JComboBox<String> player1ComboBox;
	private JComboBox<String> player2ComboBox;
	private JSpinner roundsSpinner;
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
		player1ComboBox.setBackground(Color.WHITE);
		player2ComboBox = new JComboBox<String>(createList());
		player2ComboBox.setBackground(Color.WHITE);
		
		roundsSpinner = new JSpinner ();
		roundsSpinner.setValue(1);
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
            	setAlwaysOnTop(false);
            	setVisible(false);
            	gameName = gameNameField.getText();
            	if(gameName.equals("") && recordGameIsEnabled){
            	    gameNameField.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            	    gui.console.printInfo("When game recording is selected, you have to enter a game name!", "Gamesettings");
            		return;
            	}
            	dispose();
            	//the game is started in a separate Thread to reduce the load on the eventqueue
				new Thread(){
					public void run(){
						try {
							gui.getGameLogic().startGame(recordGameIsEnabled, gameName, getPlayer1(),getPlayer2(),(int)roundsSpinner.getValue());
						} catch (ClassNotFoundException | MalformedURLException | InstantiationException
								| IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException e) {
							gui.console.printWarning("gmlc", "failed to load the ai");
							e.printStackTrace();
						}
					}
				}.start();
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
		add(roundsSpinner);
		setVisible(true);
	}
	private String[] createList() {
		File[] files = new File("resources/AI").listFiles();
		int listLength = 1;
		if(files != null) {
			for(int i = 0; i < files.length; i++) {
				if(files[i].getName().endsWith(".class")){
					listLength++;
				}
			}
		}
		playerNameList = new String[listLength];
		if(files != null) {
			int i = 0;
			int j = 0;
			while(j < listLength-1) {
				if(files[j].getName().endsWith(".class")){
					playerNameList[i] = files[j].getName();
					i++;
				}
				j++;
			}
		}
		playerNameList[listLength-1] = "player";
		return playerNameList;
	}
	public Player getPlayer1() throws ClassNotFoundException, MalformedURLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		if(player1ComboBox.getSelectedItem().equals("player")) {
			return gui.playfieldpanel;
		}
		System.out.println("file:" + new File("resources/AI").getAbsolutePath());
		url = new URL("file:" + new File("resources/AI").getAbsolutePath());
		loader = new URLClassLoader(new URL[]{ url });
		ai = loader.loadClass(((String) player1ComboBox.getSelectedItem()).substring(0,player1ComboBox.getSelectedItem().toString().length()-6));
		gui.console.printInfo("GameSettings","Class" + ai.getName() + " was loaded successfully");
		if(testForPlayerInterface(ai)) {
			return (Player) ai.getConstructor(GameLogic.class, Console.class).newInstance(gui.getGameLogic(), gui.console);
		}
		gui.console.printInfo("Gamesettings", "Because of the missing interface 'Player', Player 1 switches to standard player");
		return gui.playfieldpanel; 
	}
	public Player getPlayer2() throws ClassNotFoundException, MalformedURLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		if(player2ComboBox.getSelectedItem().equals("player")) {
			return gui.playfieldpanel;
		}
		
		if(player1ComboBox.getSelectedItem().equals((String) player2ComboBox.getSelectedItem())) {
			if(testForPlayerInterface(ai)) {
				return (Player) ai.getConstructor(GameLogic.class, Console.class).newInstance(gui.getGameLogic(), gui.console);
			}
			gui.console.printInfo("Gamesettings", "Because of the missing interface 'Player', Player 1 switches to standard player");
			return gui.playfieldpanel;
		} 
		System.out.println("file:" + new File("resources/AI").getAbsolutePath());
		url = new URL("file:" + new File("resources/AI").getAbsolutePath());
		loader = new URLClassLoader(new URL[]{ url });
		ai = loader.loadClass(((String) player2ComboBox.getSelectedItem()).substring(0,player2ComboBox.getSelectedItem().toString().length()-6));
		gui.console.printInfo("GameSettings","Class" + ai.getName() + " was loaded successfully");
		if(testForPlayerInterface(ai)) {			
			return (Player) ai.getConstructor(GameLogic.class, Console.class).newInstance(gui.getGameLogic(), gui.console);
		}
		gui.console.printInfo("Gamesettings", "Because of the missing interface Player 2 switches to standard player");
		return gui.playfieldpanel;
	}
	private boolean testForPlayerInterface(Class<?> ai2) {
		for(Class<?> c : ai2.getInterfaces()) {
			if(c.equals(Player.class)) {
				gui.console.printInfo("GameSettings","Interface player was found in " + ai2.getName());
				return true;
			}
		}
		gui.console.printWarning("GameSettings","Interface could not be found in "+ ai2.getName());
		return false;
	}
}

