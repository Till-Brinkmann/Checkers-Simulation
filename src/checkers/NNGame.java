package checkers;
/**
 * Optimized version of the GameLogic for NNTraining.
 * All methods dealing with move creation/validation can now be found in NNMove.
 */
public class NNGame {
	//Constants to represent a certain gamestate.
	public static final byte INVALID_MOVE_WHITE = -2;
	public static final byte INVALID_MOVE_RED = -1;
	public static final byte NORMAL = 0;
	public static final byte REDWIN = 1;
	public static final byte WHITEWIN = 2;
	public static final byte DRAW = 3;
	/**
	 * Red player.
	 */
	private Player pRed;
	/**
	 * White player.
	 */
	private Player pWhite;
	/**
	 * NNPlayfield the game is played on.
	 */
	private NNPlayfield field;
	/**
	 * The current gamestate.
	 * Should only be set to one of the provided constants:
	 * INVALID_MOVE_WHITE, INVALID_MOVE_RED, NORMAL, REDWIN, WHITEWIN, DRAW.
	 */
	public byte situation;
	//Turn counter for red and white
	public int turnCounterRed;
	public int turnCounterWhite;
	/**
	 * If movesWithoutJumps reaches this constant the game is finished with a draw situation.
	 */
	public static final int MAX_MOVESWITHOUTJUMPS = 100;
	public int movesWithoutJumps;
	/**
	 * Initalizes a new Game with a default(startposition) playfield.
	 * @param pRed The red player.
	 * @param pWhite The white player.
	 */
	public NNGame(Player pRed, Player pWhite){
		this(pRed, pWhite, NNPlayfield.startPosition());
	}
	/**
	 * Initalizes a new game.
	 * @param pRed The red player.
	 * @param pWhite The white player.
	 * @param field The field the game is played on.
	 */
	public NNGame(Player pRed, Player pWhite, NNPlayfield field) {
		this.pRed = pRed;
		this.pWhite = pWhite;
		this.field = field;
		turnCounterRed = 0;
		turnCounterWhite = 0;
		movesWithoutJumps = 0;
	}
	/**
	 * Starts the game. Does not return until the game is finished because a non NORMAL situation occured.
	 */
	public void start(){
		pRed.prepare(true, field);
		pWhite.prepare(false, field);
		for(;;){
			makeMove(true, pRed.requestMove());
			if(situation != NORMAL) break;
			makeMove(false, pWhite.requestMove());
			if(situation != NORMAL) break;
		}
	}
	/**
	 * Make a move of the given color. Also the situation is reevaluated.
	 * @param color Color of the player that does the move.
	 * @param m Move to execute and validate.
	 */
	private void makeMove(boolean color, NNMove m) {
		//the move is invalid
		if(!NNMove.testMove(m, field)) {
			situation = color ? INVALID_MOVE_RED : INVALID_MOVE_WHITE;
			return;
		}
		//after a certain amount of steps the game is declared as a draw(to prevent infinite games where no player ever gives up)
		if(movesWithoutJumps >= MAX_MOVESWITHOUTJUMPS) {
			situation = DRAW;
			return;
		}
		field.executeMove(m);
		movesWithoutJumps++;
		if(m.isJump()) movesWithoutJumps = 0;
		if(color) {
			turnCounterRed++;
			//no move for white is possible
			if(!NNMove.isMovingPossible(false, field)) {
				situation = REDWIN;
				return;
			}
		}
		else {
			turnCounterWhite++;
			//no move for red is possible
			if(!NNMove.isMovingPossible(true, field)) {
				situation = WHITEWIN;
				return;
			}
		}
	}
}
