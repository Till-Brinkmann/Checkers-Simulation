package checkers;
import java.io.IOException;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Move.MoveType;
import evaluation.EvaluationManager;
import gui.GUI;
import gui.GUI.AISpeed;
import main.StackExecutor;
import network.NetworkPlayer;
import task.Task;

/**
 * The GameLogic is responsible for the process of one game with the same players. Every game a new GameLogic has to be created.
 * Because it has to communicate with various other objects, it has access to the GUI and the playfield.
 * <p>
 * It also includes the static testing methods, which are responsible for the correct execution of the rules.
 * <p>
 * Moreover, the GameLogic is running on a different thread to avoid putting too much stress on the main Eventqeue.
 * @author Till
 * @author Marco
 */
public class GameLogic {
	/**
	 * All situations the game could be in.
	 */
	public enum Situations{WHITEWIN, REDWIN, DRAW, NOTHING, STOP};

	//the default playfield to use
	private Playfield field;
	private String gameName;
	private int[] stepCount = new int[2];
	private int[] jumpCount = new int[2];
	private int[] multijumpCount = new int[2];

	private Player playerWhite;
	private Player playerRed;
	private boolean redFailedOnce;
	private boolean whiteFailedOnce;

	String namePlayer2;
	String namePlayer1;
	
	private boolean gameInProgress;
	
	private boolean twoPlayerMode = false;
	private FigureColor inTurn;
	private GUI gui;
	private StackExecutor moveExecutor;
	private Task moveRequestingTask;
	
	private boolean pause;
	private int slowness;
	private int currentRound = 0;
	private int rounds;
	
	private boolean autoPfTurning;
	//for evaluation
	private EvaluationManager evaluationManager;
	private long timeBeforeMove;
	private long gameTimeBefore;
	private int winCountP1;
	private int winCountP2;
	private int drawCount;
	private int overallMovePossibilitiesP1;
	private int overallMovePossibilitiesP2;
	private Situations endSituation;
	private boolean failed;
	
