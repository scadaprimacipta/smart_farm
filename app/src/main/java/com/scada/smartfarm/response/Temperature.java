package com.scada.smartfarm.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Temperature{
	@SerializedName("bins")
	private List<Double> bins;
	@SerializedName("hist")
	private List<Double> hist;

	public List<Double> getBins(){
		return bins;
	}

	public List<Double> getHist(){
		return hist;
	}
}