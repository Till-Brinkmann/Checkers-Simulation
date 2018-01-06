package nn;

import json.JSONArray;
import json.JSONObject;
import training.NNSpecification;

public class NN
{
	public double[][] afterInputWeights;
	public double[][][] hiddenWeights;
	public double[][] toOutputWeights;
    private double sigmin;
    private double sigmax;
    private double weightMin;
    private double weightMax;
    
    public NN(int inputNeurons, int outputNeurons,  int hiddenNeurons, int hiddenlayer, double sigmin, double sigmax, double weightMin, double weightMax)
    {
        this.sigmin = sigmin;
        this.sigmax = sigmax;
        this.weightMin = weightMin;
        this.weightMax = weightMax;
        afterInputWeights = new double[hiddenNeurons][inputNeurons];
        hiddenWeights = new double[hiddenlayer-1][hiddenNeurons][hiddenNeurons];
        toOutputWeights = new double[outputNeurons][hiddenNeurons];
    }
    
    public NN(NNSpecification specs){
    	this(specs.inputs, specs.outputs, specs.hiddenNeuronCount, specs.hiddenLayerCount,
    			specs.sigmin, specs.sigmax, specs.weightMin, specs.weightMax);
    }
    
    public double[] run(double[] inputVector){
        double[] hiddenVector = new double[afterInputWeights.length];
        double[] outputVector = new double[toOutputWeights.length];
        hiddenVector = vector_matrix_multiplication(inputVector, afterInputWeights);
        for (int x = 0; x < hiddenVector.length; x++){
            hiddenVector[x] = sigmoid(hiddenVector[x]);
        }
        for (int i = 0; i < hiddenWeights.length; i++){
            hiddenVector = vector_matrix_multiplication(hiddenVector, hiddenWeights[i]);
            for (int x = 0; x < hiddenVector.length; x++){
                hiddenVector[x] = sigmoid(hiddenVector[x]);
            }
        }
        outputVector = vector_matrix_multiplication(hiddenVector, toOutputWeights);
        for (int x = 0; x < outputVector.length; x++){
            outputVector[x] = sigmoid(outputVector[x]);
        }
        return outputVector;
    }
    
    public void randomWeights(){
        for (int i = 0; i < afterInputWeights.length; i++){
            for (int x = 0; x < afterInputWeights[0].length; x++){
                afterInputWeights[i][x] = Math.random()*(weightMax-weightMin)+weightMin;
            }
        }
        for (int i = 0; i < hiddenWeights.length; i++){
            for (int x = 0; x < hiddenWeights[0].length; x++){
                for (int y = 0; y < hiddenWeights[0][0].length; y++){
                    hiddenWeights[i][x][y] = Math.random()*(weightMax-weightMin)+weightMin;
                }
            }
        }
        for (int i = 0; i < toOutputWeights.length; i++){
            for (int x = 0; x < toOutputWeights[0].length; x++){
                toOutputWeights[i][x] = Math.random()*(weightMax-weightMin)+weightMin;
            }
        }
    }
    public void changeAll(double percent){ //in percent
        for (int i = 0; i < afterInputWeights.length; i++){
            for (int x = 0; x < afterInputWeights[0].length; x++){
                afterInputWeights[i][x] += changeFunction(percent/50);
            }
        }
        for (int i = 0; i < hiddenWeights.length; i++){
            for (int x = 0; x < hiddenWeights[0].length; x++){
                for (int y = 0; y < hiddenWeights[0][0].length; y++){
                    hiddenWeights[i][x][y] += changeFunction(percent/50);
                }
            }
        }
        for (int i = 0; i < toOutputWeights.length; i++){
            for (int x = 0; x < toOutputWeights[0].length; x++){
                toOutputWeights[i][x] += changeFunction(percent/50);
            }
        }
    }
    
//    public void changeAllReal(double quality){ //real
//        for (int i = 0; i < afterInputWeights.length; i++){
//            for (int x = 0; x < afterInputWeights[0].length; x++){
//                afterInputWeights[i][x] += Math.random() < 0.9 ? (Math.random() - 0.5) * quality / 100 : -((Math.random() - 0.5) * quality / 100);
//            }
//        }
//        for (int i = 0; i < hiddenWeights.length; i++){
//            for (int x = 0; x < hiddenWeights[0].length; x++){
//                for (int y = 0; y < hiddenWeights[0][0].length; y++){
//                    hiddenWeights[i][x][y] += Math.random() < 0.9 ? (Math.random() - 0.5) * quality / 100 : -((Math.random() - 0.5) * quality / 100);
//                }
//            }
//        }
//        for (int i = 0; i < toOutputWeights.length; i++){
//            for (int x = 0; x < toOutputWeights[0].length; x++){
//                toOutputWeights[i][x] += Math.random() < 0.9 ? (Math.random() - 0.5) * quality / 100 : -((Math.random() - 0.5) * quality / 100);
//            }
//        }
//    }
    /**
     * takes the weights of each parent to generate new weights for the nn.
     * @param n1 first parent
     * @param n2 second parent
     */
    public void childFrom(NN n1, NN n2){ 
    	double[][] src = Math.random() < 0.5 ? n1.afterInputWeights : n2.afterInputWeights;
    	for(int i = 0; i < afterInputWeights.length; i++) {
    		System.arraycopy(src[i], 0, afterInputWeights[i], 0, afterInputWeights[i].length);
    	}
    	for(int i = 0; i < hiddenWeights.length; i++){
    		src = Math.random() < 0.5 ? n1.hiddenWeights[i] : n2.hiddenWeights[i];
    		for(int j = 0; j < hiddenWeights[i].length; j++) {
    			System.arraycopy(src[j], 0, hiddenWeights[i][j], 0, hiddenWeights[i][j].length);
    		}
    	}
    	src = Math.random() < 0.5 ? n1.toOutputWeights : n2.toOutputWeights;
    	for(int i = 0; i < toOutputWeights.length; i++) {
    		System.arraycopy(src[i], 0, toOutputWeights[i], 0, toOutputWeights[i].length);
    	}
    }
    
    private static double changeFunction(double scale){
    	return ((1.6*Math.random()-0.8)*(1.6*Math.random()-0.8)*(1.6*Math.random()-0.8))*scale;
    }
    
    private double sigmoid(double x) {
        return (1/( 1 + Math.pow(Math.E,(-x))) + ((sigmin + sigmax) / 2)-0.5) * (sigmax-sigmin);
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
    			.put("Sigmoid Min", sigmin)
    			.put("Sigmoid Max", sigmax)
    			.put("Weight Min", weightMin)
    			.put("Weight Max", weightMax);
    }
}