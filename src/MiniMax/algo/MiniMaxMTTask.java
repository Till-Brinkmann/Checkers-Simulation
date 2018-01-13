package algo;

import task.Task;
import checkers.Move;
import checkers.Playfield;
import datastructs.List;
/**
 * does all computations for one node in the MinMax tree
 */
public class MiniMaxMTTask implements Task, MiniMaxParent{

	private MiniMaxMTManager manager;
	private MiniMaxParent parent;
	
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
	
	public MiniMaxMTTask(MiniMaxMTManager manager, MiniMaxParent parent, Move move, Playfield pf, int depth, boolean isMaximizing) {
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
	public synchronized void compute() {
		//make move
		pf.executeMove(move);
		//test for deepest depth
		if(depth >= manager.maxDepth){
			value = evaluateMove();
			parent.notifyFinished(this);
			return;
		}
		//start computation for all available moves
		List<Move> moves = Move.getPossibleMoves((isMaximizing ? manager.playerColor : manager.enemyColor), pf);
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
			manager.getExecutor().execute(
					new MiniMaxMTTask(
							manager,
							this,
							moves.get(),
							pf.copy(),
							depth+1,
							!isMaximizing
				));
		}
		synchronized(notifyLock) {
			aStartedTasks = moves.length;
			
			//if we do not have any moves to evaluate
			if(aStartedTasks == 0) {
				//if you can not make any moves you lose!
				value = -100;
				//TODO remove again if not better
				parent.notifyFinished(this);
				return;
			}
			//for the unlikely case that every child has finished and called notifyFinished before this line is reached
			if(aFinishedTasks == aStartedTasks){
				parent.notifyFinished(this);
			}
		}
	}
	@Override
	//TODO(?) you could call this method with only a float as parameter,
	//but then you will have to find another solution for referencing the parent
	//(because the interface would not be applicable anymore)
	public synchronized void notifyFinished(MiniMaxMTTask child) {
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
			return (manager.getFigureQuantity(manager.enemyColor)-
					pf.getFigureQuantity(manager.enemyColor))+
					(pf.getFigureQuantity(manager.playerColor)-
					manager.getFigureQuantity(manager.playerColor));
	}
	
}
