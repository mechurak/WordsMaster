package com.shimnssso.wordsmaster.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shimnssso.wordsmaster.R;

import java.util.ArrayList;
import java.util.Iterator;

public class BookAdapter extends ArrayAdapter<BookAdapter.Book>{
    private static final String TAG = "BookAdapter";

    private ArrayList<Book> items;
    private Context mContext;

    public BookAdapter(Context context, int textViewResourceId, ArrayList<Book> items) {
        super(context, textViewResourceId, items);
        this.mContext = context;
        this.items = items;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.book_row, null);
        }
        final Book book = items.get(position);
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
            chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.d(TAG, "onCheckedChanged. position:" + position + ", b:" + b);
                    book.setChecked(b);
                }
            });
            chk.setChecked(book.isChecked());
        }
        return v;
    }

    public void checkAll(boolean checked) {
        for (Book b : items) {
            b.setChecked(checked);
        }
    }

    public void check(int position) {
        Book b = getItem(position);
        b.setChecked(!b.isChecked());
    }

    public ArrayList<String> getCheckedBook() {
        ArrayList<String> ret = new ArrayList<>();
        for (Book b : items) {
            if (b.isChecked()) {
                ret.add(b.getTitle());
            }
        }
        return ret;
    }

    public void removeCheckedItem() {
        Iterator<Book> i = items.iterator();
        while(i.hasNext()) {
            Book b = i.next();
            if (b.isChecked()) {
                i.remove();
            }
        }
    }

    public int getWordSize() {
        int ret = 0;
        for (Book b : items) {
            if (b.isChecked()) {
                ret += b.getSize();
            }
        }
        return ret;
    }

    public static class Book {
        private String title;
        private int size;
        private boolean checked;

        public Book (String title, int size) {
            this.title = title;
            this.size = size;
        }
        public String getTitle() { return title; }
        public int getSize() { return size; }
        public boolean isChecked() { return checked; }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }
}
