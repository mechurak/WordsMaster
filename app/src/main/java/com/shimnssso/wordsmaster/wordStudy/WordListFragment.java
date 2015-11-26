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
import com.shimnssso.wordsmaster.util.TTSHelper;

public class WordListFragment extends Fragment implements WordListActivity.WordInterface {
    private final static String TAG = "WordListFragment";

    WordListActivity mActivity;
    WordAdapter mAdapter;
    TTSHelper mTTSHelper = null;
    RecyclerView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.word_list_fragment, container, false);
        mActivity = (WordListActivity)getActivity();
        mAdapter = mActivity.getAdapter();
        mTTSHelper = TTSHelper.getInstance(getActivity().getApplicationContext());

        mListView = (RecyclerView)v.findViewById(R.id.list_word);
        LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity.getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mAdapter);
        Log.i(TAG, "setAdapter");

        //mListView.smoothScrollToPosition(mActivity.getCurrentId());
        //mListView.setSelection(mAdapter.getCurrentId());
        mListView.smoothScrollToPosition(mAdapter.getCurrentId());
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
}
