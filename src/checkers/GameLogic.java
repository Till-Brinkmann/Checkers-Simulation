package checkers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Player;
import gui.GUI;

/**
 * provides methods for game logic
 * @author Till
 *
 */

public class GameLogic {
	public enum Situations{WHITEWIN, REDWIN, DRAW,NOTHING};
	/**
	 * the default playfield to use
	 */
	private Playfield field;
	private boolean recordGameIsEnabled;
	private String gameName;
	private int turnCount = 0;
	private Player playerWhite;
	private Player playerRed;
	private boolean redFailedOnce = false;
	private boolean whiteFailedOnce = false;

	private boolean twoPlayerMode = false;
	private FigureColor inTurn;
	private GUI gui;
	public GameLogic(){
		this(new Playfield());
	}
	/**
	 *
	 * @param playfield default playfield
	 */
	public GameLogic(Playfield playfield) {
		field = playfield;
	}
	//---methods for game process---
	public void startGame( boolean pRecordGameIsEnabled, String pGameName, Player pPlayer1, Player pPlayer2) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		//if both player are one object one Player controls both white and red
		twoPlayerMode = pPlayer1 == pPlayer2;
		//choose random beginner
		if(Math.random() < 0.5){
			playerWhite = pPlayer1;
			playerRed = pPlayer2;
		}
		else {
			playerWhite = pPlayer2;
			playerRed = pPlayer1;
		}
		recordGameIsEnabled = pRecordGameIsEnabled;
		gameName = pGameName;
		turnCount = 0;
		//reset variables
		redFailedOnce = false;
		whiteFailedOnce = false;

		try {
			field.createStartPosition();
		} catch (IOException e) {
			gui.console.printWarning(
					"Could not load startposition. Please check if your playfieldsaves are at the right position",
					"Gamelogic:startGame");
			return;
		}
		
