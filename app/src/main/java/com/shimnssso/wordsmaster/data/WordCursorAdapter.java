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

import com.shimnssso.wordsmaster.R;

public class WordCursorAdapter extends CursorAdapter {
    private static final String TAG = "WordCursorAdapter";

    LayoutInflater inflater;

    public WordCursorAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.word_row, parent, false);
        bindView(v,context,cursor);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final long row_id = cursor.getLong(0);
        CheckBox chk = (CheckBox) view.findViewById(R.id.chk);
        TextView spelling = (TextView) view.findViewById(R.id.spelling);
        TextView phonetic = (TextView) view.findViewById(R.id.phonetic);
        TextView meaning = (TextView) view.findViewById(R.id.meaning);
        Button button = (Button) view.findViewById(R.id.button);


        spelling.setText(cursor.getString(1));
        phonetic.setText(cursor.getString(2));
        meaning.setText(cursor.getString(3));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "button clicked. current id: " + row_id);
            }
        });
    }
}
