package algo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

import checkers.Move;
import checkers.Playfield;
import generic.List;
/**
 * does all computations for one node in the MinMax tree
 */
public class MinMaxTask extends RecursiveTask<Float>{

	private MinMaxManager manager;
	private Move move;
	//TODO moves vielleicht lokal in compute
	private List<Move> moves;
	private List<MinMaxTask> tasks;
	private Playfield pf;
	private int depth;
	private boolean isMaximizing;
	/**
	 * move quality
	 */
	private float value;
	
	public MinMaxTask(MinMaxManager manager, Move move, Playfield pf, int depth, boolean isMaximizing) {
		this.manager = manager;
		this.move = move;
		this.pf = pf;
		this.depth = depth;
		this.isMaximizing = isMaximizing;
		moves = new List<Move>();
		tasks = new List<MinMaxTask>();
	}
	
	/*
	 * @see java.util.concurrent.RecursiveTask#compute()
	 * evaluates moves
	 */
	@Override
	protected Float compute() {
		if(depth >= manager.maxDepth){
			return new Float(evaluateMove());
		}
		//make move
		pf.executeMove(move);
		//start computation for all available moves
		moves = Move.getPossibleMoves((isMaximizing ? manager.getPlayerColor() : manager.getEnemyColor()), pf);
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
			tasks.append(
					new MinMaxTask(
							manager,
							moves.get(),
							pf.copy(),
							depth+1,
							!isMaximizing
				));
			tasks.toLast();
			manager.getFJPool().execute(tasks.get());
		}
		//get max/min result(depending on state)
		try {
			for(tasks.toFirst();tasks.hasAccess();tasks.next()){
				float current = tasks.get().get().floatValue();
				if(isMaximizing ? current > value : current < value){
					value = current;
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			//This should never happen
			manager.logError("A task.get() got interrupted or execution failed! Fix ya code!");
			e.printStackTrace();
		}
		//return value
		return new Float(value);
	}
	/**
	 * 
	 * @return the difference of the difference of the amount of own figures at depth 0
	 * and #ownFigures of current depth and the same with #enemyFigures
	 */
	private float evaluateMove() {
		return (manager.getPlayer().getPlayfield().getFigureQuantity(manager.getEnemyColor())-
				pf.getFigureQuantity(manager.getEnemyColor()))+
				(pf.getFigureQuantity(manager.getPlayerColor())-
				manager.getPlayer().getPlayfield().getFigureQuantity(manager.getPlayerColor()));
	}

}
