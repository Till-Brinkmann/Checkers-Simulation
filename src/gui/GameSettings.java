package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import checkers.GameLogic;
import checkers.Player;
import generic.List;
import gui.GUI.AISpeed;


@SuppressWarnings("serial")
public class GameSettings extends JFrame{

	/**
	 *
	 */
	Class<?> ai;
	URL url;
	URLClassLoader loader;
	
	private JPanel backgroundPanel;
	private ImageIcon gameSettingsIcon;
	//List<Class<?>> availablePlayer;
	Hashtable<String, Class<?>> availablePlayer;
	private JCheckBox recordGame;
	private JButton okButton;
	private JTextField gameNameField;
	
	private JComboBox<String> player1ComboBox;
	private String currentSelectionPlayer1;
	
	private JComboBox<String> player2ComboBox;
	private String currentSelectionPlayer2;
	
	private JSpinner roundsSpinner;
	private JSlider slownessForSlowMode;
	private JCheckBox displayCheckBox;
	private boolean recordGameIsEnabled = false;
	private String gameName;
	private GUI gui;
	private int slowness;
	private JCheckBox useCurrentPf;
	//TODO remove after other solution is tested
	//private Thread gmlcThread;
	public GameSettings(GUI pGui) {		
		super("Game Settings");
		gui = pGui;
		try {
			loader = new URLClassLoader(
					new URL[]{
							new URL("file:" + new File("resources/AI").getAbsolutePath())
							}
					);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		availablePlayer = new Hashtable<String, Class<?>>();
		createPlayerTable();
		initialize();
		createWindow();
		for(Enumeration<String> keys = availablePlayer.keys(); keys.hasMoreElements();){
			String key = keys.nextElement();
			player1ComboBox.addItem(key);
			player2ComboBox.addItem(key);
		}
	}
	private void initialize() {
		setBackground(Color.WHITE);
		gameSettingsIcon = new ImageIcon("resources/Icons/options.png");
		setIconImage(gameSettingsIcon.getImage());
		recordGame = new JCheckBox("gameRecording");
		recordGame.setBackground(Color.WHITE);
		gameNameField = new JTextField(10);
		okButton  = new JButton("confirm");
		okButton.setBackground(Color.WHITE);
		player1ComboBox = new JComboBox<String>();
		player1ComboBox.setBackground(Color.WHITE);
		player1ComboBox.addItem("Player");
		player1ComboBox.setSelectedItem("Player");
		currentSelectionPlayer1 = player1ComboBox.getSelectedItem().toString();
		player2ComboBox = new JComboBox<String>();
		player2ComboBox.addItem("Player");
		player2ComboBox.setBackground(Color.WHITE);
		player2ComboBox.setSelectedItem("Player");
		currentSelectionPlayer2 = player2ComboBox.getSelectedItem().toString();
		
		roundsSpinner = new JSpinner (new SpinnerNumberModel(1, 1, 1000, 1));
		roundsSpinner.setValue(1);
		slownessForSlowMode = new JSlider(0,2000,0);
		slownessForSlowMode.setMajorTickSpacing(1000);
		slownessForSlowMode.setSnapToTicks (true);
		slownessForSlowMode.setPaintLabels(true);
		Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
		
		table.put(0, new JLabel("fast"));
		table.put(1000, new JLabel("medium"));
		table.put(2000, new JLabel("slow"));
		slownessForSlowMode.setLabelTable(table);
		slownessForSlowMode.setForeground(Color.CYAN);
		slownessForSlowMode.setBackground(Color.WHITE);
		
		displayCheckBox = new JCheckBox("Display enabled",true);
		displayCheckBox.setBackground(Color.WHITE);
		displayCheckBox.setEnabled(false);
		
		useCurrentPf = new JCheckBox("using current playfield",false);
		useCurrentPf.setBackground(Color.WHITE);
		if(gui.getGameLogic().getPlayfield().testPlayability()) {
			useCurrentPf.setEnabled(true);
		}
		else {
			useCurrentPf.setEnabled(false);
		}
		slownessForSlowMode.addChangeListener(new ChangeListener()
        {
        	public void stateChanged(ChangeEvent evt){
        		slowness = slownessForSlowMode.getValue();
            }
        });
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
            	if(gui.getGameLogic().getInProgress()) {
            		gui.console.printWarning("A game is currently running. It has to be paused or stopped in order to create a new game.","Gamesettings");
            		return;
            	}
            	gameName = gameNameField.getText();
            	if(gameName.equals("") && recordGameIsEnabled){
            	    gameNameField.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            	    gui.console.printInfo("When game recording is selected, you have to enter a game name!", "Gamesettings");
            		return;
            	}
            	
            	//the game is started in a separate Thread to reduce the load on the eventqueue
            	if(currentSelectionPlayer1.equals("player") && currentSelectionPlayer2.equals("player")) {
					gui.setAISpeed(AISpeed.NOTACTIVE);
				}
				else {
					if(slownessForSlowMode.getValue() == 0) {
						gui.setAISpeed(AISpeed.FAST);
					}
					else if(slownessForSlowMode.getValue() == 1000) {
						gui.setAISpeed(AISpeed.MEDIUM);
					}
					else {
						gui.setAISpeed(AISpeed.SLOW);
					}
				}
				gui.setEnableResume(false);
				gui.setEnablePause(true);
				gui.setEnableStop(true);
            	//gmlcThread = new Thread(
				ForkJoinPool.commonPool().execute(new Runnable() {
            	    public void run()
            	    {
            	    	try {
            	    		Player red;
            	    		Player white;
            	    		if(Math.random() < 0.5){
            	    			red = getPlayer1();
            	    			white = getPlayer2();
            	    		}
            	    		else{
            	    			white = getPlayer1();
            	    			red = getPlayer2();
            	    		}
							gui.getGameLogic().startGame(recordGameIsEnabled, gameName, red, white,(int)roundsSpinner.getValue(),slowness, displayCheckBox.isSelected(), useCurrentPf.isSelected());
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
								 InvocationTargetException | NoSuchMethodException | SecurityException e) {
							gui.console.printWarning("gmlc", "failed to load the ai");
							e.printStackTrace();
						}
            	    }
        	    });  
            	//gmlcThread.start();
            }           
        });
		player1ComboBox.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentSelectionPlayer1 = player1ComboBox.getSelectedItem().toString();
				if(!currentSelectionPlayer1.equals("player") && !currentSelectionPlayer2.equals("player")) {
					displayCheckBox.setEnabled(true);
				}
				else {
					displayCheckBox.setEnabled(false);

				}
			}
        });
		player2ComboBox.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentSelectionPlayer2 = player2ComboBox.getSelectedItem().toString();
				if(!currentSelectionPlayer1.equals("player") && !currentSelectionPlayer2.equals("player")) {
					displayCheckBox.setEnabled(true);
				}
				else {
					displayCheckBox.setEnabled(false);
				}
			}
        });
	}
	private void createWindow() {
		setResizable(false);
		setSize(300,250);
		setAlwaysOnTop (true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		backgroundPanel = new JPanel();
		//backgroundPanel.setPreferredSize(new Dimension(200,300));
		backgroundPanel.setLayout(new BoxLayout(backgroundPanel,BoxLayout.Y_AXIS ));
		JPanel gameNamePanel = new JPanel();
		gameNamePanel.setBackground(Color.WHITE);
		gameNamePanel.setLayout(new FlowLayout());
		gameNamePanel.setPreferredSize(new Dimension(300,4));
		gameNamePanel.add(new JLabel("Game Name:"));
		gameNamePanel.add(gameNameField);
		
		JPanel recordGamePanel = new JPanel();
		recordGamePanel.setBackground(Color.WHITE);
		recordGamePanel.setLayout(new FlowLayout());
		recordGamePanel.setPreferredSize(new Dimension(300,4));
		recordGamePanel.add(recordGame);
		recordGamePanel.add(new JLabel("Rounds:"));
		recordGamePanel.add(roundsSpinner);	
		
		JPanel playerSelection = new JPanel();
		playerSelection.setBackground(Color.WHITE);
		playerSelection.setLayout(new FlowLayout());
		playerSelection.add(player1ComboBox);
		playerSelection.add(player2ComboBox);
		playerSelection.add(displayCheckBox);
		playerSelection.add(useCurrentPf);
		
		JPanel slownessPanel = new JPanel();
		slownessPanel.setPreferredSize(new Dimension(300,6));
		slownessPanel.setBackground(Color.WHITE);
		slownessPanel.add(slownessForSlowMode);
		
		JPanel okButtonPanel = new JPanel();
		okButtonPanel.setPreferredSize(new Dimension(300,6));
		okButtonPanel.setBackground(Color.WHITE);
		okButtonPanel.add(okButton);
		
		backgroundPanel.add(gameNamePanel);
		backgroundPanel.add(recordGamePanel);
		backgroundPanel.add(playerSelection);
		backgroundPanel.add(slownessPanel);
		backgroundPanel.add(okButtonPanel);
		add(backgroundPanel);		
		setVisible(true);
	}
	private void createPlayerTable() {
		File[] files = new File("resources/AI").listFiles();
		int listLength = 1;
		
		if(files != null) {
			for(int i = 0; i < files.length; i++) {
				if(files[i].getName().endsWith(".class")){
					listLength++;
					try {
						ai = loader.loadClass(files[i].getName().substring(0, files[i].getName().length()-6));
						//append only if it is a player
						if(testForPlayerInterface(ai)){
							availablePlayer.put(ai.getName(), ai);
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(files[i].getName().endsWith(".jar")){
					try {
						//ai = loader.loadClass(new JarFile(files[i]).);
						for(Enumeration<JarEntry> entries = new JarFile(files[i]).entries();entries.hasMoreElements();){
							JarEntry entry = entries.nextElement();
							if(entry.getName().startsWith("player/") && entry.getName().endsWith(".class")){
								String className = entry.getName().replace('/', '.').substring(0, entry.getName().length()-6);
								URLClassLoader jarloader = new URLClassLoader(new URL[]{files[i].toURI().toURL()});
								gui.console.printInfo(className);
								ai = jarloader.loadClass(className);
								if(testForPlayerInterface(ai)){
									availablePlayer.put(ai.getName().replace("player.", ""), ai);
								}
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	public Player getPlayer1() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, NoSuchMethodException, SecurityException{
		if(currentSelectionPlayer1.equals("Player")) {
			return gui.playfieldpanel;
		}
		ai = availablePlayer.get(player1ComboBox.getSelectedItem().toString());
		System.out.println("file:" + new File("resources/AI").getAbsolutePath());
		gui.console.printInfo("GameSettings","Class" + ai.getName() + " was loaded successfully");
		return (Player) ai.getConstructor(GameLogic.class, Console.class).newInstance(gui.getGameLogic(), gui.console);
	}
	public Player getPlayer2() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, NoSuchMethodException, SecurityException{
		if(currentSelectionPlayer2.equals("Player")) {
			return gui.playfieldpanel;
		}
		ai = availablePlayer.get(player2ComboBox.getSelectedItem().toString());
		System.out.println("file:" + new File("resources/AI").getAbsolutePath());
		gui.console.printInfo("GameSettings","Class" + ai.getName() + " was loaded successfully");
		return (Player) ai.getConstructor(GameLogic.class, Console.class).newInstance(gui.getGameLogic(), gui.console);
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
	
	public void updateBackground( Color color){
		backgroundPanel.setBackground(color);
	}
}

