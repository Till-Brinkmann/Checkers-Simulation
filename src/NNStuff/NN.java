package NNStuff;
public class NN
{
    private double[][] afterInputWeights;
    private double[][][] hiddenWeights;
    private double[][] toOutputWeights;
    private double sigmin;
    private double sigmax;
    public NN(int inputNeurons, int outputNeurons,  int hiddenNeurons, int hiddenlayer, double min, double max)
    {
        sigmin = min;
        sigmax = max;
        afterInputWeights = new double[hiddenNeurons][inputNeurons];
        hiddenWeights = new double[hiddenlayer-1][hiddenNeurons][hiddenNeurons];
        toOutputWeights = new double[outputNeurons][hiddenNeurons];
    }
    
    private double sigmoid(double x) {
        return (1/( 1 + Math.pow(Math.E,(-x))) + ((sigmin + sigmax) / 2)-0.5) * (sigmax-sigmin);
    }
    
    private double[] vector_matix_multiplication(double[] vector, double[][] matrix){
        double[] resultVector = new double[matrix.length];
        for (int i = 0; i < resultVector.length; i++){
            for (int x = 0; x < vector.length; x++){
                resultVector[i] += vector[x] * matrix[i][x];
            }
        }
        return resultVector;
    }
    
    public void setWeights(double[][] aIWeights, double[][][] hweights, double[][] tOWeights){
        afterInputWeights = aIWeights;
        hiddenWeights = hweights;
        toOutputWeights = tOWeights;
    }
    
    public double[][] getAfterInputWeights(){
        return afterInputWeights;
    }
    
    public double[][][] getHiddenWeights(){
        return hiddenWeights;
    }
    
    public double[][] getToOutputWeights(){
        return toOutputWeights;
    }
    
    public void randomWeights(double min, double max){
        for (int i = 0; i < afterInputWeights.length; i++){
            for (int x = 0; x < afterInputWeights[0].length; x++){
                afterInputWeights[i][x] = Math.random()*(max-min)+min;
            }
        }
        for (int i = 0; i < hiddenWeights.length; i++){
            for (int x = 0; x < hiddenWeights[0].length; x++){
                for (int y = 0; y < hiddenWeights[0][0].length; y++){
                    hiddenWeights[i][x][y] = Math.random()*(max-min)+min;
                }
            }
        }
        for (int i = 0; i < toOutputWeights.length; i++){
            for (int x = 0; x < toOutputWeights[0].length; x++){
                toOutputWeights[i][x] = Math.random()*(max-min)+min;
            }
        }
    }
    public void changeAllPercent(double quality){ //in percent
        for (int i = 0; i < afterInputWeights.length; i++){
            for (int x = 0; x < afterInputWeights[0].length; x++){
                afterInputWeights[i][x] *= 1 + (Math.random() - 0.5) * 2 * quality / 100;
            }
        }
        for (int i = 0; i < hiddenWeights.length; i++){
            for (int x = 0; x < hiddenWeights[0].length; x++){
                for (int y = 0; y < hiddenWeights[0][0].length; y++){
                    hiddenWeights[i][x][y] *= 1 + (Math.random() - 0.5) * 2 * quality / 100;
                }
            }
        }
        for (int i = 0; i < toOutputWeights.length; i++){
            for (int x = 0; x < toOutputWeights[0].length; x++){
                toOutputWeights[i][x] *= 1 + (Math.random() - 0.5) * 2 * quality / 100;
            }
        }
    }
    
    public void changeAllReal(double quality){ //real
        for (int i = 0; i < afterInputWeights.length; i++){
            for (int x = 0; x < afterInputWeights[0].length; x++){
                afterInputWeights[i][x] += (Math.random() - 0.5) * 2 * quality / 100;
            }
        }
        for (int i = 0; i < hiddenWeights.length; i++){
            for (int x = 0; x < hiddenWeights[0].length; x++){
                for (int y = 0; y < hiddenWeights[0][0].length; y++){
                    hiddenWeights[i][x][y] += (Math.random() - 0.5) * 2 * quality / 100;
                }
            }
        }
        for (int i = 0; i < toOutputWeights.length; i++){
            for (int x = 0; x < toOutputWeights[0].length; x++){
                toOutputWeights[i][x] += (Math.random() - 0.5) * 2 * quality / 100;
            }
        }
    }   
    public double[] run(double[] inputVector){
        double[] hiddenVector = new double[afterInputWeights.length];
        double[] outputVector = new double[toOutputWeights.length];
        hiddenVector = vector_matix_multiplication(inputVector, afterInputWeights);
        for (int x = 0; x < hiddenVector.length; x++){
            hiddenVector[x] = sigmoid(hiddenVector[x]);
        }
        for (int i = 0; i < hiddenWeights.length; i++){
            hiddenVector = vector_matix_multiplication(hiddenVector, hiddenWeights[i]);
            for (int x = 0; x < hiddenVector.length; x++){
                hiddenVector[x] = sigmoid(hiddenVector[x]);
            }
        }
        outputVector = vector_matix_multiplication(hiddenVector, toOutputWeights);
        for (int x = 0; x < outputVector.length; x++){
            outputVector[x] = sigmoid(outputVector[x]);
        }
        return outputVector;
    }
}