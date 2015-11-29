package com.shimnssso.wordsmaster.wordTest;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shimnssso.wordsmaster.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WordBlockAdapter extends RecyclerView.Adapter<WordBlockAdapter.ViewHolder> {
    private static final String TAG = "WordBlockAdapter";

    public static final int ITEM_TYPE_BASE_LINE = 0;
    public static final int ITEM_TYPE_DEFAULT = 1;

    private Context mContext;
    private String mWords;
    private List<Item> mItems_new;
    private List<String> mAnswer;
    private int mBaseLinePosition;
    private Item mBaseLineItem;

    public WordBlockAdapter(Context context, String words, boolean isChinese) {
        mContext = context;
        mWords = words;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.word_block, null, false);

        String[] wordArray = null;
        if (isChinese) {
            wordArray = mWords.split("");
        }
        else {
            wordArray = mWords.split(" ");
        }
        Log.e(TAG, "wordArray size: " + wordArray.length);

        mItems_new = new ArrayList<>();
        mAnswer = new ArrayList<>();
        for (String s : wordArray) {
            if (s.length() <= 0 ) {
                Log.d(TAG, "continue. length:" + s.length() + ", " + s);
                continue;
            }
            if (isChinese && s.equals(" ")) {
                Log.d(TAG, "continue. length:" + s.length() + ", " + s);
                continue;
            }

            mAnswer.add(s);
            TextView textView = (TextView)view.findViewById(R.id.word);
            textView.setText(s);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            Log.d(TAG, "view width: " + view.getMeasuredWidth());
            mItems_new.add(new Item(s, view.getMeasuredWidth()));
        }
        Random random = new Random(System.currentTimeMillis());
        Collections.shuffle(mItems_new, random);
        mBaseLineItem = new Item("== answer ==", 0);
        mItems_new.add(mBaseLineItem);
        mBaseLinePosition = mItems_new.indexOf(mBaseLineItem);
        Log.e(TAG, "mItems_new size: " + mItems_new.size());
        Log.e(TAG, "mBaseLinePosition: " + mBaseLinePosition);
    }

    public int getViewWidth(int position) {
        return mItems_new.get(position).width;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.word_block, parent, false);
        Log.d(TAG, "onCreateViewHolder" + v.toString());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.word.setText(mItems_new.get(position).word);
        if (mBaseLinePosition == position) {
            holder.cardView.setCardBackgroundColor(Color.GRAY);
            holder.cardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        else if (mBaseLinePosition < position) {
            // check the position is currect
            int index = position - mBaseLinePosition - 1;
            if (mItems_new.get(position).word.equals(mAnswer.get(index))) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.theme2_primary));
            }
            else {
                holder.cardView.setCardBackgroundColor(Color.RED);
            }
        }
        else if (mBaseLinePosition > position) {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (mBaseLinePosition == position) return ITEM_TYPE_BASE_LINE;
        else return ITEM_TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return mItems_new.size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "before. from " + fromPosition + ", to " + toPosition + ", base " + mBaseLinePosition);
        Log.d(TAG, "mItems_new size " + mItems_new.size());
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems_new, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems_new, i, i - 1);
            }
        }
        mBaseLinePosition = mItems_new.indexOf(mBaseLineItem);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(toPosition);
        Log.d(TAG, "from " + fromPosition + ", to " + toPosition + ", base " + mBaseLinePosition);
        if (toPosition > mBaseLinePosition && fromPosition > mBaseLinePosition) {
            int min = (fromPosition<toPosition) ? fromPosition : toPosition;
            notifyItemRangeChanged(min, mItems_new.size() - min);
        }
        else if (toPosition < mBaseLinePosition && fromPosition >= mBaseLinePosition) {
            notifyItemRangeChanged(fromPosition, mItems_new.size() - fromPosition);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView word;

        public ViewHolder(final View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview);
            word = (TextView) itemView.findViewById(R.id.word);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    Log.d(TAG, "position: " + position);
                    if (position < mBaseLinePosition) {
                        onItemMove(position, mItems_new.size() - 1);
                    }
                    else if (position > mBaseLinePosition) {
                        onItemMove(position, mBaseLinePosition);
                    }
                }
            });

        }
    }

    private class Item {
        public String word;
        public int width;

        public Item(String word, int width) {
            this.word = word;
            this.width = width;
        }
    }
}
