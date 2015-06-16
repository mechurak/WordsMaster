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
import com.shimnssso.wordsmaster.data.DbMeta.WordTableMeta;
import com.shimnssso.wordsmaster.data.WordCursorAdapter;

public class WordListActivity extends Activity {
    private final static String TAG = "WordListActivity";
    private DbHelper mDbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.word_list);

        Intent intent = getIntent();
        long bookID = intent.getLongExtra("id", 1);
        Log.i(TAG, "book id: " + bookID);

        ListView listView = (ListView)findViewById(R.id.list_word);

        mDbHelper = DbHelper.getInstance(this);

        Cursor cursor = mDbHelper.getBook(bookID);
        //final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.word_row, cursor, WordTableMeta.WORD_COLUMNS, WordTableMeta.ID_COLUMNS, 0);
        final WordCursorAdapter adapter = new WordCursorAdapter(this, cursor, 0);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) adapter.getItem(position);
                Log.d(TAG, "id from cursor = " + c.getInt(0));
                Log.d(TAG, "id " + id);

                Log.d(TAG, "title " + c.getString(1));
                Log.d(TAG, "phonetic " + c.getString(2));
                Log.d(TAG, "meaning " + c.getString(3));
                Log.d(TAG, "audio " + c.getString(4));
                Log.d(TAG, "category " + c.getString(5));
            }
        });
    }
}
