package com.shimnssso.wordsmaster.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.shimnssso.wordsmaster.R;

import java.util.ArrayList;
import java.util.HashSet;

public class BookAdapter extends ArrayAdapter<BookAdapter.Book>{
    private ArrayList<Book> items;
    private boolean[] mIsChecked;
    private Context mContext;

    public BookAdapter(Context context, int textViewResourceId, ArrayList<Book> items) {
        super(context, textViewResourceId, items);
        this.mContext = context;
        this.items = items;
        this.mIsChecked = new boolean[items.size()];
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.book_row, null);
        }
        Book book = items.get(position);
        if (book != null) {
            CheckBox chk = (CheckBox) v.findViewById(R.id.chk);
            TextView tt = (TextView) v.findViewById(R.id.title);
            TextView bt = (TextView) v.findViewById(R.id.size);
            if (tt != null){
                tt.setText(book.getTitle());
            }
            if(bt != null){
                bt.setText(String.valueOf(book.getSize()));
            }
            chk.setChecked(mIsChecked[position]);
        }
        return v;
    }

    public void checkAll(boolean checked) {
        for (int i=0; i<mIsChecked.length; i++) {
            mIsChecked[i] = checked;
        }
    }

    public void check(int position) {
        mIsChecked[position] = !mIsChecked[position];
    }

    public HashSet<String> getCheckedBook() {
        HashSet<String> ret = new HashSet<>();
        for (int i=0; i<mIsChecked.length; i++) {
            if (mIsChecked[i]) {
                Book book = items.get(i);
                ret.add(book.getTitle());
            }
        }
        return ret;
    }

    public static class Book {
        private String title;
        private int size;
        public Book (String title, int size) {
            this.title = title;
            this.size = size;
        }
        public String getTitle() { return title; }
        public int getSize() { return size; }
    }
}
