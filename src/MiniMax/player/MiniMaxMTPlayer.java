package player;

import algo.MiniMaxMTManager;
import algo.MiniMaxMTTask;
import algo.MiniMaxParent;
import checkers.Figure.FigureColor;
import datastructs.List;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import checkers.Playfield;
import gui.CommandListener;
import gui.Console;

public class MiniMaxMTPlayer implements Player, MiniMaxParent{
	
	public static final int defaultMaxDepth = 5;
	
	private GameLogic gmlc;
	public Console csl;
	
	private MiniMaxMTManager manager;
	
	private float bestValue;
	private Move bestMove;
	
	private int aStartedTasks;
	private int aFinishedTasks;
	private Object notifyLock;
	
	public MiniMaxMTPlayer(GameLogic gmlc, Console csl) {
		this.gmlc = gmlc;
		this.csl = csl;
		notifyLock = new Object();
		csl.addCommandListener(new CommandListener() {

			@Override
			public boolean processCommand(String command, String[] args) {
				if(command.equals("set")) {
					if(args.length == 2 && args[0].equals("MMMaxDepth")) {
						if(manager == null) return false;
						//creating the new manager is the easiest way to make sure that the maxDepth does not change while a move is calculated
						//(the current calculation all have references to the old manager)
						manager = new MiniMaxMTManager(manager.getPlayer(), Integer.parseInt(args[1]), manager.playerColor, manager.enemyColor);
						csl.printCommandOutput("maxDepth set to " + manager.maxDepth);
						return true;
					}
				}
				return false;
			}
			
		});
	}

	@Override
	public void prepare(FigureColor color) {
		manager = new MiniMaxMTManager(this, defaultMaxDepth, color, color == FigureColor.RED ? FigureColor.WHITE : FigureColor.RED);
	}
	@Override
	public synchronized void requestMove(){
		//reset/init variables
		bestValue = -Float.MAX_VALUE;
		bestMove = Move.INVALID;
		aStartedTasks = 0;
		aFinishedTasks = 0;
		//start maximizing minMaxTask for every possible move
		List<Move> moves = Move.getPossibleMoves(manager.playerColor, gmlc.getPlayfield());
		if(moves.length == 1){
			//nothing to test here, we have only one option
			moves.toFirst();
			gmlc.makeMove(moves.get());
			return;
		}
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
			manager.getExecutor().execute(
					new MiniMaxMTTask(
							manager,
							this,
							moves.get(),
							gmlc.getPlayfield().copy(),
							1,
							//the next task is always not maximizing
							false
					));
		}
		synchronized(notifyLock) {
			//there is a task started for every move
			aStartedTasks = moves.length;
			//for the unlikely case that every child has finished and called notifyFinished before this line is reached
			if(aFinishedTasks == aStartedTasks){
				gmlc.makeMove(bestMove);
			}
		}
	}
	@Override
	public synchronized void notifyFinished(MiniMaxMTTask child) {
		synchronized(notifyLock) {
			//choose best move
			if(child.value > bestValue){
				bestValue = child.value;
				bestMove = child.move;
			}
			aFinishedTasks++;
			//if all workers finished perform best move
			if(aFinishedTasks == aStartedTasks) {
				gmlc.makeMove(bestMove);
			}
		}
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

	@Override
	public void saveInformation(String path) {
		// TODO Auto-generated method stub
		
	}

}
