package NNStuff;

import checkers.Figure.FigureColor;
import checkers.GameLogic;
import checkers.GameLogic.Situations;
import gui.Console;
import gui.GUI;

public class NNTrainingManager {
	private GUI gui;
	private Console console;
	private GameLogic gmlc;
	private NNPlayer[] nnPlayer;
	private int nNquantity = 100;
    private double changeQuality = 50;
    private NN net;
    private int[] nnFitness;
    private double weightsMax = 1;
    private double weightsMin = 0;
    private double sigmoidMax = 1;
    private double sigmoidMin = 0;
    public int inputNeurons = 64;
    public int outputNeurons = 36;
    public int hiddenNeurons = 64;
    public int hiddenLayer = 10;
    
    //anazahl von Netzen die por Epoche weiterkommen
    public int nnSurviver;
    public int epochs = 10;
	public NNTrainingManager(GUI pGui) {
		gui = pGui;
		console = gui.console;
		gmlc = gui.getGameLogic();
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
        	nnFitness[i] = 0;
        }
        for (int i = 0; i < nNquantity; i++){
            for (int x = 0; x < nNquantity; x++){
            	if(i != x) {
            		netVSnet(i,x);
            	
            	}
            }
        }
    }
    private void netVSnet(int net1, int net2)
    {
    	gmlc.startGame(false, "Training", nnPlayer[net2], nnPlayer[net1], 1, 0, false);
    	if(FigureColor.RED == nnPlayer[net1].getFigureColor()) {
    		evaluateFitness(net1,net2);
    	}
    	else {
    		evaluateFitness(net2, net1);

    	}
    	
    }
    public void change(){
        for (int i = 1; i < nNquantity; i++){
            nnPlayer[i].net.changeAll(changeQuality);
        }
    }
    public void evaluateFitness(int startedNet, int secondNet) {
    	Situations situation = gmlc.getFinalSituation();
    	boolean failed = gmlc.getFailed();
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
			console.printInfo("NNTrainingManager", " situation in evaluateFintness transfered wrong enum!");
			return;
    	}
    	nnFitness[startedNet] += gmlc.getPlayfield().getFigureQuantity(FigureColor.RED);
    	nnFitness[secondNet] += gmlc.getPlayfield().getFigureQuantity(FigureColor.WHITE);
    	//console.printInfo("NNTrainingManager",nnFitness[startedNet] , nnFitness[secondNet]);
    }

}
