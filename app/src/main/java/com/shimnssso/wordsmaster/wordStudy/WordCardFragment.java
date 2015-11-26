package com.shimnssso.wordsmaster.wordStudy;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.data.WordAdapter;
import com.shimnssso.wordsmaster.util.AudioHelper;
import com.shimnssso.wordsmaster.util.TTSHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WordCardFragment extends Fragment implements WordListActivity.WordInterface {
    private final static String TAG = "WordCardFragment";

    WordListActivity mActivity = null;
    WordAdapter mAdapter = null;
    TTSHelper mTTSHelper = null;

    Button btn_word_next;
    Button btn_word_prev;
    Button btn_word_record;
    Button btn_tts;
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
        mTTSHelper = TTSHelper.getInstance(getActivity().getApplicationContext());

        txt_word_spelling = (TextView)v.findViewById(R.id.txt_word_spelling);
        txt_word_phonetic = (TextView)v.findViewById(R.id.txt_word_phonetic);
        txt_word_meaning = (TextView)v.findViewById(R.id.txt_word_meaning);
        txt_word_progress = (TextView)v.findViewById(R.id.txt_word_progress);

        btn_word_next = (Button)v.findViewById(R.id.btn_word_next);
        btn_word_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mCurrentId = mAdapter.getCurrentId();
                mCurrentId++;
                if (mCurrentId >= mAdapter.getItemCount()) mCurrentId = 0;
                mAdapter.setCurrentId(mCurrentId);
                refreshCurrentCard();
            }
        });
        btn_word_prev = (Button)v.findViewById(R.id.btn_word_prev);
        btn_word_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mCurrentId = mAdapter.getCurrentId();
                mCurrentId--;
                if (mCurrentId < 0) mCurrentId = mAdapter.getItemCount() - 1;
                mAdapter.setCurrentId(mCurrentId);
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
                    Cursor c = (Cursor) mAdapter.getItem();
                    String spelling = c.getString(1);
                    AudioHelper.startRecord(getActivity().getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3");
                    btn_word_record.setText("STOP");
                    mIsRecording = true;
                }
            }
        });

        btn_tts = (Button)v.findViewById(R.id.btn_tts);
        btn_tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = (Cursor) mAdapter.getItem();
                String spelling = c.getString(1);

                if (mTTSHelper != null) {
                    mTTSHelper.speak(spelling);
                }
            }
        });

        btn_word_play = (Button)v.findViewById(R.id.btn_word_play);
        btn_word_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor c = (Cursor) mAdapter.getItem();
                String spelling = c.getString(1);

                AudioHelper.play(getActivity().getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3");
            }
        });

        refreshCurrentCard();
        return v;
    }

    public void refreshCurrentCard() {
        Cursor c = (Cursor) mAdapter.getItem();
        txt_word_spelling.setText(c.getString(1));
        txt_word_phonetic.setText(c.getString(2));
        txt_word_meaning.setText(c.getString(3));

        txt_word_progress.setText( (mAdapter.getCurrentId()+1) + "/" + mAdapter.getItemCount() );

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

    @Override
    public void setVisible(int type, boolean visible) {
        mAdapter.setVisible(type, visible);
        refreshCurrentCard();
    }
}
