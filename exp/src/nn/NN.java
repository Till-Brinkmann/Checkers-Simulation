package nn;

import util.JSONable;

public abstract class NN<InputType, OutputType> implements JSONable{

	public abstract OutputType run(InputType input);
	
	public abstract double activation(double z);
	
	public abstract double activationDev(double z);
}
