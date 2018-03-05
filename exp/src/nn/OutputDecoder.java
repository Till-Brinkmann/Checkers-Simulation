package nn;
/**
 * Decodes the output of a neural network.
 * @param <OutputType> Type of output from the nn.
 * @param <DataType> Type to convert to.
 */
public interface OutputDecoder<OutputType, DataType> {
	/**
	 * Decodes a OutputType object to a DataType object.
	 * @param In Object of type OutputType to decode.
	 * @return A DataType object.
	 */
	public DataType decodeOutput(OutputType in);
}
