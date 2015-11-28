package com.shimnssso.wordsmaster.wordTest;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shimnssso.wordsmaster.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordBlockAdapter extends RecyclerView.Adapter<WordBlockAdapter.ViewHolder> implements MyItemTouchCallback.ItemTouchHelperAdapter {
    private static final String TAG = "WordBlockAdapter";

    private Context mContext;
    private String mWords;
    private List<String> mItems;
    private List<Integer> mWidths;

    public WordBlockAdapter(Context context, String words) {
        mContext = context;
        mWords = words;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.word_block, null, false);

        String[] wordArray = mWords.split(" ");
        mItems = new ArrayList<>();
        mWidths = new ArrayList<>();
        for (String s : wordArray) {
            mItems.add(s);

            TextView textView = (TextView)view.findViewById(R.id.word);
            textView.setText(s);
            CardView cardView = (CardView)view.findViewById(R.id.cardview);

            cardView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            Log.d(TAG, "cardView width: " + cardView.getMeasuredWidth());
            mWidths.add(cardView.getMeasuredWidth()+30);
        }
    }

    public int getViewWidth(int position) {
        return mWidths.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.word_block, parent, false);
        Log.d(TAG, "onCreateViewHolder" + v.toString());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.word.setText(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
                Collections.swap(mWidths, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
                Collections.swap(mWidths, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView word;

        public ViewHolder(final View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview);
            word = (TextView) itemView.findViewById(R.id.word);
        }
    }
}
