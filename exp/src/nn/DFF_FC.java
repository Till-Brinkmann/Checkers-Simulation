package nn;

import util.MatVecMath;

/**
 * Deep Feed Forward, Fully Connected net.
 * @author Till
 *
 */
public class DFF_FC extends NN<double[], double[]>{

	private double[][][] weights;
	private double[][] bias;
	
	private double sigoffset;
    private double sigscale;
    private double weightMin;
    private double weightDiff;
    
    public DFF_FC(
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
    }
    public DFF_FC(NNSpecification specs){
    	this(specs.inputs, specs.outputs, specs.hiddenNeuronCount, specs.hiddenLayerCount,
    			specs.sigmin, specs.sigmax, specs.weightMin, specs.weightMax);
    }
    
	@Override
	public double[] run(double[] input) {
		//propagate input
		for(int i = 0; i < weights.length; i++) {
			input = MatVecMath.mult(input, weights[i]);
			
		}
	}
	
	
}
