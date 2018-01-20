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
		double random = Math.random();
		double weight = 1/moves.length;
		for(moves.toFirst();moves.hasAccess();moves.next()) {
			if(random < weight) {
				return moves.get();
			}
			random -= weight;
		}
		return null;
	}
}
