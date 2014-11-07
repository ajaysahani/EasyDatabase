package com.ajra.demo;

import com.ajra.demo.database.BookContentProvider;
import com.example.easydatabasedemo.R;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddBookActivity extends Activity implements OnClickListener {

	private Button addBook;
	private EditText nameEditText;
	private EditText authorEditText;
	private EditText priceEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book);
		initForm();
	}

	private void initForm() {
		addBook = (Button) findViewById(R.id.addBtn);
		nameEditText = (EditText) findViewById(R.id.bookName);
		authorEditText = (EditText) findViewById(R.id.authorName);
		priceEditText = (EditText) findViewById(R.id.bookPrice);
		addBook.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addBtn:
			insertIntoDatabase();
			break;

		default:
			break;
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

	private void insertIntoDatabase() {
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
			Uri uri=getContentResolver().insert(BookContentProvider.BOOK_TABLE_URI,
					values);
			if(uri!=null){
				showToastMessage("Book added successuflly");
				finish();
			}
		} catch (Exception e) {
			showToastMessage(e.getMessage());
		}
	}
	
	private void showToastMessage(String message){
		Toast.makeText(this.getApplicationContext(),message ,
				Toast.LENGTH_SHORT).show();
	}
}
