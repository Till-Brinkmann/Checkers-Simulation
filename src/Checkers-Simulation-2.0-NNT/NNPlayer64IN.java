

import checkers.NNMove;
import checkers.NNPlayfield;
import datastructs.List;
import nn.NN;
import nn.NNPlayer;
import training.NNSpecification;

public class NNPlayer64IN implements NNPlayer{

	public NN net;
	
	private NNPlayfield field;
	
	public int fitness;
	public boolean didValidMove;
	
	private double[] inputVector;
	
	public int rightmoves;
	
	private double[] outputVector;
	
	public int outputSize = 32;
	public int inputSize = 64;
	
	private boolean color;
	
	private NNSpecification specs;
	public NNPlayer64IN(NNSpecification specs) {
		this.specs = specs;
		net = new NN(specs);
	}

	@Override
	public void prepare(boolean color, NNPlayfield field){
		this.color = color;
		this.field = field;
		inputVector = new double[inputSize];
	}
	
	@Override
	public NNMove requestMove(){
		byte f = 0;
		for(int y = 0;y < 8; y++){
            for(int x = 0;x < 8; x++){
            	if(y%2 == 1) {
            		if(x%2 == 1) {
            			addValueToInputVector(f);
    					f++;
            		}
            	}
            	else {
            		if(x%2 == 0) {
            			addValueToInputVector(f);
    					f++;
            		}
            	}
            }
		}
		if(color){
			outputVector = net.run(inputVector);
		}
		else {
			double[] invertedInputVector = new double[64];
			for(int i = 0; i < inputVector.length/2; i++){
				invertedInputVector[mirrorField(i)] = inputVector[i];
			}
			for(int i = inputVector.length/2; i < inputVector.length/2; i++){
				invertedInputVector[mirrorField(i-inputVector.length/2)+inputVector.length/2] = inputVector[i];
			}
			outputVector = net.run(invertedInputVector);
		}
		//add fitness based on how near it is to making a valid move
		List<NNMove> possibleMoves = NNMove.getPossibleMovesFor(color, field);
		for(byte output = 0; output < 32; output++) {
			boolean isValidDestination = false;
			boolean isValidFigurePosition = false;
			for(possibleMoves.toFirst();possibleMoves.hasAccess();possibleMoves.next()) {
				//if the nn has high values on the positions were moves are valid it gets a higher fitness
				//high values for positions that are not valid give higher minus points
				if(output == possibleMoves.get().to) {
					isValidDestination = true;
				}
				if(output == possibleMoves.get().from) {
					isValidFigurePosition = true;
				}
				if(isValidDestination && isValidFigurePosition) break;
			}
			fitness += isValidDestination ? outputVector[output] : -outputVector[output];
			fitness += isValidFigurePosition ? outputVector[output+32] : -outputVector[output+32];
		}
		
		NNMove bestMove = NNMove.INVALID;
		NNMove move = NNMove.INVALID;
		double max = Integer.MIN_VALUE;
        int choiceField = 0;
        for(int i = 0, fieldIndex = 0; i < 32; i++) {
        	fieldIndex = color ? i : mirrorField(i);
        	if(outputVector[fieldIndex] > max){
        		max = outputVector[fieldIndex];
        		choiceField = fieldIndex;
        	}
        }
        //test which figures can reach this field
        List<NNMove> availableMoves = new List<NNMove>();
        for(byte pos : field.getFigurePositionsOfType(color)){
        	move = NNMove.createMove(pos, (byte)choiceField, field);
        	if(NNMove.testMove(move, field)) {
        		availableMoves.append(move);
        	}
        }
        if(availableMoves.length != 0) {
        	//NNGUI.console.printInfo("There are available moves");
        	//there is a move that ends at this field
            //find out if a figure that can do the move is on the field which is chosen by the nn to be the move start
            max = Integer.MIN_VALUE;
    		double score = 0;
        	for(availableMoves.toFirst();availableMoves.hasAccess();availableMoves.next()){
    			move = availableMoves.get();
    			score = color ?
    					outputVector[move.from + 32] :
    					outputVector[mirrorField(move.from) + 32];
    			if(score > max){
    				bestMove = move;
    				max = score;
    			}
    		}
        }
        if(bestMove == NNMove.INVALID){
        	fitness -= 100;
        } else {
        	rightmoves++;
        	didValidMove = true;
        	fitness += 100;
        	if(bestMove.isJump()) {
        		fitness += 25;
        	}
        }
        return bestMove;
	}
	
	public void addValueToInputVector(byte f) {
		if(field.isOccupied(f)) {
			if(field.isOccupiedByColor(f, true)) {
				if(field.isKing(f)) {
					inputVector[f+32] = 1;
					inputVector[f] = 0;
				}
				else {
					inputVector[f] = 1;	
					inputVector[f+32] = 0;
				}
			}
			else {
				if(field.isKing(f)) {
					inputVector[f+32] = -1;	
					inputVector[f] = 0;	
				}
				else {
					inputVector[f] = -1;	
					inputVector[f+32] = 0;
				}
			}			
		}
		else {
			inputVector[f] = 0;
			inputVector[f+32] = 0;
		}
	}
	
	private int mirrorField(int field) {
		return Math.abs(field-31);
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
		return new NNPlayer64IN(specs);
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
