package gui;

import javax.swing.JFrame;

import checkers.GameLogic;

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
	
	public GUI(){
		gmlc = new GameLogic();
		gmlc.linkGUI(this);
		System.out.println("Init end");
	}
	public GUI(GameLogic gamelogic){
		gmlc = gamelogic;
		gmlc.linkGUI(this);
	}
	
	public static void main(String[] args) {
		new GUI();
	}

	public void linkGameLogic(GameLogic gamelogic) {
		gmlc = gamelogic;
	}

}
