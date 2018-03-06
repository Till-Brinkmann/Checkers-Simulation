import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import checkers.Figure;
import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.GameLogic;
import checkers.Move;
import checkers.Move.MoveType;
import checkers.Player;
import datastructs.List;
import gui.Console;
import json.JSONArray;
import json.JSONObject;
import json.JSONTokener;

public class NNPlayer1 implements Player {
	
	public class ReducedNN{
		
		public double[][] afterInputWeights;
		public double[][][] hiddenWeights;
		public double[][] toOutputWeights;
		public double[][] bias;
	    private double sigoffset;
	    private double sigscale;
	    
	    
	    
	    public ReducedNN() {
	    }
	    
	    public double[] run(double[] inputVector){
	        //add input bias
	        double[] ffVector; //= vectorAdd(inputVector, bias[0]);
	        //calculate values of the first hidden layer
	        ffVector = vector_matrix_multiplication(inputVector, afterInputWeights);
	        //sigmoid values and add bias for first hiddenlayer
	        for (int x = 0; x < ffVector.length; x++){
	            ffVector[x] = sigmoid(ffVector[x] + bias[0][x]);
	        }
	        //feed forward through all hidden layers
	        for (int i = 0; i < hiddenWeights.length; i++){
	            ffVector = vector_matrix_multiplication(ffVector, hiddenWeights[i]);
	            for (int x = 0; x < ffVector.length; x++){
	                ffVector[x] = sigmoid(ffVector[x] + bias[i + 1][x]);
	            }
	        }
	        //calculate output values
	        ffVector = vector_matrix_multiplication(ffVector, toOutputWeights);
	        for (int x = 0; x < ffVector.length; x++){
	        	ffVector[x] = sigmoid(ffVector[x] /*+ bias[bias.length - 1][x]*/);
	        }
	        return ffVector;
	    }
	    
	    private double sigmoid(double x) {
	        return (1/( 1 + Math.pow(Math.E,(-x))) + sigoffset) * sigscale;
	    }
	    
	    private double[] vector_matrix_multiplication(double[] vector, double[][] matrix){
	        double[] resultVector = new double[matrix.length];
	        for (int i = 0; i < resultVector.length; i++){
	            for (int x = 0; x < vector.length; x++){
	                resultVector[i] += vector[x] * matrix[i][x];
	            }
	        }
	        return resultVector;
	    }
	    
