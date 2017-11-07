package NNStuff;

import checkers.GameLogic;
import gui.Console;
import gui.GUI;

public class NNTrainingManager {
	private GUI gui;
	private Console console;
	private GameLogic gmlc;
	private NNPlayer[] nnPlayer;
	private int nNquantity;
    private double changeQuality = 50;
    private NN net;
    private int[] nnFitness;
    private double weightsMax = 1;
    private double weightsMin = 0;
    private double sigmoidMax = 1;
    private double sigmoidMin = 0;
    public int inputNeurons = 7 * 6;
    public int outputNeurons = 7;
    public int hiddenNeurons = 7 * 6;
    public int hiddenLayer = 10;
    
    //anazahl von Netzen die por Epoche weiterkommen
    public int nnSurviver;
    public int epochs = 0;
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
    	gmlc.startGame(false, "Training", nnPlayer[net2], nnPlayer[net1], 1, 0);
    	evaluateFitness();
    }
    public void change(){
        for (int i = 1; i < nNquantity; i++){
            nnPlayer[i].net.changeAll(changeQuality);
        }
    }
    public void evaluateFitness() {
    	
    }

}
