package algo;

import checkers.Figure.FigureColor;
import player.MiniMaxPlayer;

public class MinMaxWStratsManager{

	public final FigureColor playerColor;
	public final FigureColor enemyColor;

	public MinMaxWStratsManager(FigureColor playerColor) {
		this.playerColor = playerColor;
		this.enemyColor = playerColor == FigureColor.RED ? FigureColor.WHITE : FigureColor.RED;
	}

}
