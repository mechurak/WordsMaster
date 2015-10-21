package com.shimnssso.wordsmaster;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.data.DbMeta;

public class BookListActivity extends Activity {
    private final static String TAG = "BookListActivity";
    private DbHelper mDbHelper = null;
    private SimpleCursorAdapter mAdapter = null;

    Button btn_import;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.book_list);

        ListView listView = (ListView)findViewById(R.id.list_book);

        mDbHelper = DbHelper.getInstance(this);
        Cursor cursor = null;

        String[] from = {DbMeta.WordTableMeta.CATEGORY, "COUNT(*)"};
        int[] to = {R.id.title, R.id.size};
        mAdapter = new SimpleCursorAdapter(this, R.layout.book_row, cursor, from, to, 0);
        listView.setAdapter(mAdapter);
        Log.i(TAG, "setAdapter");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) mAdapter.getItem(position);
                Log.d(TAG, "bookTitle from cursor = " + c.getString(1));
                Log.d(TAG, "id " + id);

                mDbHelper.setCurrentWordId(0);

                Intent intent = new Intent(BookListActivity.this, WordListActivity.class);
                intent.putExtra("book", c.getString(1));
                startActivity(intent);
            }
        });

        btn_import = (Button)findViewById(R.id.btn_import);
        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookListActivity.this, SheetClientActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        Cursor cursor = mDbHelper.getBookList();
        mAdapter.changeCursor(cursor);


    }
}
