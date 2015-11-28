package com.shimnssso.wordsmaster.wordTest;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class MySpanSizeLookUp extends GridLayoutManager.SpanSizeLookup {
    private static final String TAG = "MySpanSizeLookUp";

    private RecyclerView mRecyclerView;
    private int unit;
    private int maxSpan;

    public MySpanSizeLookUp(RecyclerView recyclerView, int maxSpan) {
        this.mRecyclerView = recyclerView;
        this.maxSpan = maxSpan;
        unit = mRecyclerView.getWidth()/maxSpan;
        Log.d(TAG, "unit: " + unit);
    }

    @Override
    public int getSpanSize(int position) {

        WordBlockAdapter.ViewHolder holder = (WordBlockAdapter.ViewHolder)mRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder == null) {
            return maxSpan;
        }

        int width = holder.cardView.getWidth();
        int span = 1;

        unit = mRecyclerView.getWidth()/maxSpan;

        while (width > unit * span) {
            span++;
        }
        Log.d(TAG, "position: " + position + ", span: " + span);
        return span;
    }
}