		playerRed.prepare(FigureColor.RED);
		//prepare only needs to be called once for Red then
		if(!twoPlayerMode){
			playerWhite.prepare(FigureColor.WHITE);
		}
		//red always starts
		inTurn = FigureColor.RED;
		playerRed.requestMove();
	}
	public void makeMove(Move m){
		if(!(field.field[m.getX()][m.getY()].color == inTurn) || !testMove(m)){
			gui.console.printWarning("Invalid move!", "Gamelogic");
			if(inTurn == FigureColor.RED){
				if(redFailedOnce){
					finishGameTest(Situations.WHITEWIN);
				}
				else {
					redFailedOnce = true;
				}
			}
			else {
				if(whiteFailedOnce){
					finishGameTest(Situations.REDWIN);

				}
				else {
					whiteFailedOnce = true;
				}
			}
		}
		else {
			//move is valid
			field.executeMove(m);
			//automatic figureToKing check
			testFigureToKing();
			//test if game is Finished
			finishGameTest(testFinished());
			//for game recording
			if(recordGameIsEnabled) {
				infosGameRecording();
			}
			//changing turn
			turnCount++;
			inTurn = (inTurn == FigureColor.RED) ? FigureColor.WHITE : FigureColor.RED;
			switch(inTurn){
			case RED:
				playerRed.requestMove();
				break;
			case WHITE:
				playerWhite.requestMove();
				break;
			}
		}
	}
	public void requestDraw(){
		if(playerRed.acceptDraw() && playerWhite.acceptDraw()){
			finishGameTest(Situations.DRAW);
		}
	}
	//---
	private Situations testFinished(){
		if(field.getFigureQuantity(FigureColor.WHITE) == 0){
			return Situations.REDWIN;
		}
		if(field.getFigureQuantity(FigureColor.RED) == 0){
			return Situations.WHITEWIN;
		}
		//there are no moves and jumps left
		if(field.getPossibleMoves(FigureColor.RED).length == 0) {
			
			for( Figure f : field.getFiguresFor(FigureColor.RED)){
				if(Move.getPossibleJumps(f, field).length == 0) {
					return Situations.WHITEWIN;
				}
			}
		}	
		if(field.getPossibleMoves(FigureColor.WHITE).length == 0) {
			for( Figure f : field.getFiguresFor(FigureColor.WHITE)){
				if(Move.getPossibleJumps(f, field).length == 0) {
					return Situations.REDWIN;
				}
			}
		}		
			
		
		//test for draw Situation
		if(field.getMovesWithoutJumps() == 15) {
			if(playerRed.acceptDraw() && playerWhite.acceptDraw() ){
				return Situations.DRAW;
			}
		}
		
		
		if(field.getMovesWithoutJumps() == 20) {
			return Situations.DRAW;
		}
		return Situations.NOTHING;
	}
	private void finishGameTest(Situations end) {
		switch(end) {
		case DRAW:
			gui.console.printInfo("GameLogic", "");
			break;

		case REDWIN:
			gui.console.printInfo("GameLogic", "");
			break;
		case WHITEWIN:
			gui.console.printInfo("GameLogic", "");
			break;

		case NOTHING:
			return;
		}
		//TODO reset playfield and everything else
	}
	public boolean getTwoPlayerMode(){
		return twoPlayerMode;
	}
	private void infosGameRecording() {
		try {
			field.saveGameSituation(gameName, inTurn, turnCount, playerRed.getName(), playerWhite.getName());
		} catch (IOException e) {
			gui.console.printWarning("playfield could not be saved. IOException: " + e);
			e.printStackTrace();
		}
	}
	private void testFigureToKing(){
		//TODO funktioniert nicht (vllt. Farben andersrum)
		int y1 = 0;
		int y2 = 7;
		for(int x = 0; x < field.getSize();x++) {
			if(field.isOccupied(x, y1)) {
				if(field.colorOf(x, y1) == FigureColor.RED && field.getType(x, y1) == FigureType.NORMAL) {
					field.changeFigureToKing(x, y1);
				}
			}
			if(field.isOccupied(x, y2)) {
				if(field.colorOf(x, y2) == FigureColor.WHITE && field.getType(x, y2) == FigureType.NORMAL) {
					field.changeFigureToKing(x, y2);
				}
			}
		}
	}
	/**
	 * tests if the given move is possible on the given playfield
	 * @param move
	 * @param field
	 * @return false if move is not possible, true if it is
	 */
	public static boolean testMove(Move m, Playfield f){
		int x = m.getX();
		int y = m.getY();
		if(!f.isOccupied(x, y)){
			return false;
		}
		FigureColor color = f.colorOf(x, y);
		FigureType type = f.getType(x, y);
		switch(m.getMoveType()){
		case INVALID:
			return false;
		case STEP:
			if(f.getType(x,y) == FigureType.NORMAL){
				switch (color) {
				case RED:
					switch(m.getMoveDirection()){
					case BL:
					case BR:
						//normal red figures can not go backwards
						return false;
					case FL:
						//the field is not outside the playfield and is not occupied
						//so the step is possible otherwise not
						return (x - 1 >= 0 && y + 1 < f.SIZE && !f.isOccupied(x-1, y+1));
					case FR:
						return (x + 1 < f.SIZE && y + 1 < f.SIZE && !f.isOccupied(x+1, y+1));
					}
				case WHITE:
					switch(m.getMoveDirection()){
					case BL:
						return (x - 1 >= 0 && y - 1 >= 0 && !f.isOccupied(x-1, y-1));
					case BR:
						return (x + 1 < f.SIZE && y - 1 >= 0 && !f.isOccupied(x+1, y-1));
					case FL:
					case FR:
						//normal white figures can not go forwards
						return false;
					}
				}
			}
			else {//FT = King
				switch(m.getMoveDirection()){
				case BL:
					return (x - 1 >= 0 && y - 1 >= 0 && !f.isOccupied(x-1, y-1));
				case BR:
					return (x + 1 < f.SIZE && y - 1 >= 0 && !f.isOccupied(x+1, y-1));
				case FL:
					return (x - 1 >= 0 && y + 1 < f.SIZE && !f.isOccupied(x-1, y+1));
				case FR:
					return (x + 1 < f.SIZE && y + 1 < f.SIZE && !f.isOccupied(x+1, y+1));
				}
			}
		case JUMP:
		case MULTIJUMP:
			for(int i = 0; i < m.getSteps(); i++){
				switch(m.getMoveDirection(i)){
				case BL:
					if(type == FigureType.NORMAL && color == FigureColor.RED){
						return false;//normal red figures can not go backwards
					}
					if (!(x - 2 >= 0 && y - 2 >= 0 &&//two fields space,
						f.isOccupied(x-1, y-1) &&//a figure on the next field...
					   	f.colorOf(x-1, y-1) != color &&//... that is of a different color,
					   	!f.isOccupied(x-2, y-2))//no figure on the field to land on
					){
						return false;
					}
					x -= 2;
					y -= 2;
					break;
				case BR:
					if(type == FigureType.NORMAL && color == FigureColor.RED){
						return false;
					}
					if (!(x + 2 < f.SIZE && y - 2 >= 0 &&
						f.isOccupied(x+1, y-1) &&
						f.colorOf(x+1, y-1) != color &&
						!f.isOccupied(x+2, y-2))
					){
						return false;
					}
					x += 2;
					y -= 2;
					break;
				case FL:
					if(type == FigureType.NORMAL && color == FigureColor.WHITE){
						return false;//normal white figures can not go forwards
					}
					if (!(x - 2 >= 0 && y + 2 < f.SIZE &&
						f.isOccupied(x-1, y+1) &&
						f.colorOf(x-1, y+1) != color &&
						!f.isOccupied(x-2, y+2))
					){
						return false;
					}
					x -= 2;
					y += 2;
					break;
				case FR:
					if(type == FigureType.NORMAL && color == FigureColor.WHITE){
						return false;
					}
					if (!(x + 2 < f.SIZE && y + 2 < f.SIZE &&
						f.isOccupied(x+1, y+1) &&
						f.colorOf(x+1, y+1) != color &&
						!f.isOccupied(x+2, y+2))
					){
						return false;
					}
					x += 2;
					y += 2;
					break;
				}
			}
			break;
		}
		return true;
	}
	public boolean testMove(Move move){
		return testMove(move, field);
	}
	public static boolean testForMultiJump(int x, int y, Playfield f){
		return false;
	}
	public boolean testForMultiJump(int x, int y){
		return testForMultiJump(x,y, field);
	}
	public Playfield getPlayfield(){
		return field;
	}
	//TODO das muss eigentlich nicht sein
	// public void setPlayfield(Playfield f){
	// 	field = f;
	// }
	public void linkGUI(GUI gui) {
		this.gui = gui;
	}
}
