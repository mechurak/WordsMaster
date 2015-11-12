package com.shimnssso.wordsmaster.wordStudy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shimnssso.wordsmaster.R;

public class WordListFragment extends Fragment {
    private final static String TAG = "WordListFragment";

    WordListActivity mActivity;
    ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.word_list_fragment, container, false);
        mActivity = (WordListActivity)getActivity();

        mListView = (ListView)v.findViewById(R.id.list_word);
        mListView.setAdapter(mActivity.getAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mActivity.setCurrentId(position);
                mActivity.replaceFragment(WordListActivity.WORD_CARD_FRAGMENT);
            }
        });
        mListView.smoothScrollToPosition(mActivity.getCurrentId());
        return v;
    }
}
