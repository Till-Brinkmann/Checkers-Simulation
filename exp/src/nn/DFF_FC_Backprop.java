package nn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.SortingFocusTraversalPolicy;

import json.JSONArray;
import json.JSONObject;
import json.JSONTokener;
import util.MatMath;

/**
 * Deep Feed Forward, Fully Connected net.
 *
 */
public class DFF_FC_Backprop extends NN<double[], double[]>{
	
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	public double[][][] weights;
	public double[][] bias;
	
	/**
	 * sigmoided output of every neuron.
	 */
	public double[][] outputLevel;
	
	private double sigoffset;
    private double sigscale;
    private double weightMin;
    private double weightDiff;
    
    public DFF_FC_Backprop(
    		int inputNeurons, int outputNeurons,  int hiddenNeurons, int hiddenLayers,
    		double sigscale, double sigoffset, double weightMin, double weightDiff) {
    	this.sigoffset = sigoffset;//(sigmin + sigmax) / 2 - 0.5;
        this.sigscale = sigscale;//sigmax - sigmin;
        this.weightMin = weightMin;
        this.weightDiff = weightDiff;//weightMax - weightMin;
        //hidden weights
        this.weights = new double[hiddenLayers + 1][hiddenNeurons][hiddenNeurons];
        //input weights
        this.weights[0] = new double[hiddenNeurons][inputNeurons];
        //output weights
        this.weights[hiddenLayers] = new double[outputNeurons][hiddenNeurons];
        this.bias = new double[hiddenLayers][hiddenNeurons];
        
        //values after activation function. For outputs this is the net output so it does not have to be saved
        //and for inputs it is not required
        outputLevel = new double[hiddenLayers][hiddenNeurons];
    }
    public DFF_FC_Backprop(NNSpecification specs){
    	this(specs.inputs, specs.outputs, specs.hiddenNeuronCount, specs.hiddenLayerCount,
    			specs.sigscale, specs.sigoffset, specs.weightMin, specs.weightDiff);
    }
    
    /**
     * forward propagate input.
     */
	@Override
	public double[] run(double[] input) {
		double[] output = new double[input.length];
		System.arraycopy(input, 0, output, 0, input.length);
		
		int outputIndex = weights.length - 1;
		int hWL;
		//weights after input
		output = MatMath.dot(output, weights[0]);
		//same for all hiddenlayers
		for(hWL = 0; hWL < outputIndex; hWL++) {
			//apply activation function to the whole layer
			activateLayer(output);
			//save the values after the activation function was applied
			System.arraycopy(output, 0, outputLevel[hWL], 0, output.length);
			output = MatMath.dot(output, weights[hWL + 1]);
		}
		//special case for outputs: outputLevel does not need to be saved
		activateLayer(output);
		return output;
	}
	
	public void random() {
		for (int i = 0; i < weights.length; i++){
            for (int x = 0; x < weights[i].length; x++){
                for (int y = 0; y < weights[i][x].length; y++){
                    weights[i][x][y] = Math.random()*weightDiff+weightMin;
                }
            }
        }
		for(int layer = 0; layer < bias.length; layer++) {
			for(int neuron = 0; neuron < bias[layer].length; neuron++) {
				bias[layer][neuron] = Math.random()*weightDiff+weightMin;
			}
		}
	}
	
	public void activateLayer(double[] layer) {
		for(int i = 0; i < layer.length; i++) {
			layer[i] = activation(layer[i]);
		}
	}
	@Override
	public double activation(double z) {
		return sigscale/( 1 + StrictMath.pow(Math.E,-z)) + sigoffset;
	}
	//TODO auch für offset variabel machen
	@Override
	public double activationDev(double z) {
		return ((1 - activation(z)) * (activation(z) + 1))/2;
	}
	
	public static double fastActivationDev(double az) {
		return ((1 - az) * (az + 1))/2;
	}
	
	public static DFF_FC_Backprop load(File file) throws FileNotFoundException {
		FileInputStream in = new FileInputStream(file);
		JSONObject nnobject = new JSONObject(new JSONTokener(in));
		
		double sigoffset = nnobject.getDouble("Sigmoid Offset");
		double sigscale = nnobject.getDouble("Sigmoid Scale");
		
		double weightMin = nnobject.getDouble("Weight Min");
		double weightDiff = nnobject.getDouble("Weight Difference");
		
		JSONArray weightArray = nnobject.getJSONArray("Weights");
		JSONArray biasArray = nnobject.getJSONArray("Bias");
		
		int inputs = weightArray.getJSONArray(0).getJSONArray(0).length();
		int outputs = weightArray.getJSONArray(weightArray.length() - 1).length();
		int hiddenNeurons = weightArray.getJSONArray(0).length();
		int hiddenLayer = weightArray.length() - 1;
		
		DFF_FC_Backprop nn = new DFF_FC_Backprop(inputs, outputs, hiddenNeurons, hiddenLayer,
				sigscale, sigoffset, weightMin, weightDiff);
		for(int i = 0; i < hiddenNeurons; i++) {
			for(int j = 0; j < inputs; j++) {
				nn.weights[0][i][j] = weightArray.getJSONArray(0).getJSONArray(i).getDouble(j);
			}
		}
		for (int i = 0; i < hiddenLayer - 1; i++){
            for (int x = 0; x < hiddenNeurons; x++){
                for (int y = 0; y < hiddenNeurons; y++){
                    nn.weights[i + 1][x][y] = weightArray.getJSONArray(i + 1).getJSONArray(x).getDouble(y);
                }
            }
        }
		for(int i = 0; i < outputs; i++) {
			for(int j = 0; j < hiddenNeurons; j++) {
				nn.weights[hiddenLayer][i][j] = weightArray.getJSONArray(hiddenLayer).getJSONArray(i).getDouble(j);
			}
		}
		for(int layer = 0; layer < hiddenLayer; layer++) {
			for(int neuron = 0; neuron < hiddenNeurons; neuron++) {
				nn.bias[layer][neuron] = biasArray.getJSONArray(layer).getDouble(neuron);
			}
		}
		return nn;
	}
	
	public void save(File file) throws IOException {
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		if(!file.canWrite())
			throw new IllegalArgumentException("This file is (currently) not accessible!");
		FileOutputStream writer = new FileOutputStream(file);
		writer.write(DEFAULT_CHARSET.encode(toJSONObject().toString(0)).array());
		writer.close();
	}
	@Override
	public JSONObject toJSONObject() {
		return new JSONObject()
    			.put("Weights", new JSONArray(weights))
    			.put("Bias", new JSONArray(bias))
    			.put("Sigmoid Offset", sigoffset)
    			.put("Sigmoid Scale", sigscale)
    			.put("Weight Min", weightMin)
    			.put("Weight Difference", weightDiff);
	}
}
