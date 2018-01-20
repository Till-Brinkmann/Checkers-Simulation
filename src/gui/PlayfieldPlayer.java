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
public class PlayfieldPlayer implements PlayfieldDisplay, Player{

	public PlayfieldPanel playfieldpanel;
	
	public Playfield playfield;
	public GameLogic gamelogic;
	public Console console;
	public CommandListener drawDecision;
	
	public final ImageIcon kingIcon;
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
	public PlayfieldPlayer(GameLogic pGamelogic, Console pConsole){
		kingIcon = new ImageIcon("resources/Icons/dame.png");
		
		gamelogic = pGamelogic;
		console = pConsole;
		playfield = gamelogic.getPlayfield();
		playfield.setPlayfieldDisplay(this);
		
		//for move detection
		coords = new int[2][2];
		multiJumpOptions = new int[0][0];
		jumpFigures = new List<Figure>();
		//creates Panel
		playfieldpanel = new PlayfieldPanel(playfield.SIZE);
		//adds ActionListeners
		playfieldpanel.addOneActionListenerToAll(new ActionListener()
              {
              public void actionPerformed(ActionEvent event)
              {
              	String location = event.getActionCommand();
              	saveCoordinates(Character.getNumericValue(location.charAt(0)),Character.getNumericValue(location.charAt(1)));
              }
            });
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
					playfieldpanel.setButtonIcon(x, y, kingIcon);
					break;
				case NORMAL:
					playfieldpanel.setButtonIcon(x, y, null);
					break;
			}
			switch (playfield.field[x][y].getFigureColor()){
			case WHITE:
				playfieldpanel.setButtonColor(x, y, Color.white);
				return;
			case RED:
				playfieldpanel.setButtonColor(x, y, new Color(160,10,10));
				return;
		}
		}
		else {
			playfieldpanel.setButtonColor(x, y, Color.lightGray);
			playfieldpanel.setButtonIcon(x, y, null);
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
				playfieldpanel.buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				clickSituation = Click.ZERO;
				return;
			}
			//you can not move to an occupied field
			if(!playfield.isOccupied(x, y)){
				coords[1][0] = x;
				coords[1][1] = y;
				Move m = Move.createMoveFromCoords(coords);
				if(m.isInvalid() || !gamelogic.testMove(m) || (Move.jumpIsPossible(playfield.field[m.getX()][m.getY()].getFigureColor(), playfield) && m.getMoveType() == MoveType.STEP)){
					console.printWarning("Invalid move", "PlayfieldPlayer");
					playfieldpanel.buttons[coords[0][0]][coords[0][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
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
							playfieldpanel.buttons[targetX][targetY].setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
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
		playfieldpanel.buttons[coords[0][0]][coords[0][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		playfieldpanel.buttons[coords[1][0]][coords[1][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		for(int i = 0; i < multiJumpOptions.length; i++) {
			playfieldpanel.buttons[multiJumpOptions[i][0]][multiJumpOptions[i][1]].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		}
		clickSituation = Click.ZERO;
		playfieldpanel.enableAllButtons(false);
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
		playfieldpanel.buttons[x][y].setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
	}
	public void setPlayfieldPanel(PlayfieldPanel panel) {
		playfieldpanel = panel;
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
		playfieldpanel.enableAllButtons(true);
		for(int x = 0; x < playfield.SIZE; x++){
			for(int y = 0; y < playfield.SIZE; y++){
				if(playfield.isOccupied(x, y) && playfield.field[x][y].getFigureColor() != figurecolor)
					playfieldpanel.buttons[x][y].setEnabled(false);
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
