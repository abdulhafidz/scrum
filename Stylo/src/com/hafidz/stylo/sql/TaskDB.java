/**
 * 
 */
package com.hafidz.stylo.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * @author hafidz
 * 
 */
public class TaskDB extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 20;
	public static final String DATABASE_NAME = "TASK.db";

	public static final String TABLE_NAME = "TASKS";

	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ TABLE_NAME + " (" + "TASK_ID TEXT PRIMARY KEY," + "TITLE TEXT,"
			+ "DESC TEXT," + "OWNER TEXT, POS_X REAL, POS_Y REAL, STATUS INTEGER" + " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	public TaskDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);

	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	// /* Inner class that defines the table contents */
	// public static abstract class FeedEntry implements BaseColumns {
	// public static final String TABLE_NAME = "TASKS";
	//
	// }

}
