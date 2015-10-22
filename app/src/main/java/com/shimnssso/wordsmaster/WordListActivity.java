package com.shimnssso.wordsmaster;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.data.WordCursorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WordListActivity extends Activity {
    private final static String TAG = "WordListActivity";
    private final static int WORD_MODE_LIST = 0;
    private final static int WORD_MODE_CARD = 1;

    private DbHelper mDbHelper = null;
    Cursor cursor = null;
    WordCursorAdapter adapter = null;

    Button btn_change_mode;
    ListView listView;

    RelativeLayout mCardlayout;
    TextView txt_word_spelling;
    TextView txt_word_phonetic;
    TextView txt_word_meaning;
    TextView txt_word_progress;
    Button btn_word_next;
    Button btn_word_prev;
    Button btn_word_record;
    Button btn_word_play;
    CheckBox chk_word_spelling;
    CheckBox chk_word_phonetic;
    CheckBox chk_word_meaning;


    int mMode = WORD_MODE_LIST;
    int mCurrentId = 0;
    boolean mIsRecording = false;
    boolean mIsPlaying = false;

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

        btn_change_mode = (Button)findViewById(R.id.btn_change_mode);
        btn_change_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMode = (mMode == WORD_MODE_CARD) ? WORD_MODE_LIST : WORD_MODE_CARD;
                if (mMode == WORD_MODE_LIST) {
                    listView.setVisibility(View.VISIBLE);
                    mCardlayout.setVisibility(View.GONE);
                    listView.smoothScrollToPosition(mCurrentId);
                } else {
                    listView.setVisibility(View.GONE);
                    mCardlayout.setVisibility(View.VISIBLE);
                    refreshCurrentCard();
                }
            }
        });

        listView = (ListView)findViewById(R.id.list_word);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentId = position;
                btn_change_mode.performClick();
            }
        });


        //==========================================================
        mCardlayout = (RelativeLayout)findViewById(R.id.layout_word);

        txt_word_spelling = (TextView)findViewById(R.id.txt_word_spelling);
        txt_word_phonetic = (TextView)findViewById(R.id.txt_word_phonetic);
        txt_word_meaning = (TextView)findViewById(R.id.txt_word_meaning);

        txt_word_progress = (TextView)findViewById(R.id.txt_word_progress);

        btn_word_next = (Button)findViewById(R.id.btn_word_next);
        btn_word_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentId++;
                if (mCurrentId >= adapter.getCount()) mCurrentId = 0;
                refreshCurrentCard();
            }
        });
        btn_word_prev = (Button)findViewById(R.id.btn_word_prev);
        btn_word_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentId--;
                if (mCurrentId < 0) mCurrentId = adapter.getCount() - 1;
                refreshCurrentCard();
            }
        });
        chk_word_spelling = (CheckBox) findViewById(R.id.chk_word_spelling);
        chk_word_spelling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    txt_word_spelling.setVisibility(View.VISIBLE);
                else
                    txt_word_spelling.setVisibility(View.GONE);
            }
        });
        chk_word_phonetic = (CheckBox) findViewById(R.id.chk_word_phonetic);
        chk_word_phonetic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    txt_word_phonetic.setVisibility(View.VISIBLE);
                else
                    txt_word_phonetic.setVisibility(View.GONE);
            }
        });
        chk_word_meaning = (CheckBox)findViewById(R.id.chk_word_meaning);
        chk_word_meaning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    txt_word_meaning.setVisibility(View.VISIBLE);
                else
                    txt_word_meaning.setVisibility(View.GONE);
            }
        });

        btn_word_record = (Button)findViewById(R.id.btn_word_record);
        btn_word_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRecording) {
                    AudioHelper.stopRecord();
                    refreshCurrentCard();
                    btn_word_record.setText("RECORD");
                    mIsRecording = false;
                } else {
                    Cursor c = (Cursor) adapter.getItem(mCurrentId);
                    String spelling = c.getString(1);
                    AudioHelper.startRecord(getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3");
                    btn_word_record.setText("STOP");
                    mIsRecording = true;
                }
            }
        });

        btn_word_play = (Button)findViewById(R.id.btn_word_play);
        btn_word_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor c = (Cursor) adapter.getItem(mCurrentId);
                String spelling = c.getString(1);

                AudioHelper.play(getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3");
            }
        });

        refreshCurrentCard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        mMode = mDbHelper.getMode();
        mCurrentId = mDbHelper.getCurrentWordId();

        if (mMode == WORD_MODE_LIST) {
            listView.setVisibility(View.VISIBLE);
            mCardlayout.setVisibility(View.GONE);
        }
        else {
            listView.setVisibility(View.GONE);
            mCardlayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

        mDbHelper.setMode(mMode);
        mDbHelper.setCurrentWordId(mCurrentId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void refreshCurrentCard() {
        Cursor c = (Cursor) adapter.getItem(mCurrentId);
        txt_word_spelling.setText(c.getString(1));
        txt_word_phonetic.setText(c.getString(2));
        txt_word_meaning.setText(c.getString(3));

        txt_word_progress.setText( (mCurrentId+1) + "/" + adapter.getCount() );

        try {
            FileInputStream fis = new FileInputStream (new File(getFilesDir().getAbsolutePath() + File.separator + c.getString(1) + ".mp3"));
            fis.close();
            btn_word_play.setVisibility(View.VISIBLE);
        } catch (FileNotFoundException e) {
            btn_word_play.setVisibility(View.INVISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
