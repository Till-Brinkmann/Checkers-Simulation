package checkers;
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
 * @author Marco
 */
public class GameLogic {
	public enum Situations{WHITEWIN, REDWIN, DRAW,NOTHING, STOP};

	//the default playfield to use
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
	
	private boolean gameInProgress;
	
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
		currentRound = 0;
		gameInProgress = false;
		
	}
	/**
	 * This method is essential for running a game and it is therefore called if a game about to begin. Currently this happens in GameSettings and NNTrainingsMangager.
	 * First, it sets all the parameters and then it calls the createStartPosition method in playfield. After that it calls requestMove form the red player, because
	 * the red player always makes the first move. This triggers an continuous loop between requestMove and makeMove util the game is finished.
	 * <p>
	 * @param pRecordGameIsEnabled       This boolean defines a new directory with the GameName is created, in which all relevant game informations are saved. 
	 * @param pRounds					 An integer variable which represents the number of games in one run.
	 * @param pSlowness					 An integer variable which stands for the pauses between each move, so that the pace of the game changes
	 * @param pDisplayActivated          This boolean defines whether the playfieldPanel displays the game or not
	 * @param pUseCurrentPf 
	 */
	public void startGame( boolean pRecordGameIsEnabled, String pGameName, Player pPlayerRed, Player pPlayerWhite, int pRounds, int pSlowness, boolean pDisplayActivated, boolean useCurrentPf){
		gameInProgress = true;
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

		//test if an new playfield has to be created
		if(!useCurrentPf) {
			try {			
				field.createStartPosition();
			} catch (IOException e) {
				gui.console.printWarning(
					"Gamelogic:startGame",
					"Could not load startposition. Please check if your playfieldsaves are at the right position");
				return;
			}
		}
		playerRed.prepare(FigureColor.RED);
		//prepare only needs to be called once for Red then
		if(!twoPlayerMode){
			playerWhite.prepare(FigureColor.WHITE);
		}
		//red always starts
		gui.console.printInfo("GameLogic", "Therefore " + namePlayerRed + "starts first");
		gui.console.printInfo("GameLogic","Playing "+ (rounds-currentRound) + " more Rounds before reset");
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
	/**
	 * First, the method tests, if the Move transferred as a parameter is valid. After that is executes all nessecary steps for one particular
	 * turn.
	 * <p>
	 *  Also it is important to know that this method is creating a continous loop with requestMove in a Player. In detail
	 *  makeMove(actually it is moveRequesting. Is an outsourced part of makeMove) is calling requestMove in a Player and reqestMove is 
	 *  calling makeMove again. 
	 * <p>
	 * @param m         The Object Move represents an move of one figure on the board. It contains all information needed in order to be fully identified.
	 */
	public void makeMove(Move m){
		//if the movetype is invalid or the player of the figure is not in turn or testMove is false
		if(m.getMoveType() == MoveType.INVALID || field.field[m.getX()][m.getY()].getFigureColor() != inTurn || !testMove(m)){
			gui.console.printWarning("Invalid move!", "Gamelogic");
			if(inTurn == FigureColor.RED){
				if(redFailedOnce){
					finishGame(Situations.WHITEWIN,true);
					return;
				}
				else {
					redFailedOnce = true;
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
					playerWhite.requestMove();
				}
			}
		}
		else {//move is valid
			//increment turn count
			incrementTurnCounter();
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
	/**
	 * MoveRequesting has the task to decide which Player does the next move through the variable inTurn  and then
	 * calling the requestMove Method in ther right Player. 
	 * <p>
	 * It is a outsourced method from makeMove and is therefore only called by makeMove. This step ensured an more 
	 * uncluttered overview over the code.
	 */
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
	/**
	 * This method increments one of the global variables turnCounterRed and turnCounterWhite after a turn by a specific FigureColor 
	 * was finished.  
	 */
	private void incrementTurnCounter() {
		if(inTurn == FigureColor.RED) {
			turnCounterRed++;
		}
		else {
			turnCounterWhite++;
		}
	}
	/**
	 * This method checks if both players want to accept a draw by calling the method acceptDraw in both player, which returns a boolean.
	 */
	public void requestDraw(){
		if(playerRed.acceptDraw() && playerWhite.acceptDraw()){
			finishGame(Situations.DRAW,false);
		}
	}
	/**
	 * It test if a game has to be finished by certain criterias and then returns the correct situation.
	 * <p>
	 * @return     An enumeration for the passible game situations 
	 */
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
	/**
	 * FinishGame is only called if a game is certainly going to be finished. It mainly prints all the necessary information on the console.
	 * Furthermore, it starts a new game if there are more rounds in one run and resets all veriables.
	 * <p>
	 * @param end                A variable from the enumeration situation.
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
			currentRound++;
			break;

		case REDWIN:
			gui.console.printInfo("GameLogic", "Game is finished!");
			if(failed) {
				gui.console.printInfo("GameLogic", playerWhite.getName() +"(White) did a wrong move!");
			}
			
			gui.console.printInfo("GameLogic", "Result: "+ playerRed.getName() +"(Red) won the game!");
			winCountRed++;
			currentRound++;
			break;
		case WHITEWIN:
			gui.console.printInfo("GameLogic", "Game is finished!");
			if(failed) {
				gui.console.printInfo("GameLogic", playerRed.getName() +"(Red) did a wrong move!");
			}
			gui.console.printInfo("GameLogic", "Result: "+ playerWhite.getName() +"(White) won the game!");
			winCountWhite++;
			currentRound++;
			break;
		case STOP:
			gui.console.printInfo("GameLogic", "Game was stopped");
			break;
		case NOTHING:
			return;
		}		
		if(currentRound == rounds || end == Situations.STOP) {
			currentRound = 0;
//			try {
//				field.loadGameSituation(new File("resources/playfieldSaves/noFigures.pfs"));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			gui.playfieldpanel.updateDisplay();
			
			gui.console.printInfo("GameLogic", "The " + playerWhite.getName() + " (White) won " + winCountWhite + " times.");
			gui.console.printInfo("GameLogic", "The " + playerRed.getName() + " (Red) won " + winCountRed + " times.");
			gui.console.printInfo("GameLogic", "Draw: " + drawCount + " times.");
			
			//reseting variables
			gameInProgress = false;
			winCountWhite = 0;
			winCountRed = 0;
			drawCount = 0;
			gui.setAISpeed(AISpeed.NOTACTIVE);
			gui.setEnableResume(false);
			gui.setEnablePause(false);
			gui.setEnableStop(false);
			// TODO maybe statistic for ki playing against each other and creating a file with all information
		}
		else {
			try {
				field.createStartPosition();
				new Thread(){
					public void run(){
						try {
							startGame(recordGameIsEnabled, gameName, playerWhite, playerRed, rounds, slowness, displayActivated, false);
						} catch (IllegalArgumentException | SecurityException e) {
							gui.console.printWarning("GameLogic", "failed to load the ai");
							e.printStackTrace();
						}
					}
				}.start();
				//startGame(recordGameIsEnabled, gameName, playerRed, playerWhite, rounds, slowness, displayActivated);
			} catch (IOException e) {
				gui.console.printWarning("GameLogic","failed to load the pfs file startPositionForSize8");
				e.printStackTrace();
			}			
		}		
	}
	/**
	 * This method only returns a boolean, if there are two real players and therefore no AI.
	 * <p>
	 * @return boolean     A boolean which is true if there are two real players. 
	 */
	public boolean getTwoPlayerMode(){
		return twoPlayerMode;
	}
	/**
	 * This method is called after every the run turn and trasferred all information to the playfield which creates a .pfs file with all
	 * information.
	 */
	private void infosGameRecording() {
		try {
			field.saveGameSituation(gameName, inTurn, (turnCounterRed + turnCounterWhite), playerRed.getName(), playerWhite.getName());
		} catch (IOException e) {
			gui.console.printWarning("GameLogic","playfield could not be saved. IOException: " + e);
			e.printStackTrace();
		}
	}
	/**
	 * Tests if a figure reached the other side of the playfield. If this is the case, then a method in Playfield is called that changes
	 * the figure into aking
	 */
	private void testFigureToKing(){
		int y1 = 0;
		int y2 = 7;
		for(int x = 0; x < field.SIZE;x++) {
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
	 * It tests if the given move is possible on the given playfield. It is static, because it can be accessed even if there is no 
	 * Instance of the gamelogic.
	 * <p>
	 * @param move                 The Object Move represents an move of one figure on the board. It contains all information needed in order to be fully identified.
	 * @param field				   Field is an Object which represents the playfield.
	 * @return boolean             True, if the move is valid. False, if the move is wrong.
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
	/**
	 * This method is the not static version of testMove. It calls the static method testMove with the current global playfield 
	 * in the GameLogic.
	 * <p>
	 * @param move           the Object Move represents an move of one figure on the board. It contains all information needed in order to be fully identified.
	 * @return boolean       True, if the move is valid. False, if the move is wrong.
	 */
	public boolean testMove(Move move){
		return testMove(move, field);
	}
	/**
	 * This method tests, if there are multijumps among all possible jumps for a certain playfield available and then separates them into a new List.
	 * <p>
	 * @param x                 an integer variable which is representing a point on the vertical axis of the playfield.
	 * @param y	 	            an integer variable which is representing a point on the horizontal axis of the playfield.	
	 * @param field             Field is an Object which represents the playfield.
	 * @return List or null     This new List contains only all the possible multijumps on a specific playfield situation for only one player. 
	 */
	public static List<Move> testForMultiJump(int x, int y, Playfield field) {
		List<Move> list = Move.getPossibleJumps(field.field[x][y], field);
		if(list.length != 0) {
			return list;
		}
		else
		{
		return null;
		}
	}
	/**
	 * This method is the not static version of testFoMultijump. It uses the current global playfield in the Gamelogic. 
	 * <p>
	 * @param x                 an integer variable which is representing a point on the vertical axis of the playfield.
	 * @param y	 	            an integer variable which is representing a point on the horizontal axis of the playfield.	
	 * @return List or null     This new List contains only all the possible multijumps on a specific playfield situation for only one player. 
	 */
	public List<Move> testForMultiJump(int x, int y){
		return testForMultiJump(x,y, field);
	}
	/**
	 * It only return the current global playfield.
	 * 
	 * @return field
	 */
	public Playfield getPlayfield(){
		return field;
	}
	/**
	 * The GameLogic needs the current grafical interface in order to output something on the console. Here the parameter is set as the
	 * current gui. 
	 * <p>
	 * @param gui      the graphical interface which is responsible for creating the window and displaing the right playfield and console
	 */
	public void linkGUI(GUI gui) {
		this.gui = gui;
	}
	/**
	 * This method set the time of the pauses between the moves in miliseconds.
	 * <p>
	 * @param pSlowness      an Integar which saves the time between the individual moves.
	 */
	public void setSlowness(int pSlowness) {
		slowness = pSlowness;
	}
	/**	
	 * This method is responsible for pausing and continuing the current run. If the game is running an this called then the global boolean
	 * variable pause is set to true, so in the next turn the loop between makeMove and moveRequest in player will be interrupted. 
	 * <p>
	 * If the game is already paused the method moveRequesting is called. It has to be called in a new thread, because therefore the gui 
	 * can be used independetly.
	 * <p>
	 * @param b       the boolean variable is true if the game will be paused. If the game is already pause, it is false.
	 */
	public void setPause(boolean b) {
		pause = b;
		gameInProgress = false;
		if(!pause) {	
			new Thread(){
				public void run(){
					gameInProgress = true;
					moveRequesting();
				}
			}.start();
		}
	}
	/**
	 * This method returns a global variable which represents the final situation of a game. It is especially needed when neuronal networks are 
	 * going to be evaluated or to save the game information.     
	 * <p>
	 * @return endSituation     a variable from the enumeration Situations.
	 */
	public Situations getFinalSituation() {
		return endSituation;
		
	}
	/**
	 * This method  It is especially needed when neuronal networks are 
	 * going to be evaluated or to save the game information.     
	 * <p>
	 * @return failed      A boolean which shows if a game was finished by a wrong Move made by a Player.
	 */
	public boolean getFailed() {
		return failed;
	}
	/**
	 * Returns the global varible turnCounterRed which represents the total number of the red player turns. This is needed for the game
	 * information.
	 * <p>
	 * @return turnCounterRed     An integer which counts the turns the red player made.
	 */
	public int getTurnCountRed() {
		return turnCounterRed;
	}
	/**
	 * Returns the global varible turnCounterWhite which represents the total number of the white player turns. This is needed for the game
	 * information.
	 * <p>
	 * @return turnCountWhite      An integer which counts the turns the white player made.
	 */
	public int getTurnCountWhite() {
		return turnCounterWhite;
	}
	/**
	 * Returns the summ of both players turns. This is needed for the game
	 * information.
	 * <p>
	 * @return int      An integer which stands for all turns mad in one run. 
	 */
	public int getTurnCount(){
		return turnCounterRed + turnCounterWhite;
	}
	/**
	 * Return a boolean which shows if the game is currently in progress.
	 * <p>
	 * @return  boolean   True if the game is running. False if is paused or stopped.
	 */
	public boolean getInProgress() {
		return gameInProgress;
		
	}
}
