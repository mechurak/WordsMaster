package com.shimnssso.wordsmaster.wordTest;

import android.support.v7.widget.GridLayoutManager;
import android.util.Log;

public class MySpanSizeLookUp extends GridLayoutManager.SpanSizeLookup {
    private static final String TAG = "MySpanSizeLookUp";

    private WordBlockAdapter mAdapter;
    private int unit;

    public MySpanSizeLookUp(WordBlockAdapter adapter, int maxSpan, int deviceWidth) {
        this.mAdapter = adapter;
        unit = deviceWidth / maxSpan ;
        Log.d(TAG, "deviceWidth: " + deviceWidth + ", unit: " + unit);
    }

    @Override
    public int getSpanSize(int position) {
        int width = mAdapter.getViewWidth(position);

        int span = 1;
        while (width > unit * span) {
            span++;
        }
        Log.d(TAG, "position: " + position + ", span: " + span);
        return span;
    }
}
