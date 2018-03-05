package util;

import json.JSONObject;

/**
 * Classes that implement this provide a method to convert data or the whole object to a JSONObject.
 */
public interface JSONable {
	/**
	 * @return Any information as a JSONObject. This is very useful for saving the state of the object.
	 */
	public JSONObject toJSONObject();
}
