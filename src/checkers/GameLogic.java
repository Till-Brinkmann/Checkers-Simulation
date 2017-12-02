package checkers;

import java.io.File;
import java.io.IOException;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Move.MoveType;
import checkers.Player;
import generic.List;
import gui.GUI;
import gui.GUI.AISpeed;

/**
 * The GameLogic is responsible for the process of one game with the same players. Every game a new GameLogic has to be created.
 * Because it has to communicate with various other objects, it has access to the GUI and the playfield.
 * <p>
 * It also includes the static testing methods, which are responsible for the correct execution of the rules.
 * <p>
 * Moreover, the GameLogic is running on a different thread in order to avoid collision with the PlayfieldPanel.
 * @author Till
 *
 */
public class GameLogic {
	public enum Situations{WHITEWIN, REDWIN, DRAW,NOTHING, STOP};
	/**
	 * the default playfield to use
	 */
	private Playfield field;
	private boolean recordGameIsEnabled;
	private String gameName;
	private int turnCounterRed;
	private int turnCounterWhite;
	private int winCountRed;
	private int winCountWhite;
	private int drawCount;
	private Player playerWhite;
	private Player playerRed;
	private boolean redFailedOnce = false;
	private boolean whiteFailedOnce = false;

	String namePlayerWhite;
	String namePlayerRed;
	
	private boolean twoPlayerMode = false;
	private FigureColor inTurn;
	private GUI gui;
	
	private boolean pause;
	private boolean displayActivated;
	private int slowness;
	private int currentRound = 0;
	private int rounds;
	
	//For NN!
	private Situations endSituation;
	private boolean failed;
	
