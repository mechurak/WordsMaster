package com.shimnssso.wordsmaster.wordStudy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.data.WordCursorAdapter;

public class WordListFragment extends Fragment implements WordListActivity.WordInterface {
    private final static String TAG = "WordListFragment";

    WordListActivity mActivity;
    WordCursorAdapter mAdapter;
    ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.word_list_fragment, container, false);
        mActivity = (WordListActivity)getActivity();
        mAdapter = mActivity.getAdapter();

        mListView = (ListView)v.findViewById(R.id.list_word);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick");
                mAdapter.setCurrentId(position);
                mAdapter.notifyDataSetInvalidated();
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemLongClick");
                mAdapter.setCurrentId(position);
                mActivity.replaceFragment(WordListActivity.WORD_CARD_FRAGMENT);
                return true;
            }
        });

        //mListView.smoothScrollToPosition(mActivity.getCurrentId());
        mListView.setSelection(mAdapter.getCurrentId());
        return v;
    }

    @Override
    public void setVisible(int type, boolean visible) {
        mAdapter.setVisible(type, visible);

        int first = mListView.getFirstVisiblePosition();
        int last = mListView.getLastVisiblePosition();
        int mPrevPosition = mAdapter.getCurrentId();
        if (mPrevPosition < first || last < mPrevPosition) {
            mAdapter.setCurrentId(first+1);
        }
        mListView.setSelection(mAdapter.getCurrentId());
    }
}
