package gui;

import javax.swing.JButton;
import checkers.Playfield;
/**
 * 
 * @author Till
 *
 */
public class PlayfieldPanel {
	
	private JButton[] buttons = new JButton[64];
	
	public PlayfieldPanel(Playfield playfield){
		
	}
	/**
	 * @return an array of all the buttons the field exists of
	 */
	public JButton[] getButtons(){
		return buttons;
	}
}

