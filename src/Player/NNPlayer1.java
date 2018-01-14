import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import checkers.Figure;
import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import datastructs.List;
import gui.Console;
import json.JSONArray;
import json.JSONObject;

public class NNPlayer1 implements Player {
	
	public class ReducedNN{
		
		public double[][] afterInputWeights;
		public double[][][] hiddenWeights;
		public double[][] toOutputWeights;
	    private double sigmin;
	    private double sigmax;
	    
	    
	    
	    public ReducedNN(int inputNeurons, int outputNeurons,  int hiddenNeurons, int hiddenlayer, double sigmin, double sigmax) {
	    	this.sigmin = sigmin;
	        this.sigmax = sigmax;
	    	afterInputWeights = new double[hiddenNeurons][inputNeurons];
	        hiddenWeights = new double[hiddenlayer-1][hiddenNeurons][hiddenNeurons];
	        toOutputWeights = new double[outputNeurons][hiddenNeurons];
	    }
	    
	    public double[] run(double[] inputVector){
	        double[] hiddenVector = new double[afterInputWeights.length];
	        double[] outputVector = new double[toOutputWeights.length];
	        hiddenVector = vector_matrix_multiplication(inputVector, afterInputWeights);
	        for (int x = 0; x < hiddenVector.length; x++){
	            hiddenVector[x] = sigmoid(hiddenVector[x]);
	        }
	        for (int i = 0; i < hiddenWeights.length; i++){
	            hiddenVector = vector_matrix_multiplication(hiddenVector, hiddenWeights[i]);
	            for (int x = 0; x < hiddenVector.length; x++){
	                hiddenVector[x] = sigmoid(hiddenVector[x]);
	            }
	        }
	        outputVector = vector_matrix_multiplication(hiddenVector, toOutputWeights);
	        for (int x = 0; x < outputVector.length; x++){
	            outputVector[x] = sigmoid(outputVector[x]);
	        }
	        return outputVector;
	    }
	    
	    private double sigmoid(double x) {
	        return (1/( 1 + Math.pow(Math.E,(-x))) + ((sigmin + sigmax) / 2)-0.5) * (sigmax-sigmin);
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
	}
	
	private ReducedNN nn;
	
	GameLogic gmlc;
	Console csl;
	
	FigureColor color;
	
	private double[] inputVector;
	
	public NNPlayer1(GameLogic gmlc, Console csl) {
		this.gmlc = gmlc;
		this.csl = csl;
		nn = new ReducedNN(64, 64, 64, 10, -1, 1);
		inputVector = new double[64];
		loadWeights();
	}
	
	private void loadWeights() {
		File nnfile = new File("resources/NNPlayer1Info").listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String name) {
				if(name.endsWith(".json")) return true;
				return false;
			}
			
		})[0];
		char[] chars = new char[1600000];
		try {
			new FileReader(nnfile).read(chars);
			JSONObject nnobject = new JSONObject(String.valueOf(chars));
			JSONArray array = nnobject.getJSONArray("AfterInputWeights");
			JSONArray innerArray;
			for(int i = 0; i < array.length(); i++) {
				innerArray = array.getJSONArray(i);
				for(int j = 0; j < innerArray.length(); j++) {
					nn.afterInputWeights[i][j] = innerArray.getDouble(j);
				}
			}
			JSONArray innerArray2;
			array = nnobject.getJSONArray("HiddenWeights");
			for(int i = 0; i < array.length(); i++) {
				innerArray = array.getJSONArray(i);
				for(int j = 0; j < innerArray.length(); j++) {
					innerArray2 = innerArray.getJSONArray(j);
					for(int k = 0; k < innerArray2.length(); k++) {
						nn.hiddenWeights[i][j][k] = innerArray2.getDouble(k);
					}
				}
			}
			array = nnobject.getJSONArray("ToOutputWeights");
			for(int i = 0; i < array.length(); i++) {
				innerArray = array.getJSONArray(i);
				for(int j = 0; j < innerArray.length(); j++) {
					nn.toOutputWeights[i][j] = innerArray.getDouble(j);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void prepare(FigureColor color) {
		this.color = color;
	}

	@Override
	public void requestMove() {
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
		if(color == FigureColor.RED){
			gmlc.makeMove(moveDecision(nn.run(inputVector)));
		}
		else {
			double[] invertedInputVector = new double[64];
			for(int i = 0; i < inputVector.length/2; i++){
				invertedInputVector[mirrorField(i)] = inputVector[i];
			}
			for(int i = inputVector.length/2; i < inputVector.length/2; i++){
				invertedInputVector[mirrorField(i-inputVector.length/2)+inputVector.length/2] = inputVector[i];
			}
			gmlc.makeMove(moveDecision(nn.run(invertedInputVector)));
		}
	}

	public void addValueTOInputVector(int x, int y, int field) {
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
	
	private Move moveDecision(double[] outputVector) {
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
