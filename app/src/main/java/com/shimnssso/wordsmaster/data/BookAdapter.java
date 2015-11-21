package com.shimnssso.wordsmaster.data;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.wordStudy.WordListActivity;

import java.util.ArrayList;
import java.util.Iterator;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private static final String TAG = "BookAdapter";

    private ArrayList<Book> items;
    private Context mContext;

    public BookAdapter(Context context, int textViewResourceId, ArrayList<Book> items) {
        //super(context, textViewResourceId, items);
        this.mContext = context;
        this.items = items;
    }

    /*
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
    */

    public void checkAll(boolean checked) {
        for (Book b : items) {
            b.setChecked(checked);
        }
    }
/*
    public void check(int position) {
        Book b = getItem(position);
        b.setChecked(!b.isChecked());
    }
*/
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

    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_row, parent, false);
        Log.d(TAG, "onCreateViewHolder" + v.toString());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BookAdapter.ViewHolder holder, int position) {
        final Book item=items.get(position);
        Log.d(TAG, "onBindViewHolder. position: " + position);
        holder.title.setText(item.getTitle());
        holder.size.setText(String.valueOf(item.getSize()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, item.getTitle(), Toast.LENGTH_SHORT).show();

                String title = item.getTitle();

                Intent intent = new Intent(mContext, WordListActivity.class);
                intent.putExtra("book", title);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        TextView size;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            size = (TextView)itemView.findViewById(R.id.size);
            cardView=(CardView)itemView.findViewById(R.id.cardview);
        }
    }
}
