package training.session;


import datastructs.List;

/**
 * Base class for every trainingsession.
 */
public abstract class TrainingSession implements TrainingSessionEventListener{

	/**
	 * Indicates the current status.
	 * True if a training is currently running, otherwise false.
	 */
	private boolean running;
	/**
	 * Flag to indicate if training should be stopped.
	 * Test regularly (at least once) when training 
	 * (ecspecially for an indefinite amount of time) to avoid long waits for the user.
	 */
	private boolean stopTraining;
	/**
	 * Used to wait for training termination, when {@code stop()} is called.
	 * @see TrainingSession#stop()
	 */
	private final Object stopLock;
	/**
	 * The number of total epochs
	 */
	public int totalEpochs;
	/**
	 * How often a save Event should be fired (in epochs).
	 */
	public int saveInterval;
	/**
	 * A List of all registered listeners.
	 */
	private List<TrainingSessionEventListener> listenerList;
	/**
	 * The name.
	 */
	public final String name;
	
	/**
	 * Set all values to default.
	 */
	public TrainingSession(String name) {
		running = false;
		stopTraining = false;
		stopLock = new Object();
		totalEpochs = 0;
		saveInterval = 42;
		listenerList = new List<TrainingSessionEventListener>();
		this.name = name;
	}
	public TrainingSession(String name, int totalEpochs, int saveInterval) {
		running = false;
		stopTraining = false;
		stopLock = new Object();
		listenerList = new List<TrainingSessionEventListener>();
		this.totalEpochs = totalEpochs;
		this.saveInterval = saveInterval;
		this.name = name;
	}
	
	/**
	 * Do one epoch. This must be overwritten to implement the core learning algorithm.
	 */
	protected abstract void doEpoch();
	/**
	 * Train. With {@code epochs} of 0 {@code doEpoch()} is never called (no training is performed) but start and stop events are fired.
	 * When the value of {@code epochs} is negative, the training is started for an indefinite amount of time 
	 * (ok that is not entirely true. It would be started for around 4.2 billion epochs (for 32 bit integers)
	 * because the integer wraps around to MINVALUE after reaching MAXVALUE)
	 * and should be stopped with {@code stop()}.
	 * @see TrainingSession#stop()
	 */
	public void train(int epochs) {
		fireStartEvent();
		for(int epoch = 0; epoch != epochs && !stopTraining; epoch++) {
			doEpoch();
			totalEpochs++;
			fireAfterEpochEvent();
			if(totalEpochs % saveInterval == 0) {
				fireSaveIntervalEvent();
			}
		}
		//perform the steps needed to end a training
		if(stopTraining) {//if the training ended because stop() was called
			synchronized(stopLock) {
				stopLock.notify();
			}
		}
		running = false;
		fireStopEvent();
	}
	/**
	 * When called, waits until the running training is stopped.
	 */
	public final void stop() {
		//nothing to do if this session is not running
		if(!running) return;
		//this step needs synchronization to allow waiting
		synchronized(stopLock) {
			//set flag
			stopTraining = true;
			//wait until condition is satisfied
			while(running) {
				try {
					stopLock.wait();
				} catch (InterruptedException e) {
					//TODO(?) Maybe add a console warning
					e.printStackTrace();
				}
			}
			//reset flag
			stopTraining = false;
		}
	}
	
	//----Event handling----//
	/**
	 * Registers the given listener to receive events.
	 * @param l The listener to add.
	 */
	public void addTrainingSessionEventListener(TrainingSessionEventListener l) {
		//we do not want null references in the list
		if(l == null) throw new NullPointerException("The listener is null.");
		listenerList.append(l);
	}
	/**
	 * Removes an eventlistener from the list.
	 * @param l The listener to remove.
	 */
	public void removeTrainingSessionEventListener(TrainingSessionEventListener l) {
		if(l == null) throw new NullPointerException("The listener is null.");
		boolean found = false;
		for(listenerList.toFirst(); listenerList.hasAccess(); listenerList.next()) {
			if(listenerList.get() == l) {
				found = true;
				break;
			}
		}
		if(!found) throw new IllegalArgumentException("This listener is not registered here.");
		listenerList.remove();
	}
	
	private void fireStartEvent() {
		for(listenerList.toFirst(); listenerList.hasAccess(); listenerList.next()) {
			listenerList.get().beforeTrainingStarts();
		}
	}
	private void fireAfterEpochEvent() {
		for(listenerList.toFirst(); listenerList.hasAccess(); listenerList.next()) {
			listenerList.get().afterEpoch();
		}
	}
	private void fireStopEvent() {
		for(listenerList.toFirst(); listenerList.hasAccess(); listenerList.next()) {
			listenerList.get().afterTrainingStopped();
		}
	}
	private void fireSaveIntervalEvent() {
		for(listenerList.toFirst(); listenerList.hasAccess(); listenerList.next()) {
			listenerList.get().onSaveInterval();
		}
	}
}
