package gui;

import javax.swing.JFrame;

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
	
	public GUI(GameLogic gamelogic){
		gmlc = gamelogic;
		gmlc.linkGUI(this);
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
	}

}