	public GameLogic(){
		this(new Playfield());
	}
	/**
	 * The Constructor resets all counters to 0 and passes a new Playfield.
	 * <p>
	 * @param playfield  default playfield
	 */
	public GameLogic(Playfield playfield) {
		field = playfield;
		winCountRed = 0;
		winCountWhite = 0;
		drawCount = 0;
		
	}
	/**
	 * The Constructor resets all counters to 0 and passes a new Playfield.
	 * <p>
	 * @param playfield  default playfield
	 */
	public void startGame( boolean pRecordGameIsEnabled, String pGameName, Player pPlayerRed, Player pPlayerWhite, int pRounds, int pSlowness, boolean pDisplayActivated){
		pause = false;
		//how many game should be played
		rounds = pRounds;
		//if both player are one object one Player controls both white and red
		twoPlayerMode = pPlayerRed == pPlayerWhite;
		
		namePlayerRed = pPlayerRed.getName();
		namePlayerWhite = pPlayerWhite.getName();
		
		playerRed = pPlayerRed;
		playerWhite = pPlayerWhite;
		//SlowMode
		slowness = pSlowness;
		//display
		displayActivated = pDisplayActivated;
		
		recordGameIsEnabled = pRecordGameIsEnabled;
		gameName = pGameName;
		turnCounterRed = 0;
		turnCounterWhite = 0;
		//reset variables
		//TODO das gilt nicht beim richtigen game
		redFailedOnce = true;
		whiteFailedOnce = true;

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
		if(!playerRed.equals(gui.playfieldpanel)) {
			try {
				Thread.sleep(slowness);
			} catch (InterruptedException e) {
				gui.console.printWarning("");
				e.printStackTrace();
			}
		}
		playerRed.requestMove();
	}
	public void makeMove(Move m){
		if(m.getMoveType() == MoveType.INVALID || field.field[m.getX()][m.getY()].getFigureColor() != inTurn || !testMove(m)){
			gui.console.printWarning("Invalid move!", "Gamelogic");
			if(inTurn == FigureColor.RED){
				if(redFailedOnce){
					finishGameTest(Situations.WHITEWIN,true);
					return;
				}
				else {
					redFailedOnce = true;
					playerRed.requestMove();
				}
			}
			else {
				if(whiteFailedOnce){
					finishGameTest(Situations.REDWIN,true);
					return;
				}
				else {
					whiteFailedOnce = true;
					playerWhite.requestMove();
				}
			}
		}
		else {//move is valid		
			//increment turn count
			incrementTurnCounter();
			field.executeMove(m);
			//automatic figureToKing check
			testFigureToKing();
			//test if game is Finished
			Situations gamestate = testFinished();
			if(gamestate != Situations.NOTHING){
				finishGameTest(gamestate,false);
			}
			else {
				//for game recording
				if(recordGameIsEnabled) {
					infosGameRecording();
				}
				inTurn = (inTurn == FigureColor.RED) ? FigureColor.WHITE : FigureColor.RED;
				if(!pause) {
					moveRequesting();
				}
			}
		}
	}
	private void incrementTurnCounter() {
		if(inTurn == FigureColor.RED) {
			turnCounterRed++;
		}
		else {
			turnCounterWhite++;
		}
	}
	private void moveRequesting() {
		switch(inTurn){
			case RED:
				if(!playerRed.equals(gui.playfieldpanel)) {
					try {
						Thread.sleep(slowness);
					} catch (InterruptedException e) {
						gui.console.printWarning("");
						e.printStackTrace();
					}
				}
				playerRed.requestMove();
				break;
			case WHITE:
				if(!playerRed.equals(gui.playfieldpanel)) {
					try {
						Thread.sleep(slowness);
					} catch (InterruptedException e) {
						gui.console.printWarning("");
						e.printStackTrace();
					}
				}
				playerWhite.requestMove();
				break;
		}
	}
	public void requestDraw(){
		if(playerRed.acceptDraw() && playerWhite.acceptDraw()){
			finishGameTest(Situations.DRAW,false);
		}
	}
	//---
	private Situations testFinished(){
		//red has to make the next move. So if Red has just moved it does not need to move in the next round
		if(inTurn == FigureColor.WHITE && Move.getPossibleMoves(FigureColor.RED, field).length == 0) {
			return Situations.WHITEWIN;
		}
		if(inTurn == FigureColor.RED && Move.getPossibleMoves(FigureColor.WHITE, field).length == 0) {
			return Situations.REDWIN;
		}
		
		//test for draw Situation
		if(field.getMovesWithoutJumps() == 30) {
			requestDraw();
		}
		
		
		if(field.getMovesWithoutJumps() == 60) {
			return Situations.DRAW;
		}
		return Situations.NOTHING;
	}
	public void finishGameTest(Situations end, boolean pFailed) {
		failed = pFailed;
		endSituation = end;
		switch(end) {
		case DRAW:
			gui.console.printInfo("GameLogic", "Game is finished!");
			gui.console.printInfo("GameLogic", "Result: Draw!");
			drawCount++;			
			break;

		case REDWIN:
			gui.console.printInfo("GameLogic", "Game is finished!");
			if(failed) {
				gui.console.printInfo("GameLogic", playerWhite.getName() +"(White) did a wrong move!");
			}
			
			gui.console.printInfo("GameLogic", "Result: "+ playerRed.getName() +"(Red) won the game!");
			winCountRed++;
			break;
		case WHITEWIN:
			gui.console.printInfo("GameLogic", "Game is finished!");
			if(failed) {
				gui.console.printInfo("GameLogic", playerRed.getName() +"(Red) did a wrong move!");
			}
			gui.console.printInfo("GameLogic", "Result: "+ playerWhite.getName() +"(White) won the game!");
			winCountWhite++;
			break;
		case STOP:
			gui.console.printInfo("GameLogic", "Game was stopped");
			break;
		case NOTHING:
			return;
		}
		
		++currentRound;
		if(currentRound == rounds || end == Situations.STOP) {
			try {
				field.loadGameSituation(new File("resources/playfieldSaves/noFigures.pfs"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gui.playfieldpanel.updateDisplay();
			
			gui.console.printInfo("The AI" + playerWhite.getName() + " (White) won " + winCountWhite + " times.","GameLogic");
			gui.console.printInfo( "The AI" + playerRed.getName() + " (Red) won " + winCountRed + " times.","GameLogic");
			gui.console.printInfo("Draw: " + drawCount + " times.", "GameLogic");
			
			gui.setAISpeed(AISpeed.NOTACTIVE);
			gui.setEnableResume(false);
			gui.setEnablePause(false);
			gui.setEnableStop(false);
			//currentRound = 0;
			//TODO "hard" reset 
			// TODO maybe statistic for ki playing against each other and creating a file with all information
		}
		else {
			try {
				field.createStartPosition();
				new Thread(){
					public void run(){
						try {
							startGame(recordGameIsEnabled, gameName, playerWhite, playerRed, rounds, slowness, displayActivated);
						} catch (IllegalArgumentException | SecurityException e) {
							gui.console.printWarning("gmlc", "failed to load the ai");
							e.printStackTrace();
						}
					}
				}.start();
				//startGame(recordGameIsEnabled, gameName, playerRed, playerWhite, rounds, slowness, displayActivated);
			} catch (IOException e) {
				gui.console.printWarning("failed to load the pfs file startPositionForSize8","GameLogic");
				e.printStackTrace();
			}			
		}		
	}
	public boolean getTwoPlayerMode(){
		return twoPlayerMode;
	}
	private void infosGameRecording() {
		try {
			field.saveGameSituation(gameName, inTurn, (turnCounterRed + turnCounterWhite), playerRed.getName(), playerWhite.getName());
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
		//TODO wenn es kein Jump ist aber jumps möglich sind return false
		if(m.getMoveType() == MoveType.STEP && Move.getPossibleJumps(f.field[x][y], f).length != 0){
			return false;
		}
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
	public static List<Move> testForMultiJump(int x, int y, Playfield f) {
		List<Move> list =Move.getPossibleJumps(f.field[x][y], f);
		if(list.length != 0) {
			return list;
		}
		else
		{
		return null;
		}
	}
	public List<Move> testForMultiJump(int x, int y){
		return testForMultiJump(x,y, field);
	}
	public Playfield getPlayfield(){
		return field;
	}
	public void linkGUI(GUI gui) {
		this.gui = gui;
	}
	public void setSlowness(int pSlowness) {
		slowness = pSlowness;
	}
	public void setPause(boolean b) {
		pause = b;
		if(b == false) {			
			moveRequesting();
		}
	}
	
	public Situations getFinalSituation() {
		return endSituation;
		
	}
	public boolean getFailed() {
		return failed;
	}
	
	public int getTurnCountRed() {
		return turnCounterRed;
	}
	public int getTurnCountWhite() {
		return turnCounterWhite;
	}
}
