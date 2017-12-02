package NNStuff;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

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
	private int nNquantity = 4;
    private double changeQuality = 50;
    private NN net;
    private int[] nnFitness;
    private double weightsMax = 1;
    private double weightsMin = 0;
    private double sigmoidMax = 1;
    private double sigmoidMin = 0;
    public int inputNeurons = 64;
    public int outputNeurons = 64;
    public int hiddenNeurons = 64;
    public int hiddenLayer = 10;
    
    //anazahl von Netzen die per Epoche weiterkommen
    public int nnSurviver;
    public int epochs = 1;
	public NNTrainingManager(GUI pGui) {
		gui = pGui;
		console = gui.console;
		gmlc = gui.getGameLogic();
		//just a playfield with the startposition to make copies for all games
		thePlayfield = new Playfield();
		try{
			thePlayfield.createStartPosition();
		}
		catch(IOException e){
			console.printWarning("Unable to load THE Playfield", "NNMan");
		}
		nnPlayer = new NNPlayer[nNquantity];
		for(int i = 0; i < nNquantity; i++) {
			net = new NN(inputNeurons, outputNeurons, hiddenNeurons, hiddenLayer, sigmoidMin, sigmoidMax);
			nnPlayer[i] = new NNPlayer(gmlc, console,net);
			nnPlayer[i].net.randomWeights(weightsMax,weightsMin);
		}
		train(epochs);
	}
    public void train(int epochs){

        for (int i = 0; i < epochs; i++){
            console.printInfo((i+1) + "/" + epochs,"NNTrainingManager");
            theBest();
            change();
        }
    }
    public void theBest(){
    	allVSall();
        int[] highestNumbers = new int[nnSurviver];
        for (int i = 0; i < nnSurviver; i++){
            highestNumbers[i] = i;
        }
        int temp;
        for (int i = 0; i < nnSurviver; i++){
            temp = nnFitness[highestNumbers[i]];
            for (int x = 0; x < nNquantity; x++){
                if (nnFitness[x] > temp){
                    boolean choosen = false;
                    for (int z = 0; z < i; z++){
                        if (highestNumbers[z] == x){
                            choosen = true;
                        }
                    }
                    if (!choosen){
                        highestNumbers[i] = x;
                       temp = nnFitness[highestNumbers[i]];
                    }
                }
            }
            console.printInfo(i + ": " + highestNumbers + " " + nnFitness[highestNumbers[i]],"NNTrainingManager");
        }
        
        double[][][] afterInputWeights = new double[nnSurviver][hiddenNeurons][inputNeurons];
        double[][][][] hiddenWeights = new double[nnSurviver][hiddenLayer-1][hiddenNeurons][hiddenNeurons];
        double[][][] toOutputWeights = new double[nnSurviver][outputNeurons][hiddenNeurons];
        for (int i = 0; i < nnSurviver; i++){
            afterInputWeights[i] = nnPlayer[highestNumbers[i]].net.getAfterInputWeights();
            hiddenWeights[i] = nnPlayer[highestNumbers[i]].net.getHiddenWeights();
            toOutputWeights[i] = nnPlayer[highestNumbers[i]].net.getToOutputWeights();
        }
        for (int i = 0; i < nnSurviver; i++){
            for (int x = i * ( nNquantity / nnSurviver ); x < (i+1) * ( nNquantity / nnSurviver ); x++){
                nnPlayer[x].net.setWeights(afterInputWeights[i], hiddenWeights[i], toOutputWeights[i]);
            }
        }
    }
    public void allVSall(){
    	nnFitness = new int[nNquantity];
        for (int i = 0; i < nNquantity; i++){
            for (int x = 0; x < nNquantity; x++){
            	if(i != x) {
            		int i2 = i;
            		int x2 = x;
            		//ForkJoinPool.commonPool().execute(new Runnable(){
                		//public void run(){
                			netVSnet(i2,x2);
                	//	}
                //	});
            	}
            }
        }
        ForkJoinPool.commonPool().awaitQuiescence(1, TimeUnit.DAYS);
    }
    private void netVSnet(int net1, int net2)
    {
    	GameLogic gl = new GameLogic(thePlayfield.copy());
    	gl.linkGUI(gui);
    	nnPlayer[net1].setGL(gl);
    	nnPlayer[net2].setGL(gl);
    	gl.startGame(false, "Training", nnPlayer[net2], nnPlayer[net1], 1, 0, false);
       	if(FigureColor.RED == nnPlayer[net1].getFigureColor()) {
    		evaluateFitness(net1,net2, gl);
    	}
    	else {
    		evaluateFitness(net2, net1, gl);

    	}
    	
    }
    public void change(){
        for (int i = 1; i < nNquantity; i++){
            nnPlayer[i].net.changeAllPercent(changeQuality);
        }
    }
    public void evaluateFitness(int startedNet, int secondNet, GameLogic gl) {
    	Situations situation = gl.getFinalSituation();
    	boolean failed = gl.getFailed();
    	switch(situation) {
		case DRAW:
			nnFitness[startedNet] += 20;
			nnFitness[secondNet] += 20;
			break;
		case WHITEWIN:
			nnFitness[secondNet] += 100;
			if(failed) {
				nnFitness[startedNet] += -500;
			}
			break;
		case REDWIN:
			nnFitness[startedNet] += 80;
			if(failed) {
				nnFitness[secondNet] += -500;
			}
			break;
		default:			 
			console.printInfo("NNTrainingManager", "game was either stopped or paused");
			return;
    	}
    	nnFitness[startedNet] += gl.getPlayfield().getFigureQuantity(FigureColor.RED);
    	nnFitness[secondNet] += gl.getPlayfield().getFigureQuantity(FigureColor.WHITE);
    	//console.printInfo("NNTrainingManager",nnFitness[startedNet] , nnFitness[secondNet]);
    	console.print("Net " + startedNet + " scored: " + nnFitness[startedNet]);
    }

}
