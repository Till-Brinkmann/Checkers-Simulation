package training;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import checkers.NNGame;
import checkers.NNPlayfield;
import checkers.Player;
import datastructs.List;
import gui.CommandListener;
import gui.LineChart.FitnessType;
import gui.NNGUI;
import gui.TrainingPanel;
import json.JSONArray;
import json.JSONObject;
import nn.NNPlayer;
import opponent.MiniMaxAB;
import opponent.RandomAI;

/**
 * A training session represents the whole process of training a nn.
 * This includes saving all necessary setup information (permanently on disk)
 *
 */
public class TrainingSession {

	public enum TrainingMode{
		NORMAL,
		MINMAX,
		RANDOMAI,
		RANDOMSITUATIONS
	}
	public final String name;
	
	public final TrainingMode mode;
	
	public final NNSpecification nnspecs;
	
	public NNPlayer player;
	public NNPlayer[] nnPlayer;
	private float sumFitness;
	
	private boolean started;
	private boolean stopTraining;
	private boolean stopped;
	private Object waitLock;
	
	public float changePercentage;
	public float defaultChangePercentage;
	public float learnrate;
	
	public long epoch;
	
	private Player opponent;
	
	File sessionDir;
	
	private final String nnPlayerName;
	
	//for RandomAi mode
	private int rtp = 10;
	
	private List<NNPlayfield> learningSet;
	public final Comparator<NNPlayer> nnPlayerComparator = new Comparator<NNPlayer>(){			
		@Override
		public int compare(NNPlayer n1, NNPlayer n2) {
			return n1.getFitness() > n2.getFitness() ? -1 : (n1.getFitness() < n2.getFitness()  ? 1 : 0);
		}
		
	};
	
	public TrainingSession(
			String name,
			TrainingMode mode,
			NNSpecification nnspecs,
			float defaultChangePercentage,
			float changePercentage,
			float learnrate,
			int epoch,
			NNPlayer player) {
		this.name = name;
		this.mode = mode;
		this.nnspecs = nnspecs;
		this.defaultChangePercentage = defaultChangePercentage;
		this.learnrate = learnrate;
		this.changePercentage = changePercentage;
		this.player = player;
		sessionDir = new File(TrainingPanel.tsDirsDir.getPath() + "/" + name);
		nnPlayer = new NNPlayer[nnspecs.nnQuantity];
		for(int i = 0; i < nnspecs.nnQuantity; i++) {
			nnPlayer[i] = player.clone();
		}
		nnPlayerName = player.getClass().getSimpleName();
		System.out.println(nnPlayerName);
		//loadNNPlayer();
		waitLock = new Object();
		started = false;
	}
	
