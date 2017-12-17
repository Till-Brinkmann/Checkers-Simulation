package player;

import java.util.concurrent.ExecutionException;

import algo.MinMaxManager;
import algo.MinMaxTask;
import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Move.MoveType;
import checkers.Player;
import checkers.Playfield;
import generic.List;
import gui.Console;

public class MinMaxMTPlayer implements Player{
	
	public static final int maxDepth = 5;
	
	private GameLogic gmlc;
	public Console csl;
	
	private MinMaxManager manager;
	
	private List<Move> moves;
	private List<MinMaxTask> tasks;
	public MinMaxMTPlayer(GameLogic gmlc, Console csl) {
		this.gmlc = gmlc;
		this.csl = csl;
	}

	@Override
	public void prepare(FigureColor color) {
		manager = new MinMaxManager(this, maxDepth, color, color == FigureColor.RED ? FigureColor.WHITE : FigureColor.RED);
	}
	
	public void requestMove(){
		tasks = new List<MinMaxTask>();
		//start maximizing minMaxTask for every possible move
		moves = Move.getPossibleMoves(manager.getPlayerColor(), gmlc.getPlayfield());
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
			tasks.append(
					new MinMaxTask(
							manager,
							moves.get(),
							gmlc.getPlayfield().copy(),
							1, 
							//the next task is not maximizing
							false
				));
			tasks.toLast();
			manager.getFJPool().execute(tasks.get());
		}
		//choose best move
		float bestValue = -Float.MAX_VALUE;
		Move bestMove = new Move(MoveType.INVALID);
		try {
			for(tasks.toFirst(),moves.toFirst();tasks.hasAccess();tasks.next(),moves.next()){
				float current;
				tasks.get().helpQuiesce();
				current = tasks.get().get().floatValue();
				if(current > bestValue){
					bestValue = current;
					bestMove = moves.get();
				}
			}
			csl.printInfo(new Boolean(manager.getFJPool().isQuiescent()).toString() + " drhgfkljs", "MinMaxPlayer");
		} catch (InterruptedException | ExecutionException e) {
			//This should never happen
			manager.logError("A task.get() got interrupted or execution failed! Fix ya code!");
			e.printStackTrace();
		}
		//perform best move
		gmlc.makeMove(bestMove);
	}

	@Override
	public boolean acceptDraw() {
		return false;
	}
	
	@Override
	public String getName() {
		return "MiniMax algorithm based Checkers player";
	}

	public Playfield getPlayfield() {
		return gmlc.getPlayfield();
	}

}