	    public void load(File nnfile) {
	    	char[] chars = new char[(int)nnfile.length()];
			try {
				FileReader fileReader = new FileReader(nnfile);
				fileReader.read(chars);
				fileReader.close();
				JSONObject nnobject = new JSONObject(String.valueOf(chars));
				//sigmoid modifications
				sigoffset = nnobject.getDouble("Sigmoid Offset");
				sigscale = nnobject.getDouble("Sigmoid Scale");
				//weights
				JSONArray array = nnobject.getJSONArray("AfterInputWeights");
				//last dimension is 1 because it is set again later anyway
				afterInputWeights = new double[array.length()][1];
				JSONArray innerArray;
				for(int i = 0; i < array.length(); i++) {
					innerArray = array.getJSONArray(i);
					afterInputWeights[i] = new double[innerArray.length()];
					for(int j = 0; j < innerArray.length(); j++) {
						nn.afterInputWeights[i][j] = innerArray.getDouble(j);
					}
				}
				JSONArray innerArray2;
				array = nnobject.getJSONArray("HiddenWeights");
				hiddenWeights = new double[array.length()][1][1];
				for(int i = 0; i < array.length(); i++) {
					innerArray = array.getJSONArray(i);
					hiddenWeights[i] = new double[innerArray.length()][1]; 
					for(int j = 0; j < innerArray.length(); j++) {
						innerArray2 = innerArray.getJSONArray(j);
						hiddenWeights[i][j] = new double[innerArray2.length()];
						for(int k = 0; k < innerArray2.length(); k++) {
							nn.hiddenWeights[i][j][k] = innerArray2.getDouble(k);
						}
					}
				}
				array = nnobject.getJSONArray("ToOutputWeights");
				toOutputWeights = new double[array.length()][1];
				for(int i = 0; i < array.length(); i++) {
					innerArray = array.getJSONArray(i);
					toOutputWeights[i] = new double[innerArray.length()];
					for(int j = 0; j < innerArray.length(); j++) {
						nn.toOutputWeights[i][j] = innerArray.getDouble(j);
					}
				}
				//bias
				array = nnobject.getJSONArray("Bias");
				bias = new double[array.length()][1];
				for(int i = 0; i < array.length(); i++) {
					innerArray = array.getJSONArray(i);
					bias[i] = new double[innerArray.length()];
					for(int j = 0; j < innerArray.length(); j++) {
						nn.bias[i][j] = innerArray.getDouble(j);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
	
	private ReducedNN nn;
	
	private GameLogic gmlc;
	
	private FigureColor color;
	private enum NNPlayerTypes{NN32IN, NN64IN ,NN32INRE };
	private NNPlayerTypes type;
	private double[] inputVector;
	private double[] invertedInputVector;
	private double[] outputVector;
	
	public NNPlayer1(GameLogic gmlc, Console csl) throws FileNotFoundException {
		this.gmlc = gmlc;
		//TODO verschiedene Hiddenlayergrößen zulassen (nur Output und Input müssen gleich groß sein) 
		nn = new ReducedNN();
		//load the nn file
		File nnfile = new File("resources/NNPlayer1Info").listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String name) {
				if(name.endsWith(".json")) return true;
				return false;
			}			
		})[0];
		FileInputStream reader = new FileInputStream(nnfile);
        JSONObject object = new JSONObject(new JSONTokener(reader));
		switch(object.getString("NNPlayer Name")) {
		case "NNPlayer32IN":
			type = NNPlayerTypes.NN32IN;
			break;
		case "NNPlayer32INRI":
			type = NNPlayerTypes.NN32INRE;
			break;
		case "NNPlayer64IN":
			type = NNPlayerTypes.NN64IN;
			break;
		}
		int divisor;
		if(type == NNPlayerTypes.NN64IN) {
			divisor = 1;
		}
		else {
			divisor = 2;
		}
		inputVector = new double[64 / divisor];
		invertedInputVector = new double[64 / divisor];
		outputVector = new double[64 / divisor];			
		nn.load(nnfile);
	}


	@Override
	public void prepare(FigureColor color) {
		this.color = color;	
	}

	@Override
	public void requestMove() {
		//initialize input 	
		int field = 0;
		for(int y = 0;y < gmlc.getPlayfield().SIZE; y++){
            for(int x = 0;x < gmlc.getPlayfield().SIZE; x++){
            	if(y%2 == 1) {
            		if(x%2 == 1) {
            			addValueTOInputVector(x,y,field);
    					field++;
            		}
            	}
            	else {
            		if(x%2 == 0) {
            			addValueTOInputVector(x,y,field);
    					field++;
            		}
            	}
            }
		}
		//run nn
		if(color == FigureColor.RED){
			outputVector = nn.run(inputVector);
		}
		else {
			for(int i = 0; i < inputVector.length/2; i++){
				invertedInputVector[mirrorField(i)] = inputVector[i];
			}
			for(int i = inputVector.length/2; i < inputVector.length/2; i++){
				invertedInputVector[mirrorField(i-inputVector.length/2)+inputVector.length/2] = inputVector[i];
			}
			outputVector = nn.run(invertedInputVector);
		}		
		//interpret the output
		Move move = new Move(MoveType.INVALID);
		if(type == NNPlayerTypes.NN64IN) {
			move = outputInterpretation64IN(outputVector);
		}
		if(type == NNPlayerTypes.NN32IN) {
			move = outputInterpretation32IN(outputVector);
		}
		if(type == NNPlayerTypes.NN32INRE) {
			move = outputInterpretation32INRE(outputVector);
		}
		gmlc.makeMove(move);
	}
	public void addValueTOInputVector(int x, int y, int field) {
		if(type == NNPlayerTypes.NN64IN) {			
			if(gmlc.getPlayfield().isOccupied(x, y)) {
				if(gmlc.getPlayfield().colorOf(x,y) == color) {
					if(gmlc.getPlayfield().getType(x,y) == FigureType.KING) {
						inputVector[field+32] = 1;
						inputVector[field] = 0;
					}
					else {
						inputVector[field] = 1;	
						inputVector[field+32] = 0;
					}
				}
				else {
					if(gmlc.getPlayfield().getType(x,y) == FigureType.KING) {
						inputVector[field+32] = -1;	
						inputVector[field] = 0;	
					}
					else {
						inputVector[field] = -1;	
						inputVector[field+32] = 0;
					}
				}			
			}
			else {
				inputVector[field] = 0;
				inputVector[field+32] = 0;
			}
		}
		else {
			if(gmlc.getPlayfield().isOccupied(x, y)) {
				if(gmlc.getPlayfield().colorOf(x,y) == color) {
					if(gmlc.getPlayfield().getType(x,y) == FigureType.KING) {
						inputVector[field] = 3;
					}
					else {
						inputVector[field] = 1;	
					}
				}
				else {
					if(gmlc.getPlayfield().getType(x,y) == FigureType.KING) {
						inputVector[field] = -3;	
					}
					else {
						inputVector[field] = -1;	
					}
				}			
			}
			else {
				inputVector[field] = 0;
			}
		}
	}
	private Move outputInterpretation32INRE(double[] outputVector) {
		byte[] sortedOutputIndexes = this.sortOutputByValue(outputVector);
		//what is the first index that has a figure on the corresponding field.
		int[] currentField = {0,0};
			for(byte i = 0; i < sortedOutputIndexes.length; i++) {
				currentField = Move.fieldToCoords(sortedOutputIndexes[i]);
			}
			if(gmlc.getPlayfield().isOccupied(currentField[0], currentField[1]) && gmlc.getPlayfield().colorOf(currentField[0], currentField[1]) == color) {
				List<Move> possibleMoves = Move.getPossibleMoves(gmlc.getPlayfield().field[currentField[0]][currentField[1]], gmlc.getPlayfield());
				switch(possibleMoves.length) {
				case 0:
					break;
				case 1:
					possibleMoves.toFirst();
					return possibleMoves.get();
				//we have more than one move, so we have to choose the one with the highest value.
				default:
					Move move = Move.INVALID;
					double highest = -Double.MAX_VALUE;
					for(possibleMoves.toFirst(); possibleMoves.hasAccess(); possibleMoves.next()) {
						int field = Move.coordsToField(possibleMoves.get().getX(), possibleMoves.get().getY());
						if(outputVector[field] > highest) {
							move = possibleMoves.get();
							highest = outputVector[field];
						}
					}
					return move;
				}
			}	
		return Move.INVALID;
	}
	private class ValueIndexPair {
		public final double value;
		public final byte index;
		public ValueIndexPair(final double value, final byte index) {
			this.value = value;
			this.index = index;
		}
	}
	private byte[] sortOutputByValue(double[] outputVector) {
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
	private Move outputInterpretation32IN(double[] outputVector) {
			//after the output was determined the indexes of the two biggest values in the output are extracted.
				int best[] = searchForBiggestValues();
				//convert them to coordinates
				int[] fieldOne = Move.fieldToCoords(best[0]);
				int[] fieldTwo = Move.fieldToCoords(best[1]);
				int[] finishCoords;
				//get all possible moves
				List<Move> possibleMoves = Move.getPossibleMoves(color, gmlc.getPlayfield());
				//search for valid moves
				for(possibleMoves.toFirst();possibleMoves.hasAccess();possibleMoves.next()) {
					if(possibleMoves.get().getX() == fieldOne[0] && possibleMoves.get().getY() == fieldOne[1]) {
						finishCoords = possibleMoves.get().getFinishCoords();
						if(finishCoords[0] == fieldTwo[0] && finishCoords[1] == fieldTwo[1] ) {
							return possibleMoves.get();
						}
					}
					if(possibleMoves.get().getX() == fieldTwo[0] && possibleMoves.get().getY() == fieldTwo[1]) {
						finishCoords = possibleMoves.get().getFinishCoords();
						if(finishCoords[0] == fieldOne[0] && finishCoords[1] == fieldOne[1] ) {
							return possibleMoves.get();
						}
					}
				}
				//returns invalid if no valid move was found
				return Move.INVALID;
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
	private Move outputInterpretation64IN(double[] outputVector) {
		Move bestMove = Move.INVALID;
		Move move = Move.INVALID;
		double max = Integer.MIN_VALUE;
        int choiceField = 0;
        for(int i = 0, i2 = 0; i < 32; i++) {
        	i2 = color == FigureColor.RED ? i : mirrorField(i);
        	if(outputVector[i2] > max){
        		max = outputVector[i2];
        		choiceField = i2;
        	}
        }
        //test which figures can reach this field
        List<Move> availableMoves = new List<Move>();
        int[][] coords = new int[2][2];
        coords[1] = fieldToCoords(choiceField);
        for( Figure f : gmlc.getPlayfield().getFiguresFor(color)) {
        	coords[0][0] = f.x;
        	coords[0][1] = f.y;
        	move = Move.createMoveFromCoords(coords);
        	//check if the move is valid(see createMoveFromCoords doc)
        	if(gmlc.testMove(move)) {
        		availableMoves.append(move);
        	}
        }
        if(availableMoves.length == 0){
        	//return an invalid move
        	return bestMove;
        }
        else {//case: there is a move that ends at this field
        	//find out if a figure that can do the move is on the field which is chosen by the nn to be the move start
        	max = Integer.MIN_VALUE;
    		double score = 0;
        	for(availableMoves.toFirst();availableMoves.hasAccess();availableMoves.next()){
    			move = availableMoves.get();
    			score = color == FigureColor.RED ?
    					outputVector[coordsToField(move.getX(), move.getY()) + 32] :
    					outputVector[mirrorField(coordsToField(move.getX(), move.getY())) + 32];
    			if(score > max){
    				bestMove = move;
    				max = score;
    			}
    		}
        }
        return bestMove;
	}
	private int mirrorField(int field) {
		return Math.abs(field-31);
	}
	private int[] fieldToCoords(int choiceField) {
		int[] c = new int[2];
        c[0] = choiceField % 4;
        c[1] = (choiceField-c[0])/4;
        c[0] *= 2;
        if(c[1]%2 == 1){
        	c[0]++;
        }
		return c;
	}
	private int coordsToField(int x, int y){
		if(y%2 == 1){
        	x--;
        }
		x /= 2;
		return y*4+x;
	}
	
	@Override
	public String getName() {
		return "NNPlayer";
	}

	@Override
	public boolean acceptDraw() {
		return false;
	}

	@Override
	public void saveInformation(String directory) {
		
	}
	
}
