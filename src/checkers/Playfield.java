package checkers;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Move.MoveType;
import gui.PlayfieldDisplay;
import gui.PlayfieldPanel;
import gui.PlayfieldSound;
import utilities.FileUtilities;

/**
 * This class represents a playfield of variable size. It contains all methods that are needed in order to manage the playfield from 
 * other classes, especially the gamelogic. Therefore all methods have to be accessed from other classes and need the access modifier
 * "public".
 * <p>
 * We implemented this playfield by creating a 2-dimensional Figure array with a distinct size. The size describes the length on the x and y axis
 * (Its is always a square) and not the number of fields.
 * <p>
 * @author Till
 * @author Marco
 *
 */
public class Playfield {

	public final int SIZE;
	public Figure[][] field;
	private PlayfieldDisplay display;
	private PlayfieldSound sound;
	FileReader reader;
	BufferedReader bufferedReader;
	PrintWriter writer;

	int movesWithoutJumps = 0;
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
	 */
	public void createStartPosition() throws IOException{
		field = FileUtilities.loadGameSituation(new File("resources/playfieldSaves/startPositionForSize" + SIZE +".pfs"), this);
		if(display != null) display.updateDisplay();
	}
	
	public void clearField() throws IOException {
		 field = FileUtilities.loadGameSituation(new File("resources/playfieldSaves/noFigures.pfs"), this);
		 if(display != null) display.updateDisplay();
	}
	
	/**
	 * This method tries to load a specific game situation from a .pfs file. This file type saves all information that is needed to
	 * reconstruct this game situation. 
	 * <p>
	 * The file parameter needs to be a .pfs file with the correct size in order to be loaded and displayed on the playfield
	 * <p>
	 * @param file A file that respresents the path to a playfield save file.
	 * @throws IOException Thrown when the file is currently not available.
	 */
	public void setGameSituation(File file) throws IOException{
		field = FileUtilities.loadGameSituation(file, this);
		if(display != null) display.updateDisplay();
	}
	public void setGameSituation(PlayfieldPanel panel) {
		for(int y = 0; y < SIZE; y++){
			for(int x = 0; x < SIZE; x++){
				if(panel.buttons[x][y].getBackground().equals(new Color(160,10,10))) {
					if(panel.buttons[x][y].getIcon() == null) {
						field[x][y] = new Figure(x, y, FigureColor.RED, FigureType.NORMAL);
					}
					else {
						field[x][y] = new Figure(x, y, FigureColor.RED, FigureType.KING);
					}
				}
				else if(panel.buttons[x][y].getBackground() == Color.WHITE) {
					if(panel.buttons[x][y].getIcon() == null) {
						field[x][y] = new Figure(x, y, FigureColor.WHITE, FigureType.NORMAL);
					}
					else {
						field[x][y] = new Figure(x, y, FigureColor.WHITE, FigureType.KING);
					}
				}
				else {
					field[x][y] = null;
				}
			}
		}			
		if(display != null) display.updateDisplay();
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
	 * @param s A object that is responsible for playing sound with the interface DisplaySounds.
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
	 * @param color The color to test for.
	 * @return Returns the number of figures with the given color. 
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
	 * @param figurecolor
	 * @param figuretype
	 * @return The number of figures with the given color and type.
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
	 * @param x X position of the field. 
	 * @param y Y position of the field.
	 * @return Returns true if the field with the given coordinates is occupied by a figure.
	 */
	public boolean isOccupied(int x, int y){
		return (field[x][y] != null);
	}
	/**
	 * @return True, if the playfield is completely empty (has no figures on it).
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
	 * @param figurecolor FigureColor of the figures.
	 * @return Returns all figures the player with the given color has.
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
	 * @return Returns a copy of this playfield including also copied figures.
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
	 * @param x X position of the field. 
	 * @param y Y position of the field.
	 * @return Returns the FigureColor of the figure on the field if there is a figure. Otherwise null.
	 */
	public FigureColor colorOf(int x, int y) {
		if(!isOccupied(x, y)) return null;
		return field[x][y].getFigureColor();
	}
	/**
	 * 
	 * @param x X position of the field. 
	 * @param y Y position of the field.
	 * @return Returns the FigureType of the figure on the field if there is a figure. Otherwise null.
	 */
	public FigureType getType(int x, int y) {
		if(!isOccupied(x, y)) return null;
		return field[x][y].getFigureType();
	}

	public int getMovesWithoutJumps(){
		return movesWithoutJumps;
	}

	public void playWinSound() {
		if(sound != null)sound.playSound("winSound.wav");
	}
	/**
	 * A situation is only playable when both players have at least one figure.
	 * @return Returns true, if the current situation on the playfield could be played.
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
}

	
