package com.ajra.demo;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ajra.demo.database.BookContentProvider;
import com.ajra.easydatabase.EasyDatabaseContentProvider;
import com.example.easydatabasedemo.R;

public class UpdateDeleteBookActivity extends Activity implements
		OnClickListener {

	private Button updateBook;
	private Button deleteBook;
	private EditText nameEditText;
	private EditText authorEditText;
	private EditText priceEditText;
	private String bookName;
	private String authorName;
	private int bookPrice;
	private long bookId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_book);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			bookName = bundle.getString(BookContentProvider.KEY_BOOK_NAME);
			authorName = bundle.getString(BookContentProvider.KEY_AUTHOR_NAME);
			bookPrice = bundle.getInt(BookContentProvider.KEY_PRICE);
			bookId = bundle.getInt(BookContentProvider.KEY_ID);
		}
		initForm();
	}

	private void initForm() {
		updateBook = (Button) findViewById(R.id.updateBtn);
		deleteBook = (Button) findViewById(R.id.deleteBtn);
		nameEditText = (EditText) findViewById(R.id.bookName);
		authorEditText = (EditText) findViewById(R.id.authorName);
		priceEditText = (EditText) findViewById(R.id.bookPrice);
		updateBook.setOnClickListener(this);
		nameEditText.setText(bookName);
		authorEditText.setText(authorName);
		priceEditText.setText("" + bookPrice);
		updateBook.setOnClickListener(this);
		deleteBook.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.updateBtn:
			updateBookInfo();
			break;

		case R.id.deleteBtn:
			deleteBook();
			break;

		default:
			break;
		}
	}

	private void deleteBook() {
		String[] selectionArgs = new String[] { "" + bookId };
		String where = EasyDatabaseContentProvider.KEY_ID + " = ? ";
		int count = getContentResolver().delete(
				BookContentProvider.BOOK_TABLE_URI, where, selectionArgs);
		if (count > 0) {
			Toast.makeText(this.getApplicationContext(),
					"Book deleted successfully", Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	private void updateBookInfo() {
		try {
			validateForm();
			String bookName = nameEditText.getText().toString();
			String authorName = authorEditText.getText().toString();
			int bookPrice = Integer
					.parseInt(priceEditText.getText().toString());
			ContentValues values = new ContentValues();
			values.put(BookContentProvider.KEY_BOOK_NAME, bookName);
			values.put(BookContentProvider.KEY_AUTHOR_NAME, authorName);
			values.put(BookContentProvider.KEY_PRICE, bookPrice);
			String[] selectionArgs = new String[] { "" + bookId };
			int count = getContentResolver().update(
					BookContentProvider.BOOK_TABLE_URI, values,
					EasyDatabaseContentProvider.KEY_ID + " = ? ", selectionArgs);
			if (count > 0) {
				Toast.makeText(this.getApplicationContext(),
						"Book updated successfully", Toast.LENGTH_SHORT).show();
				finish();
			}
		} catch (Exception e) {
			Toast.makeText(this.getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}

	}

	private void validateForm() throws Exception {
		validateBookName();
		validateAuthorName();
		validatePrice();
	}

	private void validateBookName() throws Exception {
		if (TextUtils.isEmpty(nameEditText.getText())) {
			throw new IllegalAccessException("Book Name should not empty.");
		}
	}

	private void validateAuthorName() throws Exception {
		if (TextUtils.isEmpty(authorEditText.getText())) {
			throw new IllegalAccessException("Author Name should not empty.");
		}
	}

	private void validatePrice() throws Exception {
		if (TextUtils.isEmpty(priceEditText.getText())) {
			throw new IllegalAccessException("Price should not empty.");
		}
	}

}
