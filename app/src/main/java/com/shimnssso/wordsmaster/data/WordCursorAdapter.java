package com.shimnssso.wordsmaster.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.shimnssso.wordsmaster.util.AudioHelper;
import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.wordStudy.WordListActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WordCursorAdapter extends CursorAdapter {
    private static final String TAG = "WordCursorAdapter";
    private Context mContext;
    private DbHelper mDbHelper;

    private boolean isChecked[];
    private boolean isStarred[];
    private int mCurrentId;

    private boolean visibleSpelling = true;
    private boolean visiblePhonetic = true;
    private boolean visibleMeaning = true;

    LayoutInflater inflater;

    public WordCursorAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
        inflater = LayoutInflater.from(context);
        mContext = context;
        mDbHelper = DbHelper.getInstance();

        isChecked = new boolean[c.getCount()];
        isStarred = new boolean[c.getCount()];

        if (c.moveToFirst()) {
            do {
                final int position = c.getPosition();
                final int wordFlag = c.getInt(6);
                isStarred[position] = (wordFlag & DbMeta.WordFlag.STARRED) == DbMeta.WordFlag.STARRED;
            } while(c.moveToNext());
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.word_row, parent, false);
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        final String word = cursor.getString(1);
        final int position = cursor.getPosition();
        final int id = cursor.getInt(0);
        final int wordFlag = cursor.getInt(6);
        CheckBox chk = (CheckBox) view.findViewById(R.id.chk);
        CheckBox chk_star = (CheckBox) view.findViewById(R.id.chk_star);
        TextView spelling = (TextView) view.findViewById(R.id.spelling);
        TextView phonetic = (TextView) view.findViewById(R.id.phonetic);
        TextView meaning = (TextView) view.findViewById(R.id.meaning);
        Button button = (Button) view.findViewById(R.id.button);

        spelling.setText(cursor.getString(1));
        phonetic.setText(cursor.getString(2));
        meaning.setText(cursor.getString(3));

        if (visibleSpelling)
            spelling.setVisibility(View.VISIBLE);
        else
            spelling.setVisibility(View.GONE);
        if (visiblePhonetic)
            phonetic.setVisibility(View.VISIBLE);
        else
            phonetic.setVisibility(View.GONE);
        if (visibleMeaning)
            meaning.setVisibility(View.VISIBLE);
        else
            meaning.setVisibility(View.GONE);

        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked[position] = b;
                Log.d(TAG, "onCheckedChanged. chk, id:" + id);
            }
        });
        chk.setChecked(isChecked[position]);

        chk_star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isStarred[position] = isChecked;

                int newFlag = wordFlag;
                if (isChecked) newFlag |= DbMeta.WordFlag.STARRED;
                else newFlag &= (~DbMeta.WordFlag.STARRED);
                ContentValues value = new ContentValues();
                value.put(DbMeta.WordTableMeta.FLAG, newFlag);
                mDbHelper.updateWord(value, id);
                Log.d(TAG, "onCheckedChanged. newFlag: " + newFlag + ", id:" + id);
            }
        });
        chk_star.setChecked(isStarred[position]);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioHelper.play(mContext.getFilesDir().getAbsolutePath() + File.separator + word + ".mp3");
            }
        });

        try {
            FileInputStream fis = new FileInputStream (new File(mContext.getFilesDir().getAbsolutePath() + File.separator + word + ".mp3"));
            fis.close();
            button.setVisibility(View.VISIBLE);
        } catch (FileNotFoundException e) {
            button.setVisibility(View.INVISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (position == mCurrentId) {
            view.setBackgroundColor(Color.parseColor("#AAAAAA"));
        }
        else {
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    public void setVisible(int type, boolean visible) {
        switch (type) {

            case WordListActivity.TYPE_SPELLING:
                visibleSpelling = visible;
                break;
            case WordListActivity.TYPE_PHONETIC:
                visiblePhonetic = visible;
                break;
            case WordListActivity.TYPE_MEANING:
                visibleMeaning = visible;
                break;
            default:
                Log.e(TAG, "unexpected type " + type);
                break;
        }
        notifyDataSetInvalidated();
    }

    public boolean getVisible(int type) {
        if (type == WordListActivity.TYPE_SPELLING)
            return visibleSpelling;
        else if (type == WordListActivity.TYPE_PHONETIC)
            return visiblePhonetic;
        else if (type == WordListActivity.TYPE_MEANING)
            return visibleMeaning;
        else {
            Log.e(TAG, "unexpected type " + type);
            return false;
        }
    }

    public void checkAll(boolean checked) {
        for (int i=0; i< isChecked.length ; i++) {
            isChecked[i] = checked;
        }
    }

    public void setCurrentId(int positoin) {
        mCurrentId = positoin;
    }
    public int getCurrentId() {
        return mCurrentId;
    }

    public Object getItem() {
        return super.getItem(mCurrentId);
    }
}
