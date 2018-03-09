package opponent;

import java.util.Random;

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
		int random = new Random().nextInt(moves.length);
		int i = 0;
		for(moves.toFirst();moves.hasAccess();moves.next()) {
			if(random == i) {
				return moves.get();
			}
			i++;
		}
		return null;
	}
}
