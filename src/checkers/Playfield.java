package checkers;
import java.io.*;

import java.time.*;
import java.util.Date;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Move.MoveType;
import gui.PlayfieldDisplay;
import gui.PlayfieldSound;
import utilities.FileUtilities;

/**
 * This class represents a playfield of variable size. It contains all methods that are needed in order to manage the playfield from 
 * other classes, especially the gamelogic. Therefore all methods have to be accessed from other classes and need the access modifier
 * "public".
 * <p>
 * We realized this playfield by creating a Figure array with a distinct size. The size is describes the length one the x and y axis
 * (Its is always a square) and not the amount of fields.
 * <p>
 * @author Till
 * @author Marco
 *
 */
public class Playfield {

	public final int SIZE;
	public Figure[][] field;
	PlayfieldDisplay display;
	PlayfieldSound sound;
	Instant instant;
	boolean recordGame;
	FileReader reader;
	BufferedReader bufferedReader;
	PrintWriter writer;

	int movesWithoutJumps = 0;
	File filePath;
	/**
	 * This default constructor creates a playfield with the initial size of eight and then calls the subconstructor to set up this 
	 * playfield further.
	 */
	public Playfield() {
		this(8);
	}
	/**
	 * can be used by any superclass to customize basic required parameters
	 * @param size An integer that describes the length of the playfield on the x and y axis.
	 */
	public Playfield(int size){
		SIZE = size;
		field = new Figure[SIZE][SIZE];
	}
	
	/**
	 * Loads a start situation from the resources. It automatically searches for the right start situation for the right size. If it is
	 * not found, then it throws an exeption.
	 * <p>
	 * @throws IOException Thrown when the file containing the start situation does not exist or is not available at the moment.
	 * A specific detailed message with the error that accured in this method.
	 */
	public void createStartPosition(Playfield playfield) throws IOException{
		field = FileUtilities.loadGameSituation(new File("resources/playfieldSaves/startPositionForSize" + SIZE +".pfs"), playfield);
		if(display != null) display.updateDisplay();
	}
	
	public void clearField(Playfield playfield) throws IOException {
		 field = FileUtilities.loadGameSituation(new File("resources/playfieldSaves/noFigures.pfs"), playfield);
		 if(display != null) display.updateDisplay();
	}
	
