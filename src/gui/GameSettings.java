package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import checkers.GameLogic;
import checkers.Player;
import evaluation.EvaluationManager;
import gui.GUI.AISpeed;
import main.StackExecutor;
import task.Task;
import utilities.FileUtilities;

/**
 * Pops up when the user starts a new game.
 * It is used to set all parameters nedded to start a game (eg. which type of palyer to use, etc.).
 */
public class GameSettings extends JFrame{

	Class<?> ai;
	URL url;
	URLClassLoader loader;
	StackExecutor executor;
	
	private JPanel backgroundPanel;
	private ImageIcon gameSettingsIcon;
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
	private GameLogic gmlc;
	private int slowness;
	private JCheckBox useCurrentPf;
	/**
	 * Does everything!
	 * @param pGui A refernz to the GUI object.
	 */
	public GameSettings(GUI pGui) {	
		super("Game Settings");
		gui = pGui;
		gmlc = gui.getGameLogic();
		loader = new URLClassLoader(FileUtilities.getAiUrls());
		availablePlayer = new Hashtable<String, Class<?>>();
		executor = new StackExecutor();
		createPlayerTable();
		initialize();
		createWindow();
		for(Enumeration<String> keys = availablePlayer.keys(); keys.hasMoreElements();){
			String key = keys.nextElement();
			player1ComboBox.addItem(key);
			player2ComboBox.addItem(key);
		}
	}
	/**
	 * This method initializes all window elements and add the related actionlisteners.
	 */
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
		if(gmlc.getPlayfield().testPlayability()) {
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
            	recordGameIsEnabled = recordGame.isSelected();
            }

        });
		okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	createGame();
            }           
        });
		player1ComboBox.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentSelectionPlayer1 = player1ComboBox.getSelectedItem().toString();
				if(!currentSelectionPlayer1.equals("Player") && !currentSelectionPlayer2.equals("Player")) {
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
				if(!currentSelectionPlayer1.equals("Player") && !currentSelectionPlayer2.equals("Player")) {
					displayCheckBox.setEnabled(true);
				}
				else {
					displayCheckBox.setEnabled(false);
				}
			}
        });
	}
	/**
	 * Sets up the window properties and adds the elements to the window.
	 */
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
		gameNamePanel.add(new JLabel("Run Name:"));
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
	/**
	 * Searches for all available Players and adds them to a hashtable. 
	 */
	private void createPlayerTable() {
		File[] files = new File("resources/AI").listFiles();
		if(files != null) {
			for(int i = 0; i < files.length; i++) {
				if(files[i].getName().endsWith(".class")){
					try {
						ai = loader.loadClass(files[i].getName().substring(0, files[i].getName().length()-6));
						//append only if it is a player
						if(testForPlayerInterface(ai)){
							availablePlayer.put(ai.getName(), ai);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				else if(files[i].getName().endsWith(".jar")){
					try {
						for(Enumeration<JarEntry> entries = new JarFile(files[i]).entries();entries.hasMoreElements();){
							JarEntry entry = entries.nextElement();
							if(entry.getName().startsWith("player/") && entry.getName().endsWith(".class")){
								String className = entry.getName().replace('/', '.').substring(0, entry.getName().length()-6);
								ai = loader.loadClass(className);
								if(testForPlayerInterface(ai)){
									availablePlayer.put(ai.getName().replace("player.", ""), ai);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	/**
	 * Initiates a game by adjusting all values and options. After getting all information from the window elements, it calls the startGame() method in the GameLogic.
	 */
	private void createGame() {
		if(gmlc.isInProgress()) {
    		gui.console.printWarning("A game is currently running. It has to be paused or stopped in order to create a new game.","Gamesettings");
    		return;
    	}
		gmlc = new GameLogic();
		gui.linkGameLogic(gmlc);
		gmlc.linkGUI(gui);
		gmlc.getPlayfield().setPlayfieldSound(gui.soundsettings);
		gui.playfieldpanel.gamelogic = gmlc;
    	gameName = gameNameField.getText();
    	if(gameName.equals("") && recordGameIsEnabled){
    	    gameNameField.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
    	    gui.console.printInfo("When game recording is selected, you have to enter a game name!", "Gamesettings");
    		return;
    	}
    	
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
		gui.setEnableDisplayEnabled(displayCheckBox.isEnabled());
		gui.setDisplayEnabled(displayCheckBox.isSelected());
		
		executor.execute(new Task() {
    	    public void compute()
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
    	    		if(recordGameIsEnabled) {
    	    			gmlc.setManager(new EvaluationManager((int)roundsSpinner.getValue(),gameName , red, white));
    	    		}
    	    		gmlc.startGame(gameName, red, white,(int)roundsSpinner.getValue(),slowness, displayCheckBox.isSelected(), useCurrentPf.isSelected());
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
						 InvocationTargetException | NoSuchMethodException | SecurityException e) {
					gui.console.printWarning("GameSettings", "failed to load the ai");
					e.printStackTrace();
				}
    	    }
	    });
    	setAlwaysOnTop(false);
    	setVisible(false);
	}
	/**
	 * Returns the first Player by creating a new instance of a class represented by a .class file. 
	 * <p>
	 * @return Player
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Player getPlayer1() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, NoSuchMethodException, SecurityException{
		if(currentSelectionPlayer1.equals("Player")) {
			return gui.playfieldpanel;
		}
		ai = availablePlayer.get(player1ComboBox.getSelectedItem().toString());
		gui.console.printInfo("Class" + ai.getName() + " was loaded successfully", "GameSettings");
		return (Player) ai.getConstructor(GameLogic.class, Console.class).newInstance(gui.getGameLogic(), gui.console);
	}
	/**
	 * Returns the second Player by creating a new instance of a class represented by a .class file. 
	 * <p>
	 * @return Player
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Player getPlayer2() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, NoSuchMethodException, SecurityException{
		if(currentSelectionPlayer2.equals("Player")) {
			return gui.playfieldpanel;
		}
		ai = availablePlayer.get(player2ComboBox.getSelectedItem().toString());
		gui.console.printInfo("Class" + ai.getName() + " was loaded successfully", "GameSettings");
		return (Player) ai.getConstructor(GameLogic.class, Console.class).newInstance(gui.getGameLogic(), gui.console);
	}
	/**
	 * Tests, if a class has implemented the Player interface.
	 * @param ai2 
	 * @return True, if the class implements this interface. False, when not.
	 */
	private boolean testForPlayerInterface(Class<?> ai2) {
		for(Class<?> c : ai2.getInterfaces()) {
			if(c.equals(Player.class)) {
				gui.console.printInfo("Interface player was found in " + ai2.getName(), "GameSettings");
				return true;
			}
		}
		return false;
	}
}

