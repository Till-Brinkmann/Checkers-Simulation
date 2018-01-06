package opponent;


import checkers.NNMove;
import checkers.NNPlayfield;
import checkers.Player;
import generic.List;
import gui.CommandListener;
import gui.NNGUI;
/**
 * Version of the MiniMaxABPlayer for nn training.
 */
public class MiniMaxAB implements Player{
	/**
	 * Essentially a data structure to provide information that every MiniMaxABTask uses.
	 */
	private class MiniMaxABManager {
		/**
		 * Reference to the original playfield to acquire figure counts.
		 */
		private final NNPlayfield field;
		/**
		 * 
		 */
		public final byte maxDepth;
		
		public final boolean color;
		
		private byte figureCountRed;
		private byte figureCountWhite;
		
		public MiniMaxABManager(NNPlayfield field, byte maxDepth, boolean color) {
			this.field = field;
			this.maxDepth = maxDepth;
			this.color = color;
		}
		
		public void updateFigureCounts() {
			figureCountWhite = field.getFigureQuantityOfColor(false);
			figureCountRed = field.getFigureQuantityOfColor(true);
		}
		public byte getFigureQuantityOfColor(boolean color) {
			if(color) {
				return figureCountRed;
			}
			return figureCountWhite;
		}
	}
	
	private class MiniMaxABTask{
		private MiniMaxABManager manager;
		public NNMove move;
		private NNPlayfield pf;
		
		private byte depth;
		private boolean isMaximizing;
		/**
		 * move quality
		 */
		public float value;
		public float alpha;
		public float beta;
		private float v;
		
		public MiniMaxABTask(MiniMaxABManager manager, NNMove move, NNPlayfield pf, byte depth, boolean isMaximizing, float alpha, float beta) {
			this.manager = manager;
			this.move = move;
			this.pf = pf;
			this.depth = depth;
			this.isMaximizing = isMaximizing;
			this.alpha = alpha;
			this.beta = beta;
			value = isMaximizing ? -Float.MAX_VALUE : Float.MAX_VALUE;
		}
		
		public float compute() {
			//make move
			pf.executeMove(move);
			//test for deepest depth
			if(depth >= manager.maxDepth){
				return evaluateMove();
			}
			//start computation for all available moves
			List<NNMove> moves = NNMove.getPossibleMovesFor((isMaximizing ? manager.color : !manager.color), pf);
			for(moves.toFirst(); moves.hasAccess(); moves.next()){
				v = new MiniMaxABTask(
							manager,
							moves.get(),
							pf.copy(),
							(byte) (depth+1),
							!isMaximizing,
							alpha,
							beta
					).compute();
				if(isMaximizing){
					if(v > value){
						value = v;
						if(v >= beta) {
							//beta-cut
							return value;
						}
						alpha = v;
					}
				}else{
					if(v < value) {
						value = v;
						if(v <= alpha) {
							//alpha-cut
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
			return (manager.getFigureQuantityOfColor(!manager.color)-
					pf.getFigureQuantityOfColor(!manager.color))+
					(pf.getFigureQuantityOfColor(manager.color)-
					manager.getFigureQuantityOfColor(manager.color));
		}
	}
	
	public static final byte defaultMaxDepth = 4;
	
	private MiniMaxABManager manager;
	
	protected NNPlayfield field;
	
	private float bestValue;
	private NNMove bestMove;
	
	public float alpha;
	public float beta;
	
	public final CommandListener changemaxDepth = new CommandListener() {
		@Override
		public boolean processCommand(String command, String[] args) {
			if(command.equals("set")) {
				if(args.length == 2 && args[0].equals("MMMaxDepth")) {
					//creating a new manager is the easiest way to make sure that the maxDepth does not change while a move is calculated
					//(the current Tasks all have references to the old manager)
					manager = new MiniMaxABManager(manager.field, (byte)Integer.parseInt(args[1]), manager.color);
					NNGUI.console.printCommandOutput("maxDepth set to " + manager.maxDepth);
					return true;
				}
			}
			return false;
		}
	};
	
	public MiniMaxAB() {
		NNGUI.console.addCommandListener(changemaxDepth);
	}

	@Override
	public void prepare(boolean color, NNPlayfield field) {
		this.field = field;
		manager = new MiniMaxABManager(field, defaultMaxDepth, color);
	}

	@Override
	public NNMove requestMove(){
		//reset/init variables
		bestValue = -Float.MAX_VALUE;
		alpha = -Float.MAX_VALUE;
		beta = Float.MAX_VALUE;
		//start maximizing minMaxTask for every possible move
		List<NNMove> moves = NNMove.getPossibleMovesFor(manager.color, field);
		if(moves.length == 1) {
			moves.toFirst();
			return moves.get();
		}
		//in case the manager changes while the tasks are started
		MiniMaxABManager tmpman = manager;
		tmpman.updateFigureCounts();
		float v;
		for(moves.toFirst(); moves.hasAccess(); moves.next()){
				v = new MiniMaxABTask(
							tmpman,
							moves.get(),
							field.copy(),
							(byte)1,
							//the next task is always not maximizing
							false,
							alpha,
							beta
						).compute();
			if(v > bestValue){
				bestValue = v;
				bestMove = moves.get();
			}
		}
		return bestMove;
	}
}
