package nn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.GameLogic.Situations;
import checkers.Playfield;
import gui.Console;
import gui.GUI;

public class NNTrainingManager {
	private GUI gui;
	private Console console;
	private GameLogic gmlc;
	private Playfield thePlayfield;
	private NNPlayer[] nnPlayer;
	private int nnQuantity = 20;
    private double changePercentage = 40;
    private double weightsMax;
    private double weightsMin;
    private double sigmoidMax;
    private double sigmoidMin;
    public int inputNeurons;
    public int outputNeurons;
    public int hiddenNeurons;
    public int hiddenLayer;
    
    int notFailedCount = 0;
    
    public final Comparator<NNPlayer> nNPlayerComparator;
    //anzahl von Netzen die pro Epoche weiterkommen
    public int nnSurviver = 10;
    public int epochs = 200;
    
	public NNTrainingManager(boolean continueT, GUI pGui, int epochs, int quantity, int nnSurviver, int inputNeurons, int outputNeurons,
			                 int hiddenNeurons, int hiddenLayer, double sigmoidMin, double sigmoidMax, double weightsMin, double weightsMax) {
		nNPlayerComparator = new Comparator<NNPlayer>(){			
			@Override
			public int compare(NNPlayer o1, NNPlayer o2) {
				if(o1.fitness > o2.fitness){
					// we want descending order so 1 and -1 are swapped
					return -1;
				}
				else if(o1.fitness < o2.fitness){
					return 1;
				}
				return 0;
			}
			
		};
		this.inputNeurons = inputNeurons;
		this.outputNeurons = outputNeurons;
		this.hiddenNeurons = hiddenNeurons;
		this.hiddenLayer = hiddenLayer;
		this.sigmoidMin = (double)sigmoidMin;
		this.sigmoidMax = (double)sigmoidMax;
		this.weightsMin = (double)weightsMin;
		this.weightsMax = (double)weightsMax;
		gui = pGui;
		console = gui.console;
		gmlc = gui.getGameLogic();
		this.epochs = epochs;
		nnQuantity = quantity;
		this.nnSurviver = nnSurviver;
		//just a playfield with the startposition to make copies for all games
		thePlayfield = new Playfield();
		try{
			thePlayfield.createStartPosition();
		}
		catch(IOException e){
			console.printWarning("Unable to load THE Playfield", "NNMan");
		}
		nnPlayer = new NNPlayer[nnQuantity];
		for(int i = 0; i < nnQuantity; i++) {
			nnPlayer[i] = new NNPlayer(
					gmlc,
					console,
					new NN(inputNeurons, outputNeurons, hiddenNeurons, hiddenLayer, sigmoidMin, sigmoidMax, weightsMin, weightsMax));
			nnPlayer[i].net.randomWeights();
		}
		//TODO wenn alte nnDaten == isDa dann Bin LADEN!
		train(epochs);
	}
    public void train(int epochs){

        for (int i = 0; i < epochs; i++){
            console.printInfo((i+1) + "/" + epochs,"NNTrainingManager");
            theBest();
            for(int s = 0; s < nnSurviver; s++){
            	saveNN(nnPlayer[s].net.getAfterInputWeights(), nnPlayer[s].net.getHiddenWeights(), nnPlayer[s].net.getToOutputWeights(), "I am the best! (Nr." + s + ")");
            }
        }
        console.printInfo("Stats: \nNotFailed: " + notFailedCount, "NNTM");
    }
    public void theBest(){
    	allVSall();
    	NNPlayer tmp;
    	int oldIndex;
    	int newIndex;
    	for(int i = 0; i < nnQuantity; i++){
    		oldIndex = (int)Math.round(Math.random()*(nnQuantity-1));
    		tmp = nnPlayer[oldIndex];
    		newIndex = (int)Math.round(Math.random()*(nnQuantity-1));
    		nnPlayer[oldIndex] = nnPlayer[newIndex];
    		nnPlayer[newIndex] = tmp;
    	}
    	for(NNPlayer p : nnPlayer){
    		p.fitness = 0;
    	}
    }
    public void allVSall(){
        for (int n = 0; n < nnQuantity; n++){
            for (int x = 0; x < nnQuantity; x++){
            	//don't play against yourself
            	if(n != x) {
                	netVSnet(n,x);
            	}
            }
            Arrays.parallelSort(nnPlayer, nNPlayerComparator);
        	//refill poulation and reset fitness (because it is a "new" NN now) 
        	for(int i = nnSurviver; i < nnQuantity; i++){
        		nnPlayer[i].net.randomWeights();
        		nnPlayer[i].net.changeAllPercent(changePercentage);
        		
        	}
        	//mutate(make next generation) survivors and reset fitness
        	for(int i = 0; i < nnSurviver; i++){
        		console.printInfo("BestNet #" + i + " scored: " + nnPlayer[i].fitness);
        		nnPlayer[i].net.childFrom(randomSurvivor().net, randomSurvivor().net);
        	}
        	//mix it again to give nets that have equal scores a better chance
        	
        }
    }
    private void netVSnet(int net1, int net2)
    {
    	GameLogic gl = new GameLogic(thePlayfield.copy());
    	gl.linkGUI(gui);
    	nnPlayer[net1].setGL(gl);
    	nnPlayer[net2].setGL(gl);
    	gl.startGame(false, "Training", nnPlayer[net2], nnPlayer[net1], 1, 0, false);
       	if(FigureColor.RED == nnPlayer[net1].getFigureColor()) {
    		evaluateFitness(nnPlayer[net2], nnPlayer[net1], gl);
    	}
    	else {
    		evaluateFitness(nnPlayer[net1], nnPlayer[net2], gl);

    	}
    	
    }
    public void change(){
        for (int i = 1; i < nnQuantity; i++){
            nnPlayer[i].net.changeAllPercent(changePercentage);
        }
    }
    public void evaluateFitness(NNPlayer startedNet, NNPlayer secondNet, GameLogic gl) {
    	Situations situation = gl.getFinalSituation();
    	boolean failed = gl.getFailed();
    	if((gl.getTurnCountRed() + gl.getTurnCountWhite()) > 1){
    		notFailedCount++;
    	}   	
    	switch(situation) {
		case DRAW:
			startedNet.fitness += 20;
			secondNet.fitness += 20;
			break;
		case WHITEWIN:
			if(failed) {
				startedNet.fitness += -200;
			}
			else{
				secondNet.fitness += 500;
			}
			break;
		case REDWIN:
			if(failed) {
				secondNet.fitness += -200;
			}
			else{
				startedNet.fitness += 500;
			}
			break;
		default:			 
			console.printInfo("NNTrainingManager", "game was either stopped or paused");
			return;
    	}
    	startedNet.fitness += gl.getTurnCountRed()*10;
    	secondNet.fitness += gl.getTurnCountWhite()*10;
    	//TODO this does not do anything because the playfield is cleared after the game
    	startedNet.fitness += gl.getPlayfield().getFigureQuantity(FigureColor.RED);
    	secondNet.fitness += gl.getPlayfield().getFigureQuantity(FigureColor.WHITE);
    	console.print("Net scored: " + startedNet.fitness);
    }
    
