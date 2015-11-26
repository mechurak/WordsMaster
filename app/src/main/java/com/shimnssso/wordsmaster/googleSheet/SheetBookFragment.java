package com.shimnssso.wordsmaster.googleSheet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.BookAdapter;

public class SheetBookFragment extends Fragment {
    private final static String TAG = "SheetBookFragment";

    SheetClientActivity mActivity;
    RecyclerView mListView;
    BookAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sheet_book_fragment, container, false);
        mActivity = (SheetClientActivity)getActivity();
        mAdapter = mActivity.getBookAdapter();

        mListView = (RecyclerView)v.findViewById(R.id.list_book);
        LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity.getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mAdapter);
        Log.i(TAG, "setAdapter");
        return v;
    }
}
