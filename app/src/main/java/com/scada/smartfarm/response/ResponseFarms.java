package com.scada.smartfarm.response;

import com.google.gson.annotations.SerializedName;

public class ResponseFarms {
	@SerializedName("image")
	private String image;
	@SerializedName("box_data")
	private BoxData boxData;

	public String getImage(){
		return image;
	}

	public BoxData getBoxData(){
		return boxData;
	}
}
