package algo;

import checkers.GameLogic;
import checkers.Move;
import checkers.Playfield;
import generic.List;
/**
 * does all computations for one node in the MinMax tree
 */
public class MinMaxABTask{

	private MinMaxABManager manager;
	
	public Move move;
	private Playfield pf;
	
	private int depth;
	private boolean isMaximizing;
	/**
	 * move quality
	 */
	public float value;
	public float alpha;
	public float beta;
	public Float[] information;
	private Float v;
	private float a;	
	private float b;
	
	public MinMaxABTask(MinMaxABManager manager, Move move, Playfield pf, int depth, boolean isMaximizing, float alpha, float beta) {
		this.manager = manager;
		this.move = move;
		this.pf = pf;
		this.depth = depth;
		this.isMaximizing = isMaximizing;
		this.alpha = alpha;
		this.beta = beta;
		value = isMaximizing ? -Float.MAX_VALUE : Float.MAX_VALUE;
		//value, alpha, beta
		information = new Float[3];
	}
	
	/*
	 * @see java.util.concurrent.RecursiveAction#compute()
	 * starts children if not in last depth. if depth is equal to maxDepth (@see algo.MinMaxManager#maxDepth)
	 * just calls the parents notifyFinished callback
	 */
	public Float compute() {
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
		List<Move> moves = Move.getPossibleMoves((isMaximizing ? manager.playerColor : manager.enemyColor), pf);
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
			v = new MinMaxABTask(
						manager,
						moves.get(),
						pf.copy(),
						depth+1,
						!isMaximizing,
						alpha,
						beta
				).compute();
			if(isMaximizing && v > value){
				value = v;
				if(v >= beta) {
					//beta-cut
					manager.incrementBetaCut();
					return value;
				}
				alpha = v;
			}
			if(!isMaximizing && v < value) {
				value = v;
				if(v <= alpha) {
					//alpha-cut
					manager.incrementAlphaCut();
					return value;
				}
				beta = v;
			}
		}
		//if we do not have any moves to evaluate
		if(moves.length == 0) {
			//if you can not make any moves: you lose!
			//return -100f;
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
