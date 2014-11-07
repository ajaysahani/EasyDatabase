package com.ajra.easydatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author Ajay Sahani
 * @see Class which reduce complexity in creating database using content
 *      provider.
 */
public abstract class EasyDatabaseContentProvider extends ContentProvider {

	private String tag = EasyDatabaseContentProvider.class.getSimpleName();
	private final String CONTENT_TYPE = "vnd.android.cursor.item";
	private UriMatcher mUriMatcher;
	private LinkedHashMap<String, String> projectionMap;
	private LinkedHashMap<String, String> tableParamMap;
	private SQLiteOpenHelper mSqlHelper;
	/**
	 * KEY_ID: unique identifier for table row Here we as using its data type as
	 * 'integer primary key autoincrement' but we can override behavior by
	 * changing value for key KEY_ID; example : we want only "int" than write
	 * down following line in fillTableParamMap(HashMap<String, String>
	 * tableParamMap) method: tableParamMap.put(KEY_ID, "int");
	 * 
	 */
	public static final String KEY_ID = "_id";
	private boolean isDebaugModeOn = false;

	@Override
	public boolean onCreate() {
		initDefault();
		return true;
	}

	private void initDefault() {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher
				.addURI(getAuthority(), getTableName(), getTableIdentifier());
		
		tableParamMap = new LinkedHashMap<String, String>();
		tableParamMap.put(KEY_ID, "integer primary key autoincrement");
		fillTableParamMap(tableParamMap);
		fillProjectionMap();
		initialize();
		mSqlHelper=getSqlHelper();
	}

