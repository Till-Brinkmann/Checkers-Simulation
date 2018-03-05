package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import json.JSONObject;
import json.JSONTokener;

public class JSONUtil {

	/**
	 * Can not be instantiated.
	 */
	private JSONUtil() {}
	
	/**
	 * Tries to create a JSONObject with the data in the given file.
	 * @param file The file to load from.
	 * @return A new JSONObject.
	 * @throws IOException
	 */
	public static JSONObject loadJOFromFile(File file) throws IOException {
		FileInputStream reader = new FileInputStream(file);
		return new JSONObject(new JSONTokener(reader));
	}
}
