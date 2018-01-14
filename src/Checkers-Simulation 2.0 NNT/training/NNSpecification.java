package training;

import json.JSONObject;

public class NNSpecification {
	
	public final int nnQuantity;
	public final int nnSurviver;
	
	public final int inputs;
	public final int hiddenLayerCount;
	public final int hiddenNeuronCount;
	public final int outputs;
	
	public final float sigmin;
	public final float sigmax;
	public final float weightMin;
	public final float weightMax;
	
	public final float defaultChangePercentage;
	public final float learnrate;

	public NNSpecification(int inputs, int hiddenLayerCount, int hiddenNeuronCount, int outputs,
			float sigmin, float sigmax, float weightMin, float weightMax, int nnQuantity,
			int nnSurviver, float defaultChangePercentage, float learnrate) {
		this.inputs = inputs;
		this.hiddenLayerCount = hiddenLayerCount;
		this.hiddenNeuronCount = hiddenNeuronCount;
		this.outputs = outputs;
		this.sigmin = sigmin;
		this.sigmax = sigmax;
		this.weightMin = weightMin;
		this.weightMax = weightMax;
		this.nnQuantity = nnQuantity;
		this.nnSurviver = nnSurviver;
		this.defaultChangePercentage = defaultChangePercentage;
		this.learnrate = learnrate;
	}
	
	public String[] toStringArray() {
		return new String[] {
			Integer.toString(nnQuantity),
			Integer.toString(nnSurviver),
			Integer.toString(inputs),
			Integer.toString(hiddenLayerCount),
			Integer.toString(hiddenNeuronCount),
			Integer.toString(outputs),
			Float.toString(sigmin),
			Float.toString(sigmax),
			Float.toString(weightMin),
			Float.toString(weightMax),
			Float.toString(defaultChangePercentage),
		};
	}
	public JSONObject toJSONObject() {
		return new JSONObject()
				.put("NNQuantity", nnQuantity)
				.put("NNSurviver", nnSurviver)
				.put("Inputs", inputs)
				.put("HiddenlayerCount", hiddenLayerCount)
				.put("HiddenNeuronCount", hiddenNeuronCount)
				.put("Outputs", outputs)
				.put("Sigmoid Min", sigmin)
				.put("Sigmoid Max", sigmax)
				.put("Weight Min", weightMin)
				.put("Weight Max", weightMax)
				.put("Default ChangePercentage", defaultChangePercentage)
				.put("Learnrate", learnrate);
	}
}
