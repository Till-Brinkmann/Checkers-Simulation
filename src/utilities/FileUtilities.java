package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
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
            writer.flush();
            writer.close();
        }
    }
//	public static void saveGameSitationForEvaluation(Playfield field, Round round) throws FileNotFoundException{
//        	PrintWriter writer = new PrintWriter(new File(round.getRoundsPathString() + "/" + (round.getRound()+1) + ".pfs"));
//        	try {
//				saveGameSituation(field,round.getRoundsPathString(),"" + (round.getRound()+1), writer);
//			} catch (IOException e) {
//				
//			}
//        	writer.write("\n");
//        	writer.write("\ngame name:\n" + round.getManager().getRunName());
//        	writer.write("\n");
//        	writer.write("\nWho is playing?\n" + round.getPlayer1().getName() + " vs. " + round.getPlayer2().getName());
//        	writer.write("\n");
//        	writer.write("Turns: " + (round.getPlayer1turns()+round.getPlayer2turns()) + "\n");
//        	if(round.getInturn() == FigureColor.WHITE) {
//        		writer.write("\nIn turn: White\n");
//        	}
//        	else {
//        		writer.write("\nIn turn: Red\n");
//        	}
//        	writer.write("\nFigureQuantities:\n");
//        	writer.write("\nWhitePieces: "+ String.valueOf(field.getFigureQuantity(FigureColor.WHITE)) + "\n");
//        	writer.write("of it: " + field.getFigureTypeQuantity(FigureColor.WHITE,FigureType.NORMAL) + "Normal Figures and " + field.getFigureTypeQuantity(FigureColor.WHITE,FigureType.KING) + "Kings\n");
//        	writer.write("\nRedPieces: "+ String.valueOf(field.getFigureQuantity(FigureColor.RED)) + "\n");
//        	writer.write("of it: " + field.getFigureTypeQuantity(FigureColor.RED,FigureType.NORMAL) + "Normal Figures and " + field.getFigureTypeQuantity(FigureColor.RED,FigureType.KING) + "Kings\n");
//       
//		writer.flush();
//		writer.close();
//	}
	public static void createTimesFile(float[] minTime, float[]maxTime, float[] avgTime, float[] overallTime,String player1Name,String player2Name, String filePath) {
		try {
			PrintWriter writer = new PrintWriter(new File(filePath + "/moveTimes.txt"));
			writer.write("Player move times:\n\n");
			writer.write(player1Name + ":\n");
			writer.write("Min:" + minTime[0] + "ms  Max: " + maxTime[0] + "ms  Avg: " + avgTime[0] + "ms  Overall: " + overallTime[0] + "ms\n");
			writer.write(player2Name + ":\n");
			writer.write("Min:" + minTime[1] + "ms  Max: " + maxTime[1] + "ms  Avg: " + avgTime[1] + "ms  Overall: " + overallTime[1] + "ms\n");
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

		
	
}
