
import checkers.NNMove;
import checkers.NNPlayfield;
import datastructs.List;
import gui.NNGUI;
import nn.NN;
import nn.NNPlayer;
import training.NNSpecification;

public class NNPlayer32IN implements NNPlayer {

	public NN net;
	
	private NNPlayfield field;
	
	public int fitness;
	
	public int outputSize = 32;
	public int inputSize = 32;
	
	private double[] outputVector;
	private boolean color;

	public int rightmoves;
	public NNSpecification specs;
	public NNPlayer32IN(NNSpecification specs) {
		this.specs = specs;
		net = new NN(specs);
	}

	@Override
	public void prepare(boolean color, NNPlayfield field){
		this.color = color;
		this.field = field;
	}

	@Override
	public NNMove requestMove() {
		//runs the neural network with a specific input which consist of an 32 vector, which represents the board
		//with different values(King: 3, Figure 1, Empty: 0). Enemy figures are obtaining the negated values.
		outputVector = net.run(createInputVector(inputSize));
		//after the output was determined the index of the two biggest values in the output are extracted.
		int best[] = searchForBiggestValues();
		//
		NNMove move = NNMove.INVALID;
		//Creates a list with all possible moves.
		List<NNMove> possibleMoves = NNMove.getPossibleMovesFor(color, field);
		//test if the fields with the biggest values are a legal move.
		for(possibleMoves.toFirst();possibleMoves.hasAccess();possibleMoves.next()) {
			if(possibleMoves.get().from == best[0]) {
				if(possibleMoves.get().to == best[1]) {
					move = possibleMoves.get();
					rightmoves++;
				}
			}
			if(possibleMoves.get().from == best[1]) {
				if(possibleMoves.get().to == best[0]) {
					move = possibleMoves.get();
					rightmoves++;
				}
			}
		}
		//for evaluation purposes(setting the fitness of the NN)
		if(move != NNMove.INVALID) {
			fitness += 100;
			if(move.isJump()) {
				fitness += 100;
			}
		}
		else {
			//tests, if the field chosen by the nn is having a good attempt.
//			if(field.isOccupiedByColor((byte)best[0], color)) {
//				fitness += 10;
//				if(!field.isOccupied((byte)best[1])) {
//					fitness += 10;
//				}
//			}
//			if(field.isOccupiedByColor((byte)best[1], color)) {
//				fitness += 10;
//				if(!field.isOccupied((byte)best[0])) {
//					fitness += 10;
//				}
//			}
		}
		

		
		return move;
	}
	private double[] createInputVector(int length) {
		double[] inputVector = new double[length];
		byte f = 0;
		if(color) {
			for(int y = 0;y < 8; y++){
	            for(int x = 0;x < 8; x++){
	            	if(y%2 == 1) {
	            		if(x%2 == 1) {
	            			inputVector[f] = setInputValue(f);
	            		}
	            	}
	            	else {
	            		if(x%2 == 0) {
	            			inputVector[f] = setInputValue(f);
	            		}
	            	}
	            }
	            
			}
		}
		else {
			for(int y = 8;y >= 0; y--){
	            for(int x = 8;x >= 0; x--){
	            	if(y%2 == 1) {
	            		if(x%2 == 1) {
	            			inputVector[f] = setInputValue(f);
	            		}
	            	}
	            	else {
	            		if(x%2 == 0) {
	            			inputVector[f] = setInputValue(f);
	            		}
	            	}
	            }
	            
			}
		}
		return inputVector;
	}

	private int[] searchForBiggestValues() {
		double[] bestValues = {-1,-1 };
		int[] bestField = new int[2];
		for(int i = 0; i < outputVector.length; i++) {
			if(outputVector[i] > bestValues[1]) {
				if(outputVector[i] > bestValues[0]) {
					bestValues[0] = outputVector[i];
					bestField[0] = i;
				}
				else {
					bestValues[1] = outputVector[i];
					bestField[1] = i;
				}
			}
		}
		return bestField;
	}
	private int setInputValue(byte f) {
		if(field.isOccupied(f)) {
			if(!field.isKing(f)){
				if(field.isOccupiedByColor(f, color) == color) {
					return 1;
				}
				return -1;
			}
			else {
				if(field.isOccupiedByColor(f, color) == color) {
					return 3;
				}
				return -3;
			}
		}
		return 0;
		
	}
	public double[] getOutput() {
		return outputVector;
	}

	@Override
	public int getInputSize() {
		return inputSize;
	}

	@Override
	public int getOutputSize() {
		return outputSize;
	}

	@Override
	public NNPlayer clone() {
		return new NNPlayer32IN(specs);
	}

	@Override
	public NN getNet() {
		// TODO Auto-generated method stub
		return net;
	}

	@Override
	public int getFitness() {
		return fitness;
	}

	@Override
	public void addFitness(int value) {
		fitness += value;
	}

	@Override
	public void setFitness(int value) {
		fitness = value;
	}

	@Override
	public int getRightMoves() {
		return rightmoves;
	}

	@Override
	public void setRightMoves(int value) {
		rightmoves = value;
	}

}
