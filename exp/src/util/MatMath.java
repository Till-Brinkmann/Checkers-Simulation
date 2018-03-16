package util;

public class MatMath {
	
	private MatMath() {}
	
	/**
	 * multiplies a vector with a matrix.
	 * @param vector
	 * @param matrix
	 * @return
	 */
	public static double[] dot(double[] vector, double[][] matrix){
		final int matLen = matrix.length, vecLen = vector.length;
        double[] resultVector = new double[matLen];
        for (int i = 0; i < matLen; i++){
            for (int x = 0; x < vecLen; x++){
                resultVector[i] += vector[x] * matrix[i][x];
            }
        }
        return resultVector;
    }
}
