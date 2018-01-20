package checkers;

import checkers.Figure.FigureColor;

/**
 * This interface has to be attached to every player(e.g. : NNs, AIs, Humans) in order to ensure that every neccessary method is implemented. 
 *<p>
 * @author Till Brinkmann
 * @author Marco Adamczyk
 */
public interface Player {
	/**
	 * This method gives the Player the neccesary information, so that the Player can set up itself for the next move.
	 * <p>
	 * @param color A variable from the enum FigureColor.
	 */
	public void prepare(FigureColor color);
	/**
	 * This method is called by the gamelogic and should be the method in which the player is calculating his move. It is required that this method calls makeMove
	 * in gamelogic with the parameter of the move that the player wants to make.
	 */
	public void requestMove();

	/**
	 * Returns the name of the player.
	 * <p>
	 * @return  A string of characters that represent the name of the player  
	 */
	public String getName();
	/**
	 * This method is called in a player if a draw is requested by the other player. This method allows the player to calculate if a draw is a reasonable choice
	 * and returns a value with regard to that decision.
	 * <p>
	 * @return A boolean the determines if a requested draw will be accepted by the player.
	 */
	public boolean acceptDraw();
	/**
	 * Here the player can save its information after the game in a file. It should be saved in the given directory. 
	 * <p>
	 * @param directory A file to a directory. 
	 */
	public void saveInformation(String directory);
}
