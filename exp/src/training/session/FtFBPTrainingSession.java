package training.session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import checkers.NNMove;
import checkers.NNPlayfield;
import checkers.Player;
import datastructs.List;
import json.JSONArray;
import json.JSONObject;
import nn.DFF_FC_Backprop;
import nn.NNSpecification;
import opponent.MiniMaxAB;
import training.data.JSONSaveableTrainingSet;
import training.data.TrainingSet;
import util.JSONUtil;

public class FtFBPTrainingSession
extends JSONSaveableTrainingSession {
	
	/**
	 * A JSONSaveable Trainingset with the default loading method implementation
	 * for this type of data.
	 */
	private class DefaultTrainingSet extends JSONSaveableTrainingSet<double[],double[]>{

		public DefaultTrainingSet() {
			super();
		}
		public DefaultTrainingSet(JSONObject save) {
			super(save);
		}
		
		@Override
		protected double[] loadInputfromJSON(JSONObject save) {
			JSONArray a = save.getJSONArray("I");
			double[] input = new double[a.length()];
			for(int i = 0; i < input.length; i++) {
				input[i] = a.getDouble(i);
			}
			return input;
		}

		@Override
		protected double[] loadOutputfromJSON(JSONObject save) {
			JSONArray a = save.getJSONArray("O");
			double[] output = new double[a.length()];
			for(int i = 0; i < output.length; i++) {
				output[i] = a.getDouble(i);
			}
			return output;
		}

		@Override
		protected JSONObject saveInputToJSON(double[] input) {
			return new JSONObject()
					.put("I", new JSONArray(input));
		}

		@Override
		protected JSONObject saveOutputToJSON(double[] output) {
			return new JSONObject()
					.put("O", new JSONArray(output));
		}
	}
	/**
	 * The trainingset.
	 */
	protected DefaultTrainingSet trainingSet;
	/**
	 * Validation set.
	 */
	protected DefaultTrainingSet validationSet;
	
	private final File tsFile = new File(saveDir.getAbsolutePath() + "/Trainingset.json");
	private final File vsFile = new File(saveDir.getAbsolutePath() + "/Validationset.json");
	
	private final File nnFile = new File(saveDir.getAbsolutePath() + "/NN.json");
	
	private static final int TRAINING_SIZE = 1000000;
	private static final int VALIDATION_SIZE = 1000;
	/**
	 * NN to train.
	 */
	private DFF_FC_Backprop nn;
	
	/**
	 * Global counter for the recursive trainingset making.
	 */
	private volatile long tsSize = 0;
	private long amount = 0;
	private Player tsPlayer;
	private boolean tsColor;
	
	/**
	 * NN specifications
	 */
	public static final NNSpecification nnspecs = new NNSpecification(
			64,
			5,
			60,
			64,
			2,
			-1,
			-2,
			4,
			1,
			1);
	
	private double[][][] deltas;
	private double[][] biasDeltas;
	
	/**
	 * This is the learnrate
	 */
	private double epsilon = 0.1;
	
	public FtFBPTrainingSession(File saveDir) {
		super(saveDir);
	}
	
	public FtFBPTrainingSession(File file, String name, int totalEpochs, int saveInterval) {
		super(file, name, totalEpochs, saveInterval);
	}
	
	@Override
	public void beforeTrainingStarts() {
		//load Trainingset (if it does'nt exist: make trainingset)!!
		initDataSets();
		//init trainingset
		trainingSet.toFirst();
		//load nn
		try {
			nn = DFF_FC_Backprop.load(nnFile);
		} catch (FileNotFoundException e) {
			nn = new DFF_FC_Backprop(nnspecs);
			nn.random();
		}
		//init delta array
		//hidden weights
        deltas = new double
        		[nnspecs.hiddenLayerCount + 1]
        		[nnspecs.hiddenNeuronCount]
        		[nnspecs.hiddenNeuronCount];
        //input weights
        deltas[0] = new double
        		[nnspecs.hiddenNeuronCount]
        		[nnspecs.inputs];
        //output weights
        deltas[nnspecs.hiddenLayerCount] = new double
        		[nnspecs.outputs]
        		[nnspecs.hiddenNeuronCount];
        biasDeltas = new double
        		[nnspecs.hiddenLayerCount]
        		[nnspecs.hiddenNeuronCount];
	}
	
	/**
	 * Here the nn is forward propagated ONCE and deltas are collected for one trainingset.
	 */
	@Override
	protected void doEpoch() {
		//forward propagate
		double[] output = nn.run(trainingSet.get().input);
		//delta values of the layer after the current
		double[] lastLayerDel = new double[Math.max(nnspecs.outputs, nnspecs.hiddenNeuronCount)];
		//delta values for the current layer
		double[] thisLayerDel = new double[nnspecs.hiddenNeuronCount];
		//for every outputneuron calculate deltas and then real changes
		for(int o = 0; o < nnspecs.outputs; o++){
			//calculate tmpDelta
			lastLayerDel[o] = deltaOutput(output[o], output[o], trainingSet.get().output[o]);
			//calc real changes for every weight in the last weight matrix
			for(int w = 0; w < nnspecs.hiddenNeuronCount; w++){
				deltas[deltas.length - 1][o][w] += RdeltaW(lastLayerDel[o], nn.outputLevel[nnspecs.hiddenLayerCount - 1][w]);
			}
		}
		//go backwards through every hiddenlayer
		//must be greater than 0 (not reach 0) because we do not need the input layer
		//and for the last hiddenlayer we need a differemt inner for loop (with the size of the inputlayer)
		//start at the index of the last hiddenlayer and end at the first
		for(int layer = nnspecs.hiddenLayerCount - 1; layer > 0; layer--) {
			//for every neuron in this layer
			for(int neuron = 0; neuron < nnspecs.hiddenNeuronCount; neuron++) {
				//outputLevel does not contain the inputlayer, so -1
				thisLayerDel[neuron] = deltaHidden(nn.outputLevel[layer - 1][neuron], nn.weights[layer], neuron, lastLayerDel);
				for(int n = 0; n < nnspecs.hiddenNeuronCount; n++) {
					deltas[layer][neuron][n] += RdeltaW(thisLayerDel[neuron], nn.outputLevel[layer - 1][n]);
				}
			}
			//transfer thisLayer to lastLayer array
			System.arraycopy(thisLayerDel, 0, lastLayerDel, 0, nnspecs.hiddenNeuronCount);
			//save bias changes
			for(int bias = 0; bias < nnspecs.hiddenNeuronCount; bias++) {
				biasDeltas[layer][bias] += thisLayerDel[bias] * epsilon;
			}
		}
		//first hiddenlayer to input
		for(int neuron = 0; neuron < nnspecs.hiddenNeuronCount; neuron++) {
			thisLayerDel[neuron] = deltaHidden(trainingSet.get().input[neuron], nn.weights[0], neuron, lastLayerDel);
			for(int input = 0; input < nnspecs.inputs; input++) {
				deltas[0][neuron][input] += RdeltaW(thisLayerDel[neuron], trainingSet.get().input[input]);
			}
		}
		//save bias changes
		for(int bias = 0; bias < nnspecs.hiddenNeuronCount; bias++) {
			biasDeltas[0][bias] += thisLayerDel[bias] * epsilon;
		}
		trainingSet.next();
	}
	
	@Override
	public void afterEpoch() {
		//TODO validate on validationSet and report error
	}
	
	@Override
	public void afterTrainingStopped() {
		
	}
	/**
	 * For this trainingmode, the save interval is used as the end of the batch.
	 * The calculated changes are applied to the nn.
	 */
	@Override
	public void onSaveInterval() {
		//calculate average, and apply it to weights and biases
		for(int layer = 0; layer < nn.weights.length; layer++) {
			for(int firstN = 0; firstN < nn.weights[layer].length; firstN++) {
				for(int secondN = 0; secondN < nn.weights[layer][firstN].length; secondN++) {
					nn.weights[layer][firstN][secondN] +=  deltas[layer][firstN][secondN] / saveInterval;
					deltas[layer][firstN][secondN] = 0;
				}
			}
		}
		for(int hLayer = 0; hLayer < nnspecs.hiddenLayerCount; hLayer++) {
			for(int neuron = 0; neuron < nnspecs.hiddenNeuronCount; neuron++) {
				nn.bias[hLayer][neuron] += biasDeltas[hLayer][neuron] / saveInterval;
				biasDeltas[hLayer][neuron] = 0;
			}
		}
		//save nn for safety
		try {
			nn.save(nnFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		double error = 0;
		for(validationSet.toFirst(); validationSet.hasAccess(); validationSet.nextNoWrap()) {
			double[] output = nn.run(validationSet.get().input);
			for(int i = 0; i < output.length; i++) {
				error += Math.pow(validationSet.get().output[i] - output[i], 2);
			}
		}
		System.out.println(error / VALIDATION_SIZE);
	}
	
	/**
     * calculates the real change of a weight.
     */
    public double RdeltaW(double delta, double output){
    	return epsilon * delta * output;
    }
    /**
     * delta for the output layer weights.
     * This is also equal to the formula of the real change to biases.
     * @param outputLevel
     * @param output
     * @param wantedOutput
     * @return
     */
    public double deltaOutput(double outputLevel, double output, double wantedOutput){
    	return DFF_FC_Backprop.fastActivationDev(outputLevel) * (wantedOutput - output);
    }
    /**
     * delta of one weight in a hidden layer.
     * @param outputLevel 
     * @param weights
     * @param deltas
     * @return
     */
    public double deltaHidden(double outputLevel, double[][] weights, final int index, double[] deltas){
    	double sum = 0;
    	for (int i = 0; i < weights.length; i++){
    		sum += weights[i][index] * deltas[i];
    	}
    	return DFF_FC_Backprop.fastActivationDev(outputLevel) * sum;
    }
    
    public double deltaInput(double input, double[][] weights, final int index, double[] deltas) {
    	double sum = 0;
    	for (int i = 0; i < weights.length; i++){
    		sum += weights[i][index] * deltas[i];
    	}
    	return input * sum;
    }
	
	private void initDataSets() {
		try {
			trainingSet = new DefaultTrainingSet(JSONUtil.loadJOFromFile(tsFile));
		} catch (IOException e) {
			trainingSet = new DefaultTrainingSet();
			//red moves
			fillTrainingSet(new MiniMaxAB(), NNPlayfield.startPosition(), (TRAINING_SIZE + VALIDATION_SIZE)/2, true);
			//white moves
			fillTrainingSet(new MiniMaxAB(), NNPlayfield.startPosition(), (TRAINING_SIZE + VALIDATION_SIZE)/2, false);
			//shuffle da beat!
			System.out.println("Starting shuffle");
			trainingSet.shuffle();
			System.out.println("First shuffle done");
			trainingSet.shuffle();
			System.out.println("Finissimo");
			//save
			try {
				trainingSet.saveToFile(tsFile, 0, DEFAULT_CHARSET);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		//TODO init valSet
		try {
			validationSet = new DefaultTrainingSet(JSONUtil.loadJOFromFile(vsFile));
		} catch (IOException e) {
			validationSet = new DefaultTrainingSet();
			trainingSet.toFirst();
			for(int i = 0; i < VALIDATION_SIZE; i++) {
				validationSet.addTrainingExample(trainingSet.get());
				trainingSet.remove();
				trainingSet.next();
			}
			try {
				validationSet.saveToFile(vsFile, 0, DEFAULT_CHARSET);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Recursively builds a trainingset containing possible moves
	 * made by the given Player {@code player}.
	 * The Player plays both as white and red, so half of the moves in the data are white and half red.
	 * @param player The Player to use when generating situations
	 * @param field
	 * TODO 
	 */
	private void fillTrainingSet(Player player, NNPlayfield field, int amount, boolean color) {
		tsPlayer = player;
		tsSize = 0;
		this.amount = amount;
		tsColor = color;
		fillTrainingSet(field);
	}
	private void fillTrainingSet(NNPlayfield field) {
		List<NNMove> moves;
		NNPlayfield fieldBefore;
		NNPlayfield tmpField;
		if(tsColor) {//color is red
			if(tsSize >= amount) return;
			tmpField = field.copy();
			//make own move
			fieldBefore = tmpField.copy();
			tsPlayer.prepare(true, tmpField);
			tmpField.executeMove(tsPlayer.requestMove());
			trainingSet.addTrainingExample(pfToVec(fieldBefore, tsColor), pfToVec(tmpField, tsColor));
			tsSize++;
			//do all enemy moves
			//fieldBefore is used as another temp variable, it is not really the field before the move
			moves = NNMove.getPossibleMovesFor(false, tmpField);
			for(moves.toFirst(); moves.hasAccess(); moves.next()) {
				fieldBefore = tmpField.copy();
				//exec enemy move
				fieldBefore.executeMove(moves.get());
				fillTrainingSet(fieldBefore);
			}
			return;
		}
		//case: color is white
		//make red opponent moves
		moves = NNMove.getPossibleMovesFor(true, field);
		for(moves.toFirst(); moves.hasAccess(); moves.next()) {
			if(tsSize >= amount) return;
			tmpField = field.copy();
			//make the red move
			tmpField.executeMove(moves.get());
			//copy the situation before
			fieldBefore = tmpField.copy();
			//execute the move of player as white
			tsPlayer.prepare(false, tmpField);
			tmpField.executeMove(tsPlayer.requestMove());
			trainingSet.addTrainingExample(pfToVec(fieldBefore, tsColor), pfToVec(tmpField, tsColor));
			tsSize++;
			
			//recursively continue the game
			fillTrainingSet(tmpField);
		}
	}
	/**
	 * Lets two players play against each other
	 * and adds the moves of one of them to the trainingset.
	 * @param red The red player.
	 * @param white The white player.
	 * @param color the color of the player to record the moves from.
	 */
	@SuppressWarnings("unused")
	private void fillTrainingSetOld(TrainingSet<double[],double[]> set, Player red, Player white, boolean color) {
		NNPlayfield fieldBefore = null;
		NNPlayfield field;
		field = NNPlayfield.startPosition();
		red.prepare(color, field);
		white.prepare(color, field);
		//do at most 100 moves
		for(int moveNum = 0; moveNum < 100; moveNum++) {
			if(color) fieldBefore = field.copy();
			//red starts
			NNMove m = red.requestMove();
			//should never happen but we dont want any wrong moves in the training data
			if(!NNMove.testMove(m, field)) {
				break;
			}
			field.executeMove(m);
			if(color) {
				//add the example
				set.addTrainingExample(pfToVec(fieldBefore, color), pfToVec(field, color));
			}
			if(!NNMove.isMovingPossible(false, field)) {
				break;
			}
			if(!color) fieldBefore = field.copy();
			//make move of white
			m = white.requestMove();
			if(!NNMove.testMove(m, field)) {
				break;
			}
			field.executeMove(m);
			if(!color) set.addTrainingExample(pfToVec(fieldBefore, color), pfToVec(field, color));
			if(!NNMove.isMovingPossible(false, field)) {
				break;
			}
		}
	}
	/**
	 * Turns a playfield into an inputVector.
	 * This returns an inputVector were the NN is always at the BOTTOM.
	 */
	public double[] pfToVec(NNPlayfield in, boolean color) {
		double[] vector = new double[64];
		for(byte i = 0; i < 32; i++) {
			if(in.isOccupiedByColor(i, color)) {
				if(in.isKing(i)) vector[32 + i] = 1;
				else vector[i] = 1;
			}
			else if(in.isOccupiedByColor(i, !color)) {
				if(in.isKing(i)) vector[32 + i] = -1;
				else vector[i] = -1;
			}
		}
		if(!color) {//if the nn plays as white its figures are not located at the bottom. 
			//first half is flipped
			double[] flippedVector = flipVector(Arrays.copyOfRange(vector, 0, 32));
			for(int i = 0; i < 32; i++) {
				vector[i] = flippedVector[i];
			}
			flippedVector = flipVector(Arrays.copyOfRange(vector, 32, 64));
			for(int i = 0; i < 32; i++) {
				vector[i + 32] = flippedVector[i];
			}
		}
		return vector;
	}
	
	/**
	 * TODO maybe outsource to a math or util class.
	 * turns the given vector around its middlepoint.
	 * Therefore it must be of even length.
	 * @param v
	 * @return
	 */
	public static double[] flipVector(double[] v) {
		int length = v.length;
		//not possible to flip
		if(length % 2 == 1) return null;
		double[] t = new double[length];
		for(int i = 0; i < length; i++) {
			t[i] = v[length - 1 - i];
		}
		return t;
	}

	@Override
	public void save() throws IOException {
		try {
			//save the Session.json
			this.saveToFile();
			//TODO traingsets are saved in initTrainingset
//			//save trainingsets
//			((JSONSaveableTrainingSet<double[], double[]>) trainingSet).saveToFile(
//					tsFile,
//					DEFAULT_INDENT_FACTOR,
//					DEFAULT_CHARSET
//					);
//			((JSONSaveableTrainingSet<double[], double[]>) validationSet).saveToFile(
//					vsFile,
//					DEFAULT_INDENT_FACTOR,
//					DEFAULT_CHARSET
//					);
		} catch (IOException e) {
			throw new IOException("Unable to save the session.", e);
		}
	}
	/**
	 * Converts fields of this object to a json object.
	 * TODO Add possible new params to the save.
	 */
	@Override
	public JSONObject toJSONObject() {
		return super.toJSONObject();
	}
}
