package com.coin.karaoke.misook.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Favorite_DBopenHelper extends SQLiteOpenHelper {
	public Favorite_DBopenHelper(Context context) {
		super(context, "favorite.db", null, 1);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
			String createtable = "create table favorite_list ( _id integer primary key autoincrement,"+
			"id text, title text, portal text, category text, thumbnail_hq text, duration text);";
			db.execSQL(createtable);

	}		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               		
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exits favorite_list"); 
		onCreate(db);
	}
}