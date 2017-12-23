package algo;

import checkers.GameLogic;
import checkers.Move;
import checkers.Playfield;
import generic.List;
/**
 * does all computations for one node in the MinMax tree
 */
public class MinMaxTask{

	private MinMaxManager manager;
	
	public Move move;
	private Playfield pf;
	
	private int depth;
	private boolean isMaximizing;
	/**
	 * move quality
	 */
	public float value;
	
	public MinMaxTask(MinMaxManager manager, Move move, Playfield pf, int depth, boolean isMaximizing) {
		this.manager = manager;
		this.move = move;
		this.pf = pf;
		this.depth = depth;
		this.isMaximizing = isMaximizing;
		value = isMaximizing ? -Float.MAX_VALUE : Float.MAX_VALUE;
	}
	
	/*
	 * @see java.util.concurrent.RecursiveAction#compute()
	 * starts children if not in last depth. if depth is equal to maxDepth (@see algo.MinMaxManager#maxDepth)
	 * just calls the parents notifyFinished callback
	 */
	public float compute() {
		//make move
		if(!GameLogic.testMove(move, pf)){
			GameLogic.testMove(move, pf);
			manager.logError("");
		}
		pf.executeMove(move);
		//test for deepest depth
		if(depth >= manager.maxDepth){
			return evaluateMove();
		}
		//start computation for all available moves
		float v;
		List<Move> moves = Move.getPossibleMoves((isMaximizing ? manager.playerColor : manager.enemyColor), pf);
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
			v = new MinMaxTask(
						manager,
						moves.get(),
						pf.copy(),
						depth+1,
						!isMaximizing
				).compute();
			if(isMaximizing ? v > value : v < value){
				value = v;
			}
		}
		//if we do not have any moves to evaluate
		if(moves.length == 0) {
			//if you can not make any moves: you lose!
			return -100f;
		}
		return value;
	}
	
	/**
	 * @return the difference of the difference of the amount of own figures at depth 0
	 * and #ownFigures of current depth and the same with #enemyFigures
	 */
	private float evaluateMove() {
		return (manager.getFigureQuantity(manager.enemyColor)-
				pf.getFigureQuantity(manager.enemyColor))+
				(pf.getFigureQuantity(manager.playerColor)-
				manager.getFigureQuantity(manager.playerColor));
	}
	
}
