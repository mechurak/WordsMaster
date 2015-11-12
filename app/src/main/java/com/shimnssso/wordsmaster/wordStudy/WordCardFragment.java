package com.shimnssso.wordsmaster.wordStudy;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shimnssso.wordsmaster.AudioHelper;
import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.data.WordCursorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WordCardFragment extends Fragment {
    private final static String TAG = "WordCardFragment";

    WordListActivity mActivity = null;
    WordCursorAdapter mAdapter = null;

    Button btn_word_next;
    Button btn_word_prev;
    Button btn_word_record;
    Button btn_word_play;

    TextView txt_word_spelling;
    TextView txt_word_phonetic;
    TextView txt_word_meaning;
    TextView txt_word_progress;

    boolean mIsRecording = false;
    boolean mIsPlaying = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.word_card_fragment, container, false);

        mActivity = (WordListActivity)getActivity();
        mAdapter = mActivity.getAdapter();

        txt_word_spelling = (TextView)v.findViewById(R.id.txt_word_spelling);
        txt_word_phonetic = (TextView)v.findViewById(R.id.txt_word_phonetic);
        txt_word_meaning = (TextView)v.findViewById(R.id.txt_word_meaning);
        txt_word_progress = (TextView)v.findViewById(R.id.txt_word_progress);

        btn_word_next = (Button)v.findViewById(R.id.btn_word_next);
        btn_word_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mCurrentId = mActivity.getCurrentId();
                mCurrentId++;
                if (mCurrentId >= mAdapter.getCount()) mCurrentId = 0;
                mActivity.setCurrentId(mCurrentId);
                refreshCurrentCard();
            }
        });
        btn_word_prev = (Button)v.findViewById(R.id.btn_word_prev);
        btn_word_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mCurrentId = mActivity.getCurrentId();
                mCurrentId--;
                if (mCurrentId < 0) mCurrentId = mAdapter.getCount() - 1;
                mActivity.setCurrentId(mCurrentId);
                refreshCurrentCard();
            }
        });

        btn_word_record = (Button)v.findViewById(R.id.btn_word_record);
        btn_word_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRecording) {
                    AudioHelper.stopRecord();
                    refreshCurrentCard();
                    btn_word_record.setText("RECORD");
                    mIsRecording = false;
                } else {
                    Cursor c = (Cursor) mAdapter.getItem(mActivity.getCurrentId());
                    String spelling = c.getString(1);
                    AudioHelper.startRecord(getActivity().getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3");
                    btn_word_record.setText("STOP");
                    mIsRecording = true;
                }
            }
        });

        btn_word_play = (Button)v.findViewById(R.id.btn_word_play);
        btn_word_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor c = (Cursor) mAdapter.getItem(mActivity.getCurrentId());
                String spelling = c.getString(1);

                AudioHelper.play(getActivity().getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3");
            }
        });

        refreshCurrentCard();
        return v;
    }

    public void refreshCurrentCard() {
        Cursor c = (Cursor) mAdapter.getItem(mActivity.getCurrentId());
        txt_word_spelling.setText(c.getString(1));
        txt_word_phonetic.setText(c.getString(2));
        txt_word_meaning.setText(c.getString(3));

        txt_word_progress.setText( (mActivity.getCurrentId()+1) + "/" + mAdapter.getCount() );

        if (mAdapter.getVisible(WordListActivity.TYPE_SPELLING))
            txt_word_spelling.setVisibility(View.VISIBLE);
        else
            txt_word_spelling.setVisibility(View.GONE);
        if (mAdapter.getVisible(WordListActivity.TYPE_PHONETIC))
            txt_word_phonetic.setVisibility(View.VISIBLE);
        else
            txt_word_phonetic.setVisibility(View.GONE);
        if (mAdapter.getVisible(WordListActivity.TYPE_MEANING))
            txt_word_meaning.setVisibility(View.VISIBLE);
        else
            txt_word_meaning.setVisibility(View.GONE);

        try {
            FileInputStream fis = new FileInputStream (new File(getActivity().getFilesDir().getAbsolutePath() + File.separator + c.getString(1) + ".mp3"));
            fis.close();
            btn_word_play.setVisibility(View.VISIBLE);
        } catch (FileNotFoundException e) {
            btn_word_play.setVisibility(View.INVISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
