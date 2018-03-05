package training.data;

import datastructs.List;
import datastructs.Stack;

public class TrainingSet<InputType, OutputType> {

	public class TrainingExample {
		InputType input;
		OutputType output;
		
		public TrainingExample(InputType input, OutputType output) {
			this.input = input;
			this.output = output;
		}
		/**
		 * this is used by SaveableTrainingExample and should not be used elsewhere.
		 */
		protected TrainingExample() {}
	}
	
	List<TrainingExample> data;
	
	public TrainingSet() {
		data = new List<TrainingExample>();
	}
	
	public void addTrainingExample(TrainingExample e) {
		//we do not want null here
		if(e == null) return;
		data.append(e);
	}
	
	public void addTrainingExample(InputType input, OutputType output) {
		data.append(new TrainingExample(input, output));
	}

	public TrainingExample getFirst() {
		if(data.isEmpty()) return null;
		data.toFirst();
		return data.get();
	}
	
	public TrainingExample getNext() {
		data.next();
		return data.hasAccess() ? data.get() : null;
	}
	/**
	 * Shuffles the trainingset.
	 * This is important when a represantative
	 * but preferably small batch needs to be taken from the set.
	 */
	public void shuffle() {
		//TODO find out how to shuffle a list efficiently.
		Stack<TrainingExample> t = new Stack<TrainingExample>();
		for(data.toFirst(); data.hasAccess(); data.next()) {
			if(Math.random() < 0.5) {
				t.push(data.get());
				data.remove();
			}
		}
		data.toFirst();
		while(!t.isEmpty()) {
			if(Math.random() < 0.5) data.insert(t.pop());
			if(data.hasAccess()) data.next();
			else break;
		}
		//in case not all items have been appended (could happen because it is random)
		while(!t.isEmpty()) {
			data.append(t.pop());
		}
	}
	
}
