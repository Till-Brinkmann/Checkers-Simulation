package training;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import checkers.NNGame;
import gui.NNGUI;
import json.JSONObject;
import nn.NNPlayer;
import opponent.MiniMaxAB;

/**
 * A training session represents the whole process of training a nn.
 * TODO This includes saving all necessary setup information (permanently on disk)
 *
 */
public class TrainingSession {

	public enum TrainingMode{
		NORMAL,
		MINMAX,
		RULES,
		STRATEGY
	}
	public final String name;
	
	public final TrainingMode mode;
	
	public final NNSpecification nnspecs;
	
	public NNPlayer[] nnPlayer;
	private float sumFitness;
	
	private boolean started;
	private boolean stopTraining;
	private boolean stopped;
	private Object waitLock;
	
	public float changePercentage;
	
	public final Comparator<NNPlayer> nnPlayerComparator = new Comparator<NNPlayer>(){			
		@Override
		public int compare(NNPlayer n1, NNPlayer n2) {
			return n1.fitness > n2.fitness ? -1 : (n1.fitness < n2.fitness  ? 1 : 0);
		}
		
	};
	
	public TrainingSession(String name, TrainingMode mode, NNSpecification nnspecs) {
		this.name = name;
		this.mode = mode;
		this.nnspecs = nnspecs;
		this.changePercentage = nnspecs.defaultChangePercentage;
		nnPlayer = new NNPlayer[nnspecs.nnQuantity];
		//TODO try loading NNPlayer from disk
		if(nnPlayer[0] == null){
			//we do not have any players to continue training with
			//load new ones
			for(int i = 0; i < nnspecs.nnQuantity; i++){
				nnPlayer[i] = new NNPlayer(nnspecs);
				nnPlayer[i].net.randomWeights();
			}
		}
		waitLock = new Object();
		started = false;
	}
	
	public void train(){
		started = true;
		stopTraining = false;
		stopped = false;
		NNGUI.console.printInfo("Starting Training", name);
		switch(mode){
		case NORMAL:
			trainNormal();
			break;
		case MINMAX:
			trainWMiniMax();
			break;
		case RULES:
			break;
		case STRATEGY:
			break;
		}
		stopped = true;
		synchronized(waitLock) {
			waitLock.notify();
		}
	}
	
	private void trainNormal(){
		while(!stopTraining){
			//do one epoch
        	//make sure fitness is reset
        	resetPlayer();
			//start with playing all vs all
			for(NNPlayer p : nnPlayer){
				for(NNPlayer s : nnPlayer){
					if(p != s) new NNGame(p, s).start();
				}
			}
			sortAndCalculateSum();
        	for(int i = nnspecs.nnSurviver; i < nnspecs.nnQuantity; i++){
        		nnPlayer[i].net.childFrom(weightedRandomSelection().net, weightedRandomSelection().net);
        		nnPlayer[i].net.changeAll(changePercentage);
        	}
        	//mix it again to give nets that have equal scores a better chance
        	randomizeArray();
        	//TODO diesen wert auch editierbar machen
        	//in der gui updaten
        	changePercentage /= (1 + nnspecs.learnrate);
		}
	}
	
	private void trainWMiniMax() {
		MiniMaxAB miniMax = new MiniMaxAB();
		while(!stopTraining) {
			resetPlayer();
			for(NNPlayer p : nnPlayer) {
				//starts first
				new NNGame(p, miniMax)
				.start();
				//and as second
				new NNGame(miniMax, p)
				.start();
			}
			sortAndCalculateSum();
			for(int i = nnspecs.nnSurviver; i < nnspecs.nnQuantity; i++){
        		nnPlayer[i].net.childFrom(weightedRandomSelection().net, weightedRandomSelection().net);
        		nnPlayer[i].net.changeAll(changePercentage);
        	}
			changePercentage /= (1 + nnspecs.learnrate);
		}
	}
	//----training helper methods start----
	private NNPlayer weightedRandomSelection(){
		float random = (float)Math.random() * sumFitness;
		for(NNPlayer p : nnPlayer) {
			if(random < p.fitness) {
				return p;
			}
			random -= p.fitness;
		}
    	return nnPlayer[nnspecs.nnQuantity-1];
    }
	/**
	 * sorts the nnPlayer from best to worst fitness
	 */
	private void sortAndCalculateSum() {
		//sort array descending based on fitness
		Arrays.parallelSort(nnPlayer, nnPlayerComparator);
		NNGUI.console.printInfo("Best Fitness(non normalized): " + nnPlayer[0].fitness, name);
    	//calculate sum
		sumFitness = 0;
    	for(int i = 0; i < nnspecs.nnQuantity; i++){
    		//all values have to be at least 0
    		nnPlayer[i].fitness -= nnPlayer[nnspecs.nnQuantity-1].fitness;
    		sumFitness += nnPlayer[i].fitness;
    	}
    	NNGUI.console.printInfo("Best Fitness: " + nnPlayer[0].fitness, name);
    	NNGUI.console.printInfo("Average Fitness: " + sumFitness/nnPlayer.length, name);
	}
	/**
	 * sets the fitness of every player in the nnPlayer array to 0 and didValidMove to false.
	 */
	private void resetPlayer() {
		for(NNPlayer p : nnPlayer) {
    		p.fitness = 0;
    		p.didValidMove = false;
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
			while(!stopped) {
				try {
					waitLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		started = false;
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
		.put("Current Changepercentage", changePercentage);
	}
	
	@Override
	public String toString(){
		return name;
	}
}
