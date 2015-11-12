package com.shimnssso.wordsmaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.shimnssso.wordsmaster.data.BookAdapter;
import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.googleSheet.SheetClientActivity;
import com.shimnssso.wordsmaster.wordStudy.WordListActivity;

import java.util.ArrayList;

public class BookListActivity extends Activity {
    private final static String TAG = "BookListActivity";
    private DbHelper mDbHelper = null;
    private BookAdapter mAdapter = null;
    ListView mListView;

    CheckBox chk_all;
    Button btn_delete;
    Button btn_import;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.book_list);

        mListView = (ListView)findViewById(R.id.list_book);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookAdapter.Book book = mAdapter.getItem(position);
                String title = book.getTitle();

                mDbHelper.setCurrentWordId(0);

                Intent intent = new Intent(BookListActivity.this, WordListActivity.class);
                intent.putExtra("book", title);
                startActivity(intent);
            }
        });

        chk_all = (CheckBox)findViewById(R.id.chk_all);
        chk_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAdapter.checkAll(b);
                mAdapter.notifyDataSetChanged();
            }
        });

        btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> checkedBooks = mAdapter.getCheckedBook();
                if (checkedBooks.size() > 0) {
                    mDbHelper.deleteWords(checkedBooks);
                    mAdapter.removeCheckedItem();
                    mAdapter.notifyDataSetChanged();
                }
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

        mDbHelper = DbHelper.getInstance(this);
        ArrayList<BookAdapter.Book> mBookList = mDbHelper.getBookList();
        mAdapter = new BookAdapter(BookListActivity.this, R.layout.book_row, mBookList);
        mListView.setAdapter(mAdapter);
        Log.i(TAG, "setAdapter");
    }
}
