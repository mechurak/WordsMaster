package com.shimnssso.wordsmaster;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.data.DbMeta.WordTableMeta;

public class WordListActivity extends Activity {
    private final static String TAG = "WordListActivity";
    private DbHelper mDbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.word_list);

        ListView listView = (ListView)findViewById(R.id.list_word);

        mDbHelper = DbHelper.getInstance(this);

        Cursor cursor = mDbHelper.getBook(1);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.word_row, cursor, WordTableMeta.WORD_COLUMNS, WordTableMeta.ID_COLUMNS, 0);

        listView.setAdapter(adapter);
    }
}
