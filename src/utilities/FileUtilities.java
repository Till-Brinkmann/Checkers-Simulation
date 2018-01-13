package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import checkers.Figure.FigureColor;
import checkers.Figure.FigureType;
import checkers.GameLogic.Situations;
import datastructs.List;
import checkers.Figure;

import checkers.Playfield;
/**
 * This static class provides useful methods for loading or saving different files and objects.
 */
public class FileUtilities {
	
	public static Figure[][] loadGameSituation(File file, Playfield playfield) throws IOException{
		Figure[][] field = new Figure[8][8];

		FileReader reader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(reader);
		try {
			String info = bufferedReader.readLine();
			if(Integer.parseInt(info) != playfield.SIZE){
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
		} finally {
			bufferedReader.close();
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
	public static void createTimesFile(double[] minTime, double[]maxTime, double[] avgTime, double[] overallTime,String player1Name,String player2Name, String filePath, double gameTime) {
		try {
			PrintWriter writer = new PrintWriter(new File(filePath + "/moveTimes.txt"));
			writer.write("Player move times:\n\n");
			writer.write(player1Name + ":\n");
			writer.write("Min:" + minTime[0] + "ns  Max: " + maxTime[0] + "ns  Avg: " + avgTime[0] + "ns  Overall: " + overallTime[0] + "ns\n");
			writer.write(player2Name + ":\n");
			writer.write("Min:" + minTime[1] + "ns  Max: " + maxTime[1] + "ns  Avg: " + avgTime[1] + "ns  Overall: " + overallTime[1] + "ns\n");
			writer.write("Length of the entire game:" + gameTime + "ms\n");
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static boolean searchForEqualFiles(String directory, String fileName) {
		File[] files = new File(directory).listFiles();
		for(int i = 0;i < files.length; i++) {
			 if(files[i].getName().equals(fileName)) {
				 return true;
			 }
		}
		return false;
	}
	public static void createGameSummaryFile(Situations endSituation, boolean failed, String name, String name2,
			String absolutePath, int stepCount[], int jumpCount[], int multijumpCount[], int[] movePossibilitiesAvg, int[] movePossibilitiesOverall) {
		try {
			PrintWriter writer = new PrintWriter(new File(absolutePath + "/gameSummary.txt"));
			writer.write("RedPlayer: " + name + "\nWhitePlayer: " + name2 + "\n\n");
			switch(endSituation) {
			case DRAW:
				writer.write("The result was a draw.\n");
				break;
			case REDWIN:
				writer.write(name +" won the game.\n");
				break;
			case STOP:
				writer.write("Game was stopped.\n");
				break;
			case WHITEWIN:
				writer.write(name2 + "won the game.\n");
				break;
			case NOTHING:
				break;			
			}
			if(failed) {
				writer.write("The loser did a wrong move\n\n");
			}
			writer.write("Counts " + name + ":\n");
			writer.write("Steps: " + stepCount[0] + "\n");
			writer.write("Jumps: " + jumpCount[0] + "\n");
			writer.write("MultiJumps: " + multijumpCount[0] + "\n");
			writer.write("Move possibilities overall: " + movePossibilitiesOverall[0] + "\n");	
			writer.write("Move possibilities avg: " + movePossibilitiesAvg[0] + "\n\n");
			writer.write("Counts " + name2 + ":\n");
			writer.write("Steps: " + stepCount[1] + "\n");
			writer.write("Jumps: " + jumpCount[1] + "\n");
			writer.write("MultiJumps: " + multijumpCount[1] + "\n");
			writer.write("Move possibilities overall: " + movePossibilitiesOverall[1] + "\n");	
			writer.write("Move possibilities avg: " + movePossibilitiesAvg[0] + "\n\n");
			writer.write("");
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public static URL[] getAiUrls() {
		List<URL> urls = new List<URL>();
		try {
			File aiDir = new File("resources/AI");
			urls.append(aiDir.toURI().toURL());
			File[] files = aiDir.listFiles();
			for(File f : files) {
				if(f.getName().endsWith(".jar")) {
					urls.append(f.toURI().toURL());
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URL[] urlArray = new URL[urls.length];
		urls.toFirst();
		for(int i = 0; i < urls.length; i++) {
			urlArray[i] = urls.get();
			urls.next();
		}
		return urlArray;
	}



}
