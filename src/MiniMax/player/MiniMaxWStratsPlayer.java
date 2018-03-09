package player;

import algo.MinMaxWStratsManager;
import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import datastructs.List;
import gui.Console;
import main.StackExecutor;

public class MiniMaxWStratsPlayer implements Player{

	private GameLogic gmlc;
	private Console csl;
	
	private MinMaxWStratsManager manager;
	
	private final StackExecutor executor;
	
	public MiniMaxWStratsPlayer(GameLogic gmlc, Console csl) {
		this.gmlc = gmlc;
		this.csl = csl;
		this.executor = new StackExecutor();
	}

	@Override
	public void prepare(FigureColor color) {
		this.manager = new MinMaxWStratsManager(color);
	}

	@Override
	public void requestMove() {
		List<Move> moves = Move.getPossibleMoves(manager.playerColor, gmlc.getPlayfield());
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean acceptDraw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveInformation(String directory) {
		// TODO Auto-generated method stub
		
	}

}
