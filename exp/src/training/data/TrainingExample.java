package training.data;

public class TrainingExample<InputType, OutputType> {
	public InputType input;
	public OutputType output;
	
	public TrainingExample(InputType input, OutputType output) {
		this.input = input;
		this.output = output;
	}
	/**
	 * this is used by SaveableTrainingExample and should not be used elsewhere.
	 */
	protected TrainingExample() {}
}

