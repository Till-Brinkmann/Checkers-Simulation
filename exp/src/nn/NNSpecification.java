package nn;

import json.JSONObject;

public class NNSpecification {
	
	public final int inputs;
	public final int hiddenLayerCount;
	public final int hiddenNeuronCount;
	public final int outputs;
	
	public final float sigscale;
	public final float sigoffset;
	public final float weightMin;
	public final float weightDiff;
	
	

	public NNSpecification(int inputs, int hiddenLayerCount, int hiddenNeuronCount, int outputs,
			float sigscale, float sigoffset, float weightMin, float weightDiff, int nnQuantity,
			int nnSurviver) {
		this.inputs = inputs;
		this.hiddenLayerCount = hiddenLayerCount;
		this.hiddenNeuronCount = hiddenNeuronCount;
		this.outputs = outputs;
		this.sigscale = sigscale;
		this.sigoffset = sigoffset;
		this.weightMin = weightMin;
		this.weightDiff = weightDiff;
	}
	
	public String[] toStringArray() {
		return new String[] {
			Integer.toString(inputs),
			Integer.toString(hiddenLayerCount),
			Integer.toString(hiddenNeuronCount),
			Integer.toString(outputs),
			Float.toString(sigscale),
			Float.toString(sigoffset),
			Float.toString(weightMin),
			Float.toString(weightDiff),
		};
	}
	public JSONObject toJSONObject() {
		return new JSONObject()
				.put("Inputs", inputs)
				.put("Hiddenlayer Count", hiddenLayerCount)
				.put("Hiddenneuron Count", hiddenNeuronCount)
				.put("Outputs", outputs)
				.put("Sigmoid Scale", sigscale)
				.put("Sigmoid Offset", sigoffset)
				.put("Weight Min", weightMin)
				.put("Weight Difference", weightDiff);
	}
}
