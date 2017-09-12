package gui;

import javax.swing.JButton;
import checkers.Playfield;
/**
 * 
 * @author Till
 *
 */
public class PlayfieldPanel {
	
	private JButton[] buttons;
	
	public PlayfieldPanel(Playfield playfield){
		buttons = new JButton[playfield.SIZE*playfield.SIZE];
	}
	/**
	 * @return an array of all the buttons the field exists of
	 */
	public JButton[] getButtons(){
		return buttons;
	}
}

