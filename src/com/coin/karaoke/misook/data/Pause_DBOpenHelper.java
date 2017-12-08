package com.coin.karaoke.misook.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Pause_DBOpenHelper extends SQLiteOpenHelper {
	public Pause_DBOpenHelper(Context context) {
		super(context, "video_pause.db", null, 1);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
			String createtable = "create table video_pause ( _id integer primary key autoincrement,"+
			"video_title text, video_currentPosition integer, video_pubDate text);";
			db.execSQL(createtable);

	}		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               		
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exits video_pause"); 
		onCreate(db);
	}
}