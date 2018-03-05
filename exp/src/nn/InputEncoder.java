package nn;
/**
 * Implemented by classes that support encoding the input for a nn.
 * @param <DataType> The type of object to decode to an input type.
 * @param <InputType> The type of data that is taken by the nn as input.
 */
public interface InputEncoder<DataType, InputType> {
	/**
	 * Encodes a InputType object to a InputType object.
	 * @param input The object to encode.
	 * @return Encoded input.
	 */
	public InputType encodeInput(DataType in);
}
