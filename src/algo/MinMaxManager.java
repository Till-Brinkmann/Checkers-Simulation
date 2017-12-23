package algo;

import checkers.Figure.FigureColor;
import player.MiniMaxPlayer;

public class MinMaxManager {
	
	public final MiniMaxPlayer player;
	
	public final FigureColor playerColor;
	public final FigureColor enemyColor;
	/**
	 * maximun depth the recursion is allowed to go
	 */
	public final int maxDepth;

	private int figureCountWhite;
	private int figureCountRed;
	
	public MinMaxManager(MiniMaxPlayer player, int maxDepth, FigureColor playerColor, FigureColor enemyColor) {
		this.player = player;
		this.maxDepth = maxDepth;
		this.playerColor = playerColor;
		this.enemyColor = enemyColor;
	}
	
	public void logError(String msg) {
		//DEBUG: System.out.println(msg);
		player.csl.printError(msg, player.getName());
	}
	
	public void updateFigureCounts() {
		figureCountWhite = player.getPlayfield().getFigureQuantity(FigureColor.WHITE);
		figureCountRed = player.getPlayfield().getFigureQuantity(FigureColor.RED);
	}
	public int getFigureQuantity(FigureColor color) {
		switch(color){
		case WHITE:
			return figureCountWhite;
		case RED:
			return figureCountRed;
		}
		return 0;
	}
}
