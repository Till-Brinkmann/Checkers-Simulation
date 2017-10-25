package checkers;
import java.io.*;

import java.time.*;
import java.util.Date;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Move.MoveType;
import generic.List;
import gui.PlayfieldDisplay;


public class Playfield {

	public final int SIZE;
	public Figure[][] field;
	PlayfieldDisplay display;
	Instant instant;
	boolean recordGame;
	FileReader reader;
	BufferedReader bufferedReader;
	PrintWriter writer;

	int movesWithoutJumps = 0;

	public Playfield() {
		this(8);
	}

	/**
	 * can be used by any superclass to customize basic required parameters
	 * @param size
	 */
	public Playfield(int size){
		SIZE = size;
		createNewPlayfield();
	}
	public void createNewPlayfield(){
	    field = new Figure[SIZE][SIZE];
	}
	public void createStartPosition() throws IOException{
   		//loadGameSituation(new File("Checkers Simulation 2.0/resources/playfieldSaves/startPositionForSize8.pfs"));
		loadGameSituation(new File("resources/playfieldSaves/startPositionForSize8.pfs"));
	}

	public void loadGameSituation(File file) throws IOException{
		reader = new FileReader(file);
		bufferedReader = new BufferedReader(reader);

		String info = bufferedReader.readLine();
		if(Integer.parseInt(info) != SIZE){
			//TODO what to do here?
			throw new IOException("This savefile does not work for this playfield!");
		}
		else{
			info = bufferedReader.readLine();
			if(info.length() != SIZE*SIZE){
				throw new IOException("The save file is corrupted!");
			}
			int index = 0;
	        for(int y = 0;y < SIZE; y++){
	            for(int x = 0;x < SIZE; x++){
	            	switch(info.charAt(index)){
	            	case '0':
	            		field[x][y] = null;
	            		index++;
	            		break;
	                case '1':
	                	field[x][y] = new Figure(x, y, FigureColor.RED, FigureType.NORMAL);
	                	index++;
	                	break;
		            case '3':
		            	field[x][y] = new Figure(x, y, FigureColor.RED, FigureType.KING);
		            	index++;
	                    break;
		            case '2':
		            	field[x][y] = new Figure(x, y, FigureColor.WHITE, FigureType.NORMAL);
		            	index++;
	                    break;
		            case '4':
		            	field[x][y] = new Figure(x, y, FigureColor.WHITE, FigureType.KING);
		            	index++;
	                    break;
	                default:

	                	return;
	            	}
	            }
			}
	        if(display != null) display.updateDisplay();
		}
	}
	String gameName;
	int turnCount;
	FigureColor inTurn;
	String player1Name;
	String player2Name;
	public void saveGameSituation(String pGameName,FigureColor pInTurn, int pTurnCount, String pPlayer1Name, String pPlayer2Name) throws IOException {
		gameName = pGameName;
		inTurn = pInTurn;
		turnCount = pTurnCount;
		player1Name = pPlayer1Name;
		player2Name = pPlayer2Name;
		saveGameSituation();
	}
	public void saveGameSituation() throws IOException{
		long currentTime = new Date().getTime();
		String fileName = String.valueOf(currentTime);
		if(recordGame){
			// TODO es muss nicht jedes mal ein neuer ordner erstellt werden
			File filePath = new File("resources/" + gameName);
			filePath.mkdirs();
			File file = new File("resources/" + gameName + "/" + fileName + ".pfs");
			file.createNewFile();
			writer = new PrintWriter(file);
		}
		else {
			File file = new File("resources/playfieldSaves/"+ fileName +".pfs");
			file.createNewFile();
			writer = new PrintWriter(file);
		}

		//write PlayfieldSize
		writer.write(String.valueOf(SIZE));
		writer.write("\n");
		//write playfield
		//even numbers:White
		//uneven numbers:Red
        for(int y = 0;y < SIZE; y++){
            for(int x = 0;x < SIZE; x++){
            	if(isOccupied(x,y)){
            		if(field[x][y].getFigureColor() == FigureColor.RED){
            			if(field[x][y].getFigureType() == FigureType.NORMAL){
            				writer.write("1");
            			}
            			else{
            				writer.write("3");
            			}
            		}
            		else{
            			if(field[x][y].getFigureType() == FigureType.NORMAL){
            				writer.write("2");
            			}
            			else{
            				writer.write("4");
            			}
            		}
            	}
            	else{
            		writer.write("0");

            	}
            }
        }
        if(recordGame) {
        	writer.write("\n");
        	writer.write("\ngame name:\n" + gameName);
        	writer.write("\n");
        	writer.write("\nWho is playing?\n" + player1Name + " vs. " + player2Name);
        	writer.write("\n");
        	writer.write("Turns: " + turnCount + "\n");
        	if(inTurn == FigureColor.WHITE) {
        		writer.write("\nIn turn: White\n");
        	}
        	else {
        		writer.write("\nIn turn: Red\n");
        	}
        	writer.write("\nFigureQuantities:\n");
        	writer.write("\nWhitePieces: "+ String.valueOf(getFigureQuantity(FigureColor.WHITE)) + "\n");
        	writer.write("of it: " + getFigureTypeQuantity(FigureColor.WHITE,FigureType.NORMAL) + "Normal Figures and " + getFigureTypeQuantity(FigureColor.WHITE,FigureType.KING) + "Kings\n");
        	writer.write("\nRedPieces: "+ String.valueOf(getFigureQuantity(FigureColor.RED)) + "\n");
        	writer.write("of it: " + getFigureTypeQuantity(FigureColor.RED,FigureType.NORMAL) + "Normal Figures and " + getFigureTypeQuantity(FigureColor.RED,FigureType.KING) + "Kings\n");
        }
		writer.flush();
		writer.close();
	}
	public void enableGameRecording(boolean selected){
		recordGame = selected;
	}

