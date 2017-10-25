package checkers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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

	String namePlayerWhite;
	String namePlayerRed;
	
	private boolean twoPlayerMode = false;
	private FigureColor inTurn;
	private GUI gui;
	
	private int currentRound = 0;
	private int rounds;
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
	public void startGame( boolean pRecordGameIsEnabled, String pGameName, Player pPlayer1, Player pPlayer2, int pRounds){
		rounds = pRounds;
		//if both player are one object one Player controls both white and red
		twoPlayerMode = pPlayer1 == pPlayer2;
		String namePlayer1 = "Human Player1";
		String namePlayer2 = "Human Player2";
		if(pPlayer1 != null) {
			namePlayer1 = pPlayer1.getName();
		}
		if(pPlayer2 != null) {
			namePlayer2 = pPlayer2.getName();
			
		}
		
		//choose random beginner
		if(Math.random() < 0.5){
			playerWhite = pPlayer1;
			gui.console.printInfo("gmlc","The White pieces have been assigned to " + namePlayer1 + "");
			namePlayerWhite = namePlayer1;
			playerRed = pPlayer2;
			gui.console.printInfo("gmlc","The red pieces have been assigned to " + namePlayer2 + "");
			namePlayerRed = namePlayer2;
		}
		else {
			playerWhite = pPlayer2;
			gui.console.printInfo("gmlc","The White pieces have been assigned to " + namePlayer2 + "");
			namePlayerWhite = namePlayer2;
			playerRed = pPlayer1;
			gui.console.printInfo("gmlc","The red pieces have been assigned to " + namePlayer1 + "");
			namePlayerRed = namePlayer1;
		}
		recordGameIsEnabled = pRecordGameIsEnabled;
		gameName = pGameName;
		turnCount = 0;
		//reset variables
		redFailedOnce = false;
		whiteFailedOnce = false;

		//testet ob empty weil ja schon ein psf geloaded werden konnte
		if(field.isEmpty()) {
			try {			
				field.createStartPosition();
			} catch (IOException e) {
				gui.console.printWarning(
						"Could not load startposition. Please check if your playfieldsaves are at the right position",
						"Gamelogic:startGame");
				return;
			}
		}
		playerRed.prepare(FigureColor.RED);
		//prepare only needs to be called once for Red then
		if(!twoPlayerMode){
			playerWhite.prepare(FigureColor.WHITE);
		}
		//red always starts
		gui.console.printInfo("gmlc", "Therefore " + namePlayerRed + "starts first");
		gui.console.printInfo("gmlc","Playing "+ (rounds-currentRound) + " more Rounds before reset");
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
		else {//move is valid
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
		if(Move.getPossibleMoves(FigureColor.RED, field).length == 0) {
			return Situations.WHITEWIN;
		}
		if(Move.getPossibleMoves(FigureColor.WHITE, field).length == 0) {
			return Situations.REDWIN;
		}
		
		//test for draw Situation
		if(field.getMovesWithoutJumps() == 15) {
			if(playerRed.acceptDraw() && playerWhite.acceptDraw() ){
				return Situations.DRAW;
			}
		}
		
		
		if(field.getMovesWithoutJumps() == 30) {
			return Situations.DRAW;
		}
		return Situations.NOTHING;
	}
	private void finishGameTest(Situations end) {
		switch(end) {
		case DRAW:
			gui.console.printInfo("GameLogic", "Game is finished!");
			gui.console.printInfo("GameLogic", "Result: Draw!");
			break;

		case REDWIN:
			gui.console.printInfo("GameLogic", "Game is finished!");
			gui.console.printInfo("GameLogic", "Result: Red won the game!");
			break;
		case WHITEWIN:
			gui.console.printInfo("GameLogic", "Game is finished!");
			gui.console.printInfo("GameLogic", "Result: White won the game!");
			break;

		case NOTHING:
			return;
		}
		try {
			field.createStartPosition();
		} catch (IOException e1) {
			gui.console.printWarning("gmlc","failed to load the pfs file startPositionForSize8");
			e1.printStackTrace();
		}
		currentRound++;
		if(currentRound == rounds) {
			//TODO "hard" reset 
			// TODO maybe statistic for ki playing against each other and creating a file with all information
		}
		else {
			startGame(recordGameIsEnabled, gameName, playerRed, playerWhite, rounds);
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
		int y1 = 0;
		int y2 = 7;
		for(int x = 0; x < field.getSize();x++) {
			if(field.isOccupied(x, y1)) {
				if(field.colorOf(x, y1) == FigureColor.WHITE && field.getType(x, y1) == FigureType.NORMAL) {
					field.changeFigureToKing(x, y1);
				}
			}
			if(field.isOccupied(x, y2)) {
				if(field.colorOf(x, y2) == FigureColor.RED && field.getType(x, y2) == FigureType.NORMAL) {
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
