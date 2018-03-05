package training.session;

import java.io.File;
import java.io.IOException;

public abstract class SaveableTrainingSession extends TrainingSession {

	/**
	 * The save directory for this session as a File object.
	 */
	public final File saveDir;
	
	public SaveableTrainingSession(File saveDir, String name) {
		super(name);
		this.saveDir = saveDir;
	}

	public SaveableTrainingSession(File saveDir, String name, int totalEpochs, int saveInterval) {
		super(name, totalEpochs, saveInterval);
		this.saveDir = saveDir;
	}
	
	public abstract void save() throws IOException;
}
