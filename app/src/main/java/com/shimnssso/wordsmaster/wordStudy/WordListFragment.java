package com.shimnssso.wordsmaster.wordStudy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shimnssso.wordsmaster.R;

public class WordListFragment extends Fragment implements WordListActivity.WordInterface {
    private final static String TAG = "WordListFragment";

    WordListActivity mActivity;
    WordAdapter mAdapter;
    RecyclerView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.word_list_fragment, container, false);
        mActivity = (WordListActivity)getActivity();
        mAdapter = mActivity.getAdapter();

        mListView = (RecyclerView)v.findViewById(R.id.list_word);
        LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity.getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mAdapter);
        Log.i(TAG, "setAdapter");

        //mListView.smoothScrollToPosition(mAdapter.getCurrentId());
        mListView.scrollToPosition(mAdapter.getCurrentId());
        return v;
    }

    @Override
    public void setVisible(int type, boolean visible) {
        mAdapter.setVisible(type, visible);
        mAdapter.notifyDataSetChanged();

        /*
        int first = mListView.getFirstVisiblePosition();
        int last = mListView.getLastVisiblePosition();
        int mPrevPosition = mAdapter.getCurrentId();
        if (mPrevPosition < first || last < mPrevPosition) {
            mAdapter.setCurrentId(first+1);
        }
        mListView.setSelection(mAdapter.getCurrentId());
        */
    }

    @Override
    public void moveTo(int position) {
        mListView.scrollToPosition(position);
    }
}
