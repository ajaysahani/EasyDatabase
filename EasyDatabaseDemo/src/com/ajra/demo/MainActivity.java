package com.ajra.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.easydatabasedemo.R;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private Button addBook;
	private Button showAllBook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addBook = (Button) findViewById(R.id.addBtn);
		showAllBook = (Button) findViewById(R.id.showAll);
		addBook.setOnClickListener(this);
		showAllBook.setOnClickListener(this);
	}

	private void openAddBook() {
		Intent intent = new Intent(this.getApplicationContext(),
				AddBookActivity.class);
		startActivity(intent);
	}

	private void showAllBook() {
		Intent intent = new Intent(this.getApplicationContext(),
				AllBookActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addBtn:
			openAddBook();
			break;

		case R.id.showAll:
			showAllBook();
			break;

		default:
			break;
		}
	}
}
