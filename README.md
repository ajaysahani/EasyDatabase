EasyDatabase
====================
This library design to create database using ContentProvider and keeping in mind reduce complexity in database table creation and easy to maintain code. 

Features
========

* To create Database using ContentProvider so that we can share our database to other app if needed.
* Uses ContentProvider in Object Oriented way such that  each table have seprate ContentProvider so easy to maintain.
* Developer have to write down very less code to create table so save development time.
* Very simple way to create table creation query String.
* Work with Cursor LoaderManager so no need to close cursor explicitly.
* Use standard android ContentResolver to execute query.

Compatibility
=========
* **Library** : API 1
* **LibrarySample** : API 1


How to use library
====================
1:Create Your Content Provider which will be responsible for table creation.
2:Override all method which is needed as per your requirement.
example:

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

3:Create Sql Helper class and that should be singleton to improve performance and to avoid multiple data instance creation.
example:
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

4:define Content provider inside AndroidMainfest file.
example:
<provider
            android:name="com.ajra.demo.database.BookContentProvider"
            android:authorities="@string/book_authority_name"
            android:exported="false" />

If we would like to share data of table  android:exported="true"

5:We are done with table creation.
6:Now use it.
example:
ContentValues values = new ContentValues();
values.put(BookContentProvider.KEY_BOOK_NAME, bookName);
Uri uri=getContentResolver().insert(BookContentProvider.BOOK_TABLE_URI,
					values);



Progaurd
========
No need to add any extra config.


Debugging 
=========

In EasyDatabaseContentProvider class their is method called setDebaugModeOn(boolean isDebaugModeOn) override this method.
To grep log use "EasyDatabaseContentProvider" as grep string.

License
=======
   Copyright 2014-present Ajay Sahani

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


