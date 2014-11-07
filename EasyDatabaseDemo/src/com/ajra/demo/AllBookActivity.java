package com.ajra.demo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ListView;
import android.widget.Toast;

import com.ajra.demo.database.BookContentProvider;
import com.example.easydatabasedemo.R;

public class AllBookActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private ListView listView;
	private AllBookAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_all);

		listView = (ListView) findViewById(R.id.allViewList);
		if (getCursorCount() > 0) {
			getSupportLoaderManager().initLoader(0, null, this);
			adapter = new AllBookAdapter(this, null);
			listView.setAdapter(adapter);
		} else {
			Toast.makeText(getApplicationContext(), "Book not avalaible",
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private int getCursorCount() {
		int count = 0;
		Cursor cursor = getContentResolver().query(
				BookContentProvider.BOOK_TABLE_URI, null, null, null, null);
		if (cursor != null) {
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle bundle) {
		return new CursorLoader(getApplicationContext(),
				BookContentProvider.BOOK_TABLE_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.changeCursor(cursor);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
	}

}
