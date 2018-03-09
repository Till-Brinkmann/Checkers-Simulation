package training.session;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import checkers.NNMove;
import checkers.NNPlayfield;
import checkers.Player;
import json.JSONArray;
import json.JSONObject;
import nn.DFF_FC_Backprop;
import opponent.MiniMaxAB;
import opponent.RandomAI;
import training.data.JSONSaveableTrainingSet;
import training.data.TrainingSet;
import util.JSONUtil;

public class FtFBPTrainingSession
extends BackPropagationTrainingSession<double[], NNPlayfield, double[], NNPlayfield> {
	
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
			JSONArray a = save.getJSONArray("Input");
			double[] input = new double[a.length()];
			for(int i = 0; i < input.length; i++) {
				input[i] = a.getDouble(i);
			}
			return input;
		}

		@Override
		protected double[] loadOutputfromJSON(JSONObject save) {
			JSONArray a = save.getJSONArray("Output");
			double[] output = new double[a.length()];
			for(int i = 0; i < output.length; i++) {
				output[i] = a.getDouble(i);
			}
			return output;
		}

		@Override
		protected JSONObject saveInputToJSON(double[] input) {
			return new JSONObject()
					.put("Input", new JSONArray(input));
		}

		@Override
		protected JSONObject saveOutputToJSON(double[] output) {
			return new JSONObject()
					.put("Output", new JSONArray(output));
		}
		
	}
	
	private final File tsFile = new File(saveDir.getAbsolutePath() + "/Trainingset.json");
	private final File vsFile = new File(this.saveDir.getAbsolutePath() + "/Validationset.json");
	/**
	 * NN to train.
	 */
	private DFF_FC_Backprop nn;
	
	/**
	 * Used by {@code encodeInput()} to set the right values and determine if the field should be flipped
	 */
	private boolean nnColor;
	
	public FtFBPTrainingSession(File saveDir) {
		super(saveDir);
		// TODO Auto-generated constructor stub
	}
	
	private void initDataSets() {
		try {
			ts = new DefaultTrainingSet(JSONUtil.loadJOFromFile(tsFile));
		} catch (IOException e) {
			e.printStackTrace();
			ts = new DefaultTrainingSet();
			//let a MiniMax play against RandomAi and record playfields before and after move
			for(int i = 0; i < 10000; i++) {
				fillTrainingSet(ts, new MiniMaxAB(), new RandomAI(), true);
				//fillTrainingSet(mm, rand, false);
			}
		}
		//TODO maybe the validationSet should change every epoch and this is not neccessary anymore
		try {
			validationSet = new DefaultTrainingSet(JSONUtil.loadJOFromFile(vsFile));
		} catch (IOException e) {
			e.printStackTrace();
			validationSet = new DefaultTrainingSet();
			//let a MiniMax play against RandomAi and record playfields before and after move
			for(int i = 0; i < 10; i++) {
				fillTrainingSet(validationSet, new RandomAI(), new RandomAI(), true);
				//fillTrainingSet(mm, rand, false);
			}
		}
	}
	
	@Override
	protected void doEpoch() {
		//TODO Bei der Berechnung der Costfunction auf unendlichkeit prüfen
		
	}
	
	@Override
	public void beforeTrainingStarts() {
		//TODO load Trainingset (for now: make trainingset)!!
		initDataSets();
		//TODO load NN(s)
		
	}
	@Override
	public void afterEpoch() {
		// TODO Auto-generated method stub

	}
	@Override
	public void afterTrainingStopped() {
		// TODO save NN(s)
	}
	@Override
	public void onSaveInterval() {
		// TODO save NN(s)
	}
	
	@Override
	public void evaluate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void improve() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String report() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Lets two players play against each other
	 * and adds the moves of one of them to the trainingset.
	 * @param red The red player.
	 * @param white The white player.
	 * @param color the color of the player to record the moves from.
	 */
	private void fillTrainingSet(TrainingSet<double[],double[]> set, Player red, Player white, boolean color) {
		NNPlayfield fieldBefore = null;
		NNPlayfield field;
		field = NNPlayfield.startPosition();
		red.prepare(color, field);
		white.prepare(color, field);
		//set global variable for the encode function
		nnColor = color;
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
				set.addTrainingExample(encodeInput(fieldBefore), encodeInput(field));
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
			if(!color) set.addTrainingExample(encodeInput(fieldBefore), encodeInput(field));
			if(!NNMove.isMovingPossible(false, field)) {
				break;
			}
		}
	}
	/**
	 * Turns a playfield into an inputVector.
	 * This returns an inputVector were the NN is always at the BOTTOM.
	 */
	@Override
	public double[] encodeInput(NNPlayfield in) {
		double[] vector = new double[64];
		for(byte i = 0; i < 32; i++) {
			if(in.isOccupiedByColor(i, nnColor)) {
				if(in.isKing(i)) vector[32 + i] = 1;
				else vector[i] = 1;
			}
			else if(in.isOccupiedByColor(i, !nnColor)) {
				if(in.isKing(i)) vector[32 + i] = -1;
				else vector[i] = -1;
			}
		}
		if(!nnColor) {//if the nn plays as white its figures are not located at the bottom. 
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
	/**
	 * In this case it is equal to {@code encodeInput(NNPlayfield)}.
	 */
	@Override
	public double[] encodeOutput(NNPlayfield in) {
		return encodeInput(in);
	}

	@Override
	public void save() throws IOException {
		try {
			//save the Session.json
			this.saveToFile();
			//save trainingsets
			((JSONSaveableTrainingSet<double[], double[]>) ts).saveToFile(
					tsFile,
					DEFAULT_INDENT_FACTOR,
					DEFAULT_CHARSET
					);
			((JSONSaveableTrainingSet<double[], double[]>) validationSet).saveToFile(
					vsFile,
					DEFAULT_INDENT_FACTOR,
					DEFAULT_CHARSET
					);
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
