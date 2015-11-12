package com.shimnssso.wordsmaster.googleSheet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.data.SheetAdapter;


public class SheetListFragment extends Fragment{
    private final static String TAG = "SheetListFragment";

    SheetClientActivity mActivity;
    ListView mListView;
    SheetAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sheet_list_fragment, container, false);
        mActivity = (SheetClientActivity)getActivity();
        mAdapter = mActivity.getSheetAdapter();
        Log.d(TAG, "mAdapter : " + mAdapter.toString());

        mListView = (ListView)v.findViewById(R.id.list_sheet);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "id " + id + ", position " + position);
                mActivity.setCurrentSheet(position);
            }
        });
        return v;
    }

}
