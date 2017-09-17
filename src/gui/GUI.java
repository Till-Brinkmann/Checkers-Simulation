package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import checkers.GameLogic;

@SuppressWarnings("serial")
public class GUI extends JFrame{

	private GameLogic gmlc;
	
	/*
	 * settings that are outsourced to different frames
	 * to improve codestructure/readability
	 */
	public ColorSettings colorsettings;
	public GameSettings gamesettings;
	public SoundSettings soundsettings;
	
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
		playfieldpanel = new PlayfieldPanel(gmlc.getPlayfield());
		console = new Console();		
		colorsettings = new ColorSettings(this,true);
		gamesettings = new GameSettings();
		soundsettings = new SoundSettings();		
	}
	private void createWindow(){	
		setResizable(true);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		dameWhite = new ImageIcon();
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
        JMenuItem loadgame = new JMenuItem("load game");
        JMenuItem savegame = new JMenuItem("save Game");
        game.add(newgame);
        game.add(loadgame);
        game.add(savegame);
        menubar.add(game);
        
        JMenu preferences = new JMenu("Preferences");
        JMenuItem color = new JMenuItem("Color");
        JMenuItem sound = new JMenuItem("Sound");
        JCheckBoxMenuItem showmoves = new JCheckBoxMenuItem("show moves");
        JCheckBoxMenuItem showfieldnumbers = new JCheckBoxMenuItem("show field numbers");
        preferences.add(color);
        preferences.add(sound);
        preferences.add(showmoves);
        preferences.add(showfieldnumbers);
        menubar.add(preferences);
        
        JMenu help = new JMenu("Help");
        JMenuItem aboutcs = new JMenuItem("About Checker Simulation");
        JMenuItem guide = new JMenuItem("Guide");
        help.add(guide);
        help.add(aboutcs);
        menubar.add(help);
        
        newgame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	gamesettings.enableVisibility();
            }
        });
        loadgame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {

            }
        });      
        savegame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {

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
            	soundsettings.enableVisibility();
            }
        });      
        showmoves.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {

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

            }
        });      
        aboutcs.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {

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
}
