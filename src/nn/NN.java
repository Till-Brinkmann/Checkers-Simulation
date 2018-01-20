package nn;

import json.JSONArray;
import json.JSONObject;
import training.NNSpecification;

public class NN
{
	public double[][] afterInputWeights;
	public double[][][] hiddenWeights;
	public double[][] toOutputWeights;
	public double[][] bias;
    private double sigoffset;
    private double sigscale;
    private double weightMin;
    private double weightDiff;
    
    public NN(int inputNeurons, int outputNeurons,  int hiddenNeurons, int hiddenlayer, double sigmin, double sigmax, double weightMin, double weightMax)
    {
        sigoffset = (sigmin + sigmax) / 2 -0.5;
        this.sigscale = sigmax - sigmin;
        this.weightMin = weightMin;
        weightDiff = weightMax - weightMin;
        afterInputWeights = new double[hiddenNeurons][inputNeurons];
        hiddenWeights = new double[hiddenlayer-1][hiddenNeurons][hiddenNeurons];
        toOutputWeights = new double[outputNeurons][hiddenNeurons];
        //Size of first dimension is the number of layers and size of second the number of neurons inside this layer.
        bias = new double[hiddenlayer + 2][hiddenNeurons];
        //set to size of input layer
        bias[0] = new double[inputNeurons];
        //output bias (has to be the size of the output)
        bias[hiddenlayer + 1] = new double[outputNeurons];
    }
    
    public NN(NNSpecification specs){
    	this(specs.inputs, specs.outputs, specs.hiddenNeuronCount, specs.hiddenLayerCount,
    			specs.sigmin, specs.sigmax, specs.weightMin, specs.weightMax);
    }
    
    public double[] run(double[] inputVector){
        //add input bias
        double[] ffVector; //= vectorAdd(inputVector, bias[0]);
        //calculate values of the first hidden layer
        ffVector = vector_matrix_multiplication(inputVector, afterInputWeights);
        //sigmoid values and add bias for first hiddenlayer
        for (int x = 0; x < ffVector.length; x++){
            ffVector[x] = sigmoid(ffVector[x] + bias[1][x]);
        }
        //feed forward through all hidden layers
        for (int i = 0; i < hiddenWeights.length; i++){
            ffVector = vector_matrix_multiplication(ffVector, hiddenWeights[i]);
            for (int x = 0; x < ffVector.length; x++){
                ffVector[x] = sigmoid(ffVector[x] + bias[i + 1][x]);
            }
        }
        //calculate output values
        ffVector = vector_matrix_multiplication(ffVector, toOutputWeights);
        for (int x = 0; x < ffVector.length; x++){
        	ffVector[x] = sigmoid(ffVector[x] + bias[bias.length - 1][x]);
        }
        return ffVector;
    }
    