    private NNPlayer randomSurvivor(){
    	long index = Math.round(Math.random()*(nnSurviver-1));
    	return nnPlayer[(int)index];
    }

    private void saveNN(double[][] aIW, double[][][] hW, double[][] tOW, String name){
    	File dir = new File("resources/NNSave");
    	if(!dir.exists()){
			dir.mkdirs();
    	}
    	File file = new File("resources/NNSave/" + name  + "info" + ".nni");
    	try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			writer.write("\nweightsMax:\n");
			writer.write(Double.toString(weightsMax));
			writer.write("\n weightsMin:\n");
			writer.write(Double.toString(weightsMin));
			writer.write("\n sigmoidMax:\n");
			writer.write(Double.toString(sigmoidMax));
			writer.write("\n sigmoidMin:\n");
			writer.write(Double.toString(sigmoidMin));
			writer.write("\n inputNeurons:\n");
			writer.write(Double.toString(inputNeurons));
			writer.write("\n outputNeurons:\n");
			writer.write(Double.toString(outputNeurons));
			writer.write("\n hiddenNeurons:\n");
			writer.write(Double.toString(hiddenNeurons));
			writer.write("\n hiddenLayer:\n");
			writer.write(Double.toString(hiddenLayer));
			writer.write("\n afterInputWeigths:\n");
	    	for(double[] ws : aIW){
	    		for(double w : ws){
	    			writer.write(Double.toString(w) + "\n");	    			
	    		}
	    	}
	    	writer.write("\n hiddenWeights:\n");
	    	for(double[][] l : hW){
	    		for(double[] ws : l){
		    		for(double w : ws){
		    			writer.write(Double.toString(w) + "\n");
		    		}
		    	}
	    	}
	    	writer.write("\n toOutputWeights:\n");
	    	for(double[] ws : tOW){
	    		for(double w : ws){
	    			writer.write(Double.toString(w) + "\n");
	    		}
	    	}
	    	writer.flush();
	    	writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	

    }
}
