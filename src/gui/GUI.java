package gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import checkers.GameLogic;
@SuppressWarnings("serial")
public class GUI extends JFrame{

	private GameLogic gmlc;
	/*
	 * settings that are outsourced to different frames
	 * to improve codestructure/readability
	 */
	public ShowMoves showMovesWindow;
	public Guide guideWindow;
	public AboutCS aboutcsWindow;
	public ColorSettings colorsettings;
	public GameSettings gamesettings;
	public SoundSettings soundsettings;
	public JFileChooser filechooser;
	public FileFilter filter;

	public Console console;
	public PlayfieldPanel playfieldpanel;

	public ImageIcon dameWhite;


	public GUI(GameLogic gamelogic){
		super("Checker Simulation");

		gmlc = gamelogic;
		gmlc.linkGUI(this);
		initialize();
		createWindow();

	}
	public GUI(){
		this(new GameLogic());

	}

	public static void main(String[] args) {
		new GUI();

	}

	public void linkGameLogic(GameLogic gamelogic) {
		gmlc = gamelogic;
	}
	private void initialize(){
		console = new Console();
		playfieldpanel = new PlayfieldPanel(gmlc ,console);
		colorsettings = new ColorSettings(this,true);
		soundsettings = new SoundSettings(this);
		aboutcsWindow = new AboutCS(); 
		guideWindow = new Guide();
		showMovesWindow = new ShowMoves();
	}
	private void createWindow(){
		setResizable(true);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		dameWhite = new ImageIcon("resources/Icons/dame.png");
		setIconImage(dameWhite.getImage());

		setJMenuBar(createMenuBar());
		add(playfieldpanel);
		add(console);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	private JMenuBar createMenuBar(){
        JMenuBar menubar = new JMenuBar();
        menubar.setBackground(Color.WHITE);
        JMenu game = new JMenu("Game");
        JMenuItem newgame = new JMenuItem("new game");
        newgame.setBackground(Color.WHITE);
        JMenuItem loadgame = new JMenuItem("load game");
        loadgame.setBackground(Color.WHITE);
        JMenuItem savegame = new JMenuItem("save Game");
        savegame.setBackground(Color.WHITE);;
        game.add(newgame);
        game.add(loadgame);
        game.add(savegame);
        menubar.add(game);

        JMenu preferences = new JMenu("Preferences");
        JMenuItem color = new JMenuItem("Color");
        color.setBackground(Color.WHITE);
        JMenuItem sound = new JMenuItem("Sound");
        sound.setBackground(Color.WHITE);
        JCheckBoxMenuItem showmoves = new JCheckBoxMenuItem("show moves");
        showmoves.setBackground(Color.WHITE);
        JCheckBoxMenuItem showfieldnumbers = new JCheckBoxMenuItem("show field numbers");
        showfieldnumbers.setBackground(Color.WHITE);
        preferences.add(color);
        preferences.add(sound);
        preferences.add(showmoves);
        preferences.add(showfieldnumbers);
        menubar.add(preferences);

        JMenu help = new JMenu("Help");
        help.setBackground(Color.WHITE);
        JMenuItem aboutcs = new JMenuItem("About Checker Simulation");
        aboutcs.setBackground(Color.WHITE);
        JMenuItem guide = new JMenuItem("Guide");
        guide.setBackground(Color.WHITE);
        JMenuItem rules = new JMenuItem("Rules");
        rules.setBackground(Color.WHITE);
        help.add(guide);
        help.add(aboutcs);
        help.add(rules);
        menubar.add(help);

        newgame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	gamesettings = new GameSettings(GUI.this);
            }
        });
        loadgame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	filechooser = new JFileChooser();
            	filter = new FileNameExtensionFilter("pfs","txt");
            	filechooser.setDialogTitle("load playfield file");
            	int rueckgabeWert = filechooser.showOpenDialog(null);
            	filechooser.addChoosableFileFilter(filter);
            	//File muss erst ausgewählt werden! Testfile:
            	if(rueckgabeWert == JFileChooser.APPROVE_OPTION){
            		File file = filechooser.getSelectedFile();
		        	try {
					    gmlc.getPlayfield().loadGameSituation(file);
						console.printInfo("Playfield " + file.getName() + " loaded!");
					} catch (IOException e) {
						console.printWarning(file.getName() + " could not be loaded: " + e, "Load Playfield");

					}
            	}
//            	if(file.getName().substring(file.getName().length()-4, file.getName().length()) != ".pfs"){
//            		console.printInfo("File ending not .pfs!");
//            	}
//            	else{

            	}

        });
        savegame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	try {
					gmlc.getPlayfield().saveGameSituation();
					console.printInfo("Playfield saved!");
				} catch (IOException e) {
					console.printWarning("Playfield saving error: "+ e);
				}
            }
        });
        color.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	colorsettings.setVisible(true);
            }
        });
        sound.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	soundsettings.setVisible(true);
            }
        });
        showmoves.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e) {
                if(showmoves.isSelected()) {
                	showMovesWindow.setVisible(true);
                }
                else {
                	showMovesWindow.setVisible(false);
                }
              }
        });
        showfieldnumbers.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
        	playfieldpanel.buttonNumeration(showfieldnumbers.isSelected());
            }

        });
        guide.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	guideWindow.setVisible(true);
            }
        });
        aboutcs.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	soundsettings.selectSound("moveSound");
            	aboutcsWindow.setVisible(true);
            }
        });
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
        return menubar;
	}

	public void updateBackground( Color color){
		setBackground(color);
		console.updateBackground(color);
		playfieldpanel.setBackground(color);
	}

	public void updateForeground( Color color){
		setForeground(color);
		console.updateForeground(color);
		playfieldpanel.setForeground(color);
	}
	public GameLogic getGameLogic() {
		return gmlc;
	}
}