	private void fillProjectionMap(){
		projectionMap = new LinkedHashMap<String, String>();
		for (Map.Entry<String, String> entry : tableParamMap.entrySet()) {
		    String key = entry.getKey();
			projectionMap.put(key, key);
		}
	}
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count = 0;
		if (uri == null || getUriMatcher() == null) {
			log(tag, "uri is null? " + (null == uri) + " uri matcher is null? "
					+ (null == getUriMatcher()) + " in delete method");
			return count;
		}
		String tableName = getTableName();
		SQLiteDatabase db = getWriteableDataBase();
		if (db != null && db.isOpen()) {
			try {
				if (TextUtils.isEmpty(tableName) == false) {
					db.beginTransaction();
					count = db.delete(tableName, where, whereArgs);
					db.setTransactionSuccessful();
					db.endTransaction();
					if (count > 0) {
						notifyChangeToObeservers(uri, null);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (count == 0) {
			log(tag,
					"FAILED TO DELETE COUNT = 0 table " + tableName
							+ " WHERE : " + where + " WHEREARGS "
							+ Arrays.toString(whereArgs));
		} else {
			log(tag, "delete complete " + tableName + " where : " + where
					+ " whereargs " + Arrays.toString(whereArgs)
					+ "Successfully deleted : " + count);
		}
		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (uri == null || getUriMatcher() == null) {
			log(tag, "uri is null? " + (null == uri) + " uri matcher is null? "
					+ (null == getUriMatcher()) + " in query method");
			return null;
		}
		if (null == values) {
			values = new ContentValues();
		}
		SQLiteDatabase db = getWriteableDataBase();
		long rowId = 0;
		Uri newRowUri = null;
		String tableName = null;
		if (null != db && db.isOpen()) {
			try {
				tableName = getTableName();
				Uri contentUri = getContentURI();
				if (TextUtils.isEmpty(tableName) == false && null != contentUri) {
					db.beginTransaction();
					rowId = db.insertOrThrow(tableName, null, values);
					db.setTransactionSuccessful();
					db.endTransaction();
					if (rowId > 0) {
						newRowUri = ContentUris.withAppendedId(contentUri,
								rowId);
						notifyChangeToObeservers(newRowUri, null);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (newRowUri == null) {
			log(tag, "FAILED TO INSERT COUNT = 0 table " + tableName);
		} else {
			log(tag,
					"insert complete " + tableName + " values : "
							+ values.valueSet() + " new FEED_URI " + newRowUri
							+ " rowId " + rowId);
		}
		return newRowUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (uri == null || getUriMatcher() == null) {
			log(tag, "uri is null? " + (null == uri) + " uri matcher is null? "
					+ (null == getUriMatcher()) + " in query method");
			return null;
		}
		SQLiteQueryBuilder queryBuilder = getQueryBuilder();
		SQLiteDatabase sqliteDataBase = getReadableDataBase();
		Cursor cursor = null;
		if (null != sqliteDataBase && null != queryBuilder
				&& sqliteDataBase.isOpen()) {
			try {
				sqliteDataBase.beginTransaction();
				cursor = queryBuilder.query(sqliteDataBase, projection,
						selection, selectionArgs, null, null, sortOrder);
				sqliteDataBase.setTransactionSuccessful();
				sqliteDataBase.endTransaction();
				if (null != cursor) {
					cursor.setNotificationUri(
							getContext().getContentResolver(), uri);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// here we can not close the cursor because it will be used by
				// client application to retrieve data from .
			}
		} else {
			log(tag, "mSQLiteDataBaseObj is null? " + (null == sqliteDataBase)
					+ " mSQLiteQueryBuilderObj is null? "
					+ (null == queryBuilder) + " mSQLiteDataBaseObj is open? "
					+ (sqliteDataBase.isOpen()));
			log(tag, "returning null cursor");
		}
		int count = 0;
		String tableName = getTableName();
		if (cursor == null || (count = cursor.getCount()) == 0) {
			log(tag,
					"QUERY FAILED COUNT = 0 table " + tableName + " WHERE : "
							+ selection + " WHEREARGS "
							+ Arrays.toString(selectionArgs) + " projection "
							+ Arrays.toString(projection) + " SORT "
							+ sortOrder);
		} else {
			log(tag, "Query complete " + tableName + " where : " + selection
					+ " whereargs " + Arrays.toString(selectionArgs)
					+ " projection " + Arrays.toString(projection)
					+ " Successfully fetched : " + count);
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		int count = 0;
		if (uri == null || getUriMatcher() == null) {
			log(tag, "uri is null? " + (null == uri) + " uri matcher is null? "
					+ (null == getUriMatcher()) + " in update method ");
			return count;
		}
		String tableName = null;
		SQLiteDatabase db = getWriteableDataBase();
		if (null != db && db.isOpen()) {
			try {
				tableName = getTableName();
				db.beginTransaction();
				count = db.update(tableName, values, where, whereArgs);
				db.setTransactionSuccessful();
				db.endTransaction();
				if (count > 0) {
					notifyChangeToObeservers(uri, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			log(tag, "db is null ?" + (null == db));
			if (null != db) {
				log(tag, "db is open ?" + (db.isOpen()));
			}
		}
		if (count == 0) {
			log(tag, "UPDATE FAILED COUNT = 0 table " + tableName + " WHERE : "
					+ where + " WHEREARGS " + Arrays.toString(whereArgs));
		} else {
			log(tag,
					"update complete " + tableName + " values : "
							+ values.valueSet() + " where : " + where
							+ " whereargs " + Arrays.toString(whereArgs)
							+ " count " + count);
		}
		return count;
	}

	private SQLiteDatabase getReadableDataBase() {
		if (mSqlHelper == null) {
			mSqlHelper=getSqlHelper();
		}
		return mSqlHelper.getReadableDatabase();
	}

	private SQLiteDatabase getWriteableDataBase() {
		if (mSqlHelper == null) {
			mSqlHelper=getSqlHelper();
		}
		return mSqlHelper.getWritableDatabase();
	}

	private void notifyChangeToObeservers(Uri uri, ContentObserver observer) {
		getContext().getContentResolver().notifyChange(uri, observer);
	}

	private SQLiteQueryBuilder getQueryBuilder() {
		SQLiteQueryBuilder sQLiteQueryBuilderObj = null;
		sQLiteQueryBuilderObj = new SQLiteQueryBuilder();
		sQLiteQueryBuilderObj.setTables(getTableName());
		sQLiteQueryBuilderObj.setProjectionMap(getProjectionMap());
		return sQLiteQueryBuilderObj;
	}

	@Override
	public String getType(Uri uri) {
		final int tableIdentifier = getTableIdentifier();
		if (null == uri || null == getUriMatcher()) {
			if (isDebaugModeOn()) {
				throw new IllegalArgumentException("Empty URI " + uri);
			}
		}
		if (getUriMatcher().match(uri) == tableIdentifier) {
			return getContentType();
		} else {
			if (isDebaugModeOn()) {
				throw new IllegalArgumentException("Unknown URI " + uri);
			}
		}
		return null;
	}

	/**
	 * Helper method which will take table name and HashMap which has key as
	 * column name and value as data types ,which will be use to create query
	 * string. Simple method which is reduce effort in creating table query
	 * string.
	 * 
	 * @param tableName
	 *            : table name for particular table.
	 * @param tableParamMap
	 *            :hash map which has key as column name and value as data
	 *            types.
	 * @return table creation string
	 */
	protected String getTableCreationString(String tableName,
			HashMap<String, String> tableParamMap) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("create table ");
		strBuilder.append(tableName);
		strBuilder.append(" ( ");// "("
		Iterator<Map.Entry<String, String>> it = tableParamMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = it.next();
			strBuilder.append(pairs.getKey() + " ");// " " Separator between key
													// value
			strBuilder.append(pairs.getValue());
			if (it.hasNext()) {
				strBuilder.append(" , ");// ","
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
		strBuilder.append(" ); ");
		return strBuilder.toString();
	}

	private HashMap<String, String> getProjectionMap() {
		return projectionMap;
	}

	private UriMatcher getUriMatcher() {
		return mUriMatcher;
	}

	protected String getFormattedUri() {
		String formattedUri = "content://" + getAuthority() + "/"
				+ getTableName();
		return formattedUri;
	}

	protected Uri getContentURI() {
		return Uri.parse(getFormattedUri());
	}

	protected String getContentType() {
		return CONTENT_TYPE;
	}

	/**
	 * Helper method which return table creation string.
	 * 
	 * @return table string.
	 */
	protected String getQueryTableString() {
		return getTableCreationString(getTableName(), tableParamMap);
	}

	/**
	 * Method to initialize SQL helper object. example) sqlHelper=new
	 * MySqlHelper();
	 * 
	 * @param sqlHelper
	 */
	public abstract SQLiteOpenHelper getSqlHelper();

	/**
	 * initialize your basic content here. example:1)initialize static table URI
	 * variable so that you can use it with in your SQLiteOpenHelper class.
	 * 2)initialize static query table string variable so that you can use it
	 * with in your SQLiteOpenHelper class to create table.
	 */
	protected abstract void initialize();

	protected abstract String getTableName();

	/**
	 * @return unique identifier for table.
	 */
	protected abstract int getTableIdentifier();

	protected abstract String getAuthority();

	/**
	 * Helper method which takes Hashmap and fill it with key as column name and
	 * value as data types for particular column and get used in column creation
	 * when table get create.
	 * 
	 * @param tableParamMap
	 *            :hash map which has key as column name and value as data
	 *            types.
	 */
	protected abstract void fillTableParamMap(
			HashMap<String, String> tableParamMap);


	protected boolean isDebaugModeOn() {
		return isDebaugModeOn;
	}

	protected void setDebaugModeOn(boolean isDebaugModeOn) {
		this.isDebaugModeOn = isDebaugModeOn;
	}

	private void log(final String TAG, final String MESSAGE) {
		if (isDebaugModeOn()) {
			Log.d(TAG, MESSAGE);
		}
	}

	protected void setTag(final String TAG) {
		this.tag = TAG;
	}

}
