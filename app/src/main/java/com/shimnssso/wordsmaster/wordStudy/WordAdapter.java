package com.shimnssso.wordsmaster.wordStudy;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shimnssso.wordsmaster.Constants;
import com.shimnssso.wordsmaster.ForegroundService;
import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.data.DbMeta;
import com.shimnssso.wordsmaster.util.TTSHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder>{
    private static final String TAG = "WordAdapter";

    private Cursor mCursor = null;
    private Context mContext = null;

    private boolean isChecked[];
    private boolean isStarred[];
    private int mCurPosition;

    private boolean visibleSpelling = true;
    private boolean visiblePhonetic = true;
    private boolean visibleMeaning = true;

    public WordAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;

        isChecked = new boolean[mCursor.getCount()];
        isStarred = new boolean[mCursor.getCount()];

        if (mCursor.moveToFirst()) {
            do {
                final int position = mCursor.getPosition();
                final int wordFlag = mCursor.getInt(5);
                isStarred[position] = (wordFlag & DbMeta.WordFlag.STARRED) == DbMeta.WordFlag.STARRED;
            } while(mCursor.moveToNext());
        }
    }

    public void setStarred(boolean starred) {
        isStarred[mCurPosition] = starred;
    }

    public boolean getStarred() {
        return isStarred[mCurPosition];
    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.word_row, parent, false);
        Log.d(TAG, "onCreateViewHolder" + v.toString());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder. position: " + position);
        mCursor.moveToPosition(position);

        holder.spelling.setText(mCursor.getString(1));
        holder.phonetic.setText(mCursor.getString(2));
        holder.meaning.setText(mCursor.getString(3));
        holder.starred.setChecked(isStarred[position]);

        if (visibleSpelling)
            holder.spelling.setVisibility(View.VISIBLE);
        else
            holder.spelling.setVisibility(View.GONE);
        if (visiblePhonetic)
            holder.phonetic.setVisibility(View.VISIBLE);
        else
            holder.phonetic.setVisibility(View.GONE);
        if (visibleMeaning)
            holder.meaning.setVisibility(View.VISIBLE);
        else
            holder.meaning.setVisibility(View.GONE);

        if (position == mCurPosition) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.theme2_primary));
            holder.btn_card.setVisibility(View.VISIBLE);
            holder.btn_test.setVisibility(View.VISIBLE);
        }
        else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.background_floating_material_light));
            holder.btn_card.setVisibility(View.GONE);
            holder.btn_test.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView spelling;
        TextView phonetic;
        TextView meaning;
        CheckBox starred;

        Button btn_card;
        Button btn_test;

        public ViewHolder(final View itemView) {
            super(itemView);

            cardView = (CardView)itemView.findViewById(R.id.cardview);
            spelling = (TextView)itemView.findViewById(R.id.spelling);
            phonetic = (TextView)itemView.findViewById(R.id.phonetic);
            meaning = (TextView)itemView.findViewById(R.id.meaning);
            starred = (CheckBox)itemView.findViewById(R.id.starred);

            btn_card = (Button)itemView.findViewById(R.id.btn_card);
            btn_test = (Button)itemView.findViewById(R.id.btn_test);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    Log.d(TAG, "position: " + position + ", cursor: " + mCursor.getPosition());
                    mCursor.moveToPosition(position);
                    String spelling = mCursor.getString(1);

                    Intent intent = new Intent(mContext, ForegroundService.class);
                    intent.putExtra("spelling", spelling);
                    try {
                        FileInputStream fis = new FileInputStream (new File(mContext.getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3"));
                        fis.close();
                        intent.setAction(Constants.Action.PLAY);
                    } catch (FileNotFoundException e) {
                        intent.setAction(Constants.Action.TTS);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mContext.startService(intent);

                    int prevPosition = mCurPosition;
                    mCurPosition = position;
                    notifyItemChanged(prevPosition);
                    notifyItemChanged(mCurPosition);
                }
            });

            starred.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final int position = getAdapterPosition();
                    Log.d(TAG, "position: " + position + ", cursor: " + mCursor.getPosition());
                    mCursor.moveToPosition(position);

                    isStarred[position] = isChecked;
                    final int id = mCursor.getInt(0);
                    final int wordFlag = mCursor.getInt(5);

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

            btn_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WordListActivity.mHandler.sendEmptyMessage(WordListActivity.MSG_CARD_MODE);
                }
            });

            btn_test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WordListActivity.mHandler.sendEmptyMessage(WordListActivity.MSG_TEST_MODE);
                }
            });
        }
    }

    public void setCurrentId(int position) {
        mCurPosition = position;
    }
    public int getCurrentId() {
        return mCurPosition;
    }

    public Object getItem()
    {
        mCursor.moveToPosition(mCurPosition);
        return mCursor;
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
