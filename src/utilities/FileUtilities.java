package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.Player;
import checkers.Figure;

import checkers.Playfield;

public class FileUtilities {
	public static Figure[][] loadGameSituation(File file, Playfield playfield) throws IOException{
		Figure[][] field = new Figure[8][8];
		
		FileReader reader = new FileReader(file);
		@SuppressWarnings("resource")
		BufferedReader bufferedReader = new BufferedReader(reader);

		String info = bufferedReader.readLine();
		if(Integer.parseInt(info) != playfield.SIZE){
			//TODO what to do here?
			throw new IOException("This savefile does not work for this playfield!");
		}
		else{
			info = bufferedReader.readLine();
			if(info.length() != playfield.SIZE*playfield.SIZE){
				throw new IOException("The save file is corrupted!");
			}
			int index = 0;
	        for(int y = 0;y < playfield.SIZE; y++){
	            for(int x = 0;x < playfield.SIZE; x++){
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

	                	return null;
	            	}
	            }
			}
		}
		return field;
	}
	public static void saveGameSituation(Playfield field, String filePath, String fileName) throws IOException{
		File file = new File(filePath + "/" + fileName + ".pfs");
		
		file.createNewFile();
		PrintWriter writer = new PrintWriter(file);

		//write PlayfieldSize
		writer.write(String.valueOf(field.SIZE));
		writer.write("\n");
		//write playfield
		//even numbers:White
		//uneven numbers:Red
        for(int y = 0;y < field.SIZE; y++){
            for(int x = 0;x < field.SIZE; x++){
            	if(field.isOccupied(x,y)){
            		if(field.field[x][y].getFigureColor() == FigureColor.RED){
            			if(field.field[x][y].getFigureType() == FigureType.NORMAL){
            				writer.write("1");
            			}
            			else{
            				writer.write("3");
            			}
            		}
            		else{
            			if(field.field[x][y].getFigureType() == FigureType.NORMAL){
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
	public static void createTimesFile(double[] minTime, double[]maxTime, double[] avgTime, double[] overallTime,String player1Name,String player2Name, String filePath) {
		try {
			PrintWriter writer = new PrintWriter(new File(filePath + "/moveTimes.txt"));
			writer.write("Player move times:\n\n");
			writer.write(player1Name + ":\n");
			writer.write("Min:" + minTime[0] + "ns  Max: " + maxTime[0] + "ns  Avg: " + avgTime[0] + "ns  Overall: " + overallTime[0] + "ns\n");
			writer.write(player2Name + ":\n");
			writer.write("Min:" + minTime[1] + "ns  Max: " + maxTime[1] + "ns  Avg: " + avgTime[1] + "ns  Overall: " + overallTime[1] + "ns\n");
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void createInformationFile(String name1, String name2, String startedFirst, Player winner, Player looser, int turns, int figureCounterPlayer1, int figureCounterPlayer2, int[] player1Moves, int[] player2Moves, String filePath) {
		try {
			PrintWriter writer = new PrintWriter(new File(filePath + "/roundInformation.txt"));
			writer.write(name1 + " played against " + name2 + "\n\n");
			writer.write(startedFirst + " started first\n\n");
			if(winner == null || looser == null) {
				writer.write("End situation: Draw\n\n");
			}else 
			{
				writer.write("\"End situation: " + winner.getName() + " won the game\n\n");
			}
			writer.write(turns + "turns played\n");
			writer.write(name1 + " lost " + (12 - figureCounterPlayer1) + " figures\n");
			writer.write(name2 + " lost " + (12 - figureCounterPlayer2) + " figures\n\n");
			writer.write("Made moves:\n");
			writer.write(name1 + ":\n");
			writer.write("Steps:" + player1Moves[0]);
			writer.write("Jumps:" + player1Moves[1]);
			writer.write("MuitJumps:" + player1Moves[2]);
			writer.write(name2 + ":\n");
			writer.write("Steps:" + player2Moves[0]);
			writer.write("Jumps:" + player2Moves[1]);
			writer.write("MuitJumps:" + player2Moves[2]);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public static boolean searchForEqualFiles(String directory, String fileName) {
		File files[] = new File(directory).listFiles();
		for(int i = 0;i < files[i].length(); i++) {
			 if(files[i].getName().equals(fileName)) {
				 return true;
			 }
		}
		return false;
}
}