	public GameLogic(){
		this(new Playfield());
	}
	/**
	 * The Constructor sets all counters to 0 and passes a new Playfield.
	 * <p>
	 * @param playfield  default playfield
	 */
	public GameLogic(Playfield playfield) {
		field = playfield;
		winCountP1 = 0;
		winCountP2 = 0;
		drawCount = 0;
		currentRound = 0;
		gameInProgress = false;
		moveExecutor = new StackExecutor("GameLogic:MoveExecutor");
		moveRequestingTask = new Task() {

			@Override
			public void compute() {
				moveRequesting();
			}
			
		};
	}
	/**
	 * 
	 * @param gameName
	 * @param player1
	 * @param player2
	 * @param rounds
	 * @param slowness
	 * @param displayActivated
	 * @param useCurrentPf
	 */
	public void startGame(String gameName, Player player1, Player player2, int rounds, int slowness, boolean displayActivated, boolean useCurrentPf, boolean autoPfTurning){
		//reset moveWindow
		gui.movesWindow.resetTextArea();
		
		gameInProgress = true;
		pause = false;
		//how many games should be played
		this.rounds = rounds;
		//if the display should be turned after every move
		this.autoPfTurning = autoPfTurning;
		if(currentRound == 0) {
			//if both players are one object one Player controls both white and red
			twoPlayerMode = player1 == player2;
			namePlayer1 = player1.getName();
			namePlayer2 = player2.getName();
		}
		
		this.playerRed = player1;
		this.playerWhite = player2;
		
		redFailedOnce = false;
		whiteFailedOnce = false;
		//SlowMode
		this.slowness = slowness;
		
		gui.playfieldplayer.playfield = field;
		//set the new Playfield as the field to display if displaying is enabled
		if(!displayActivated) {
			field.setPlayfieldDisplay(null);
		}
		else {
			field.setPlayfieldDisplay(gui.playfieldplayer);
		}
		this.gameName = gameName;
		
		if(evaluationManager != null) {
			evaluationManager.createRound(currentRound);
		}
		//reset Varibles fpr
		for(int i = 0; i<2; i++) {
		stepCount[i] = 0;
		jumpCount[i] = 0;
		multijumpCount[i] = 0;
		}
		//test if a new playfield has to be created
		if(!useCurrentPf) {
			try {			
				field.createStartPosition();
			} catch (IOException e) {
				gui.console.printWarning(
					"Gamelogic:startGame",
					"Could not load startposition. Please check if your playfieldsave folder contains a startposition file");
				finishGame(Situations.STOP,false);
				return;
			}
		}
		//turn the playfield in the right direction
		if(gui.playfieldplayer.playfieldpanel.reversed == true) {
			gui.playfieldplayer.playfieldpanel.turnPlayfield();
		}
		player1.prepare(FigureColor.RED);
		//prepare only needs to be called once for Red then
		if(!twoPlayerMode){
			player2.prepare(FigureColor.WHITE);
		}
		//red always starts
		gui.console.printInfo("GameLogic", playerRed.getName() + "starts first.");
		gui.console.printInfo("GameLogic","Playing " + (rounds-currentRound) + " more Rounds.");
		inTurn = FigureColor.RED;
		if(!player1.equals(gui.playfieldplayer)) {
			try {
				Thread.sleep(slowness);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//set beginning moveTimes
		if(evaluationManager != null) {
			timeBeforeMove = System.nanoTime();
			gameTimeBefore = System.currentTimeMillis();
		}
		player1.requestMove();
	}
	/**
	 * First, the method tests, if the Move transferred as a parameter is valid. After that it executes all nessecary steps for one particular
	 * turn.
	 * <p>
	 *  Also it is important to know that this method is creating a continous loop with requestMove in a Player. In detail
	 *  makeMove (actually it is moveRequesting) is calling requestMove in a Player and requestMove is 
	 *  calling makeMove again.
	 * <p>
	 * @param m The Object Move represents a move of one figure on the board. It contains all information needed in order to be fully identified.
	 */
	public void makeMove(Move m){
		//save move times
		if(evaluationManager != null) {
			if(inTurn == FigureColor.RED && currentRound % 2 == 0) {
				evaluationManager.getRound(currentRound).setMoveTime((System.nanoTime()-timeBeforeMove),playerRed);
				overallMovePossibilitiesP1 += Move.getPossibleMoves(FigureColor.RED,field).length;
			}
			else {
				evaluationManager.getRound(currentRound).setMoveTime((System.nanoTime()-timeBeforeMove),playerWhite);
				overallMovePossibilitiesP2 += Move.getPossibleMoves(FigureColor.WHITE,field).length;
			}
		}
		//if the movetype is invalid or the player of the figure is not in turn or testMove returns false
		if(m == null || m.getMoveType() == MoveType.INVALID || field.field[m.getX()][m.getY()].getFigureColor() != inTurn || !testMove(m)){
			if(m == null) {
				gui.console.printError("Move is null!", "Gamelogic");
			}
			else {
				gui.console.printWarning("Invalid move!", "Gamelogic");
			}
			if(inTurn == FigureColor.RED){
				if(redFailedOnce){
					finishGame(Situations.WHITEWIN,true);
					return;
				}
				else {
					redFailedOnce = true;
					timeBeforeMove = System.nanoTime();
					playerRed.requestMove();
				}
			}
			else {
				if(whiteFailedOnce){
					finishGame(Situations.REDWIN,true);
					return;
				}
				else {
					whiteFailedOnce = true;
					timeBeforeMove = System.nanoTime();
					playerWhite.requestMove();
				}
			}
		}
		else {//move is valid
			//update moveWindow
			gui.movesWindow.addMove(m);
			//increment move count
			incrementTurnCounter(m);
			//we need to move before testing the other things
			field.executeMove(m);
			//automatic figureToKing check
			testFigureToKing();
			//test if game is Finished
			Situations gamestate = testFinished();
			if(gamestate != Situations.NOTHING){
				finishGame(gamestate,false);
			}
			else {
				//for game recording
				if(evaluationManager != null) {
					evaluationManager.getRound(currentRound).saveGameSituation(this);
				}
				inTurn = (inTurn == FigureColor.RED) ? FigureColor.WHITE : FigureColor.RED;
				if(!pause) {
					moveExecutor.execute(moveRequestingTask);
				}
				//if there is a online game then send the move to the other player
				if(gui.networkmanager.runningOnlineGame) {
					if(inTurn == FigureColor.RED && playerRed.getClass().equals(NetworkPlayer.class) || inTurn == FigureColor.WHITE && playerWhite.getClass().equals(NetworkPlayer.class) ) {
						gui.networkmanager.sendMove(m);
					}
				}
			}
		}
	}
	/**
	 * MoveRequesting has the task to decide which Player does the next move through the variable inTurn  and then
	 * calling the requestMove Method of the right Player. 
	 * <p>
	 * It is a outsourced method from makeMove and is therefore only called by makeMove. This step ensures a more 
	 * uncluttered overview over the code.
	 */
	private void moveRequesting() {
		if(autoPfTurning) {
			gui.playfieldplayer.playfieldpanel.turnPlayfield();
		}
		switch(inTurn){
			case RED:
				if(!playerRed.equals(gui.playfieldplayer)) {
					try {
						Thread.sleep(slowness);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				timeBeforeMove = System.nanoTime();
				playerRed.requestMove();
				break;
			case WHITE:
				if(!playerWhite.equals(gui.playfieldplayer)) {
					try {
						Thread.sleep(slowness);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				timeBeforeMove = System.nanoTime();
				playerWhite.requestMove();
				break;
		}
	}
	/**
	 * This method increments one of the global variables moveCount, after a turn was finished.  
	 * @param m   A move.
	 */
	private void incrementTurnCounter(Move m) {
		int i;
		if(inTurn == FigureColor.RED) {
			i = 0;
		}
		else {
			i = 1;
		}
		switch(m.getMoveType()) {
		case INVALID:
			break;
		case JUMP:
			jumpCount[i]++;
			break;
		case MULTIJUMP:
			multijumpCount[i]++;
			break;
		case STEP:
			stepCount[i]++;
			break;
		}
	}
	/**
	 * This method checks if both players want to accept a draw by calling the method acceptDraw in both player, which returns a boolean.
	 */
	public void requestDraw(){
		if(playerRed.acceptDraw() && playerWhite.acceptDraw()) finishGame(Situations.DRAW,false);
	}
	/**
	 * It tests if a game has to be finished by certain criterias and then returns the correct situation.
	 * <p>
	 * @return An enumeration for the passible game situations 
	 */
	private Situations testFinished(){
		//white has to make the next move, because red has just moved and it does not need to move in the next round.
		if(inTurn == FigureColor.WHITE && Move.getPossibleMoves(FigureColor.RED, field).length == 0) {
			return Situations.WHITEWIN;
		}
		if(inTurn == FigureColor.RED && Move.getPossibleMoves(FigureColor.WHITE, field).length == 0) {
			return Situations.REDWIN;
		}
		
		//ask for draw
		if(field.getMovesWithoutJumps() == 30) {
			requestDraw();
		}
		
		if(field.getMovesWithoutJumps() == 100) {
			gui.console.printWarning("To many moves without a jump. Forcing draw.", "GameLogic");
			return Situations.DRAW;
		}
		return Situations.NOTHING;
	}
	/**
	 * FinishGame is only called if a game is certainly going to be finished. It mainly prints all the necessary information to the console.
	 * Furthermore, it starts a new game if there are more rounds in this run and resets all variables.
	 * <p>
	 * @param end                A variable from the enum situation.
	 * @param pFailed            A boolean which is showing if one of the two players lost by doing a wrong move
	 */
	public void finishGame(Situations end, boolean pFailed) {
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
			//the color of the players change every round.
			if(failed) {
				gui.console.printInfo("GameLogic", playerWhite.getName() +"(White) did a wrong move!");
			}
			gui.console.printInfo("GameLogic", "Result: "+ playerRed.getName() +"(Red) won the game!");
			if(currentRound % 2 == 0) {
				winCountP1++;
			}
			else {
				winCountP2++;
			}
			break;
		case WHITEWIN:
			gui.console.printInfo("GameLogic", "Game is finished!");
			if(failed) {
				gui.console.printInfo("GameLogic", playerRed.getName() +"(Red) did a wrong move!");
			}
			gui.console.printInfo("GameLogic", "Result: "+ playerWhite.getName() +"(White) won the game!");
			if(currentRound % 2 == 1) {
				winCountP1++;
			}
			else {
				winCountP2++;
			}
			break;
		case STOP:
			gui.console.printInfo("GameLogic", "Game was stopped");
			break;
		case NOTHING:
			return;
		}		
		if(evaluationManager != null) {
			evaluationManager.getRound(currentRound).setgameTime((System.currentTimeMillis() - gameTimeBefore));
			evaluationManager.getRound(currentRound).evaluateGame(this);
		}
		currentRound++;
		if(currentRound == rounds || end == Situations.STOP) {
			gui.console.printInfo("GameLogic", "The " + namePlayer1 +
					" (started as: Red, " + ((currentRound - 1) % 2 == 0 ? "Red" : "White") + " in last game)" +
					" won " + winCountP1 + " times.");
			gui.console.printInfo("GameLogic", "The " + namePlayer2 +
					" (started as: White, " + ((currentRound - 1) % 2 == 1 ? "Red" : "White") + " in last game)" +
					" won " + winCountP2 + " times.");
			gui.console.printInfo("GameLogic", "Draw: " + drawCount + " times.");
			//reset variables
			currentRound = 0;
			gameInProgress = false;
			winCountP2 = 0;
			winCountP1 = 0;
			drawCount = 0;
			gui.setAISpeed(AISpeed.NOTACTIVE);
			gui.setEnableResume(false);
			gui.setEnablePause(false);
			gui.setEnableStop(false);
			gui.setDisplayEnabled(true);
			gui.setEnableDisplayEnabled(false);
			
			//resets field borders
			gui.playfieldplayer.playfieldpanel.resetBorders();
			//stop running online game
			if(gui.networkmanager.runningOnlineGame) {
				gui.networkmanager.runningOnlineGame = false;
			}
			if(evaluationManager != null) evaluationManager.runEvaluation();
		}
		else {
			try {
				field.createStartPosition();
				moveExecutor.execute(new Task() {

					@Override
					public void compute() {
						startGame(gameName, playerWhite, playerRed, rounds, slowness, gui.displayEnabled.isSelected(), false,autoPfTurning);
					}
					
				});
			} catch (IOException e) {
				gui.console.printWarning("GameLogic","failed to load the pfs file startPositionForSize" + field.SIZE);
				e.printStackTrace();
			}			
		}		
	}
	/**
	 * This method only returns true, if both players are handled by the same object.
	 * <p>
	 * @return boolean A boolean which is true if there are two real players. 
	 */
	public boolean getTwoPlayerMode(){
		return twoPlayerMode;
	}
	/**
	 * Tests if a figure reached the other side of the playfield. If this is the case, then a method in Playfield is called that changes
	 * the figure into a king.
	 */
	private void testFigureToKing(){
		for(int x = 0; x < field.SIZE;x++) {
			if(field.isOccupied(x, 0)) {
				if(field.colorOf(x, 0) == FigureColor.WHITE && field.getType(x, 0) == FigureType.NORMAL) {
					field.changeFigureToKing(x, 0);
				}
			}
			if(field.isOccupied(x, field.SIZE-1)) {
				if(field.colorOf(x, field.SIZE-1) == FigureColor.RED && field.getType(x, field.SIZE-1) == FigureType.NORMAL) {
					field.changeFigureToKing(x, field.SIZE-1);
				}
			}
		}
	}
	/**
	 * It tests if the given move is possible on the given playfield. It is static, because it should be possible to access it without instantiating GameLogic.
	 * <p>
	 * @param m The Object Move represents an move of one figure on the board. It contains all information needed in order to be fully identified.
	 * @param f	Field is an Object which represents the playfield.
	 * @return boolean True, if the move is valid. Otherwise false.
	 */
	public static boolean testMove(Move m, Playfield f){
		int x = m.getX();
		int y = m.getY();
		if(!f.isOccupied(x, y)){
			return false;
		}
		FigureColor color = f.colorOf(x, y);
		FigureType type = f.getType(x, y);
		//if the move is a step, but there are jumps possible, you have to choose a jump so return false
		if(m.getMoveType() == MoveType.STEP && Move.jumpIsPossible(f.field[x][y].getFigureColor(), f)){
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
					   	(!f.isOccupied(x-2, y-2) || (m.getX() == x-2 && m.getY() == y-2)))//no figure on the field to land on (except itself)
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
						(!f.isOccupied(x+2, y-2) || (m.getX() == x+2 && m.getY() == y-2)))
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
						(!f.isOccupied(x-2, y+2) || (m.getX() == x-2 && m.getY() == y+2)))
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
						(!f.isOccupied(x+2, y+2) || (m.getX() == x+2 && m.getY() == y+2)))
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
	/**
	 * This method is the non static version of testMove. It calls the static method testMove with the current global playfield 
	 * in the GameLogic.
	 * <p>
	 * @param move           the Object Move represents an move of one figure on the board. It contains all information needed in order to be fully identified.
	 * @return boolean       True, if the move is valid. False, if the move is wrong.
	 */
	public boolean testMove(Move move){
		return testMove(move, field);
	}
	/**
	 * Returns the current global playfield.
	 * 
	 * @return field
	 */
	public Playfield getPlayfield(){
		return field;
	}
	/**
	 * The GameLogic needs the current graphical interface in order to output something to the console. Here the parameter is set as the
	 * current gui.
	 * <p>
	 * @param gui The graphical interface which is responsible for creating the window and displaying the right playfield and console
	 */
	public void linkGUI(GUI gui) {
		this.gui = gui;
	}
	/**
	 * This method sets the time of the pauses between the moves in milliseconds.
	 * <p>
	 * @param pSlowness An Integer that holds the time between the individual moves in ms.
	 */
	public void setSlowness(int pSlowness) {
		slowness = pSlowness;
	}
	/**	
	 * This method is responsible for pausing and continuing the current run. If the game is running an this called then the global boolean
	 * variable pause is set to true, so in the next turn the loop between makeMove and moveRequest in player will be interrupted. 
	 * <p>
	 * If the game is already paused the method moveRequesting is called.
	 * <p>
	 * @param b The boolean variable is true if the game will be paused. If the game is already pause, it is false.
	 */
	public void setPause(boolean b) {
		pause = b;
		if(!pause) {	
			moveExecutor.execute(moveRequestingTask);
		}
	}
	/**
	 * This method returns a global variable which represents the final situation of a game. It is especially needed when saving the game information. 
	 * <p>
	 * @return endSituation     a variable from the enumeration Situations.
	 */
	public Situations getFinalSituation() {
		return endSituation;
		
	}
	/**
	 * It is especially needed to save the game information.     
	 * <p>
	 * @return failed A boolean which shows if a game was finished by a wrong Move made by a Player.
	 */
	public boolean getFailed() {
		return failed;
	}
	/**
	 * @return gameInProgress Returns true if a game is running, otherwise false. 
	 */
	public boolean isInProgress() {
		return gameInProgress;	
	}
	
	public void setManager(EvaluationManager manager) {
		evaluationManager = manager;
	}
	public Player getPlayerRed() {
		return playerRed;
	}
	public Player getPlayerWhite() {
		return playerWhite;
	}
	public FigureColor getInTurn() {
		return inTurn;
	}
	public int[] getStepCount() {
		return stepCount;
	}
	public int[] getJumpCount() {
		return jumpCount;
	}
	public int[] getMultijump() {
		return multijumpCount;
	}
	public int getWinCountRed() {
		return winCountP1;
	}
	public int getWinCountWhite() {
		return winCountP2;
	}
	public int getDrawCount() {
		return drawCount;
	}
	public int getOverallMovePossibilitiesRed() {		
		return overallMovePossibilitiesP1;
	}
	public int getOverallMovePossibilitiesWhite() {
		return overallMovePossibilitiesP2;
	}
	public int getTurnCount() {
		return getTurnCountRed() + getTurnCountWhite();
	}
	public int getTurnCountRed() {
		return stepCount[0] + jumpCount[0] + multijumpCount[0];
	}
	public int getTurnCountWhite() {
		return stepCount[1] + jumpCount[1] + multijumpCount[1];
	}
}