	private void loadNNPlayer() {
		//IOException err = new IOException("The nn file does not match the requirements of this trainingsession.");
		File[] nnfiles = new File(sessionDir.getPath() + "/NNPlayer").listFiles();
		FileReader fileReader;
		//temp vars for nn parameters
		double[][] afterInputWeights;
		double[][][] hiddenWeights;
		double[][] toOutputWeights;
        double [][] bias;
		//char buffer
		char[] chars = new char[1000000];
		//try loading every file
		String errmsg = "The nn file does not match the requirements of this trainingsession.";
		if(nnfiles != null) {
			for(int nncounter = 0; nncounter < nnfiles.length; nncounter++) {
				try {
					//reset temps
					afterInputWeights = new double[nnspecs.hiddenNeuronCount][nnspecs.inputs];
					hiddenWeights = new double[nnspecs.hiddenLayerCount - 1][nnspecs.hiddenNeuronCount][nnspecs.hiddenNeuronCount];
					toOutputWeights = new double[nnspecs.outputs][nnspecs.hiddenNeuronCount];
			        bias = new double[nnspecs.hiddenLayerCount][nnspecs.hiddenNeuronCount];
					//create a new buffer that is big enough to hold all data
					chars = new char[(int)nnfiles[nncounter].length()];
					//init filereader
					fileReader = new FileReader(nnfiles[nncounter]);
					//read all chars and close
					fileReader.read(chars);
					fileReader.close();
					//create a new JSONObject from the char array
					JSONObject nnobject = new JSONObject(String.valueOf(chars));
					//load all information from the nnobject
					JSONArray array = nnobject.getJSONArray("AfterInputWeights");
					if(array.length() != afterInputWeights.length)
						throw new IOException(errmsg);
					JSONArray innerArray;
					for(int i = 0; i < array.length(); i++) {
						innerArray = array.getJSONArray(i);
						if(innerArray.length() != afterInputWeights[i].length)
							throw new IOException(errmsg);
						for(int j = 0; j < innerArray.length(); j++) {
							afterInputWeights[i][j] = innerArray.getDouble(j);
						}
					}
					JSONArray innerArray2;
					array = nnobject.getJSONArray("HiddenWeights");
					if(array.length() != hiddenWeights.length)
						throw new IOException(errmsg);
					for(int i = 0; i < array.length(); i++) {
						innerArray = array.getJSONArray(i);
						if(innerArray.length() != hiddenWeights[i].length)
							throw new IOException(errmsg);
						for(int j = 0; j < innerArray.length(); j++) {
							innerArray2 = innerArray.getJSONArray(j);
							if(innerArray2.length() != hiddenWeights[i][j].length)
								throw new IOException(errmsg);
							for(int k = 0; k < innerArray2.length(); k++) {
								hiddenWeights[i][j][k] = innerArray2.getDouble(k);
							}
						}
					}
					array = nnobject.getJSONArray("ToOutputWeights");
					if(array.length() != toOutputWeights.length)
						throw new IOException(errmsg);
					for(int i = 0; i < array.length(); i++) {
						innerArray = array.getJSONArray(i);
						if(innerArray.length() != toOutputWeights[i].length)
							throw new IOException(errmsg);
						for(int j = 0; j < innerArray.length(); j++) {
							toOutputWeights[i][j] = innerArray.getDouble(j);
						}
					}
					array = nnobject.getJSONArray("Bias");
					if(array.length() != bias.length)
						throw new IOException(errmsg);
					for(int i = 0; i < array.length(); i++) {
						innerArray = array.getJSONArray(i);
						if(innerArray.length() != bias[i].length)
							throw new IOException(errmsg);
						for(int j = 0; j < innerArray.length(); j++) {
							bias[i][j] = innerArray.getDouble(j);
						}
					}
					nnPlayer[nncounter] = player.clone();
					nnPlayer[nncounter].getNet().afterInputWeights = afterInputWeights;
					nnPlayer[nncounter].getNet().hiddenWeights = hiddenWeights;
					nnPlayer[nncounter].getNet().toOutputWeights = toOutputWeights;
					nnPlayer[nncounter].getNet().bias = bias;
				} catch (Exception e) {
					//TODO Maybe print that on the program console.
					e.printStackTrace();
					//something bad happened so just make a new player with random weights
					nnPlayer[nncounter] = player.clone();
					nnPlayer[nncounter].getNet().randomWeights();
				}
				//we filled the array so we are done
				if(nncounter == nnPlayer.length - 1) return;
			}
			//fill the rest with new random players
			for(int i = nnfiles.length; i < nnPlayer.length; i++) {
				nnPlayer[i] = player.clone();
				nnPlayer[i].getNet().randomWeights();
			}
			return;
		}
		for(int i = 0; i < nnPlayer.length; i++) {
			nnPlayer[i] = player.clone();
			nnPlayer[i].getNet().randomWeights();
		}
	}
	
