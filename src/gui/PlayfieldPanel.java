package gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import checkers.Playfield;
/**
 * 
 * @author Till
 *
 */
public class PlayfieldPanel extends JPanel {
	private Playfield playfield;
	private JButton[][] buttons;
	
	public PlayfieldPanel(Playfield pPlayfield){
		super();
		playfield = pPlayfield;
		buttons = new JButton[playfield.SIZE][playfield.SIZE];
		createPlayfield();
	}
	public void createPlayfield(){		
		setLayout(new GridLayout(playfield.SIZE,playfield.SIZE));
		setPreferredSize(new Dimension(700,700));
        for (int y = 7; y >= 0; y--) {
            for(int x = 0; x < 8; x++){
                buttons[x][y] = new JButton ();
                buttons[x][y].setBackground(Color.lightGray);
                buttons[x][y].setVerticalTextPosition(SwingConstants.BOTTOM);
                buttons[x][y].setHorizontalTextPosition(SwingConstants.CENTER);
                buttons[x][y].setIconTextGap(0);
                add (buttons[x][y] );
            }
        }
        for (int y = 7; y >= 0; y--) {
            for(int x = 0; x < 8; x++){
            	buttons[x][y].addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        //Da mann wissen muss, welcher Button geklicht wurde, muss zuerst der Name des Buttons herausgefunden werden
                        //buttonNameToInt(event.getActionCommand());
                    }
                  });
            }
        }
	}
	public void buttonNumeration(boolean selected) {
        for (int y = 7; y >= 0; y--) {
            for(int x = 0; x < 8; x++){
            	if(selected){
            		buttons[x][y].setText( "(" + (x+1) + ")(" + (y+1) + ")" );
            	}
            	else{
            		buttons[x][y].setText("");
            	}
            }
        }
		
	}
	/**
	 * @return an array of all the buttons the field exists of
	 */	
	public JButton[][] getButtons(){
		return buttons;
	}
}

