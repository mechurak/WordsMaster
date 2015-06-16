package com.shimnssso.wordsmaster;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.data.DbMeta;

public class BookListActivity extends Activity {
    private final static String TAG = "BookListActivity";
    private DbHelper mDbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.book_list);

        ListView listView = (ListView)findViewById(R.id.list_book);

        mDbHelper = DbHelper.getInstance(this);
        Cursor cursor = mDbHelper.getBookList();

        String[] from = {DbMeta.CategoryTableMeta.TITLE, DbMeta.CategoryTableMeta.SIZE};
        int[] to = {R.id.title, R.id.size};
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.book_row, cursor, from, to, 0);
        listView.setAdapter(adapter);
        Log.i(TAG, "setAdapter");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) adapter.getItem(position);
                Log.d(TAG, "id from cursor = " + c.getInt(0));
                Log.d(TAG, "id " + id);
                Log.d(TAG, "title " + c.getString(1));

                Intent intent=new Intent(BookListActivity.this, WordListActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }
}
