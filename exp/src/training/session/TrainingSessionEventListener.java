package training.session;

public interface TrainingSessionEventListener {

	public void beforeTrainingStarts();
	
	public void afterEpoch();
	
	public void afterTrainingStopped();
	
	public void onSaveInterval();
}
