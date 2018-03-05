package nn;

import json.JSONObject;

public class NNSpecification {
	
	public final int inputs;
	public final int hiddenLayerCount;
	public final int hiddenNeuronCount;
	public final int outputs;
	
	public final float sigmin;
	public final float sigmax;
	public final float weightMin;
	public final float weightMax;
	
	

	public NNSpecification(int inputs, int hiddenLayerCount, int hiddenNeuronCount, int outputs,
			float sigmin, float sigmax, float weightMin, float weightMax, int nnQuantity,
			int nnSurviver) {
		this.inputs = inputs;
		this.hiddenLayerCount = hiddenLayerCount;
		this.hiddenNeuronCount = hiddenNeuronCount;
		this.outputs = outputs;
		this.sigmin = sigmin;
		this.sigmax = sigmax;
		this.weightMin = weightMin;
		this.weightMax = weightMax;
	}
	
	public String[] toStringArray() {
		return new String[] {
			Integer.toString(inputs),
			Integer.toString(hiddenLayerCount),
			Integer.toString(hiddenNeuronCount),
			Integer.toString(outputs),
			Float.toString(sigmin),
			Float.toString(sigmax),
			Float.toString(weightMin),
			Float.toString(weightMax),
		};
	}
	public JSONObject toJSONObject() {
		return new JSONObject()
				.put("Inputs", inputs)
				.put("Hiddenlayer Count", hiddenLayerCount)
				.put("Hiddenneuron Count", hiddenNeuronCount)
				.put("Outputs", outputs)
				.put("Sigmoid Min", sigmin)
				.put("Sigmoid Max", sigmax)
				.put("Weight Min", weightMin)
				.put("Weight Max", weightMax);
	}
}
