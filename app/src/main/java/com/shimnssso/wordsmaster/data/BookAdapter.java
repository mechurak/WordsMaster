package com.shimnssso.wordsmaster.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.wordStudy.WordListActivity;

import java.util.ArrayList;
import java.util.Iterator;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private static final String TAG = "BookAdapter";

    private ArrayList<Book> items;
    private Context mContext;
    private int parentLayout;
    private int mSelectedItemNum = 0;
    private BookAdpaterListener mListener;

    public BookAdapter(Context context, int parentLayout, ArrayList<Book> items) {
        this.mContext = context;
        this.items = items;
        this.parentLayout = parentLayout;

        /*
        if (this.parentLayout == R.layout.book_list) {
            isSelectMode = false;
        }
        else if (this.parentLayout == R.layout.sheet_list) {
            isSelectMode = true;
        }
        */
    }

    public void checkAll(boolean checked) {
        for (Book b : items) {
            b.setChecked(checked);
        }
        if (checked) mSelectedItemNum = items.size();
        else mSelectedItemNum = 0;
        if (mListener != null) {
            mListener.onSelectedNumChanged(mSelectedItemNum);
        }
        notifyDataSetChanged();
    }

    public boolean check(int position) {
        Book b = items.get(position);
        b.setChecked(!b.isChecked());
        Log.d(TAG, "check. position " + position + " " + b.isChecked());
        return b.isChecked();
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
        mSelectedItemNum = 0;
        if (mListener != null) {
            mListener.onSelectedNumChanged(mSelectedItemNum);
        }
        notifyDataSetChanged();
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
    public void onBindViewHolder(final BookAdapter.ViewHolder holder, final int position) {
        final Book item=items.get(position);
        Log.d(TAG, "onBindViewHolder. position: " + position);

        // mark  the view as selected:
        //holder.itemView.setSelected(item.isChecked());
        //holder.cardView.setSelected(item.isChecked());

        holder.cardView.setCardBackgroundColor(item.isChecked() ? Color.LTGRAY : Color.WHITE);
        holder.title.setText(item.getTitle());
        holder.size.setText(String.valueOf(item.getSize()));
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

        public ViewHolder(final View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.title);
            size = (TextView)itemView.findViewById(R.id.size);
            cardView=(CardView)itemView.findViewById(R.id.cardview);

            itemView.setClickable(true);

            // Handle item click and set the selection
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "onClick. test" );

                    if (mSelectedItemNum > 0) {
                        RecyclerView parentView = (RecyclerView)itemView.getParent();
                        int position = parentView.getChildAdapterPosition(v);
                        boolean isChecked = check(position);
                        notifyItemChanged(position);
                        if (isChecked) mSelectedItemNum++;
                        else mSelectedItemNum--;

                        if (mListener != null) mListener.onSelectedNumChanged(mSelectedItemNum);
                    }

                    else {
                        Log.e(TAG, "onClick in non select mode");
                        Intent intent = new Intent(mContext, WordListActivity.class);
                        intent.putExtra("book", title.getText());
                        mContext.startActivity(intent);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.e(TAG, "onLongClick");
                    RecyclerView parentView = (RecyclerView)itemView.getParent();
                    int position = parentView.getChildAdapterPosition(v);
                    boolean isChecked = check(position);
                    notifyItemChanged(position);
                    if (isChecked) mSelectedItemNum++;
                    else mSelectedItemNum--;

                    if (mListener != null) mListener.onSelectedNumChanged(mSelectedItemNum);
                    return true;
                }
            });
        }
    }


    public void setListener(BookAdpaterListener listener) {
        mListener = listener;
    }

    public interface BookAdpaterListener {
        void onSelectedNumChanged(int selectItemNum);
    }
}
