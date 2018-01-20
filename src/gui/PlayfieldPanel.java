package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PlayfieldPanel extends JPanel{
	private final int SIZE;
	public JButton[][] buttons;

	
	public boolean reversed;
	public PlayfieldPanel(int size) {
		super();
		SIZE = size;
		setLayout(new GridLayout(SIZE,SIZE));
		setPreferredSize(new Dimension(700,700));
		createButtons();
		enableAllButtons(false);
	}

	private void createButtons() {
		buttons = new JButton[SIZE][SIZE];
        for (int y = SIZE - 1; y >= 0 ; y--) {
            for(int x = 0; x < SIZE; x++){
                buttons[x][y] = new JButton();
                buttons[x][y].setBackground(Color.lightGray);
                buttons[x][y].setVerticalTextPosition(SwingConstants.BOTTOM);
                buttons[x][y].setHorizontalTextPosition(SwingConstants.CENTER);
                buttons[x][y].setIconTextGap(0);
                buttons[x][y].setActionCommand(x +""+ y);
                buttons[x][y].setEnabled(true);
                buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY)); 
                add(buttons[x][y]);
                reversed = false;
            }
        }
	}
	public void turnPlayfield() {
		removeButtons();
		addButtons();
	}
	private void addButtons() {
		if(!reversed) {
	        for (int y = 0; y < SIZE ; y++) {
	            for(int x = SIZE-1; x >= 0; x--){
	        		add(buttons[x][y]);
	            }            
			}
	        reversed = true;
		}
		else {
	        for (int y = SIZE - 1; y >= 0 ; y--) {
	            for(int x = 0; x < SIZE; x++){
	            	add(buttons[x][y]);
	            }
	        }   
	        reversed = false;
		}
        validate();

	}
	private void removeButtons() {
        for (int y = SIZE - 1; y >= 0 ; y--) {
            for(int x = 0; x < SIZE; x++){
        		remove(buttons[x][y]);   
            }	            
        }
	}
	public JButton[][] getButtons(){
		return buttons;
	}
	/**
	 * Changes the color of distinct one button.
	 * <p>
	 * @param x       An integer variable which is representing a point on the vertical axis of the button array.
	 * @param y	 	  An integer variable which is representing a point on the horizontal axis of the button array.	
	 * @param color A Color object
	 */
	public void setButtonColor(int x, int y, Color color){
		buttons[x][y].setBackground(color);
	}
	/**
	 * Sets the icon of distinct button.
	 * <p>
	 * @param x       An integer variable which is representing a point on the vertical axis of the button array.
	 * @param y	 	  An integer variable which is representing a point on the horizontal axis of the button array.	
	 * @param icon An ImageIcon object.
	 */
	public void setButtonIcon(int x, int y, ImageIcon icon) {
		buttons[x][y].setIcon(icon);
	}
	/**
	 * Adds to every button specific coordinates, which describes the buttons location on the board.
	 * <p>
	 * @param selected True, if the coordinates should be added. False, if not.
	 */
	public void buttonNumeration(boolean selected) {
        for (int y = 0; y < SIZE; y++) {
            for(int x = 0; x < SIZE; x++){
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
	 * Sets the buttons color and icon as if the board is empty.
	 */
	public void clearField() {
		  for (int y = 0; y < SIZE; y++) {
	            for(int x = 0; x < SIZE; x++){
	    			setButtonColor(x, y, Color.lightGray);
	    			setButtonIcon(x, y, null);
	            }
	       }
	}
	public void enablePlayableButtons() {
		for(int x = 0; x < SIZE; x++){
			for(int y = 0; y < SIZE; y++){
				if((y+3)%2 != 0) {
					if((x + y + 6)%2 == 0) {
						enableButton(x,y,true);
					}
				}		
				else {
					if((x + y + 6)%2 == 0) {
						enableButton(x,y,true);
					}
				}
			}
		}
	}
	/**
	 * Change if the buttons should be clickable.
	 * <p>
	 * @param enabled True, if the buttons should be clickable. False, if not.
	 */
	public void enableAllButtons(boolean enabled) {
		for(int x = 0; x < SIZE; x++){
			for(int y = 0; y < SIZE; y++){
				enableButton(x,y,enabled);
			}
		}
	}
	public void enableButton(int x, int y, boolean enabled) {
		buttons[x][y].setEnabled(enabled);
	}
	public void addActionListener(int x, int y,ActionListener al) {
		buttons[x][y].addActionListener(al);
	}
	public void addOneActionListenerToAll(ActionListener al) {
        for (int y = 0; y < SIZE; y++) {
            for(int x = 0; x < SIZE; x++){
            	addActionListener(x,y,al);
            }
        }
	}	
}
