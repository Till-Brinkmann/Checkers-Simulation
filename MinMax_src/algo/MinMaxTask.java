package algo;

import java.util.concurrent.RecursiveAction;

import checkers.Move;
import checkers.Playfield;
import generic.List;
/**
 * does all computations for one node in the MinMax tree
 */
public class MinMaxTask extends RecursiveAction implements MinMaxParent{

	private MinMaxManager manager;
	private MinMaxParent parent;
	
	public Move move;
	private Playfield pf;
	
	private int depth;
	private boolean isMaximizing;
	
	private int aStartedTasks;
	private int aFinishedTasks;
	private Object notifyLock;
	/**
	 * move quality
	 */
	public float value;
	
	public MinMaxTask(MinMaxManager manager, MinMaxParent parent, Move move, Playfield pf, int depth, boolean isMaximizing) {
		this.manager = manager;
		this.parent = parent;
		this.move = move;
		this.pf = pf;
		this.depth = depth;
		this.isMaximizing = isMaximizing;
		aFinishedTasks = 0;
		notifyLock = new Object();
		value = isMaximizing ? -Float.MAX_VALUE : Float.MAX_VALUE;
	}
	
	/*
	 * @see java.util.concurrent.RecursiveAction#compute()
	 * starts children if not in last depth. if depth is equal to maxDepth (@see algo.MinMaxManager#maxDepth)
	 * just calls the parents notifyFinished callback 
	 */
	@Override
	protected void compute() {
		//make move
		pf.executeMove(move);
		//test for deepest depth
		if(depth >= manager.maxDepth){
			value = evaluateMove();
			parent.notifyFinished(this);
			return;
		}
		//start computation for all available moves
		List<Move> moves = Move.getPossibleMoves((isMaximizing ? manager.getPlayerColor() : manager.getEnemyColor()), pf);
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
			manager.getFJPool().execute(
					new MinMaxTask(
							manager,
							this,
							moves.get(),
							pf.copy(),
							depth+1,
							!isMaximizing
				));
		}
		//TODO maybe aStartedTasks has to be synchronized with the lock
		aStartedTasks = moves.length;
		//if we do not have any moves to evaluate
		if(aStartedTasks == 0) {
			manager.logError("ZERO MOVESSSSSSSSSSSSSS!!");
			//if you can not make any moves you lose!
			value = -100;
			parent.notifyFinished(this);
		}
	}
	@Override
	//TODO(?) you could call this method with only a float as parameter,
	//but then you will have to find another solution for referencing the parent
	//(because the interface would not be applicable anymore)
	public void notifyFinished(MinMaxTask child) {
		synchronized(notifyLock) {
			//get max/min result(depending on state)
			if(isMaximizing ? (child.value > value) : (child.value < value)){
				value = child.value;
			}
			aFinishedTasks++;
			//if all tasks completed, notify parent
			if(aFinishedTasks == aStartedTasks){
				parent.notifyFinished(this);
			}
		}
	}
	
	/**
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
