/*
 * com.ziofront.android.contacts
 * Contact.java
 * Jiho Park    2009. 11. 27.
 *
 * Copyright (c) 2009 ziofront.com. All Rights Reserved.
 */
package com.coin.karaoke.misook.data;


public class Favorite_Data {
	int _id;
	String id; 
	String title;
	String portal;
	String category;
	String thumbnail_hq;
	String duration;
	public Favorite_Data(int _id, String id, String title, String portal, String category, String thumbnail_hq, String duration){
		this._id = _id;
		this.id = id;
		this.title = title;
		this.portal = portal;
		this.category = category;
		this.thumbnail_hq = thumbnail_hq;
		this.duration = duration;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getThumbnail_hq() {
		return thumbnail_hq;
	}
	public void setThumbnail_hq(String thumbnail_hq) {
		this.thumbnail_hq = thumbnail_hq;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getPortal() {
		return portal;
	}
	public void setPortal(String portal) {
		this.portal = portal;
	}
}