	private void loadChartData() {
		File chartData = new File(sessionDir.getAbsoluteFile() + "/Epochdata.json");
		if(!chartData.exists()) return;
		char[] chars = new char[(int)chartData.length()];
		try {
			FileReader fileReader = new FileReader(chartData);
			fileReader.read(chars);
			fileReader.close();
			JSONObject data = new JSONObject(String.valueOf(chars));
			JSONArray maxArray = data.getJSONArray("Maximum");
			JSONArray avgArray = data.getJSONArray("Average");
			JSONArray maxNonNormArray = data.getJSONArray("Max Non Normalized");
			for(int i = 0; i < Math.min(maxNonNormArray.length(), Math.min(avgArray.length(), maxArray.length())); i++) {
				NNGUI.chart.addFitness(avgArray.getDouble(i), FitnessType.AVG);
				NNGUI.chart.addFitness(maxArray.getDouble(i), FitnessType.MAX);
				NNGUI.chart.addFitness(maxNonNormArray.getDouble(i), FitnessType.NONNORMALIZED);
				NNGUI.chart.increaseIndex(1);
			}
			//NNGUI.chart.reloadChart(avg, max, maxNonNorm, );
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void train(){
		NNGUI.console.printInfo("Reloading NNPlayer...", name);
		loadNNPlayer();
		NNGUI.chart.clearDataSet();
		loadChartData();
		started = true;
		stopTraining = false;
		stopped = false;
		NNGUI.console.printInfo("Starting Training", name);
		//do some setup
		opponent = mode == TrainingMode.MINMAX ? new MiniMaxAB() : null;
		if(mode == TrainingMode.RANDOMAI) {
			opponent = new RandomAI();
			NNGUI.console.addCommandListener(new CommandListener() {
				@Override
				public boolean processCommand(String command, String[] args) {
					if(command.equals("set") && args.length == 2) {
						if(args[0].equals("Rounds")) {
							rtp = Integer.parseInt(args[1]);
							return true;
						}
					}
					return false;
				}
				
			});
		}
		if(mode == TrainingMode.RANDOMSITUATIONS) {
			//create lerning set
			learningSet = new List<NNPlayfield>();
			for(int i = 0; i < 10; i++) {
				learningSet.concat(new NNGame(new RandomAI(), new RandomAI()).getRandomSituations(50));
			}
		}
		//training loop
		while(!stopTraining) {
			switch(mode){
			case NORMAL:
				trainNormal();
				break;
			case MINMAX:
				trainAgainstMiniMax();
				break;
			case RANDOMAI:
				trainAgainstRandomAI(rtp);
				break;
			case RANDOMSITUATIONS:
				trainWithRandomSituations();
				break;
			}
				
			
			epoch++;
			//save in case the program or the computer crashes.
			if(epoch % 10 == 0) {
				save();
			}
		}
		//save when stopped
		save();
		synchronized(waitLock) {
			stopped = true;
			waitLock.notify();
			started = false;
		}
	}
	
	private void trainWithRandomSituations() {
		resetPlayer();
		int moves = 0;
		//NNPlayfield[] situations = selectSituations(100);
		for(NNPlayer p : nnPlayer){
			for(learningSet.toFirst(); learningSet.hasAccess(); learningSet.next()) {
				if(new NNGame(p, null, learningSet.get()).oneMove()) {
					moves++;
				}
				if(new NNGame(null, p, learningSet.get()).oneMove()) {
					moves++;
				}
			}
//			for(NNPlayfield situation: situations) {
//				if(new NNGame(p, null, situation).oneMove()) {
//					moves++;
//				}
//				if(new NNGame(null, p, situation).oneMove()) {
//					moves++;
//				}
//			}
		}
		NNGUI.console.printInfo("Correct move executions: " + moves);
		sortAndCalculateSum();
    	for(int i = nnspecs.nnSurviver; i < nnspecs.nnQuantity; i++){
    		nnPlayer[i].getNet().childFrom(weightedRandomSelection().getNet(), weightedRandomSelection().getNet());
    		nnPlayer[i].getNet().changeAll(changePercentage, false);
    	}
    	//mix it again to give nets that have equal scores a better chance
    	randomizeArray();		
	}
	private NNPlayfield[] selectSituations(int amount) {

		NNPlayfield[] selectedSet = new NNPlayfield[amount];
		int i = 0;
		learningSet.toFirst();

		while(i < amount) {
			if(Math.random() < 0.01) {
				selectedSet[i] = learningSet.get();
				if(selectedSet[i] == null) {
					System.getenv();;;
				}
				i++;
			}
			learningSet.next();
			if(!learningSet.hasAccess()) {
				learningSet.toFirst();
			}
		}
		return selectedSet;
		
	}
	private void trainNormal(){
		//do one epoch
    	//make sure fitness is reset
    	resetPlayer();
		//start with playing all vs all
    	NNPlayfield field;
    	int redFigures, whiteFigures;
		for(NNPlayer p : nnPlayer){
			for(NNPlayer s : nnPlayer){
				if(p != s) {
					NNGame game = new NNGame(p, s);
					game.start();
					field = game.getField();
					redFigures = field.getFigureQuantityOfColor(true);
					whiteFigures = field.getFigureQuantityOfColor(false);
					p.setFitness(redFigures- whiteFigures);
					s.setFitness(whiteFigures - redFigures);
					
				}
			}
		}
		sortAndCalculateSum();
    	for(int i = nnspecs.nnSurviver; i < nnspecs.nnQuantity; i++){
    		nnPlayer[i].getNet().childFrom(weightedRandomSelection().getNet(), weightedRandomSelection().getNet());
    		nnPlayer[i].getNet().changeAll(changePercentage,true);
    	}
    	//mix it again to give nets that have equal scores a better chance
    	randomizeArray();
    	//TODO diesen wert auch editierbar machen
    	//in der gui updaten
    	changePercentage *= (1 - learnrate);
	}
	
	private void trainAgainstMiniMax() {
		NNGame game;
		NNPlayfield field;
		byte endSituation;
		resetPlayer();
		for(NNPlayer p : nnPlayer) {
//			//starts first
//			new NNGame(p, opponent)
//			.start();
//			//and as second
//			new NNGame(opponent, p)
//			.start();
			//starts first
			game = new NNGame(p, opponent);
			game.start();
			endSituation = game.getSituation();
			field = game.getField();
			if(endSituation == 1) {
				p.addFitness(1000);		
				System.out.print("Warum?");
			}
			if(endSituation == 3) {
				p.addFitness(100);
			}
				
			p.addFitness(12 - field.getFigureQuantityOfColor(false));
			
			//and as second
			game = new NNGame(opponent, p);
			game.start();
			endSituation = game.getSituation();
			field = game.getField();
			if(endSituation == 2) {
				p.addFitness(1000);
			}
			if(endSituation == 3) {
				p.addFitness(100);
				System.out.print("Warum?");
			}
			p.addFitness(12 - field.getFigureQuantityOfColor(true));
		}
		sortAndCalculateSum();
		for(int i = nnspecs.nnSurviver; i < nnspecs.nnQuantity; i++){
    		nnPlayer[i].getNet().childFrom(weightedRandomSelection().getNet(), weightedRandomSelection().getNet());
    		nnPlayer[i].getNet().changeAll(changePercentage,false);
    	}
		changePercentage *= (1 - learnrate);
	}
	private void trainAgainstRandomAI(int roundsToPlay) {
		resetPlayer();
		NNGame game;
		NNPlayfield field;
		byte endSituation;
		for(NNPlayer p : nnPlayer) {
			for(int rounds = 0; rounds < roundsToPlay; rounds++) {
				//starts first
				game = new NNGame(p, opponent);
				game.start();
				endSituation = game.getSituation();
				field = game.getField();
				if(endSituation == 1) {
					p.addFitness(100);					
				}
				p.addFitness(field.getFigureQuantityOfColor(true) - field.getFigureQuantityOfColor(false));
				
				//and as second
				game = new NNGame(opponent, p);
				game.start();
				endSituation = game.getSituation();
				field = game.getField();
				if(endSituation == 2) {
					p.addFitness(100);
				}
				p.addFitness(field.getFigureQuantityOfColor(false) - field.getFigureQuantityOfColor(true));
			}
		}
		sortAndCalculateSum();
		for(int i = nnspecs.nnSurviver; i < nnspecs.nnQuantity; i++){
    		nnPlayer[i].getNet().childFrom(weightedRandomSelection().getNet(), weightedRandomSelection().getNet());
    		nnPlayer[i].getNet().changeAll(changePercentage,false);
    	}
		//changePercentage *= (1 - learnrate);
	}
	
	//----training helper methods start----//
	private NNPlayer weightedRandomSelection(){
		float random = (float)Math.random() * sumFitness;
		for(NNPlayer p : nnPlayer) {
			if(random < p.getFitness()) {
				return p;
			}
			random -= p.getFitness();
		}
    	return nnPlayer[nnspecs.nnQuantity-1];
    }
	/**
	 * sorts the nnPlayer from best to worst fitness
	 */
	private void sortAndCalculateSum() {
		//sort array descending based on fitness
		Arrays.parallelSort(nnPlayer, nnPlayerComparator);
		NNGUI.console.printInfo("Best Fitness(non normalized): " + nnPlayer[0].getFitness(), name);
		NNGUI.chart.addFitness(nnPlayer[0].getFitness(), FitnessType.NONNORMALIZED);
    	//calculate sum
		sumFitness = 0;
    	for(int i = 0; i < nnspecs.nnQuantity; i++){
    		//all values have to be at least 0
    		//TODO warum?
    		nnPlayer[i].addFitness( -nnPlayer[nnspecs.nnQuantity-1].getFitness());
    		sumFitness += nnPlayer[i].getFitness();
    	}
    	NNGUI.console.printInfo("Best Fitness: " + nnPlayer[0].getFitness(),name);
    	NNGUI.chart.addFitness(nnPlayer[0].getFitness(), FitnessType.MAX);
    	NNGUI.console.printInfo("Average Fitness: " + sumFitness/nnPlayer.length, name);
    	NNGUI.chart.addFitness(sumFitness/nnPlayer.length, FitnessType.AVG);
    	NNGUI.chart.increaseIndex(1);
	}
	/**
	 * sets the fitness of every player in the nnPlayer array to 0 and didValidMove to false.
	 */
	private void resetPlayer() {
		for(NNPlayer p : nnPlayer) {
    		p.setFitness(0);
    	}
	}
	/**
	 * mixes the contents of the nnPlayer array by exchanging player from two randomly chosen indexes.
	 */
	private void randomizeArray() {
		NNPlayer tmp;
    	int oldIndex;
    	int newIndex;
    	for(int i = 0; i < nnspecs.nnQuantity; i++){
    		oldIndex = (int)Math.round(Math.random()*(nnspecs.nnQuantity-1));
    		tmp = nnPlayer[oldIndex];
    		newIndex = (int)Math.round(Math.random()*(nnspecs.nnQuantity-1));
    		nnPlayer[oldIndex] = nnPlayer[newIndex];
    		nnPlayer[newIndex] = tmp;
    	}
	}
	//----training helper methods end----
	/**
	 * sets the stopTraining flag and then waits until the training is stopped.
	 */
	public void awaitStopping() {
		if(!isRunning()) return;
		stopTraining = true;
		synchronized(waitLock) {
			while(isRunning()) {
				try {
					waitLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * @return Returns true if a training is running currently, otherwise false.
	 */
	public boolean isRunning() {
		return started && !stopped;
	}
	/**
	 * @return Returns a JSONObject that represents this trainingsession.
	 */
	public JSONObject toJsonObject() {
		return new JSONObject()
		.put("Name", name)
		.put("Mode", mode.name())
		.put("NNSpecs", nnspecs.toJSONObject())
		.put("Current Changepercentage", changePercentage)
		.put("Default Changepercentage", defaultChangePercentage)
		.put("Learnrate", learnrate)
		.put("Epoch", epoch)
		.put("NNPlayer Name", nnPlayerName);
	}
	
	public void save() {
		FileOutputStream writer;
		File nnPlayerDir;
		try {
			if(!sessionDir.exists()) sessionDir.mkdirs();
			writer = new FileOutputStream(new File(sessionDir.getPath() + "/Session.json"));
			writer.write(toJsonObject().toString(2).getBytes("UTF-8"));
			writer.close();
			writer = new FileOutputStream(new File(sessionDir.getPath() + "/Epochdata.json"));
			writer.write(NNGUI.chart.toJSONObject().toString(2).getBytes("UTF-8"));
			writer.close();
			nnPlayerDir = new File(sessionDir.getPath() + "/NNPlayer");
			if(!nnPlayerDir.exists()) nnPlayerDir.mkdir();
			for(int i1 = 0; i1 < nnPlayer.length; i1++) {
				writer = new FileOutputStream(nnPlayerDir.getPath()+ "/" +  i1 + ".json");
				writer.write(nnPlayer[i1].getNet().toJSONObject().put("NNPlayer Name", this.nnPlayerName).toString(2).getBytes("UTF-8"));
				writer.close();
			}
		} catch (IOException e) {
			NNGUI.console.printError("IO Error while saving session " + name, "TrainingPanel");
			e.printStackTrace();
		}
		
	}
	@Override
	public String toString(){
		return name;
	}
	public void clearSession() {
		File epochData = new File(sessionDir.getAbsolutePath() + "/Epochdata.json");
		if(epochData.exists()) {
			epochData.delete();
		}
		File[] nnfiles = new File(sessionDir.getPath() + "/NNPlayer").listFiles();
		if(nnfiles.length != 0) {
			for(int nncounter = 0; nncounter < nnfiles.length; nncounter++) {
				nnfiles[nncounter].delete();
			}
		}
	}
}
