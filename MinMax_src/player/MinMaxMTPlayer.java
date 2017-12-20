package player;

import algo.MinMaxManager;
import algo.MinMaxParent;
import algo.MinMaxTask;
import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import checkers.Playfield;
import generic.List;
import gui.CommandListener;
import gui.Console;

public class MinMaxMTPlayer implements Player, MinMaxParent{
	
	public static final int defaultMaxDepth = 5;
	
	private GameLogic gmlc;
	public Console csl;
	
	private MinMaxManager manager;
	
	private float bestValue;
	private Move bestMove;
	
	private int aStartedTasks;
	private int aFinishedTasks;
	private Object notifyLock;
	
	public MinMaxMTPlayer(GameLogic gmlc, Console csl) {
		this.gmlc = gmlc;
		this.csl = csl;
		notifyLock = new Object();
		csl.addCommandListener(new CommandListener() {
			@Override
			public boolean processCommand(String command, String[] args) {
				if(command.equals("set")) {
					if(args.length == 2 && args[0].equals("MMMaxDepth")) {
						//creating a new manager is the easiest way to make sure that the maxDepth does not change while a move is calculated
						//(the current Tasks all have references to the old manager)
						manager = new MinMaxManager(manager.getPlayer(), Integer.parseInt(args[1]), manager.getPlayerColor(), manager.getEnemyColor());
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
		manager = new MinMaxManager(this, defaultMaxDepth, color, color == FigureColor.RED ? FigureColor.WHITE : FigureColor.RED);
	}
	@Override
	public void requestMove(){
		//reset/init variables
		bestValue = -Float.MAX_VALUE;
		bestMove = Move.INVALID;
		aStartedTasks = 0;
		aFinishedTasks = 0;
		//start maximizing minMaxTask for every possible move
		List<Move> moves = Move.getPossibleMoves(manager.getPlayerColor(), gmlc.getPlayfield());
		//in case the manager changes while the tasks are started
		MinMaxManager tmpman = manager;
		tmpman.updateFigureCounts();
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
			manager.getFJPool().execute(
					new MinMaxTask(
							tmpman,
							this,
							moves.get(),
							gmlc.getPlayfield().copy(),
							1,
							//the next task is always not maximizing
							false
						));
		}
		//there is a task started for every move
		aStartedTasks = moves.length;
	}
	@Override
	public boolean acceptDraw() {
		return false;
	}
	@Override
	public String getName() {
		return "MiniMax algorithm based Checkers player";
	}
	
	@Override
	public void notifyFinished(MinMaxTask child) {
		synchronized(notifyLock) {
			//choose best move
			if(child.value > bestValue){
				bestValue = child.value;
				bestMove = child.move;
			}
			aFinishedTasks++;
			//if all workers finished perform best move
			if(aFinishedTasks == aStartedTasks) {
				manager.logError("Best move value: " + bestValue);
				gmlc.makeMove(bestMove);
			}
		}
	}
	
	public Playfield getPlayfield(){
		return gmlc.getPlayfield();
	}
}
