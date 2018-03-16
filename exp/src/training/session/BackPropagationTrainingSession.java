package training.session;

import java.io.File;

import json.JSONObject;
import nn.InputEncoder;
import nn.OutputEncoder;
import training.Trainer;
import training.data.TrainingSet;

public abstract class BackPropagationTrainingSession<InputType, InputDataType, OutputType, OutputDataType>
extends JSONSaveableTrainingSession
implements Trainer, InputEncoder<InputDataType, InputType>, OutputEncoder<OutputDataType, OutputType>{
	/**
	 * The trainingset.
	 */
	protected TrainingSet<InputType,OutputType> trainingSet;
	/**
	 * Validation set.
	 */
	protected TrainingSet<InputType,OutputType> validationSet;
	
	public BackPropagationTrainingSession(File file, String name, int totalEpochs, int saveInterval) {
		super(file, name, totalEpochs, saveInterval);
	}
	
	public BackPropagationTrainingSession(File saveDir, JSONObject save) {
		super(saveDir, save);
	}
	
	public BackPropagationTrainingSession(File saveDir) {
		super(saveDir);
	}
}
