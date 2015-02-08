/**
 * 
 */
package com.hafidz.stylo.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author hafidz
 * 
 */
public class MemberDB extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 20;
	public static final String DATABASE_NAME = "MEMBER.db";

	public static final String TABLE_NAME = "MEMBERS";

	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ TABLE_NAME + " (" + "NAME TEXT PRIMARY KEY," + "EMAIL TEXT,"
			+ "POS_Y REAL" + " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	public MemberDB(Context context) {
		super(context, MemberDB.DATABASE_NAME, null, MemberDB.DATABASE_VERSION);
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
