package com.example.unmcampuslocatorapp;
/*
 * class AssetReader used for abqbuildings.txt. needs to be modified to support all
 * text files we will be using (easy task). all objects set as string type.
 * perhaps change this rather than converting later on??
 */



public class AssetsReader {
	
	public String title;
	public String latitude;
	public String longitude;
	public String buildingAbbr;

	public String getBuildingAbbr() {
		return buildingAbbr;
	}
	public void setBuildingAbbr(String buildingAbbr) {
		this.buildingAbbr = buildingAbbr;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	
}