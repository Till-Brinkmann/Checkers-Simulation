package nn;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import checkers.Figure;
import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.GameLogic;
import checkers.Move;
import checkers.Player;
import checkers.Playfield;
import generic.List;
import gui.Console;
import utilities.FileUtilities;

public class NNPlayer implements Player {
	/*
	 * das nn bekommt das feld als input und gibt das feld nach dem zug aus.
	 * Dann wird der h�chste wert im outputfeld genommen und es wird geguckt ob eine figur dieses erreichen kann
	 * 
	 */

	private double[] inputVector;
	private FigureColor aiFigureColor;
    public double fitness;
    NN net;
    public GameLogic gmlc;
    public Console console;
    public Playfield playfield; 
	public NNPlayer(GameLogic pGmlc, Console pConsole, NN pNet){
		gmlc = pGmlc;
		console = pConsole;        
        net = pNet;
        playfield = gmlc.getPlayfield();
	}
	@Override
	public void prepare(FigureColor color) {
		aiFigureColor = color;
		//TODO a static size here is unsafe
		inputVector = new double[64];
	}
	@Override
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
			gmlc.makeMove(moveDecision(net.run(inputVector)));
		}
		else {
			double[] invertedInputVector = new double[64];
			for(int i = 0; i < inputVector.length/2; i++){
				invertedInputVector[mirrorField(i)] = inputVector[i];
			}
			for(int i = inputVector.length/2; i < inputVector.length/2; i++){
				invertedInputVector[mirrorField(i-inputVector.length/2)+inputVector.length/2] = inputVector[i];
			}
			gmlc.makeMove(moveDecision(net.run(invertedInputVector)));
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
		return "standard NN";
	}
	@Override
	public void saveInformation(String directory) {
		File file;
		String fileName = "NNPlayer Information.txt";
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
	@Override
	public boolean acceptDraw() {
		// TODO Auto-generated method stub
		return false;
	}
	public FigureColor getFigureColor() {
		return aiFigureColor;	
	}

}
