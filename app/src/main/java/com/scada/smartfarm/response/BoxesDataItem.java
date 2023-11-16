package com.scada.smartfarm.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BoxesDataItem{
	@SerializedName("absolute_position")
	private List<Integer> absolutePosition;
	@SerializedName("relative_position")
	private List<Double> relativePosition;
	@SerializedName("temperature")
	private Temperature temperature;


	public Temperature getTemperature(){
		return temperature;
	}

	public List<Integer> getAbsolutePosition(){
		return absolutePosition;
	}

	public List<Double> getRelativePosition(){
		return relativePosition;
	}
}