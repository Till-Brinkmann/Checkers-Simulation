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
	private void createWindow(){	
		playfieldpanel = new PlayfieldPanel(gmlc.getPlayfield());
		console = new Console();
		
		colorsettings = new ColorSettings();
		gamesettings = new GameSettings();
		soundsettings = new SoundSettings();
		
		setResizable(true);
		setLayout(new GridLayout(1,2));
		dameWhite = new ImageIcon();
		setIconImage(dameWhite .getImage());
		
		
		setJMenuBar(createMenuBar());
		add(playfieldpanel);
		add(console);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);

		
	}
	private JMenuBar createMenuBar(){
        JMenuBar menubar = new JMenuBar();
        JMenu game = new JMenu("Game");
        JMenuItem newgame = new JMenuItem("new game");
        JMenuItem loadgame = new JMenuItem("load game");
        JMenuItem savegame = new JMenuItem("save Game");
        game.add(newgame);
        game.add(loadgame);
        game.add(savegame);
        menubar.add(game);
        
        JMenu preferences = new JMenu("Settings");
        JMenuItem color = new JMenuItem("Color");
        JMenuItem sound = new JMenuItem("Sound");
        JCheckBoxMenuItem showmoves = new JCheckBoxMenuItem("Show Moves");
        preferences.add(color);
        preferences.add(sound);
        preferences.add(showmoves);
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

            }
        });       
        sound.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {

            }
        });      
        showmoves.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {

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
}
