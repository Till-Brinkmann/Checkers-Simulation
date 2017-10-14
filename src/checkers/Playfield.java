package checkers;
import java.io.*;

import java.time.*;
import java.util.Date;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Move.MoveType;
import gui.PlayfieldDisplay;
import gui.PlayfieldPanel;


public class Playfield {

	public final int SIZE;
	public Figure[][] field;
	PlayfieldDisplay display;
	Instant instant;
	boolean recordGame;
	FileReader reader;
	BufferedReader bufferedReader;
	PrintWriter writer;

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
			throw new IOException("This savefile does not work for this palyfield!");
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
	public void saveGameSituation() throws IOException{
		long currentTime = new Date().getTime();
		String fileName = String.valueOf(currentTime);
		File file = new File("rescources/playfieldSaves/"+ fileName +".pfs");
		file.createNewFile();
		writer = new PrintWriter(file);
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
			//TODO do jump and multijump stuff
		}
		if(display != null) display.updateDisplay();
	}

	public int getFigureQuantity(FigureColor color){
		int quantity = 0;
		for(int y = 0;y < SIZE; y++){
            for(int x = 0;x < SIZE; x++){
            	if(isOccupied(x,y) && field[x][y].color == color){
            		quantity++;
            	}
            }
		}
		return quantity;
	}

	public boolean isOccupied(int x, int y){
		return field[x][y] != null;
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
            	copy.field[x][y] = field[x][y].copy();
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
}
