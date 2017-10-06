package checkers;

import checkers.Figure.FigureColor;

/**
 * this provides an interface to attach everything as a player
 * (e.g.: NNs, AIs, Humans).
 * 
 * @author Till
 *
 */
public interface Player {

	public void prepare(FigureColor color);
	public void requestMove();

}
