package gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.*;

import checkers.Figure;
import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Move.MoveType;
import datastructs.List;
import checkers.Player;
import checkers.Playfield;
/**
 * The PlayfieldPanel consists of a field of buttons that can display the contents of a playfield.
 * It also implements the Player interface to allow the user to interact with the program as a player.
 */
public class PlayfieldPanel extends JPanel implements PlayfieldDisplay, Player{

	public final ImageIcon king;
	public Playfield playfield;
	private JButton[][] buttons;
	public GameLogic gamelogic;
	public Console console;
	public CommandListener drawDecision;
	
	//for move-making
	private int[][] coords;
	private int[][] multiJumpOptions;
	private enum Click{ZERO,FIRST,SECOND};
	private Click clickSituation = Click.ZERO;
	private FigureColor figurecolor;
	List<Figure> jumpFigures;
	private boolean wantsDraw = false;
	List<Move> list;
	
	//for playfield turning
	public FigureColor pfTurnDirection;

	/**
	 * Sets the needed references and initializes all necessary object. After that it calls the method createPlayfieldPanel().
	 * <p>
	 * @param pGamelogic
	 * @param pConsole
	 */
	public PlayfieldPanel(GameLogic pGamelogic, Console pConsole){
		super();
		king = new ImageIcon("resources/Icons/dame.png");
		gamelogic = pGamelogic;
		console = pConsole;
		playfield = gamelogic.getPlayfield();
		playfield.setPlayfieldDisplay(this);
		coords = new int[2][2];
		multiJumpOptions = new int[0][0];
		jumpFigures = new List<Figure>();
		buttons = new JButton[playfield.SIZE][playfield.SIZE];
		createPlayfieldPanel();
		enableAllButtons(false);
	}
	/**
	 * Sets window preferences, adds buttons to the button array and adds actionlisteners to every button.
	 */
	public void createPlayfieldPanel(){
		setLayout(new GridLayout(playfield.SIZE,playfield.SIZE));
		setPreferredSize(new Dimension(700,700));
        for (int y = playfield.SIZE - 1; y >= 0 ; y--) {
            for(int x = 0; x < playfield.SIZE; x++){
                buttons[x][y] = new JButton();
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
        pfTurnDirection = FigureColor.RED;
	}
	public void turnPlayfield() {		
		removeButtons();
		if(pfTurnDirection == FigureColor.RED) {
	        for (int y = 0; y < playfield.SIZE ; y++) {
	            for(int x = playfield.SIZE-1; x >= 0; x--){
	        		add(buttons[x][y]);
	            }            
			}
	        pfTurnDirection = FigureColor.WHITE;
		}
		else {
	        for (int y = playfield.SIZE - 1; y >= 0 ; y--) {
	            for(int x = 0; x < playfield.SIZE; x++){
	            	add(buttons[x][y]);
	            }
	        }
	        pfTurnDirection = FigureColor.RED;
		}
        validate();

	}
	public void removeButtons() {
        for (int y = playfield.SIZE - 1; y >= 0 ; y--) {
            for(int x = 0; x < playfield.SIZE; x++){
            	if(pfTurnDirection == FigureColor.RED) {
            		remove(buttons[x][y]);   
            	}
            }	            
        }
	}
	/**
	 * Adds to every button specific coordinates, which describes the buttons location on the board.
	 * <p>
	 * @param selected True, if the coordinates should be added. False, if not.
	 */
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
	/**
	 * Changes the color of distinct one button.
	 * <p>
	 * @param x       An integer variable which is representing a point on the vertical axis of the button array.
	 * @param y	 	  An integer variable which is representing a point on the horizontal axis of the button array.	
	 * @param color A Color object
	 */
	private void setButtonColor(int x, int y, Color color){
		buttons[x][y].setBackground(color);
	}
	/**
	 * Sets the icon of distinct button.
	 * <p>
	 * @param x       An integer variable which is representing a point on the vertical axis of the button array.
	 * @param y	 	  An integer variable which is representing a point on the horizontal axis of the button array.	
	 * @param icon An ImageIcon object.
	 */
	private void setButtonIcon(int x, int y, ImageIcon icon) {
		buttons[x][y].setIcon(icon);
	}

	@Override
	/**
	 * Updates every button.
	 */
	public void updateDisplay() {
		for(int y = 0; y < playfield.SIZE; y++){
			for(int x = 0; x < playfield.SIZE; x++){
					updateField(x, y);
			}
		}
	}
	@Override
	/**
	 * Compares a certain button to the exact same field on the board and sets it correct properties.
	 * <p>
	 * @param x       An integer variable which is representing a point on the vertical axis of the button array.
	 * @param y	 	  An integer variable which is representing a point on the horizontal axis of the button array.	
	 */
	public void updateField(int x, int y) {
		if(playfield.isOccupied(x, y)){
			switch (playfield.field[x][y].getFigureType()){
				case KING:
					setButtonIcon(x, y, king);
					break;
				case NORMAL:
					setButtonIcon(x, y, null);
					break;
			}
			switch (playfield.field[x][y].getFigureColor()){
			case WHITE:
				setButtonColor(x, y, Color.white);
				return;
			case RED:
				setButtonColor(x, y, new Color(160,10,10));
				return;
		}
		}
		else {
			setButtonColor(x, y, Color.lightGray);
			setButtonIcon(x, y, null);
		}
	}
	/**
	 * Sets the buttons color and icon as if the board is empty.
	 */
	public void clearField() {
		  for (int y = 0; y < playfield.SIZE; y++) {
	            for(int x = 0; x < playfield.SIZE; x++){
	    			setButtonColor(x, y, Color.lightGray);
	    			setButtonIcon(x, y, null);
	            }
	        }
	}
	/**
	 * This method executes different actions depending on the button that was clicked.
	 * <p>
	 * @param x       An integer variable which is representing a point on the vertical axis of the button array.
	 * @param y	 	  An integer variable which is representing a point on the horizontal axis of the button array.	
	 */
	private void saveCoordinates(int x, int y) {
		switch(clickSituation) {
		case ZERO:
			if(playfield.isOccupied(x, y)){
				if(Move.jumpIsPossible(playfield.field[x][y].getFigureColor(), playfield)){
					if(Move.getPossibleJumps(playfield.field[x][y], playfield).length != 0) {
						selectFigure(x, y);
					}
				}
				else {
						selectFigure(x,y);
				}
			}
			break;
		case FIRST:
			if(coords[0][0] == x && coords[0][1] == y){
				//unselect figure
				buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				clickSituation = Click.ZERO;
				return;
			}
			//you can not move to an occupied field
			if(!playfield.isOccupied(x, y)){
				coords[1][0] = x;
				coords[1][1] = y;
				Move m = Move.createMoveFromCoords(coords);
				if(m.isInvalid() || !gamelogic.testMove(m) || (Move.jumpIsPossible(playfield.field[m.getX()][m.getY()].getFigureColor(), playfield) && m.getMoveType() == MoveType.STEP)){
					console.printWarning("Invalid move", "PlayfieldPanel");
					buttons[coords[0][0]][coords[0][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
					clickSituation = Click.ZERO;
					return;
				}
				if(m.getMoveType() == MoveType.JUMP){
					//multiJumpTesting
					list = Move.getMultiJumps(coords[0][0],coords[0][1], gamelogic.getPlayfield());
					if(list.length != 0) {
						multiJumpOptions = new int[list.length][2];
						int counter = 0;
						for(list.toFirst();list.hasAccess();list.next()) {
							int targetX = list.get().getX();
							int targetY = list.get().getY();
							for(int i = 0; i < list.get().getSteps(); i++) {
								switch(list.get().getMoveDirection(i)) {
									case BL:
										targetX-=2;
										targetY-=2;
										break;
									case BR:
										targetX+=2;
										targetY-=2;
										break;
									case FL:
										targetX-=2;
										targetY+=2;
										break;
									case FR:
										targetX+=2;
										targetY+=2;
										break;
								}
							}
							multiJumpOptions[counter][0] = targetX;
							multiJumpOptions[counter++][1] = targetY;
							buttons[targetX][targetY].setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
						}
						clickSituation = Click.SECOND;
						return;
					}	
					
				}
				resetAndExecute(m);
			}
			break;
		case SECOND:
			list.toFirst();
			for(int i = 0; i < list.length;i++) {
				if(multiJumpOptions[i][0] == x && multiJumpOptions[i][1] == y) {
					resetAndExecute(list.get());
					return;
				}
				list.next();
			}
			break;
		}

	}
	/**
	 * After the buttons were clicked and a valid move could be created, all included variables are reseted and the move is passed to the Gamelogic.
	 * <p>
	 * @param m A Move object.
	 */
	private void resetAndExecute(Move m) {
		if(gamelogic.getTwoPlayerMode()){
			//toggle color
			figurecolor = (figurecolor == FigureColor.RED) ? FigureColor.WHITE : FigureColor.RED;
		}		
		buttons[coords[0][0]][coords[0][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		buttons[coords[1][0]][coords[1][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		for(int i = 0; i < multiJumpOptions.length; i++) {
			buttons[multiJumpOptions[i][0]][multiJumpOptions[i][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		}
		clickSituation = Click.ZERO;
		enableAllButtons(false);
		gamelogic.makeMove(m);
	}
	/**
	 * The spot that was clicked is saved and the buttons lineBorder thickness increases.
	 * <p>
	 * @param x       an integer variable which is representing a point on the vertical axis of the button array.
	 * @param y	 	  an integer variable which is representing a point on the horizontal axis of the button array.	
	 */
	private void selectFigure(int x, int y){
		coords[0][0] = x;
		coords[0][1] = y;
		clickSituation = Click.FIRST;
		buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
	}
	/**
	 * Change if the buttons should be clickable.
	 * <p>
	 * @param enabled True, if the buttons should be clickable. False, if not.
	 */
	public void enableAllButtons(boolean enabled) {
		for(int x = 0; x < playfield.SIZE; x++){
			for(int y = 0; y < playfield.SIZE; y++){
				buttons[x][y].setEnabled(enabled);
			}
		}
	}

	
	/**
	 * Sets the color that the playfieldpanel player has.
	 */
	@Override
	public void prepare(FigureColor color) {
		figurecolor = color;
	}
	
	/**
	 * Enables all buttons, so that a move can be made.
	 * This method is called by the Gamelogic.
	 */
	@Override
	public void requestMove() {
		//enable all buttons exept for enemy figures
		enableAllButtons(true);
		for(int x = 0; x < playfield.SIZE; x++){
			for(int y = 0; y < playfield.SIZE; y++){
				if(playfield.isOccupied(x, y) && playfield.field[x][y].getFigureColor() != figurecolor)
				buttons[x][y].setEnabled(false);
			}
		}
	}
	/**
	 * Returns the name when seen as a player.
	 */
	@Override
	public String getName(){
		return "Human player (PlayfieldPanel)";
	}
	@Override
	/**
	 * Creates a Field in a certain path with specific information after a game.
	 */
	public void saveInformation(String pathName) {
		File file = new File(pathName + "/Playfieldpanel(real Player) Information.txt");
		PrintWriter writer ;
		try {
			writer = new PrintWriter(file);
			writer.write("No information the playfieldpanel");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public boolean acceptDraw(){
		return wantsDraw;
	}
}
