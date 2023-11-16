package com.scada.smartfarm.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BoxData{
	@SerializedName("boxes_data")
	private List<BoxesDataItem> boxesData;
	@SerializedName("labels")
	private List<String> labels;

	public List<BoxesDataItem> getBoxesData(){
		return boxesData;
	}

	public List<String> getLabels(){
		return labels;
	}
}