package nn;

public abstract class NN<InputType, OutputType> {

	public abstract OutputType run(InputType input);
	
	public abstract double activation(double z);
	
	public abstract double activationDev(double z);
}
