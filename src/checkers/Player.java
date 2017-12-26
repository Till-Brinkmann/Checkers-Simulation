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
	 * @param color An variable from the enumeration.
	 */
	public void prepare(FigureColor color);
	/**
	 * 
	 */
	public void requestMove();
	/*
	 * 
	 */
	public String getName();
	/**
	 * 
	 * @return
	 */
	public boolean acceptDraw();
	/**
	 * 
	 * @param directory
	 */
	public void saveInformation(String directory);
	/**
	 * 
	 */
}
