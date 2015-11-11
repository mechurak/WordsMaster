package com.shimnssso.wordsmaster.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.shimnssso.wordsmaster.data.DbMeta.GlobalTableMeta;
import com.shimnssso.wordsmaster.data.DbMeta.WordTableMeta;

import java.util.ArrayList;


public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";

    private Context mContext;
    private static DbHelper mInstance = null;

    public static DbHelper getInstance(Context context) {
        // for debug
        //context.deleteDatabase(DbMeta.DATABASE_NAME);

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
                + WordTableMeta.AUDIO_PATH + " TEXT, "
                + WordTableMeta.CATEGORY + " TEXT"
                + " )"
        );
        for(String[] word : DbMeta.tempBook) {
            db.execSQL( "INSERT INTO " + WordTableMeta.TABLE_NAME + " VALUES (null,?,?,?,?,?)", word);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GlobalTableMeta.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WordTableMeta.TABLE_NAME);
        onCreate(db);
    }

    public Cursor getBookList() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                WordTableMeta.ID,
                WordTableMeta.CATEGORY,
                "COUNT(*)"
        };
        return db.query(WordTableMeta.TABLE_NAME, columns, null, null, WordTableMeta.CATEGORY, null, null);
    }

    public Cursor getWordList(String book) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(WordTableMeta.TABLE_NAME, null, WordTableMeta.CATEGORY+"='"+book+"'", null, null, null, null);
    }


    public void deleteWords(String sheetTitle, ArrayList<String> titleList) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(WordTableMeta.TABLE_NAME);
        sb.append(" WHERE ");
        for (int i=0; i<titleList.size()-1; i++) {
            sb.append(WordTableMeta.CATEGORY);
            sb.append("='" + sheetTitle + " - " + titleList.get(i) + "' or ");
        }
        sb.append(WordTableMeta.CATEGORY);
        sb.append("='" + sheetTitle + " - " + titleList.get(titleList.size()-1) + "'");
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
}
