package com.shimnssso.wordsmaster.wordStudy;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.data.WordCursorAdapter;

public class WordListActivity extends FragmentActivity {
    private final static String TAG = "WordListActivity";
    public final static int WORD_LIST_FRAGMENT = 0;
    public final static int WORD_CARD_FRAGMENT = 1;

    public final static int TYPE_SPELLING = 100;
    public final static int TYPE_PHONETIC = 101;
    public final static int TYPE_MEANING = 102;


    private DbHelper mDbHelper = null;
    Cursor cursor = null;

    WordCursorAdapter adapter = null;
    public WordCursorAdapter getAdapter() {
        return adapter;
    }

    CheckBox chk_word_spelling;
    CheckBox chk_word_phonetic;
    CheckBox chk_word_meaning;

    int mCurrentId = 0;
    public void setCurrentId(int id) {
        mCurrentId = id;
    }
    public int getCurrentId() {
        return mCurrentId;
    }

    int mCurrentFragmentIndex = WORD_LIST_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.word_list);

        Intent intent = getIntent();
        String bookTitle = intent.getStringExtra("book");
        Log.i(TAG, "bookTitle: " + bookTitle);

        mDbHelper = DbHelper.getInstance();
        cursor = mDbHelper.getWordList(bookTitle);
        adapter = new WordCursorAdapter(this, cursor, 0);

        chk_word_spelling = (CheckBox) findViewById(R.id.chk_word_spelling);
        chk_word_spelling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.setVisible(TYPE_SPELLING, isChecked);
                if (mCurrentFragmentIndex == WORD_CARD_FRAGMENT) {
                    WordCardFragment fragment = (WordCardFragment) getSupportFragmentManager().findFragmentById(R.id.word_fragment);
                    fragment.refreshCurrentCard();
                }
            }
        });
        chk_word_phonetic = (CheckBox) findViewById(R.id.chk_word_phonetic);
        chk_word_phonetic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.setVisible(TYPE_PHONETIC, isChecked);
                if (mCurrentFragmentIndex == WORD_CARD_FRAGMENT) {
                    WordCardFragment fragment = (WordCardFragment) getSupportFragmentManager().findFragmentById(R.id.word_fragment);
                    fragment.refreshCurrentCard();
                }
            }
        });
        chk_word_meaning = (CheckBox)findViewById(R.id.chk_word_meaning);
        chk_word_meaning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.setVisible(TYPE_MEANING, isChecked);
                if (mCurrentFragmentIndex == WORD_CARD_FRAGMENT) {
                    WordCardFragment fragment = (WordCardFragment) getSupportFragmentManager().findFragmentById(R.id.word_fragment);
                    fragment.refreshCurrentCard();
                }
            }
        });

        replaceFragment(mCurrentFragmentIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        mCurrentId = mDbHelper.getCurrentWordId();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        mDbHelper.setCurrentWordId(mCurrentId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }


    public void replaceFragment(int newFragmentIndex) {

        Fragment newFragment = null;

        Log.d(TAG, "replaceFragment " + newFragmentIndex);

        newFragment = getFragment(newFragmentIndex);

        // replace fragment
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.word_fragment, newFragment);

        // Commit the transaction
        transaction.commit();
        mCurrentFragmentIndex = newFragmentIndex;
    }

    private Fragment getFragment(int index) {
        Fragment newFragment = null;

        switch (index) {
            case WORD_LIST_FRAGMENT:
                newFragment = new WordListFragment();
                break;
            case WORD_CARD_FRAGMENT:
                newFragment = new WordCardFragment();
                break;
            default:
                Log.e(TAG, "unexpected index " + index);
                break;
        }
        return newFragment;
    }
}