    public void randomWeights(){
        for (int i = 0; i < afterInputWeights.length; i++){
            for (int x = 0; x < afterInputWeights[0].length; x++){
                afterInputWeights[i][x] = Math.random()*weightDiff+weightMin;
            }
        }
        for (int i = 0; i < hiddenWeights.length; i++){
            for (int x = 0; x < hiddenWeights[0].length; x++){
                for (int y = 0; y < hiddenWeights[0][0].length; y++){
                    hiddenWeights[i][x][y] = Math.random()*weightDiff+weightMin;
                }
            }
        }
        for (int i = 0; i < toOutputWeights.length; i++){
            for (int x = 0; x < toOutputWeights[0].length; x++){
                toOutputWeights[i][x] = Math.random()*weightDiff+weightMin;
            }
        }
        for (int i = 0; i < bias.length; i++){
            for (int x = 0; x < bias[i].length; x++){
            	bias[i][x] = Math.random()*weightDiff+weightMin;
            }
        }
    }
    public void changeAll(double percent){ //in percent
    	double scale = percent / 100;
        for (int i = 0; i < afterInputWeights.length; i++){
            for (int x = 0; x < afterInputWeights[0].length; x++){
                afterInputWeights[i][x] *= changeFunction(scale);
                if(afterInputWeights[i][x] == 0) afterInputWeights[i][x] += changeFunction(0.00001);
            }
        }
        for (int i = 0; i < hiddenWeights.length; i++){
            for (int x = 0; x < hiddenWeights[0].length; x++){
                for (int y = 0; y < hiddenWeights[0][0].length; y++){
                    hiddenWeights[i][x][y] *= changeFunction(scale);
                    if(hiddenWeights[i][x][y] == 0) hiddenWeights[i][x][y] += changeFunction(0.00001);
                }
            }
        }
        for (int i = 0; i < toOutputWeights.length; i++){
            for (int x = 0; x < toOutputWeights[0].length; x++){
                toOutputWeights[i][x] *= changeFunction(scale);
            	if(toOutputWeights[i][x] == 0) toOutputWeights[i][x] += changeFunction(0.00001);
            }
        }
        for (int i = 0; i < bias.length; i++){
            for (int x = 0; x < bias[i].length; x++){
            	bias[i][x] *= changeFunction(scale);
            	if(bias[i][x] == 0) bias[i][x] += changeFunction(0.00001);
            }
        }
    }
    /**
     * takes the weights of each parent to generate new weights for the nn.
     * @param n1 first parent
     * @param n2 second parent
     */
    public void childFrom(NN n1, NN n2){ 
    	//input weights
    	double[][] src = Math.random() < 0.5 ? n1.afterInputWeights : n2.afterInputWeights;
    	for(int i = 0; i < afterInputWeights.length; i++) {
    		System.arraycopy(src[i], 0, afterInputWeights[i], 0, afterInputWeights[i].length);
    	}
    	//hidden weights
    	for(int i = 0; i < hiddenWeights.length; i++){
    		src = Math.random() < 0.5 ? n1.hiddenWeights[i] : n2.hiddenWeights[i];
    		for(int j = 0; j < hiddenWeights[i].length; j++) {
    			System.arraycopy(src[j], 0, hiddenWeights[i][j], 0, hiddenWeights[i][j].length);
    		}
    	}
    	//ouput weights
    	src = Math.random() < 0.5 ? n1.toOutputWeights : n2.toOutputWeights;
    	for(int i = 0; i < toOutputWeights.length; i++) {
    		System.arraycopy(src[i], 0, toOutputWeights[i], 0, toOutputWeights[i].length);
    	}
    	//bias
    	src = Math.random() < 0.5 ? n1.bias : n2.bias;
    	for(int i = 0; i < bias.length; i++) {
    		System.arraycopy(src[i], 0, bias[i], 0, bias[i].length);
    	}
    }
    
    private static double changeFunction(double scale){
    	double random = Math.random() < 0.9 ? Math.random() : -Math.random();
    	return (StrictMath.pow(2*random-1, 3) + 1)*scale;
    }
    
    private double sigmoid(double x) {
        return (1/( 1 + StrictMath.pow(Math.E,(-x))) + sigoffset) * sigscale;
    }
    
    private static double[] vector_matrix_multiplication(double[] vector, double[][] matrix){
        double[] resultVector = new double[matrix.length];
        for (int i = 0; i < resultVector.length; i++){
            for (int x = 0; x < vector.length; x++){
                resultVector[i] += vector[x] * matrix[i][x];
            }
        }
        return resultVector;
    }
    
    public JSONObject toJSONObject() {
    	return new JSONObject()
    			.put("AfterInputWeights", new JSONArray(afterInputWeights))
    			.put("HiddenWeights", new JSONArray(hiddenWeights))
    			.put("ToOutputWeights", new JSONArray(toOutputWeights))
    			.put("Bias", new JSONArray(bias))
    			.put("Sigmoid Offset", sigoffset)
    			.put("Sigmoid Scale", sigscale)
    			.put("Weight Min", weightMin)
    			.put("Weight Difference", weightDiff);
    }
}