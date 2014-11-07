package com.ajra.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajra.demo.database.BookContentProvider;
import com.example.easydatabasedemo.R;

public class AllBookAdapter extends CursorAdapter {

	private Activity activity;
	private LayoutInflater inflater;

	public AllBookAdapter(Activity activity, Cursor cursor) {
		super(activity.getApplicationContext(), cursor, false);
		this.activity = activity;
		this.inflater = LayoutInflater.from(activity);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Holder holder = (Holder) view.getTag();
		setValue(holder, cursor);
	}

	@SuppressLint("InflateParams")
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View convertView = inflater.inflate(R.layout.book_row, null);
		Holder holder = new Holder();
		holder.nameTxtView = (TextView) convertView.findViewById(R.id.name);
		holder.authorTxtView = (TextView) convertView.findViewById(R.id.author);
		holder.priceTxtView = (TextView) convertView.findViewById(R.id.price);
		holder.parentView = convertView;
		convertView.setTag(holder);
		setValue(holder, cursor);
		return convertView;
	}

	private void setValue(Holder holder, Cursor cursor) {
		final String bookName = cursor.getString(cursor
				.getColumnIndex(BookContentProvider.KEY_BOOK_NAME));
		final String authorName = cursor.getString(cursor
				.getColumnIndex(BookContentProvider.KEY_AUTHOR_NAME));
		final int price = cursor.getInt(cursor
				.getColumnIndex(BookContentProvider.KEY_PRICE));
		final int id = cursor.getInt(cursor
				.getColumnIndex(BookContentProvider.KEY_ID));
		holder.nameTxtView.setText(bookName);
		holder.authorTxtView.setText(authorName);
		holder.priceTxtView.setText("" + price);
		holder.parentView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putString(BookContentProvider.KEY_BOOK_NAME, bookName);
				extras.putString(BookContentProvider.KEY_AUTHOR_NAME,
						authorName);
				extras.putInt(BookContentProvider.KEY_PRICE, price);
				extras.putInt(BookContentProvider.KEY_ID, id);
				openUpateDeleteActivity(extras);
			}
		});
	}

	private void openUpateDeleteActivity(Bundle extras) {
		Intent intent = new Intent(activity.getApplicationContext(),
				UpdateDeleteBookActivity.class);
		intent.putExtras(extras);
		activity.startActivity(intent);
	}

	class Holder {
		View parentView;
		TextView nameTxtView;
		TextView authorTxtView;
		TextView priceTxtView;

	}
}
