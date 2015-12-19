package com.shimnssso.wordsmaster.wordStudy;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shimnssso.wordsmaster.Constants;
import com.shimnssso.wordsmaster.ForegroundService;
import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.data.DbMeta;
import com.shimnssso.wordsmaster.util.AudioHelper;
import com.shimnssso.wordsmaster.wordTest.OrderTestActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WordCardFragment extends Fragment implements WordListActivity.WordInterface {
    private final static String TAG = "WordCardFragment";

    WordListActivity mActivity = null;
    WordAdapter mAdapter = null;

    Button btn_word_next;
    Button btn_word_prev;
    Button btn_word_record;
    Button btn_word_del;
    Button btn_word_test;

    CheckBox chk_starred;

    TextView txt_word_spelling;
    TextView txt_word_phonetic;
    TextView txt_word_meaning;
    TextView txt_word_progress;

    LinearLayout txt_box;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.word_card_fragment, container, false);

        mActivity = (WordListActivity)getActivity();
        mAdapter = mActivity.getAdapter();

        txt_word_spelling = (TextView)v.findViewById(R.id.txt_word_spelling);
        txt_word_phonetic = (TextView)v.findViewById(R.id.txt_word_phonetic);
        txt_word_meaning = (TextView)v.findViewById(R.id.txt_word_meaning);
        txt_word_progress = (TextView)v.findViewById(R.id.txt_word_progress);

        txt_box = (LinearLayout)v.findViewById(R.id.txt_box);
        txt_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getItemCount() == 0) return;
                Cursor c = (Cursor) mAdapter.getItem();
                String spelling = c.getString(1);

                Intent intent = new Intent(mActivity, ForegroundService.class);
                intent.putExtra("spelling", spelling);
                try {
                    FileInputStream fis = new FileInputStream (new File(mActivity.getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3"));
                    fis.close();
                    intent.setAction(Constants.Action.PLAY);
                } catch (FileNotFoundException e) {
                    intent.setAction(Constants.Action.TTS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mActivity.startService(intent);
            }
        });

        btn_word_next = (Button)v.findViewById(R.id.btn_word_next);
        btn_word_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getItemCount() == 0) return;
                int mCurrentId = mAdapter.getCurrentPosition();
                mCurrentId++;
                if (mCurrentId >= mAdapter.getItemCount()) mCurrentId = 0;
                mAdapter.setCurrentPosition(mCurrentId);
                refreshCurrentCard();
            }
        });
        btn_word_prev = (Button)v.findViewById(R.id.btn_word_prev);
        btn_word_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getItemCount() == 0) return;
                int mCurrentId = mAdapter.getCurrentPosition();
                mCurrentId--;
                if (mCurrentId < 0) mCurrentId = mAdapter.getItemCount() - 1;
                mAdapter.setCurrentPosition(mCurrentId);
                refreshCurrentCard();
            }
        });

        btn_word_record = (Button)v.findViewById(R.id.btn_word_record);
        btn_word_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getItemCount() == 0) return;
                Cursor c = (Cursor) mAdapter.getItem();
                String spelling = c.getString(1);
                AudioHelper.startRecord(getActivity().getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3");
                Log.d(TAG, "startRecord");

                AlertDialog dialog = new AlertDialog.Builder(mActivity).setTitle("Record").setMessage("Speak loudly").
                        setPositiveButton("done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AudioHelper.stopRecord();
                                Log.d(TAG, "stopRecord");
                                refreshCurrentCard();
                            }
                        }).show();
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });

        btn_word_del = (Button)v.findViewById(R.id.btn_word_del);
        btn_word_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getItemCount() == 0) return;
                Cursor c = (Cursor) mAdapter.getItem();
                File file = new File(getActivity().getFilesDir().getAbsolutePath() + File.separator + c.getString(1) + ".mp3");
                file.delete();
                refreshCurrentCard();
            }
        });

        btn_word_test = (Button)v.findViewById(R.id.btn_word_test);
        btn_word_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getItemCount() == 0) return;
                Intent intent = new Intent(mActivity.getApplicationContext(), OrderTestActivity.class);
                Cursor c = (Cursor) mAdapter.getItem();
                intent.putExtra("spelling", c.getString(1));
                intent.putExtra("phonetic", c.getString(2));
                intent.putExtra("meaning", c.getString(3));
                startActivity(intent);
            }
        });

        chk_starred = (CheckBox)v.findViewById(R.id.chk_starred);
        chk_starred.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mAdapter.getItemCount() == 0) return;
                mAdapter.setStarred(isChecked);

                Cursor c = (Cursor) mAdapter.getItem();
                final int id =c.getInt(0);
                final int wordFlag = c.getInt(5);

                int newFlag = wordFlag;
                if (isChecked) newFlag |= DbMeta.WordFlag.STARRED;
                else newFlag &= (~DbMeta.WordFlag.STARRED);
                ContentValues value = new ContentValues();
                value.put(DbMeta.WordTableMeta.FLAG, newFlag);
                DbHelper dbHelper = DbHelper.getInstance();
                dbHelper.updateWord(value, id);
                Log.d(TAG, "onCheckedChanged. newFlag: " + newFlag + ", id:" + id);
            }
        });

        refreshCurrentCard();
        return v;
    }

    public void refreshCurrentCard() {
        if (mAdapter.getItemCount() <= 0) {
            txt_word_progress.setText("0/0");
            txt_word_spelling.setText("");
            txt_word_phonetic.setText("");
            txt_word_meaning.setText("");
            btn_word_record.setVisibility(View.GONE);
            return;
        }

        Cursor c = (Cursor) mAdapter.getItem();
        txt_word_spelling.setText(c.getString(1));
        txt_word_phonetic.setText(c.getString(2));
        txt_word_meaning.setText(c.getString(3));

        txt_word_progress.setText( (mAdapter.getCurrentPosition()+1) + "/" + mAdapter.getItemCount() );

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

        btn_word_record.setVisibility(View.VISIBLE);

        chk_starred.setChecked(mAdapter.getStarred());

        try {
            FileInputStream fis = new FileInputStream (new File(getActivity().getFilesDir().getAbsolutePath() + File.separator + c.getString(1) + ".mp3"));
            fis.close();
            btn_word_del.setVisibility(View.VISIBLE);
        } catch (FileNotFoundException e) {
            btn_word_del.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setVisible(int type, boolean visible) {
        mAdapter.setVisible(type, visible);
        refreshCurrentCard();
    }

    @Override
    public void moveTo(int position) {
        refreshCurrentCard();
    }
}
