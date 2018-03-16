package training.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import json.JSONObject;
import json.JSONTokener;
import util.JSONUtil;
import util.JSONable;
/**
 * When loading a JSONSaveableTrainingSession the savedir is searched for a Session.json file.
 * If this file is found in the directory it is opened and the "Class Name" parameter is read.
 * Then the loader tries to load the class with this name
 * and to instantiate it with the save directory as a parameter.
 *
 */
public abstract class JSONSaveableTrainingSession extends SaveableTrainingSession implements JSONable{
	
	/**
	 * This default charset is used when none is explicitly specified when saving.
	 */
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	/**
	 * Default indent factor for converting the json object to a string.
	 */
	public static final int DEFAULT_INDENT_FACTOR = 2;
	/**
	 * Default name for the save file.
	 */
	public static String SAVE_NAME = "Session.json";

	public JSONSaveableTrainingSession(File saveDir, String name, int totalEpochs, int saveInterval) {
		super(saveDir, name, totalEpochs, saveInterval);
	}
	
	public JSONSaveableTrainingSession(File saveDir, JSONObject save) {
		super(saveDir, save.getString("Name"));
		this.totalEpochs = save.getInt("Epochs");
		this.saveInterval = save.getInt("Save Interval");
	}
	public JSONSaveableTrainingSession(File saveDir) {
		super(saveDir, getSaveObject(saveDir).getString("Name"));
		//TODO(?) This inefficiency of calling getDefaultSaveObject() twice
		//could maybe be removed.
		JSONObject save = getSaveObject(saveDir);
		this.totalEpochs = save.getInt("Epochs");
		this.saveInterval = save.getInt("Save Interval");
	}
	
	private static JSONObject getSaveObject(File dir) {
		JSONObject save = null;
		File saveFile = new File(dir.getAbsolutePath() + "/" + SAVE_NAME);
		try {
			save = JSONUtil.loadJOFromFile(saveFile);
		} catch (IOException e) {
			throw new IllegalArgumentException("This save directory contains no default save file.");
		}
		return save;
	}
	/**
	 * 
	 * @param indentFactor Indent factor to use when converting the json object to a String.
	 * @param charset Charset to encode the string to bytes.
	 * @throws IOException
	 */
	public void saveToFile(int indentFactor, Charset charset) throws IOException {
		File file = new File(saveDir.getAbsolutePath() + "/" + SAVE_NAME);
		//TODO there is probably other stuff to test before continuing
		if(charset == null)
			throw new IllegalArgumentException("The charset is null.");
		if(!file.exists()) file.createNewFile();
		if(!file.canWrite())
			throw new IllegalArgumentException("This file is (currently) not accessible!");
		if(indentFactor < 0)
			throw new IllegalArgumentException("indentFactor must be at least 0.");
		FileOutputStream writer = new FileOutputStream(file);
		writer.write(charset.encode(toJSONObject().toString(indentFactor)).array());
		writer.close();
	}
	public void saveToFile(int indentFactor, String charsetName) throws IOException {
		saveToFile(indentFactor, Charset.forName(charsetName));
	}
	public void saveToFile(int indentFactor) throws IOException {
		saveToFile(indentFactor, DEFAULT_CHARSET);
	}
	public void saveToFile() throws IOException {
		saveToFile(DEFAULT_INDENT_FACTOR, DEFAULT_CHARSET);
	}
	/**
	 * When this method is overwritten to save the own data
	 * the super method should be called to save all data that is handled by supertypes.
	 * Probably the best way is to do this:
	 * <p>
	 * <code>
	 * 	public void toJSONObject() { <ul>
	 * 		return super.toJSONObject() <br>
	 * 			.put("Your Key", yourValue) <br>
	 * 			.put("Another Key", anotherValue); </ul>
	 * 	}
	 * <p>
	 * </code>
	 */
	public JSONObject toJSONObject() {
		return new JSONObject()
				.put("Class Name", getClass().getName())
				.put("Name", name)
				.put("Epochs", totalEpochs)
				.put("Save Interval", saveInterval);
	}
}
