package gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Move.MoveDirection;
import checkers.Playfield;
/**
 *
 * @author Till
 *
 */
@SuppressWarnings("serial")
public class PlayfieldPanel extends JPanel implements PlayfieldDisplay{

	private Playfield playfield;
	private JButton[][] buttons;
	public GUI gui;
	//for move-making
	int x1 = 0;
	int y1 = 0;
	int x2 = 0;
	int y2 = 0;
	boolean alreadyOneMove = false;

	public PlayfieldPanel(Playfield pPlayfield,GUI pGui){
		super();
		gui = pGui;
		playfield = pPlayfield;
		playfield.setPlayfieldDisplay(this);
		buttons = new JButton[playfield.SIZE][playfield.SIZE];
		createPlayfieldPanel();
	}
	public void createPlayfieldPanel(){
		setLayout(new GridLayout(playfield.SIZE,playfield.SIZE));
		setPreferredSize(new Dimension(700,700));
        for (int y = 0; y < playfield.SIZE; y++) {
            for(int x = 0; x < playfield.SIZE; x++){
                buttons[x][y] = new JButton ();
                buttons[x][y].setBackground(Color.lightGray);
                buttons[x][y].setVerticalTextPosition(SwingConstants.BOTTOM);
                buttons[x][y].setHorizontalTextPosition(SwingConstants.CENTER);
                buttons[x][y].setIconTextGap(0);
                buttons[x][y].setActionCommand(x +""+ y);
                buttons[x][y].setEnabled(true);
                buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                add (buttons[x][y] );
            }
        }
        for (int y = 0; y < playfield.SIZE; y++) {
            for(int x = 0; x < playfield.SIZE; x++){
            	buttons[x][y].addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                    	String location = event.getActionCommand();
                    	saveCoordinates(Character.getNumericValue(location.charAt(0)),Character.getNumericValue(location.charAt(1)));
                    }
                  });
            }
        }
	}
	public void buttonNumeration(boolean selected) {
        for (int y = 0; y < playfield.SIZE; y++) {
            for(int x = 0; x < playfield.SIZE; x++){
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

	public void setButtonColor(int x, int y, Color color){
		buttons[x][y].setBackground(color);
	}
	private void setButtonIcon(int x, int y, Icon icon) {
		buttons[x][y].setIcon(icon);
	}

	@Override
	public void updateDisplay() {
		for(int y = 0; y < playfield.SIZE; y++){
			for(int x = 0; x < playfield.SIZE; x++){
					updateField(x, y);
			}
		}
	}
	@Override
	public void updateField(int x, int y) {
		if(playfield.isOccupied(x, y)){
			switch (playfield.field[x][y].getFigureType()){
				case KING:// TODO add icon for king
					setButtonIcon(x, y, null);
				case NORMAL:
					setButtonIcon(x, y, null);
					switch (playfield.field[x][y].getFigureColor()){
						case WHITE:
							setButtonColor(x, y, Color.white);
							return;
						case GREEN:
						//TODO das richtige grün für die figuren finden
							setButtonColor(x, y, new Color(0,165,0));
							return;
					}
			}
		}
		else {
			setButtonColor(x, y, Color.lightGray);
			setButtonIcon(x, y, null);
		}
	}

	private void saveCoordinates(int x, int y) {

		if(!alreadyOneMove){
			if(playfield.isOccupied(x, y)){
				x1 = x;
				y1 = y;
				alreadyOneMove = true;
				buttons[x1][y1].setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
			}
		}
		else{
			alreadyOneMove = false;
			if(x1 != x || y1 !=y){
				buttons[x1][y1].setBorder(BorderFactory.createLineBorder(Color.GRAY));
				x2 = x;
				y2 = y;
				createMove();
			}//unselect figure
			else {
				buttons[x1][y1].setBorder(null);
			}
		}
	}
	public void createMove(){
		MoveDirection direction = MoveDirection.INVALID;
		int steps = y2-y1;
		int xShift = x2-x1;
		if(Math.abs(steps) == Math.abs(xShift)){
			if(steps < 0){
				if(xShift < 0){
					direction = MoveDirection.BL;
				}
				if(xShift > 0){
					direction = MoveDirection.BR;
				}
			}
			if(steps > 0){
				if(xShift < 0){
					direction = MoveDirection.FL;
				}
				if(xShift > 0){
					direction = MoveDirection.FR;
				}
			}
		}
		Move m = new Move(direction, steps, x1, y1);
		if(gui.getGameLogic().testMove(m)){
			playfield.executeMove(m);
		}
		else{
			//Fehlermeldung
		}

	}
	public void createMove(int x,int y){
		//Method for Multi jump
		if(gui.getGameLogic().testForMultiJump(x, y)){
			buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}
	}
}
