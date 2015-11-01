package com.shimnssso.wordsmaster.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.shimnssso.wordsmaster.AudioHelper;
import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.WordListActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WordCursorAdapter extends CursorAdapter {
    private static final String TAG = "WordCursorAdapter";
    private Context mContext;

    private boolean visibleSpelling = true;
    private boolean visiblePhonetic = true;
    private boolean visibleMeaning = true;

    LayoutInflater inflater;

    public WordCursorAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.word_row, parent, false);
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String word = cursor.getString(1);
        CheckBox chk = (CheckBox) view.findViewById(R.id.chk);
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
}
