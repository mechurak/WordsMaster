package com.shimnssso.wordsmaster.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.shimnssso.wordsmaster.BookAdapter;
import com.shimnssso.wordsmaster.data.DbMeta.GlobalTableMeta;
import com.shimnssso.wordsmaster.data.DbMeta.WordTableMeta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";

    private Context mContext;
    private static DbHelper mInstance = null;

    public static DbHelper getInstance(Context context) {
        // for debug
        context.deleteDatabase(DbMeta.DATABASE_NAME);

        if (mInstance == null) {
            mInstance = new DbHelper(context);
        }
        return mInstance;
    }

    public static DbHelper getInstance() {
        return mInstance;
    }

    private DbHelper(Context context) {
        super(context, DbMeta.DATABASE_NAME, null, DbMeta.DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE " + GlobalTableMeta.TABLE_NAME + " ("
                + GlobalTableMeta.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GlobalTableMeta.MODE + " INTEGER, "
                + GlobalTableMeta.CUR_ID + " INTEGER"
                + " )"
        );
        db.execSQL("INSERT INTO " + GlobalTableMeta.TABLE_NAME + " VALUES (null,0,0)");

        db.execSQL("CREATE TABLE " + WordTableMeta.TABLE_NAME + " ("
                        + WordTableMeta.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + WordTableMeta.SPELLING + " TEXT, "
                        + WordTableMeta.PHONETIC + " TEXT, "
                        + WordTableMeta.MEANING + " TEXT, "
                        + WordTableMeta.CATEGORY + " TEXT, "
                        + WordTableMeta.FLAG + " INTEGER, "
                        + WordTableMeta.UPDATE_TIME + " INTEGER, "
                        + "UNIQUE(" + WordTableMeta.SPELLING + ", " + WordTableMeta.CATEGORY + ")"
                        + " )"
        );

        try {
            InputStream is = mContext.getAssets().open("data.txt");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            String sql = "INSERT INTO " + WordTableMeta.TABLE_NAME + " VALUES (null,?,?,?,?,?,null)";
            SQLiteStatement statement = db.compileStatement(sql);

            while ((line = br.readLine()) != null) {
                String[] row = line.split("\\t");
                Log.d(TAG, Arrays.toString(row));

                statement.clearBindings();
                statement.bindAllArgsAsStrings(row);
                statement.executeInsert();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GlobalTableMeta.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WordTableMeta.TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<BookAdapter.Book> getBookList() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                WordTableMeta.CATEGORY,
                "COUNT(*)"
        };
        ArrayList<BookAdapter.Book> ret = new ArrayList<>();
        Cursor c = db.query(WordTableMeta.TABLE_NAME, columns, null, null, WordTableMeta.CATEGORY, null, null);
        if (c.moveToFirst()) {
            do {
                BookAdapter.Book book = new BookAdapter.Book(c.getString(0), c.getInt(1));
                ret.add(book);
            } while (c.moveToNext());
        }
        Log.d(TAG, "getBookList. size:" + ret.size());
        return ret;
    }

    public Cursor getWordList(String book) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(WordTableMeta.TABLE_NAME, null, WordTableMeta.CATEGORY+"='"+book+"'", null, null, null, null);
    }

    public Cursor getStarredWordList(String book) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(WordTableMeta.TABLE_NAME, null, WordTableMeta.CATEGORY+"='"+book+"' AND "
                + WordTableMeta.FLAG+" & "+ DbMeta.WordFlag.STARRED+" = " + DbMeta.WordFlag.STARRED, null, null, null, null);
    }

    public void deleteWords(String sheetTitle, long updateTime, ArrayList<String> titleList) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(WordTableMeta.TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(WordTableMeta.UPDATE_TIME + "!=" + updateTime + " and ");
        sb.append("(");
        for (int i=0; i<titleList.size()-1; i++) {
            sb.append(WordTableMeta.CATEGORY);
            sb.append("='" + sheetTitle + " - " + titleList.get(i) + "' or ");
        }
        sb.append(WordTableMeta.CATEGORY);
        sb.append("='" + sheetTitle + " - " + titleList.get(titleList.size()-1) + "'");
        sb.append(")");
        db.execSQL(sb.toString());
    }

    public void deleteWords(ArrayList<String> titleList) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(WordTableMeta.TABLE_NAME);
        sb.append(" WHERE ");
        for (int i=0; i<titleList.size()-1; i++) {
            sb.append(WordTableMeta.CATEGORY);
            sb.append("='" + titleList.get(i) + "' or ");
        }
        sb.append(WordTableMeta.CATEGORY);
        sb.append("='" + titleList.get(titleList.size()-1) + "'");
        db.execSQL(sb.toString());
    }

    public int getMode() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + GlobalTableMeta.MODE + " FROM " + GlobalTableMeta.TABLE_NAME, null);
        c.moveToFirst();
        int ret = c.getInt(0);
        c.close();
        return ret;
    }
    public void setMode(int mode) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + GlobalTableMeta.TABLE_NAME + " SET " + GlobalTableMeta.MODE + "=" + mode);
    }

    public int getCurrentWordId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + GlobalTableMeta.CUR_ID + " FROM " + GlobalTableMeta.TABLE_NAME, null);
        c.moveToFirst();
        int ret = c.getInt(0);
        c.close();
        return ret;
    }
    public void setCurrentWordId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + GlobalTableMeta.TABLE_NAME + " SET " + GlobalTableMeta.CUR_ID + "=" + id);
    }

    public int updateWord(ContentValues values, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.update(WordTableMeta.TABLE_NAME, values, WordTableMeta.ID+"="+id, null);
        Log.d(TAG, "updateWord. ret: " + result);
        return result;
    }
}
