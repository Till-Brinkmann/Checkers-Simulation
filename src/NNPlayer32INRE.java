
import java.util.Arrays;
import java.util.Comparator;

import checkers.NNMove;
import checkers.NNPlayfield;
import datastructs.List;
import nn.NN;
import nn.NNPlayer;
import training.NNSpecification;

public class NNPlayer32INRE implements NNPlayer {

	public NN net;
	
	private NNPlayfield field;
	
	public int fitness;
	
	public int outputSize = 32;
	public int inputSize = 32;
	
	private double[] outputVector;
	private boolean color;

	public int rightmoves;
	public NNSpecification specs;
	public NNPlayer32INRE(NNSpecification specs) {
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
		NNMove move = NNMove.INVALID;
		//runs the neural network with a specific input which consist of an 32 vector, which represents the board
		//with different values(King: 3, Figure 1, Empty: 0). Enemy figures are obtaining the negated values.
		outputVector = net.run(createInputVector(inputSize));
		byte[] sortedOutputIndexes = this.sortOutputByValue();
		//what is the first index that has a figure on the corresponding field.
		for(byte i = 0; i < sortedOutputIndexes.length; i++) {
			if(field.isOccupiedByColor(sortedOutputIndexes[i], color)) {
				byte chosenField = sortedOutputIndexes[i];
				List<NNMove> possibleMoves = NNMove.getPossibleMoves(chosenField, color, field);
				switch(possibleMoves.length) {
				case 0:
					break;
				case 1:
					possibleMoves.toFirst();
					return possibleMoves.get();
				//we have more than one move, so we have to choose the one with the highest value.
				default:
					double highest = -Double.MAX_VALUE;
					for(possibleMoves.toFirst(); possibleMoves.hasAccess(); possibleMoves.next()) {
						if(outputVector[possibleMoves.get().to] > highest) {
							move = possibleMoves.get();
							highest = outputVector[possibleMoves.get().to];
						}
					}
					return move;
				}
			}
				
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
	private class ValueIndexPair {
		public final double value;
		public final byte index;
		public ValueIndexPair(final double value, final byte index) {
			this.value = value;
			this.index = index;
		}
	}
	private byte[] sortOutputByValue() {
		byte[] indexes = new byte[outputVector.length];
		ValueIndexPair[] pairs = new ValueIndexPair[outputVector.length];
		//create pairs
		for(int i = 0; i < pairs.length; i++) {
			pairs[i] = new ValueIndexPair(outputVector[i], (byte)i);
		}
		Arrays.sort(pairs, new Comparator<ValueIndexPair>() {

			@Override
			public int compare(ValueIndexPair arg0, ValueIndexPair arg1) {
				return arg0.value > arg1.value ? -1 : arg0.value < arg1.value ? 1 : 0;
			}
			
		});
		for(int i = 0; i < indexes.length; i++) {
			indexes[i] = pairs[i].index;
		}
		return indexes;
	}
	/**
	 * Searches for the biggest value on field and returns it's index.
	 * @return index
	 */
	private int searchForBiggestValue() {
		double[] bestValues = {-1,-1 };
		int bestField = 0;
		for(int i = 0; i < outputVector.length; i++) {
			if(outputVector[i] > bestValues[1]) {
				if(outputVector[i] > bestValues[0]) {
					bestValues[0] = outputVector[i];
					bestField = i;
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
		return new NNPlayer32INRE(specs);
	}

	@Override
	public NN getNet() {
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
