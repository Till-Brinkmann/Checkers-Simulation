package algo;

import checkers.Figure.FigureColor;
import main.StackExecutor;
import player.MiniMaxMTPlayer;

public class MiniMaxMTManager {

	private final StackExecutor executor;
	
	private final MiniMaxMTPlayer player;
	
	public final FigureColor playerColor;
	public final FigureColor enemyColor;
	
	private int figureCountWhite;
	private int figureCountRed;
	/**
	 * maximun depth the recursion is allowed to go
	 */
	public final int maxDepth;
	
	public MiniMaxMTManager(MiniMaxMTPlayer player, int maxDepth, FigureColor playerColor, FigureColor enemyColor) {
		executor = new StackExecutor();
		this.player = player;
		this.maxDepth = maxDepth;
		this.playerColor = playerColor;
		this.enemyColor = enemyColor;
	}
	
	public StackExecutor getExecutor(){
		return executor;
	}

	public void updateFigureCounts() {
		figureCountWhite = player.getPlayfield().getFigureQuantity(FigureColor.WHITE);
		figureCountRed = player.getPlayfield().getFigureQuantity(FigureColor.RED);
	}
	public synchronized int getFigureQuantity(FigureColor color) {
		switch(color){
		case WHITE:
			return figureCountWhite;
		case RED:
			return figureCountRed;
		}
		return 0;
	}
	
	public MiniMaxMTPlayer getPlayer(){
		return player;
	}
	
	public void logError(String msg) {
		player.csl.printError(msg, player.getName());
	}
}
