package checkers;

public interface Player {
	/**
	 * Called on game start to pass the playfield and a figure color to play with.
	 * @param color The figure color to play as.
	 * @param field The playfield the game is played on.
	 */
	public void prepare(boolean color, NNPlayfield field);
	/**
	 * Used to request a move from the player when it is in turn.
	 * @return Returns the move the player wants to make.
	 */
	public NNMove requestMove();
}
