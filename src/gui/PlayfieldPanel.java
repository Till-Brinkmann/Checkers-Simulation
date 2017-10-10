package gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import checkers.Figure;
import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Move.MoveDirection;
import checkers.Move.MoveType;
import checkers.Player;
import checkers.Playfield;

import generic.List;
/**
 *
 * @author Till
 *
 */
@SuppressWarnings("serial")
public class PlayfieldPanel extends JPanel implements PlayfieldDisplay, Player{

	private Playfield playfield;
	private JButton[][] buttons;
	GameLogic gamelogic;
	Console console;
	//for move-making
	private int[][] coords;
	boolean alreadyOneMove = false;
	
	//the PlayfieldPanel must support up to two player
	FigureColor figurecolor;
	boolean twoPlayerMode = false;
	List<Figure> jumpFigures;

	public PlayfieldPanel(GameLogic pGamelogic, Console pConsole){
		super();
		gamelogic = pGamelogic;
		playfield = gamelogic.getPlayfield();
		playfield.setPlayfieldDisplay(this);
		coords = new int[2][2];
		jumpFigures = new List<Figure>();
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
						case RED:
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
				if(jumpIsPossible()){
					//if a jump is possible figures that can 
					jumpFigures.toFirst();
					while(jumpFigures.hasAccess()){
						if(playfield.field[x][y] == jumpFigures.getContent()){
							selectFigure(jumpFigures.getContent().x, jumpFigures.getContent().y);
						}
					}
				}
				else {
					if(playfield.field[x][y].color == figurecolor){
						selectFigure(x,y);
					}
				}
			}
		}
		else{
			//you can not move to an occupied field
			if(!playfield.isOccupied(x, y)){
				if(coords[0][0] != x && coords[0][1] != y){
					Move m = Move.makeMove(coords);
					if(m.isInvalid()){
						//TODO cancel move
					}
					else {
						if(m.getMoveType() == MoveType.JUMP){
							//TODO look for possibilities to multijump
						}
						else{
							gamelogic.makeMove(m);
						}
					}
				}
				else {
					//unselect figure
					buttons[x][y].setBorder(null);
					alreadyOneMove = false;
				}
			}
		}
	}
	private void selectFigure(int x, int y){
		coords[0][0] = x;
		coords[0][1] = y;
		alreadyOneMove = true;
		buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
	}
	
	private boolean jumpIsPossible() {
		boolean canJump = false;
		jumpFigures = new List<Figure>();
		for(Figure f : playfield.getFiguresFor(figurecolor)){
			if(Move.getAllJumps(f, playfield).length > 0){
				canJump = true;
				jumpFigures.append(f);
			}
		}
		return canJump;
	}
	public void createMove(int x,int y){
		//Method for Multi jump
		if(gamelogic.testForMultiJump(x, y)){
			buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}
	}
	@Override
	public void prepare(FigureColor color) {
		if(figurecolor == null){
			figurecolor = color;
		}
		else {//two players are wanted
			figurecolor = FigureColor.RED; //red always starts
			twoPlayerMode = true;
		}
	}
	@Override
	public void requestMove() {
		if(twoPlayerMode){
			//toggle color
			figurecolor = figurecolor == FigureColor.RED ? FigureColor.WHITE : FigureColor.RED;
			//TODO enable only the own figures(for this figurecolor)
		} else {
			enableAllButtons(true);
		}
	}
	
	private void enableAllButtons(boolean enabled) {
		for(int x = 0; x < playfield.SIZE; x++){
			for(int y = 0; y < playfield.SIZE; y++){
				buttons[x][y].setEnabled(enabled);
			}
		}
	}
}
