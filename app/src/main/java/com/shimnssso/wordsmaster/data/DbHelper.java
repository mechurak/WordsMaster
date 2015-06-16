package com.shimnssso.wordsmaster.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shimnssso.wordsmaster.data.DbMeta.CategoryTableMeta;
import com.shimnssso.wordsmaster.data.DbMeta.WordTableMeta;


public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";

    private Context mContext;
    private static DbHelper mInstance = null;

    public static DbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DbHelper(context);
        }
        return mInstance;
    }

    private DbHelper(Context context) {
        super(context, DbMeta.DATABASE_NAME, null, DbMeta.DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CategoryTableMeta.TABLE_NAME + " ("
                + CategoryTableMeta.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CategoryTableMeta.TITLE + " TEXT, "
                + CategoryTableMeta.SIZE + " INTEGER )"
        );

        db.execSQL( "CREATE TABLE " + WordTableMeta.TABLE_NAME + " ("
                + WordTableMeta.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WordTableMeta.SPELLING + " TEXT, "
                + WordTableMeta.PHONETIC + " TEXT, "
                + WordTableMeta.MEANING + " TEXT, "
                + WordTableMeta.AUDIO_PATH + " TEXT, "
                + WordTableMeta.CATEGORY + " INTEGER )"
        );

        int size = 0;
        for(String[] word : DbMeta.tempBook) {
            db.execSQL( "INSERT INTO " + WordTableMeta.TABLE_NAME + " VALUES (null,?,?,?,?,?)", word);
            size++;
        }

        db.execSQL( "INSERT INTO " + CategoryTableMeta.TABLE_NAME + " VALUES (null, 'default'," + size + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoryTableMeta.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WordTableMeta.TABLE_NAME);
        onCreate(db);
    }

    public Cursor getBookList() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(CategoryTableMeta.TABLE_NAME, null, null, null, null, null, null);
    }

    public Cursor getBook(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(WordTableMeta.TABLE_NAME, null, WordTableMeta.CATEGORY+"="+id, null, null, null, null);
    }






    /*

    // 새로운 Contact 함수 추가
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // id 에 해당하는 Contact 객체 가져오기
    public Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return contact;
    }

    // 모든 Contact 정보 가져오기
    public List<contact> getAllContacts() {
        List<contact> contactList = new ArrayList<contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    //Contact 정보 업데이트
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating word_row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    // Contact 정보 삭제하기
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }

    // Contact 정보 숫자
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    */
}
