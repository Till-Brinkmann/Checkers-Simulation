package gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import checkers.GameLogic;
import checkers.GameLogic.Situations;
import network.NetworkManager;
import utilities.FileUtilities;

/**
 * Main class of the program and the main user interface.
 * @author Till
 *
 */
public class GUI extends JFrame{
	/**
	 * Reference to the current GameLogic.
	 */
	public GameLogic gmlc;
	//settings that are outsourced to different frames to improve codestructure/readability
	public Moves movesWindow;
	public PlayfieldEditor playfieldeditor;
	public NetworkManager networkmanager;
	public ColorSettings colorsettings;
	public GameSettings gamesettings;
	public SoundSettings soundsettings;
	public JFileChooser filechooser;
	public FileFilter filter;

	public Console console;
	public PlayfieldPlayer playfieldplayer;

	public ImageIcon kingIcon;

	public JMenuItem resume;
	public JMenuItem pause;
	public JMenuItem stop;
	
	JRadioButtonMenuItem slow;
	JRadioButtonMenuItem medium;
	JRadioButtonMenuItem fast;
	
	public JCheckBoxMenuItem displayEnabled;
	/**
	 * Describes how fast a computer player is allowed to move. 
	 */
	public enum AISpeed{SLOW, MEDIUM, FAST, NOTACTIVE}
	public AISpeed aiSpeed;
	public GUI(GameLogic gamelogic){
		super("Checker Simulation 2.0");

		gmlc = gamelogic;
		gmlc.linkGUI(this);
		initialize();
		createWindow();
		console.printInfo("The user interface was loaded successfully. Now it is ready to be explored. Have fun!","GUI");
		console.printInfo("All available commands can be found under /availableCommands", "GUI");
		
	}
	/**
	 * This constructor creates a new GameLogic automatically.
	 */
	public GUI(){
		this(new GameLogic());
	}
	/**
	 * Entry point of the program.
	 * <p>
	 * @param args Additional arguments.
	 */
	public static void main(String[] args) {
		new GUI();

	}
	/**
	 * Connects a GameLogic to the console
	 * <p>
	 * @param A Gamelogic object.
	 */
	public void linkGameLogic(GameLogic gamelogic) {
		gmlc = gamelogic;
	}
	/**
	 * Initializes all important objects.
	 */
	private void initialize(){	
		console = new Console();
		playfieldplayer = new PlayfieldPlayer(gmlc ,console);
		colorsettings = new ColorSettings(this, Color.BLACK, Color.LIGHT_GRAY);
		soundsettings = new SoundSettings(this);
		playfieldeditor = new PlayfieldEditor(console, this);
		movesWindow = new Moves();
		networkmanager = new NetworkManager(this,console);
		
		console.setNetworkManager(networkmanager);
	}
	/**
	 * This method sets the basic components of the window.
	 */
	private void createWindow(){
		setResizable(true);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		kingIcon = new ImageIcon("resources/Icons/dame.png");
		setIconImage(kingIcon.getImage());

		setJMenuBar(createMenuBar());
		add(playfieldplayer.playfieldpanel);
		add(console.panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	/**
	 * This method creates a JMenuBar object, adds all elements to it and returns it.
	 * <p>
	 * @return A JMenuBar object.
	 */
	private JMenuBar createMenuBar(){
        JMenuBar menubar = new JMenuBar();
        menubar.setBackground(Color.WHITE);
        menubar.add(createGameMenu());        
        menubar.add(createRunMenu());	
        menubar.add(createExtrasMenu());
        menubar.add(createPreferencesMenu());                                   
        menubar.add(createNetworkMenu());
        menubar.add(createHelpMenu());
        return menubar;
	}
	/**
	 * Creates a JMenu and adds multiple JMenuItems. To every JMenuItem a actionlisteners is added.
	 * <p>
	 * @return A JMenu object.
	 */
	public JMenu createGameMenu() {
        JMenu game = new JMenu("Game");
        JMenuItem newgame = new JMenuItem("New Run");
        newgame.setBackground(Color.WHITE);
        newgame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	gamesettings = new GameSettings(GUI.this);
            }
        });
        JMenuItem loadgame = new JMenuItem("Load Situation");
        loadgame.setBackground(Color.WHITE);
        loadgame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	filechooser = new JFileChooser();
            	filter = new FileNameExtensionFilter("pfs","txt");
            	filechooser.setDialogTitle("load playfield file");
            	int rueckgabeWert = filechooser.showOpenDialog(null);
            	filechooser.setCurrentDirectory(new File("resources/PlayfieldSaves"));
            	filechooser.addChoosableFileFilter(filter);
            	//File muss erst ausgewählt werden! Testfile:
            	if(rueckgabeWert == JFileChooser.APPROVE_OPTION){
            		File file = filechooser.getSelectedFile();
		        	try {
		        		gmlc.getPlayfield().setGameSituation(file);
		        		console.printInfo("Playfield was loaded successfully.");
					} catch (IOException e) {
						console.printWarning(file.getName() + " could not be loaded: " + e, "Load Playfield");
					
					}
            	}
        	}

        });
        JMenuItem savegame = new JMenuItem("Save Situation");
        savegame.setBackground(Color.WHITE);       
        savegame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	try {
					FileUtilities.saveGameSituation(gmlc.getPlayfield(), "resources/playfieldSaves", "" + System.currentTimeMillis());
					console.printInfo("Playfield saved.");
				} catch (IOException e) {
					console.printWarning("Playfield could not be saved");
				}
            }
        });
        JMenuItem openResources = new JMenuItem("Open Resources");
        openResources.setBackground(Color.WHITE);
        openResources.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	try {
					Desktop.getDesktop().open(new File("resources"));
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        });
        game.add(newgame);
        game.add(loadgame);
        game.add(savegame);
        game.add(openResources);
        return game;
	}
	/**
	 * Creates a JMenu and adds multiple JMenuItems. To every JMenuItem a actionlistener is added.
	 * <p>
	 * @return A JMenu object.
	 */
	public JMenu createExtrasMenu() {
		JMenu extras = new JMenu("Extras");
		extras.setBackground(Color.WHITE);
		JMenuItem pfEditor = new JMenuItem("Editor");
        pfEditor.setBackground(Color.WHITE);
        pfEditor.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	playfieldeditor.setVisible(true);
            }
        });
        JMenuItem color = new JMenuItem("Color");
        color.setBackground(Color.WHITE);
        color.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	colorsettings.setVisible(true);
            }
        });
        JMenuItem sound = new JMenuItem("Sound");
        sound.setBackground(Color.WHITE);
        sound.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	soundsettings.setVisible(true);
            }
        });
        JMenuItem showmoves = new JMenuItem("Moves");
        showmoves.setBackground(Color.WHITE);
        showmoves.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	movesWindow.setVisible(true);
            }
        });
        
        extras.add(pfEditor);
        extras.add(color);
        extras.add(sound);
        extras.add(showmoves);
        return extras;
	}
	/**
	 * Creates a JMenu and adds multiple JMenuItems. To every JMenuItem a actionlistener is added.
	 * <p>
	 * @return A JMenu object.
	 */
	public JMenu createPreferencesMenu() {
        JMenu preferences = new JMenu("Preferences");
        preferences.setBackground(Color.WHITE);

        JMenuItem turnPf = new JMenuItem("Flip playfield");
        turnPf.setBackground(Color.WHITE);
        turnPf.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	playfieldplayer.playfieldpanel.turnPlayfield();
            }
        });
        JCheckBoxMenuItem showfieldnumbers = new JCheckBoxMenuItem("show field numbers");
        showfieldnumbers.setBackground(Color.WHITE);   
        showfieldnumbers.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
        	playfieldplayer.playfieldpanel.buttonNumeration(showfieldnumbers.isSelected());
            }

        });        
        //has to be accessibled
        displayEnabled = new JCheckBoxMenuItem("display field");
        displayEnabled.setBackground(Color.WHITE);
        displayEnabled.setSelected(true);
        displayEnabled.setEnabled(false);
        displayEnabled.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent event)
	            {
	            	if(displayEnabled.isSelected()) {
	            		playfieldplayer.playfield.setPlayfieldDisplay(playfieldplayer);
	            		
	            		
	            	}else {
	            		playfieldplayer.playfield.setPlayfieldDisplay(null);
	            		playfieldplayer.playfieldpanel.clearField();
	            	}
	            }

	        });
	
        JMenu speed = new JMenu("AI Speed");
        speed.setBackground(Color.WHITE);
        slow = new JRadioButtonMenuItem("Slow",false);
        slow.setBackground(Color.WHITE);
        slow.setEnabled(false);
        slow.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	if(slow.isSelected()) {
            		slow.setSelected(true);
            		medium.setSelected(false);
            		medium.updateUI();
            		fast.setSelected(false);
            		fast.updateUI();
            		gmlc.setSlowness(2000);
            	}
            }
        });
        medium = new JRadioButtonMenuItem("Medium",false);
		medium.setBackground(Color.WHITE);
		medium.setEnabled(false);
		medium.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent event)
	            {
	            	if(medium.isSelected()) {
	            		slow.setSelected(false);
	            		slow.updateUI();
	            		medium.setSelected(true);
	            		fast.setSelected(false);
	            		gmlc.setSlowness(1000);
	            	}
	            }
	        });
		fast = new JRadioButtonMenuItem("Fast",false);
		fast.setBackground(Color.WHITE);
		fast.setEnabled(false);
        fast.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	if(fast.isSelected()) {
            		slow.setSelected(false);
            		slow.updateUI();
            		medium.setSelected(false);
            		medium.updateUI();
            		fast.setSelected(true);
            		gmlc.setSlowness(0);
            	}
            }
        });
		speed.add(slow);
		speed.add(medium);
		speed.add(fast);
				   
		preferences.add(turnPf);
        preferences.add(speed);
        preferences.add(showfieldnumbers);
        preferences.add(displayEnabled);
        return preferences;
	}
	/**
	 * Creates a JMenu and adds multiple JMenuItems. To every JMenuItem a actionlisteners is added.
	 * <p>
	 * @return A JMenu object.
	 */
	public JMenu createRunMenu() {
		JMenu run = new JMenu("Run");
        resume = new JMenuItem("Resume");
        resume.setBackground(Color.WHITE);
        resume.setEnabled(false);
        resume.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	resume.setEnabled(false);
            	pause.setEnabled(true);
            	gmlc.setPause(false);
            	playfieldplayer.playfieldpanel.enableAllButtons(true);
            }
        });
        pause = new JMenuItem("Pause");
        pause.setBackground(Color.WHITE);
        pause.setEnabled(false);
        pause.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {          	
            	pause.setEnabled(false);
            	resume.setEnabled(true);
            	gmlc.setPause(true);
            	playfieldplayer.playfieldpanel.enableAllButtons(false);
            }
        });
        stop = new JMenuItem("Stop");
        stop.setBackground(Color.WHITE);
        stop.setEnabled(false);
        stop.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	stop.setEnabled(false);
            	gmlc.setPause(true);
            	gmlc.finishGame(Situations.STOP,false);
            	playfieldplayer.playfieldpanel.enableAllButtons(false);
            }
        });
        run.add(resume);
        run.add(pause);
        run.add(stop);
        return run;
	}
	/**
	 * Creates a JMenu and adds multiple JMenuItems. To every JMenuItem a actionlisteners is added.
	 * <p>
	 * @return A JMenu object.
	 */
	public JMenu createHelpMenu() {
        JMenu help = new JMenu("Help");
        help.setBackground(Color.WHITE);
        JMenuItem guide = new JMenuItem("Guide");
        guide.setBackground(Color.WHITE);
        guide.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	try {
					Desktop.getDesktop().browse(new URI("https://github.com/Gametypi/Checkers-Simulation"));
				} catch (IOException | URISyntaxException e) {
					console.printError("URL to our Github page was not found", "GUI");
					e.printStackTrace();
				}
            }
        });
        JMenuItem rules = new JMenuItem("Rules");
        rules.setBackground(Color.WHITE);
        rules.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                try {
                    Desktop.getDesktop().open(new File("resources/rules_of_checkers_english.pdf"));
                } catch (IOException e) {
                	console.printWarning("gui", "could not find or open the pdf file");
                    e.printStackTrace();
                }
            }
        });
        help.add(guide);
        help.add(rules);
        return help;
	}
	/**
	 * Creates a JMenu and adds multiple JMenuItems. To every JMenuItem a actionlisteners is added.
	 * <p>
	 * @return A JMenu object.
	 */
	public JMenu createNetworkMenu() {
		JMenu network = new JMenu("Network");
        network.setBackground(Color.WHITE);
        JMenuItem changeUserName = new JMenuItem("Change username"); 
        changeUserName.setBackground(Color.WHITE);
        changeUserName.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                networkmanager.changeUsername();
            }
        });
        JMenuItem createServer = new JMenuItem("Create Server");
        createServer.setBackground(Color.WHITE);
        createServer.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	networkmanager.createServer(6000);
            }
        });
        JMenuItem connectToServer = new JMenuItem("Connect to a server");
        connectToServer.setBackground(Color.WHITE);
        connectToServer.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	networkmanager.createClient("localhost", 6666);

            }
        });
        JMenuItem closeConnection = new JMenuItem("Close connection");
        closeConnection.setBackground(Color.WHITE);
        closeConnection.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	networkmanager.closeConnection();


            }
        });
        JMenuItem sendGameRequest = new JMenuItem("Send a game request");
        sendGameRequest.setBackground(Color.WHITE);
        sendGameRequest.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	networkmanager.sendGameRequest();
            }
        });
        network.add(changeUserName);
        network.add(createServer);
        network.add(connectToServer);
        network.add(closeConnection);
        network.add(sendGameRequest);
        return network;
	}
	/**
	 * Updates the color of the console and playfieldpanel.
	 * <p>
	 * @param color A Color object.
	 */
	public void updateBackground( Color color){
		setBackground(color);
		console.updateBackground(color);
		playfieldplayer.playfieldpanel.setBackground(color);
	}
	/**
	 * Updates the color of the text in the console.
	 * <p>
	 * @param color A Color object.
	 */
	public void updateForeground( Color color){
		setForeground(color);
		console.updateForeground(color);
		playfieldplayer.playfieldpanel.setForeground(color);
	}
	public GameLogic getGameLogic() {
		return gmlc;
	}
	public void setAISpeed(AISpeed speed) {
		aiSpeed = speed;
		switch(aiSpeed) {
		case FAST:
			fast.setSelected(true);
			break;
		case MEDIUM:
			medium.setSelected(true);
			break;
		case NOTACTIVE:
			fast.setSelected(false);
			medium.setSelected(false);
			slow.setSelected(false);
			fast.setEnabled(false);
			medium.setEnabled(false);
			slow.setEnabled(false);
			return;
		case SLOW:
			slow.setSelected(true);
			break;
		}
		slow.setEnabled(true);
		medium.setEnabled(true);
		fast.setEnabled(true);
	}
	public void setEnableResume(boolean a) {
		resume.setEnabled(a);
	}
	public void setEnablePause(boolean a) {
		pause.setEnabled(a);
	}
	public void setEnableStop(boolean a) {
		stop.setEnabled(a);
	}
	public void setDisplayEnabled(boolean enabled) {
		displayEnabled.setSelected(enabled);
		if(!enabled) {
			playfieldplayer.playfieldpanel.clearField();
		}
	}
	public void setEnableDisplayEnabled(boolean b) {
		displayEnabled.setEnabled(b);
	}
	
}
