package opponent;

import checkers.NNMove;
import checkers.NNPlayfield;
import checkers.Player;
import datastructs.List;

public class RandomAI implements Player{
	
	private boolean color;
	
	private NNPlayfield field;
	
	public RandomAI() {
		
	}

	@Override
	public void prepare(boolean color, NNPlayfield field) {
		this.color = color;
		this.field = field;
	}

	@Override
	public NNMove requestMove() {
		List<NNMove> moves = NNMove.getPossibleMovesFor(color, field);
		long random = Math.round(Math.random()*moves.length);
		moves.toFirst();
		for(int i = 0; i < random && moves.hasAccess(); i++) {
			moves.next();
		}
		return moves.get();
	}
}
