package training.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import json.JSONArray;
import json.JSONObject;
import training.data.TrainingSet;
import training.data.TrainingExample;
import util.JSONArrayable;
import util.JSONable;
/**
 * Provides the training data as a JSONObject or JSONArray for easy saving or sharing.
 * @author Till
 *
 * @param <InputType>
 * @param <OutputType>
 */
public abstract class JSONSaveableTrainingSet<InputType, OutputType> 
	extends TrainingSet<InputType, OutputType>
	implements JSONable, JSONArrayable {
	
	/**
	 * Base class of every saveable TrainingExample
	 */
	public class JSONSaveableTrainingExample
	extends TrainingExample<InputType, OutputType>
	implements JSONArrayable{
		
		public JSONSaveableTrainingExample(JSONArray save) {
			//TODO(?) this is probably not the best way, but super does not work
			//because you need to invoke the load methods.
			input = loadInputfromJSON(save.getJSONObject(0));
			output = loadOutputfromJSON(save.getJSONObject(1));
		}
		/**
		 * Passthrough constructor.
		 * @param input
		 * @param output
		 * @see TrainingSet.TrainingExample#TrainingExample(Object, Object)
		 */
		public JSONSaveableTrainingExample(InputType input, OutputType output) {
			super(input, output);
			//this.parent = parent;
		}
		
		public JSONArray toJSONArray() {
			return new JSONArray()
					.put(saveInputToJSON(input))
					.put(saveOutputToJSON(output));
		}
	}
	/**
	 * Creates an empty trainingset.
	 */
	public JSONSaveableTrainingSet() {
		super();
	}
	/**
	 * Attempts to fill the triningset with values from the JSONObject.
	 * @param save A JSONObject that can be loaded by this class.
	 */
	public JSONSaveableTrainingSet(JSONObject save) {
		JSONArray vals = save.getJSONArray("TrainingExamples");
		for(int i = 0, len = vals.length(); i < len; i++) {
			append(new JSONSaveableTrainingExample(vals.getJSONArray(i)));
		}
	}
	/**
	 * Add a new example.
	 * @param e The example to add. Can not be null and must be an instance of .
	 */
	@Override
	public void addTrainingExample(TrainingExample<InputType, OutputType> e) {
		//we do not want null here
		if(e == null) return;
		if(e instanceof JSONSaveableTrainingSet.JSONSaveableTrainingExample) {
			append(e);
			return;
		}
		throw new IllegalArgumentException("The trainingexample has to be json saveable.");
	}
	@Override
	public void addTrainingExample(InputType input, OutputType output) {
		append(new JSONSaveableTrainingExample(input, output));
	}
	//TODO this methods could be static if they were not dealing with generic types.
	//There could be a better way.
	/**
	 * Should be overwritten to load an input object from json.
	 * If the data can not be loaded, a exception should be thrown.
	 */
	protected abstract InputType loadInputfromJSON(JSONObject save);
	/**
	 * Should be overwritten to load an output object from json.
	 */
	protected abstract OutputType loadOutputfromJSON(JSONObject save);
	
	protected abstract JSONObject saveInputToJSON(InputType input);
	
	protected abstract JSONObject saveOutputToJSON(OutputType output);
	
	/**
	 * 
	 * @param file The file to save to.
	 * @param indentFactor Indent factor to use when converting the json object to a String.
	 * @param charset Charset to encode the string to bytes.
	 * @throws IOException
	 */
	public void saveToFile(File file, int indentFactor, Charset charset) throws IOException {
		//TODO there is probably other stuff to test before continuing
		if(file == null || charset == null)
			throw new IllegalArgumentException("The charset or the file is null.");
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		if(!file.canWrite())
			throw new IllegalArgumentException("This file is (currently) not accessible!");
		if(indentFactor < 0)
			throw new IllegalArgumentException("indentFactor must be at least 0.");
		FileOutputStream writer = new FileOutputStream(file);
		String str = toJSONObject().toString(indentFactor);
		ByteBuffer b = charset.encode(str);
		System.out.println("Writing");
		writer.write(b.array());
		writer.close();
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray toJSONArray() {
		JSONArray array = new JSONArray();
		for(toFirst(); hasAccess(); nextNoWrap()) {
			array.put(((JSONSaveableTrainingExample) get()).toJSONArray());
		}
		return array;
	}

	@Override
	public JSONObject toJSONObject() {
		return new JSONObject()
				.put("TrainingExamples", toJSONArray());
	}
	
	
}
