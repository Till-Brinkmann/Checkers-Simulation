package algo;

import checkers.Figure.FigureColor;
import player.MiniMaxABPlayer;

public class MinMaxABManager {
	
	public final MiniMaxABPlayer player;
	
	public final FigureColor playerColor;
	public final FigureColor enemyColor;
	/**
	 * maximun depth the recursion is allowed to go
	 */
	public final int maxDepth;
	public final boolean random;
	private int figureCountWhite;
	private int figureCountRed;
	
	private int alphaCuts;
	private int betaCuts;
	public MinMaxABManager(MiniMaxABPlayer player, int maxDepth, FigureColor playerColor, FigureColor enemyColor, boolean random) {
		this.player = player;
		this.maxDepth = maxDepth;
		this.playerColor = playerColor;
		this.enemyColor = enemyColor;
		this.random = random;
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
	public void incrementAlphaCut() {
		alphaCuts++;
	}
	public void incrementBetaCut() {
		betaCuts++;
	}
	public int getAlphaCuts() {
		return alphaCuts;
		
	}
	public int getBetaCuts() {
		return betaCuts;
	}
}
