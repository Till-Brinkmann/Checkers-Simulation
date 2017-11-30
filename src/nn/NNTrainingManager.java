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
    private double changePercentage = 50;
    private double weightsMax = 10;
    private double weightsMin = -10;
    private double sigmoidMax = 1;
    private double sigmoidMin = -1;
    public int inputNeurons = 64;
    public int outputNeurons = 64;
    public int hiddenNeurons = 64;
    public int hiddenLayer = 10;
    
    int notFailedCount = 0;
    
    public final Comparator<NNPlayer> nNPlayerComparator;
    //anzahl von Netzen die per Epoche weiterkommen
    public int nnSurviver = 10;
    public int epochs = 200;
    
	public NNTrainingManager(GUI pGui, int epochs, int quantity, int nnSurviver) {
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
            //change();
        }
        console.printInfo("Stats: \nNotFailed: " + notFailedCount, "NNTM");
    }
    public void theBest(){
    	allVSall();
    	Arrays.parallelSort(nnPlayer, nNPlayerComparator);
    	//refill poulation and reset fitness (because it is a "new" NN now) 
    	for(int i = nnSurviver; i < nnQuantity; i++){
    		nnPlayer[i].net.randomWeights();
    		nnPlayer[i].net.changeAllPercent(changePercentage);
    		nnPlayer[i].fitness = 0;
    	}
    	//mutate(make next generation) survivors and reset fitness
    	for(int i = 0; i < nnSurviver; i++){
    		console.printInfo("BestNet #" + i + " scored: " + nnPlayer[i].fitness);
    		nnPlayer[i].net.childFrom(randomSurvivor().net, randomSurvivor().net);
    		nnPlayer[i].fitness = 0;
    	}
    	//mix it again to give nets that have equal scores a better chance
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
    	return;
    }
    public void allVSall(){
        for (int i = 0; i < nnQuantity; i++){
            for (int x = 0; x < nnQuantity; x++){
            	if(i != x) {
                	netVSnet(i,x);
            	}
            }
        }
        //ForkJoinPool.commonPool().awaitQuiescence(1, TimeUnit.DAYS);
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
    	if(gl.getTurnCount() > 1){
    		notFailedCount++;
    	}
    	switch(situation) {
		case DRAW:
			startedNet.fitness += 20;
			secondNet.fitness += 20;
			break;
		case WHITEWIN:
			secondNet.fitness += 100;
			if(failed) {
				startedNet.fitness += -500;
			}
			break;
		case REDWIN:
			startedNet.fitness += 80;
			if(failed) {
				secondNet.fitness += -500;
			}
			break;
		default:			 
			console.printInfo("NNTrainingManager", "game was either stopped or paused");
			return;
    	}
    	startedNet.fitness += gl.getPlayfield().getFigureQuantity(FigureColor.RED);
    	secondNet.fitness += gl.getPlayfield().getFigureQuantity(FigureColor.WHITE);
    	//console.printInfo("NNTrainingManager",nnFitness[startedNet] , nnFitness[secondNet]);
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
    	File file = new File("resources/NNSave/" + name);
    	try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			//inputs
			writer.write("{\n");
	    	for(double[] ws : aIW){
	    		writer.write("{\n");
	    		for(double w : ws){
	    			writer.write(Double.toString(w) + ", ");
	    		}
	    		writer.write("}\n");
	    	}
	    	writer.write("}\n");
	    	//hidden
	    	writer.write("{\n");
	    	for(double[][] l : hW){
	    		writer.write("{\n");
	    		for(double[] ws : l){
		    		writer.write("{\n");
		    		for(double w : ws){
		    			writer.write(Double.toString(w) + ", ");
		    		}
		    		writer.write("}\n");
		    	}
	    	writer.write("}\n");
	    	}
	    	writer.write("}\n");
	    	//outputs
	    	writer.write("{\n");
	    	for(double[] ws : tOW){
	    		writer.write("{\n");
	    		for(double w : ws){
	    			writer.write(Double.toString(w) + ", ");
	    		}
	    		writer.write("}\n");
	    	}
	    	writer.write("}\n");
	    	writer.flush();
	    	writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
