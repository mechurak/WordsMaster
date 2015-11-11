package com.shimnssso.wordsmaster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.shimnssso.wordsmaster.data.BookAdapter;

public class SheetBookFragment extends Fragment {
    private final static String TAG = "SheetBookFragment";

    SheetClientActivity mActivity;
    ListView mListView;
    BookAdapter mAdapter;

    CheckBox chk_all_at_sheet;
    Button btn_import_sheet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sheet_book_fragment, container, false);
        mActivity = (SheetClientActivity)getActivity();
        mAdapter = mActivity.getBookAdapter();

        mActivity.setTitle("Books");

        mListView = (ListView)v.findViewById(R.id.list_book_at_sheet);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "id " + id + ", position " + position);
                mActivity.setCurrentSheet(position);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "id " + id + ", position " + position);
                mAdapter.check(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        chk_all_at_sheet = (CheckBox)v.findViewById(R.id.chk_all_at_sheet);
        chk_all_at_sheet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAdapter.checkAll(isChecked);
                mAdapter.notifyDataSetChanged();
            }
        });

        btn_import_sheet = (Button)v.findViewById(R.id.btn_import_sheet);
        btn_import_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.importBook();
            }
        });

        //mAdapter.notifyDataSetChanged();
        return v;
    }
}
