package checkers;
/**
 * provides methods for game logic
 * @author Till
 *
 */
public class GameRules {
	/**
	 * the default playfield to use
	 */
	private Playfield field;
	/**
	 * 
	 * @param playfield default playfield
	 */
	public GameRules(Playfield playfield) {
		field = playfield;
	}
	/**
	 * tests if the given move is possible on the given playfield
	 * @param move
	 * @param field
	 * @return 0 if not possible
	 */
	public static int testMove(Move m, Playfield f){
		return 0;
	}
	public int testMove(Move move){
		return testMove(move, field);
	}

}
