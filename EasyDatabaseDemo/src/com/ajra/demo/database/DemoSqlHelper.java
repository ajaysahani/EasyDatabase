package com.ajra.demo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author Ajay Sahani
 * @see: This class should be singleton ,to avoid database lock issue.
 * 
 */
public class DemoSqlHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "DemoDatabase";
	private static DemoSqlHelper sqlHelperInstance;

	public static DemoSqlHelper getSQLHelperInstance(Context context) {
		if (sqlHelperInstance == null) {
			sqlHelperInstance = new DemoSqlHelper(context);
		}
		return sqlHelperInstance;
	}

	/**
	 * @param context
	 *            This class should be singleton ,to avoid database lock issue.
	 */
	private DemoSqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BookContentProvider.BOOK_TABLE_CREATION_STRING);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TO BE IMPLEMENTED IF DB VERSION INCREMENTED FROM 1;
	}

}