	/**
	 * This method tries to load a specific game situation from a .pfs file. This file type saves all information that is needed to
	 * reconstruct this game sitation. 
	 * <p>
	 * The file parameter needs to be a .pfs file with the correct size in order to be loaded and displayed on the playfield
	 * <p>
	 * @param file             A file that respresents the path to a playfield save file.
	 * @throws IOException Thrown when the file is currently not available. A specific detailed meassage with the error that accured in this method.
	 */
	public void setGameSituation(File file) throws IOException{
		field = FileUtilities.loadGameSituation(file, this);
		if(display != null) display.updateDisplay();
	}
	/**
	 * enables 
	 * @param selected
	 */
	public void enableGameRecording(boolean selected){
		recordGame = selected;
	}
	/**
	 * Sets the object that is responsible for displaying the contents of this playfield
	 * <p>
	 * @param d The object that wants to display the playfield.
	 * <p>
	 * @see gui.PlayfieldDisplay
	 * @see gui.PlayfieldPanel
	 */
	public void setPlayfieldDisplay(PlayfieldDisplay d){
		display = d;
	}
	/**
	 * Sets the object that is responsible for establishing a connection to the object that has the the task to playsounds. This object has to have the interface 
	 * DisplaySounds.
	 * <p>
	 * @param A object that is responsible for playing sound with the interface DisplaySounds.
	 */
	public void setPlayfieldSound(PlayfieldSound s) {
		sound = s;
	}
	/**
	 * In this method one figure on the playfield changes from a normal figure to a king fingure by calling the figureType method with the parameter FigureType.KING
	 * in a certain figure. 
	 * <p>
	 * @param x       An integer variable which is representing a point on the vertical axis of the playfield.
	 * @param y	 	  An integer variable which is representing a point on the horizontal axis of the playfield.	
	 */
	public void changeFigureToKing(int x, int y){
		if(sound != null)sound.playSound("toDameSound.wav");
		field[x][y].setFigureType(FigureType.KING);
		if(display != null) display.updateDisplay();
	}
	/**
	 * By calling this method a speficic move will be executed on the playfield. It changes the references on the field array.
	 * <p>
	 * The method also tests if the move is valid. If this is not given the method returns without changing the playfield.
	 * <p>
	 * @param m A move object that should be executed on the playfield.
	 */
	public void executeMove(Move m){
		//coords array for displaying the move
		int[][] coords= new int[m.getSteps()+1][2];
		//x and y before move execution
		int x = m.getX();
		int y = m.getY();
		coords[0][1] = x;
		coords[0][1] = y;
		if(m.getMoveType() == MoveType.INVALID){
			//can not execute an invalid move
			return;
		}
		else if(m.getMoveType() == MoveType.STEP ){
			if(sound != null)sound.playSound("moveSound.wav");
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
			for(int s = 0, steps = m.getSteps(); s < steps; s++){
				if(sound != null)sound.playSound("beatSound.wav");
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
	/**
	 * 
	 * @param color
	 * @return
	 */
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
	/**
	 * Returns the number of figures on the playfield from one color.
	 * <p>
	 * @param color
	 * @return
	 */
	public int getFigureTypeQuantity(FigureColor figurecolor, FigureType figuretype) {
		int quantity = 0;
		for(int y = 0;y < SIZE; y++){
            for(int x = 0;x < SIZE; x++){
            	if(isOccupied(x,y) && field[x][y].getFigureColor() == figurecolor && field[x][y].getFigureType() == figuretype){
            		quantity++;
            	}
            }
		}
		return quantity;
	}
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isOccupied(int x, int y){
		return (field[x][y] != null);
	}
	/**
	 * 
	 * @return
	 */
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
	/**
	 * 
	 * @param figurecolor
	 * @return
	 */
	public Figure[] getFiguresFor(FigureColor figurecolor) {
		int counter = 0;
		Figure[] figures = new Figure[getFigureQuantity(figurecolor)];
		for(int y = 0;y < SIZE; y++){
            for(int x = 0;x < SIZE; x++){
            	if(isOccupied(x,y) && field[x][y].getFigureColor() == figurecolor){
            		figures[counter] = field[x][y];
            		counter++;
            	}
            }
		}
		return figures;
	}
	/**
	 * 
	 * @return
	 */
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
	/**
	 * 
	 */
	public FigureColor colorOf(int x, int y) {
		return field[x][y].getFigureColor();
	}
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public FigureType getType(int x, int y) {
		return field[x][y].getFigureType();
	}
	/**
	 * 
	 * @return
	 */
	public int getMovesWithoutJumps(){
		return movesWithoutJumps;
	}
	/**
	 * 
	 */
	public void playWinSound() {
		if(sound != null)sound.playSound("winSound.wav");
	}
	/**
	 * 
	 * @return
	 */
	public boolean testPlayability() {
		int whiteFigures = 0;
		int redFigures = 0;
		for(int y = 0;y < SIZE; y++) {
			for(int x = 0;x < SIZE; x++) {
				if(isOccupied(x,y)) {
					if(field[x][y].getFigureColor() == FigureColor.RED) {
						redFigures++;
					}
					else {
						whiteFigures++;
					}
				}
			}
		}
		if(whiteFigures > 0 && redFigures > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	public void createPaths(String gameName) {
		filePath = new File("resources/RecordedGames/" + gameName);
		filePath.mkdirs();
		File filePathToGameSituations = new File("resources/RecordedGames/" + gameName + "/GameSituations");
		filePathToGameSituations.mkdirs();
	}
	public File getFilePath() {
		return filePath;
	}
}

	
