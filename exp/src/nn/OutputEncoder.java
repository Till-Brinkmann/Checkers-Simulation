package nn;
/**
 * Encodes data to an output of a neural network.
 * This is important for backpropagation because you need the encoded output
 * from the wanted data to calculate the cost.
 * @param <DataType> Type to convert from.
 * @param <OutputType> Type of output from the nn.
 */
public interface OutputEncoder<DataType, OutputType> {
	/**
	 * Decodes a OutputType object to a DataType object.
	 * @param In Object of type OutputType to decode.
	 * @return A DataType object.
	 */
	public OutputType encodeOutput(DataType in);
}