	public void setPlayfieldDisplay(PlayfieldDisplay d){
		display = d;
	}

	public void changeFigureToKing(int x, int y){
		field[x][y].setFigureType(FigureType.KING);
	}

	public void executeMove(Move m){
		//TODO alles
		//x and y after move execution
		int x = m.getX();
		int y = m.getY();
		if(m.getMoveType() == MoveType.INVALID){
			//can not execute an invalid move
			return;
		}
		else if(m.getMoveType() == MoveType.STEP ){
			movesWithoutJumps++;
			switch(m.getMoveDirection()){
			case BL:
				field[x-1][y-1] = field[x][y];
				field[x][y] = null;
				field[x-1][y-1].x = x-1;
				field[x-1][y-1].y = y-1;
				break;
			case BR:
				field[x+1][y-1] = field[x][y];
				field[x][y] = null;
				field[x+1][y-1].x = x+1;
				field[x+1][y-1].y = y-1;
				break;
			case FL:
				field[x-1][y+1] = field[x][y];
				field[x][y] = null;
				field[x-1][y+1].x = x-1;
				field[x-1][y+1].y = y+1;
				break;
			case FR:
				field[x+1][y+1] = field[x][y];
				field[x][y] = null;
				field[x+1][y+1].x = x+1;
				field[x+1][y+1].y = y+1;
				break;
			}
		}
		else{
			movesWithoutJumps = 0;
			//TODO do jump and multijump stuff
			for(int s = 0; s < m.getSteps(); s++){
				switch(m.getMoveDirection(s)){
				case BL:
					field[x-2][y-2] = field[x][y];
					field[x][y] = null;
					//delete opponents figure 
					field[x-1][y-1] = null;
					x-=2;
					y-=2;
					field[x][y].x = x;
					field[x][y].y = y;
					break;
				case BR:
					field[x+2][y-2] = field[x][y];
					field[x][y] = null;
					//delete opponents figure 
					field[x+1][y-1] = null;
					x+=2;
					y-=2;
					field[x][y].x = x;
					field[x][y].y = y;
					break;
				case FL:
					field[x-2][y+2] = field[x][y];
					field[x][y] = null;
					//delete opponents figure 
					field[x-1][y+1] = null;
					x-=2;
					y+=2;
					field[x][y].x = x;
					field[x][y].y = y;
					break;
				case FR:
					field[x+2][y+2] = field[x][y];
					field[x][y] = null;
					//delete opponents figure 
					field[x+1][y+1] = null;
					x+=2;
					y+=2;
					field[x][y].x = x;
					field[x][y].y = y;
					break;
				}
			}
		}
		if(display != null) display.updateDisplay();
	}

	public int getFigureQuantity(FigureColor color){
		int quantity = 0;
		for(int y = 0;y < SIZE; y++){
            for(int x = 0;x < SIZE; x++){
            	if(isOccupied(x,y) && colorOf(x,y) == color){
            		quantity++;
            	}
            }
		}
		return quantity;
	}
	public int getFigureTypeQuantity(FigureColor figurecolor, FigureType figuretype) {
		int quantity = 0;
		for(int y = 0;y < SIZE; y++){
            for(int x = 0;x < SIZE; x++){
            	if(isOccupied(x,y) && field[x][y].color == figurecolor && field[x][y].type == figuretype){
            		quantity++;
            	}
            }
		}
		return quantity;
	}
	public boolean isOccupied(int x, int y){
		return (field[x][y] != null);
	}
	public boolean isEmpty(){
		for(int x = 0; x < 8; x++) {
			for(int y = 0; y < 8; y++) {
				if(field[x][y] != null) {
					return false;
				}
			}
		}
		return true;
	}
	public Figure[] getFiguresFor(FigureColor figurecolor) {
		int counter = 0;
		Figure[] figures = new Figure[getFigureQuantity(figurecolor)];
		for(int y = 0;y < SIZE; y++){
            for(int x = 0;x < SIZE; x++){
            	if(isOccupied(x,y) && field[x][y].color == figurecolor){
            		figures[counter] = field[x][y];
            		counter++;
            	}
            }
		}
		return figures;
	}

	public Playfield copy() {
		Playfield copy = new Playfield(SIZE);
		for(int y = 0;y < SIZE; y++){
            for(int x = 0;x < SIZE; x++){
            	if(isOccupied(x,y)){
            		copy.field[x][y] = field[x][y].copy();
            	}
            	else {
            		copy.field[x][y] = null;
            	}
            }
		}
		return copy;
	}

	public FigureColor colorOf(int x, int y) {
		return field[x][y].getFigureColor();
	}

	public FigureType getType(int x, int y) {
		return field[x][y].getFigureType();
	}
	public int getMovesWithoutJumps(){
		return movesWithoutJumps;
	}
	public int getSize() {
		return SIZE;
	}
	
}
