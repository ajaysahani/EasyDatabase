package com.ajra.demo.database;

import java.util.HashMap;

import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.ajra.easydatabase.EasyDatabaseContentProvider;
import com.example.easydatabasedemo.R;

public class BookContentProvider extends EasyDatabaseContentProvider {

	public static String BOOK_TABLE_CREATION_STRING;
	public static Uri BOOK_TABLE_URI;
	public final static String BOOK_TABLE_NAME = "BookTable";
	private final int BOOK_TABLE_IDENTIFIER = 101;
	// Define column name key
	public static final String KEY_BOOK_NAME = "Name";
	public static final String KEY_AUTHOR_NAME = "Author";
	public static final String KEY_PRICE = "Price";

	@Override
	protected void initialize() {
		// This variable get use in table creation in SQL Helper class because
		// of that it is static
		BOOK_TABLE_CREATION_STRING = getQueryTableString();
		// This variable get use in query fire from any where in app because
		// of that it is static
		BOOK_TABLE_URI = getContentURI();

	}

	@Override
	public SQLiteOpenHelper getSqlHelper() {
		SQLiteOpenHelper sqlHelper = DemoSqlHelper
				.getSQLHelperInstance(getContext());
		return sqlHelper;
	}

	@Override
	protected void fillTableParamMap(HashMap<String, String> tableParamMap) {
		tableParamMap.put(KEY_ID, "INTEGER PRIMARY KEY autoincrement");
		tableParamMap.put(KEY_BOOK_NAME, "varchar(50) not null");
		tableParamMap.put(KEY_AUTHOR_NAME, "varchar(50)");
		tableParamMap.put(KEY_PRICE, "int");

	}

	@Override
	protected void fillProjectionMap(HashMap<String, String> projectionMap) {
		projectionMap.put(KEY_BOOK_NAME, KEY_BOOK_NAME);
		projectionMap.put(KEY_AUTHOR_NAME, KEY_AUTHOR_NAME);
		projectionMap.put(KEY_PRICE, KEY_PRICE);
	}

	@Override
	protected String getTableName() {
		return BOOK_TABLE_NAME;
	}

	@Override
	protected int getTableIdentifier() {
		return BOOK_TABLE_IDENTIFIER;
	}

	@Override
	protected String getAuthority() {
		return getContext().getResources().getString(
				R.string.book_authority_name);
	}

}
