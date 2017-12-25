import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Move.MoveType;
import generic.List;
import gui.Console;
import nn.NN;
import utilities.FileUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import checkers.Figure;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import checkers.Playfield;

public class NNPlayer1 implements Player{

	private double[] inputVector;
	private FigureColor aiFigureColor;
	private GameLogic gmlc;
	private Console console;
	private Playfield playfield;
	private NN nn;
	private String name = "standard NNPlayer1";
	public NNPlayer1(GameLogic gmlc, Console console) {
		this.gmlc = gmlc;
		this.console = console;        
        
        playfield = gmlc.getPlayfield();
		setNN();
		
	}
	private void setNN() {
	    double[][] afterInputWeights = null;
	    double[][][] hiddenWeights = null;
	    double[][] toOutputWeights = null;		
	    //standard initialization
	    double weightsMax = 0;
	    double weightsMin = 0;
	    double sigmoidMax = 0;
	    double sigmoidMin = 0;
	    int inputNeurons = 0;
	    int outputNeurons = 0;
	    int hiddenNeurons = 0;
	    int hiddenLayer = 0;
		try {
			//the file has to have a specific name
			File directory = new File("resources/NNPlayer1Info");
			String[] files = directory.list();
			if(files.length == 0) {
				console.printWarning("No NN file was found in the NNPlayer folder. Game will be cancelled.", "NNPlayer");
				return;
			}
			File file = new File("resources/NNPlayer1Info/" + files[0]);
			FileReader reader = new FileReader(file);
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(reader);

		      String line;
		      int lineNumber = 1;
		      while ((line = bufferedReader.readLine()) != null) {
		    	  switch(lineNumber) {
		    	  case 3:
		    		  weightsMax = Double.parseDouble(line);
		    		  break;
		    	  case 5:
		    		  weightsMin = Double.parseDouble(line);
		    		  break;		    	  
		    	  case 7:
		    		  sigmoidMax = Double.parseDouble(line);
		    		  break;		    	  
			  	  case 9:
			  		  sigmoidMin = Double.parseDouble(line);
					  break;				  
				  case 11:
					  inputNeurons = (int) Double.parseDouble(line);
					  break;				  
				  case 13:
					  outputNeurons = (int) Double.parseDouble(line);
					  break;				  
			  	  case 15:
			  		  hiddenNeurons = (int) Double.parseDouble(line);
					  break;				  
				  case 17:
					  hiddenLayer = (int) Double.parseDouble(line);
					  break;	
				  case 18:
				      afterInputWeights = new double[hiddenNeurons][inputNeurons];
				      hiddenWeights = new double[hiddenLayer-1][hiddenNeurons][hiddenNeurons];
				      toOutputWeights = new double[outputNeurons][hiddenNeurons];
					  break;					  
			      }
	    	  if(lineNumber == 18) {
	    		  for(int i = 0; i < hiddenNeurons; i++) {
	    			  for(int j = 0; j < inputNeurons; j++) {	    				  
	    				  afterInputWeights[i][j] = Double.parseDouble(line = bufferedReader.readLine());
	    				  lineNumber++;
	    			  }
	    		  }
	    		  line = bufferedReader.readLine();
	    		  line = bufferedReader.readLine();
	    		  for(int i = 0; i < hiddenLayer-1; i++) {
	    			for(int j = 0; j < hiddenNeurons;j++) {
	    				for(int k = 0; k < hiddenNeurons; k++) {
	    					hiddenWeights[i][j][k] = Double.parseDouble(line = bufferedReader.readLine());
	    					lineNumber++;	
	    				}
	    			}
	    		  }
	    		  line = bufferedReader.readLine();
	    		  line = bufferedReader.readLine();
	    		  for(int i = 0; i < outputNeurons; i++) {
	    			 for(int j = 0; j < hiddenNeurons; j++) {
	    				 toOutputWeights[i][j] = Double.parseDouble(line = bufferedReader.readLine());
	    				 lineNumber++;
	    			 } 
	    		  }
    		  }
		      lineNumber++;		      
		    }		    
			nn = new NN(inputNeurons, outputNeurons, hiddenNeurons, hiddenLayer, sigmoidMin, sigmoidMax, weightsMin, weightsMax);
			nn.setWeights(afterInputWeights, hiddenWeights, toOutputWeights);
			
		} catch (IOException e) {
			//console.printWarning("No NN file was found in the NNPlayer folder. Game will be cancelled.", "NNPlayer");
			//e.printStackTrace();
		} 

	}
	@Override
	public void prepare(FigureColor color) {
		aiFigureColor = color;
		//TODO a static size here is unsafe
		inputVector = new double[64];
		
	}

	public void requestMove() {

		int field = 0;
		for(int y = 0;y < playfield.SIZE; y++){
            for(int x = 0;x < playfield.SIZE; x++){
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
		if(aiFigureColor == FigureColor.RED){
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
		if(playfield.isOccupied(x, y)) {
			if(playfield.colorOf(x,y) == aiFigureColor) {
				if(playfield.getType(x,y) == FigureType.KING) {
					inputVector[field+32] = 1;
					inputVector[field] = 0;
				}
				else {
					inputVector[field] = 1;	
					inputVector[field+32] = 0;
				}
			}
			else {
				if(playfield.getType(x,y) == FigureType.KING) {
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
		//find out the field it wants to move to
		double max = Integer.MIN_VALUE;
        int choiceField = 0;
        for(int i = 0, i2 = 0; i < 32; i++) {
        	i2 = aiFigureColor == FigureColor.RED ? i : mirrorField(i);
        	if(outputVector[i2] > max){
        		max = outputVector[i2];
        		choiceField = i2;
        	}
        }
        //test which figures can reach this field
        //TODO do it!
        List<Move> availableMoves = new List<Move>();
        int[][] coords = new int[2][2];
        coords[1] = fieldToCoords(choiceField);
        for( Figure f : gmlc.getPlayfield().getFiguresFor(aiFigureColor)) {
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
    			score = aiFigureColor == FigureColor.RED ?
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
        c[0] = choiceField%4;
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
	public void setGL(GameLogic gl){
		gmlc = gl;
		playfield = gmlc.getPlayfield();
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public boolean acceptDraw() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void saveInformation(String directory) {
		File file;
		String fileName = "NNPlayer1 Information.txt";
		if(FileUtilities.searchForEqualFiles(directory, fileName)){
			file = new File(directory + "/" + "(1)" + fileName);
		}
		else {
			file = new File(directory + "/" + fileName) ;
		}

		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PrintWriter writer ;
		try {
			writer = new PrintWriter(file);
			writer.write("No information for this ai");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public FigureColor getFigureColor() {
		return aiFigureColor;	
	}
}
