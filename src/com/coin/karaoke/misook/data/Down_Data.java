/*
 * com.ziofront.android.contacts
 * Contact.java
 * Jiho Park    2009. 11. 27.
 *
 * Copyright (c) 2009 ziofront.com. All Rights Reserved.
 */
package com.coin.karaoke.misook.data;


public class Down_Data {
	int _id;
	String title; 
	String enclosure;
	String pubDate;
	String image;
	String description_title;
	String provider;
	public Down_Data(int _id, String title, String enclosure, String pubDate, String image, String description_title, String provider){
		this._id = _id;
		this.title = title;
		this.enclosure = enclosure;
		this.pubDate = pubDate;
		this.image = image;
		this.description_title = description_title;
		this.provider = provider;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getEnclosure() {
		return enclosure;
	}
	public void setEnclosure(String enclosure) {
		this.enclosure = enclosure;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getDescription_title() {
		return description_title;
	}
	public void setDescription_title(String description_title) {
		this.description_title = description_title;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
}