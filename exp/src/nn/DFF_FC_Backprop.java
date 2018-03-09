package nn;

import util.MatVecMath;

/**
 * Deep Feed Forward, Fully Connected net.
 *
 */
public class DFF_FC_Backprop extends NN<double[], double[]>{

	private double[][][] weights;
	private double[][] bias;
	
	/**
	 * sigmoided output of every neuron.
	 */
	private double[][] outputLevel;
	/**
	 * pre sigmoid activation.
	 */
	private double[][] activationLevel;
	
	private double sigoffset;
    private double sigscale;
    private double weightMin;
    private double weightDiff;
    
    public DFF_FC_Backprop(
    		int inputNeurons, int outputNeurons,  int hiddenNeurons, int hiddenLayers,
    		double sigmin, double sigmax, double weightMin, double weightMax) {
    	this.sigoffset = (sigmin + sigmax) / 2 - 0.5;
        this.sigscale = sigmax - sigmin;
        this.weightMin = weightMin;
        this.weightDiff = weightMax - weightMin;
        //hidden weights
        this.weights = new double[hiddenLayers][hiddenNeurons][hiddenNeurons];
        //input weights
        this.weights[0] = new double[hiddenNeurons][inputNeurons];
        //output weights
        this.weights[hiddenLayers] = new double[outputNeurons][hiddenNeurons];
        this.bias = new double[hiddenLayers][hiddenNeurons];
        
        //backprop data
        activationLevel = new double[hiddenLayers + 2][hiddenNeurons];
        activationLevel[0] = new double[inputNeurons];
        activationLevel[activationLevel.length] = new double[outputNeurons];
        
        outputLevel = new double[hiddenLayers + 2][hiddenNeurons];
        outputLevel[0] = new double[inputNeurons];
        outputLevel[activationLevel.length] = new double[outputNeurons];
    }
    public DFF_FC_Backprop(NNSpecification specs){
    	this(specs.inputs, specs.outputs, specs.hiddenNeuronCount, specs.hiddenLayerCount,
    			specs.sigmin, specs.sigmax, specs.weightMin, specs.weightMax);
    }
    
	@Override
	public double[] run(double[] input) {
		//propagate input
		//same for all hiddenlayer
		System.arraycopy(input, 0, activationLevel[0], 0, input.length);
		System.arraycopy(input, 0, outputLevel[0], 0, input.length);
		for(int i = 0; i < weights.length; i++) {
			input = MatVecMath.mult(input, weights[i]);
			System.arraycopy(input, 0, activationLevel[i + 1], 0, input.length);
			activateLayer(input);
			System.arraycopy(input, 0, outputLevel[i + 1], 0, input.length);
		}
		return input;
	}
	
	public void activateLayer(double[] layer) {
		for(int i = 0; i < layer.length; i++) {
			layer[i] = activation(layer[i]);
		}
	}
	@Override
	public double activation(double z) {
		return (1/( 1 + StrictMath.pow(Math.E,(-z))) + sigoffset) * sigscale;
	}
	@Override
	public double activationDev(double z) {
		return activation(z) * (1 - activation(z));
	}
	
	/**
     * 
     */
    public double deltaW(double epsilon, double delta, double output){
    	return -epsilon * delta * output;
    }

    public double deltaOutput(double input, double output, double wantedOutput){
    	return activationDev(input) * (wantedOutput - output);
    }

    public double deltaHidden(double input, double[] weights, double[] deltas){
    	double sum = 0;
    	for (int i = 0; i < weights.length; i++){
    		sum += weights[i] * deltas[i];
    	}
    	return activationDev(input) * sum;
    }
}
