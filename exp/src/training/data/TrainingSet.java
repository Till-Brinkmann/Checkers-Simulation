package training.data;

import java.util.Random;

import datastructs.List;
import datastructs.Stack;

public class TrainingSet<InputType, OutputType> extends List<TrainingExample<InputType, OutputType>>{

	public TrainingSet() {
		super();
	}
	
	public void addTrainingExample(TrainingExample<InputType, OutputType> e) {
		//we do not want null here
		if(e == null) return;
		append(e);
	}
	
	public void addTrainingExample(InputType input, OutputType output) {
		append(new TrainingExample<InputType, OutputType>(input, output));
	}

	public TrainingExample<InputType, OutputType> getFirst() {
		if(isEmpty()) return null;
		toFirst();
		return get();
	}
	
	public TrainingExample<InputType, OutputType> getNext() {
		if(!hasAccess()) {
			toFirst();
			return get();
		}
		TrainingExample<InputType, OutputType> e = get();
		next();
		return e;
	}
	
	@Override
	public void next() {
		if(!hasNext()) {
			toFirst();
			return;
		}
		super.next();
	}
	
	public void nextNoWrap() {
		super.next();
	}
	
	/**
	 * Shuffles the trainingset.
	 * This is important when a represantative
	 * but preferably small batch needs to be taken from the set.
	 */
	public void shuffle() {
		//TODO find out how to shuffle a list efficiently.
		Stack<TrainingExample<InputType, OutputType>> t =
				new Stack<TrainingExample<InputType, OutputType>>();
		Random r = new Random();
		for(toFirst(); hasAccess(); super.next()) {
			if(r.nextBoolean()) {
				t.push(get());
				remove();
			}
		}
		toFirst();
		while(!t.isEmpty()) {
			if(r.nextBoolean()) insert(t.pop());
			if(hasAccess()) super.next();
			else break;
		}
		//in case not all items have been appended (could happen because it is random)
		while(!t.isEmpty()) {
			append(t.pop());
		}
	}
	
}
