package com.shimnssso.wordsmaster.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.shimnssso.wordsmaster.R;

import java.util.List;

public class SheetAdapter extends ArrayAdapter<SpreadsheetEntry> {
    private List<SpreadsheetEntry> items;
    private Context mContext;

    public SheetAdapter(Context context, int textViewResourceId, List<SpreadsheetEntry> items) {
        super(context, textViewResourceId, items);
        mContext = context;
        this.items = items;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.sheet_row, null);
        }
        SpreadsheetEntry sheet = items.get(position);
        if (sheet != null) {
            TextView tt = (TextView) v.findViewById(R.id.txt_sheet_title);
            TextView bt = (TextView) v.findViewById(R.id.txt_sheet_size);
            if (tt != null){
                tt.setText(sheet.getTitle().getPlainText());
            }
            if(bt != null){
                bt.setText(sheet.getUpdated().toUiString());
            }
        }
        return v;
    }
}
