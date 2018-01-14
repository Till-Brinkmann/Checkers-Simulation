package algo;

import checkers.GameLogic;
import checkers.Move;
import checkers.Playfield;
import datastructs.List;
/**
 * does all computations for one node in the MinMax tree
 */
public class MiniMaxABTask{

	private MiniMaxManager manager;
	private MiniMaxABManager abm;
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
	private float v;
	
	public MiniMaxABTask(MiniMaxManager manager, MiniMaxABManager abm, Move move, Playfield pf, int depth, boolean isMaximizing, float alpha, float beta) {
		this.manager = manager;
		this.abm = abm;
		this.move = move;
		this.pf = pf;
		this.depth = depth;
		this.isMaximizing = isMaximizing;
		this.alpha = alpha;
		this.beta = beta;
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
			manager.logError("BIG FAIL");
		}
		pf.executeMove(move);
		//test for deepest depth
		if(depth >= manager.maxDepth){
			return evaluateMove();
		}
		//start computation for all available moves
		List<Move> moves = Move.getPossibleMoves((isMaximizing ? manager.playerColor : manager.enemyColor), pf);
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
			v = new MiniMaxABTask(
						manager,
						abm,
						moves.get(),
						pf.copy(),
						depth+1,
						!isMaximizing,
						alpha,
						beta
				).compute();
			if(isMaximizing){
				if(v > value){
					value = v;
					if(v >= beta) {
						//beta-cut
						abm.incrementBetaCut();
						return value;
					}
					alpha = v;
				}
			}else{
				if(v < value) {
					value = v;
					if(v <= alpha) {
						//alpha-cut
						abm.incrementAlphaCut();
						return value;
					}
					beta = v;
				}
			}
		}
		//if we do not have any moves to evaluate
		if(moves.length == 0) {
			//you can not make any moves: you lose!
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
