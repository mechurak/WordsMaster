package com.shimnssso.wordsmaster.googleSheet;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;
import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.BookAdapter;
import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.data.DbMeta;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SheetClientActivity extends AppCompatActivity implements BookAdapter.BookAdpaterListener {
    private static final String TAG = "SheetClientActivity";
    public final static int SHEET_LIST_FRAGMENT = 0;
    public final static int SHEET_BOOK_FRAGMENT = 1;

    private int mCurrentFragmentIndex = SHEET_LIST_FRAGMENT;

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

    private final static String SHEET_SCOPE = "https://spreadsheets.google.com/feeds";
    private final static String mScopes = "oauth2:" + SHEET_SCOPE;

    private final static String TAG_SPELLING = "spelling";
    private final static String TAG_MEANING = "meaning";
    private final static String TAG_PHONETIC = "phonetic";


    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
    SpreadsheetService mService;
    private SpreadsheetEntry mCurrentSheet;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private SheetAdapter mSheetAdapter;
    public SheetAdapter getSheetAdapter() { return mSheetAdapter; }

    private BookAdapter mBookAdapter;
    public BookAdapter getBookAdapter() { return mBookAdapter; }

    List<SpreadsheetEntry> mSpreadsheets;
    ArrayList<BookAdapter.Book> mBookList;

    ProgressDialog mDialog;
    int mPosDialog = 0;
    ProgressBar mProgressBar;

    private boolean mIsSelectMode = false;

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    /**
     * Attempts to retrieve the username.
     * If the account is not yet known, invoke the picker. Once the account is known,
     * start an instance of the AsyncTask to get the auth token and do work with it.
     */
    private void getUsername() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            if (isDeviceOnline()) {
                new GetSheetListTask().execute();
            } else {
                Toast.makeText(this, "network is not available", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sheet_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_sheet_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.title_word_list,
                R.string.title_book_list);

        // for select mode
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // event when click home button
                Log.e(TAG, "onClick");
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.primary)));
                getSupportActionBar().setTitle(R.string.title_sheet_list);
                invalidateOptionsMenu();
                mBookList.clear();

                replaceFragment(SHEET_LIST_FRAGMENT);
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Toast.makeText(SheetClientActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        mService = new SpreadsheetService("MySpreadsheetIntegration");
        mService.setProtocolVersion(SpreadsheetService.Versions.V3);

        //mTitle = (TextView)findViewById(R.id.txt_title_at_sheet);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        mBookList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mEmail == null) {
            Log.d(TAG, "onResume. mEmail == null");
            pickUserAccount();
        } else {
            if (isDeviceOnline()) {
                if (mSheetAdapter == null) {
                    new GetSheetListTask().execute();
                }
                else {
                    replaceFragment(SHEET_LIST_FRAGMENT);
                }
            } else {
                Toast.makeText(this, "network is not available", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, "You should pick an account", Toast.LENGTH_SHORT).show();
            }
        }
        // Handle the result from exceptions

    }



    /**
     * This method is a hook for background threads and async tasks that need to
     * provide the user a response UI when an exception occurs.
     */
    private void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            SheetClientActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    private boolean isDeviceOnline() {
        // TODO return network status
        return true;
    }

    public class GetSheetListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.e(TAG, "onPreExecute in GetSheetListTask");

            mProgressBar.setVisibility(View.VISIBLE);
            //mProgressBar.setIndeterminate(true);
            //mProgressBar.setMax(100);
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String token = fetchToken();
                if (token != null) {
                    Log.e(TAG, "token: " + token);
                    mService.setHeader("Authorization", "Bearer " + token);

                    // Define the URL to request.  This should never change.
                    URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");

                    // Make a request to the API and get all spreadsheets.
                    SpreadsheetFeed feed = mService.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
                    mSpreadsheets = feed.getEntries();
                    Log.e(TAG, "spreadsheets size: " + mSpreadsheets.size());

                    // Iterate through all of the spreadsheets returned
                    for (SpreadsheetEntry spreadsheet : mSpreadsheets) {
                        // Print the title of this spreadsheet to the screen
                        Log.i(TAG, "sheet: " + spreadsheet.getTitle().getPlainText());
                        Log.i(TAG, "id: " + spreadsheet.getId());
                        Log.i(TAG, "key: " + spreadsheet.getKey());
                        DateTime dateTime = spreadsheet.getUpdated();
                        Log.i(TAG, "updated: " + dateTime.toString());
                    }

                    mSheetAdapter = new SheetAdapter(SheetClientActivity.this, R.layout.sheet_row, mSpreadsheets);

                    // **Insert the good stuff here.**
                    // Use the token to access the user's Google data.
                    //...
                }
            } catch (IOException e) {
                e.printStackTrace();
                // The fetchToken() method handles Google-specific exceptions,
                // so this indicates something went wrong at a higher level.
                // TIP: Check for network connectivity before starting the AsyncTask.
                //...
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.e(TAG, "onPostExecute in GetSheetListTask");

            mProgressBar.setVisibility(View.GONE);
            replaceFragment(SHEET_LIST_FRAGMENT);
        }
    }


    public class GetBookListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
            //mProgressBar.setIndeterminate(true);
            //mProgressBar.setMax(100);
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String token = fetchToken();
                if (token != null) {
                    Log.i(TAG, "token: " + token);
                    mService.setHeader("Authorization", "Bearer " + token);


                    WorksheetFeed worksheetFeed = mService.getFeed(mCurrentSheet.getWorksheetFeedUrl(), WorksheetFeed.class);
                    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
                    Log.d(TAG, "worksheets size: " + worksheets.size());
                    WorksheetEntry worksheet = worksheets.get(0);


                    // Fetch the list feed of the worksheet.
                    //URL listFeedUrl = worksheet.getListFeedUrl();
                    //ListFeed listFeed = mService.getFeed(listFeedUrl, ListFeed.class);

                    // Fetch column 4, and every row after row 1.
                    URL cellFeedUrl = new URI(worksheet.getCellFeedUrl().toString()
                            + "?min-row=1&min-col=1&max-col=1").toURL();
                    CellFeed cellFeed = mService.getFeed(cellFeedUrl, CellFeed.class);

                    // Iterate through each row, printing its cell values.
                    String prevStr = "(prev)";
                    String curStr = "(cur)";
                    int size = 0;
                    for (CellEntry cell : cellFeed.getEntries()) {
                        // Print the first column's cell value
                        Log.d(TAG, "cur : " + cell.getCell().getInputValue() + ", size:" + size);
                        curStr = cell.getCell().getInputValue();
                        if (!curStr.equals(prevStr) && !prevStr.equals("(prev)")){
                            BookAdapter.Book book = new BookAdapter.Book(prevStr, size);
                            mBookList.add(book);
                            Log.d(TAG, "added " + book.getTitle() + " " + book.getSize());
                            size = 1;
                        }
                        else {
                            size++;
                        }
                        prevStr = curStr;
                    }
                    // add last book
                    BookAdapter.Book book = new BookAdapter.Book(prevStr, size);
                    mBookList.add(book);
                    Log.d(TAG, "added " + book.getTitle() + " " + book.getSize());

                    mBookAdapter = new BookAdapter(SheetClientActivity.this, R.layout.sheet_list, mBookList);
                    mBookAdapter.setListener(SheetClientActivity.this);

                    // **Insert the good stuff here.**
                    // Use the token to access the user's Google data.
                    //...
                }
            } catch (IOException e) {
                // The fetchToken() method handles Google-specific exceptions,
                // so this indicates something went wrong at a higher level.
                // TIP: Check for network connectivity before starting the AsyncTask.
                //...
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mProgressBar.setVisibility(View.GONE);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setTitle("0");
            invalidateOptionsMenu();
            mIsSelectMode = false;
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.theme2_primary)));
            replaceFragment(SHEET_BOOK_FRAGMENT);
        }
    }


    public class ImportBookTask extends AsyncTask<Void, Integer, Integer> {

        private ArrayList<String> mBooks;
        private int mWordSize;

        public ImportBookTask (ArrayList<String> books, int wordSize) {
            mBooks = books;
            mWordSize = wordSize;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog= new ProgressDialog(SheetClientActivity.this);
            mDialog.setTitle("Progress");
            mDialog.setMessage("Importing.....");
            mDialog.setMax(mWordSize);
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setCanceledOnTouchOutside(false);

            mDialog.show();
        }

        /**
         * Executes the asynchronous job. This runs when you call execute()
         * on the AsyncTask instance.
         */
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                String token = fetchToken();
                if (token != null) {
                    Log.i(TAG, "token: " + token);
                    mService.setHeader("Authorization", "Bearer " + token);

                    WorksheetFeed worksheetFeed = mService.getFeed(mCurrentSheet.getWorksheetFeedUrl(), WorksheetFeed.class);
                    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
                    Log.d(TAG, "worksheets size: " + worksheets.size());
                    WorksheetEntry worksheet = worksheets.get(0);

                    // Fetch the list feed of the worksheet.
                    URL listFeedUrl = worksheet.getListFeedUrl();
                    ListFeed listFeed = mService.getFeed(listFeedUrl, ListFeed.class);

                    String sheetTitle = mCurrentSheet.getTitle().getPlainText();
                    DbHelper dbHelper = DbHelper.getInstance();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    SQLiteStatement stmt = db.compileStatement(
                            "INSERT OR REPLACE INTO " + DbMeta.WordTableMeta.TABLE_NAME
                                    + " VALUES (null,?,?,?,?,"
                                    + "(SELECT " + DbMeta.WordTableMeta.FLAG + " FROM " + DbMeta.WordTableMeta.TABLE_NAME
                                    + " WHERE (" + DbMeta.WordTableMeta.SPELLING + "=?) AND (" + DbMeta.WordTableMeta.CATEGORY + "=?) )"
                                    + ",?)"
                    );
                    long updateTime = System.currentTimeMillis();

                    // Iterate through each row, printing its cell values.
                    for (ListEntry row : listFeed.getEntries()) {
                        String title = row.getTitle().getPlainText();

                        // ignore current row (mBooks does not contain current book)
                        if (!mBooks.contains(title)) {
                            continue;
                        }
                        Log.d(TAG, "====== row:" + title);
                        String phonetic = row.getCustomElements().getValue(TAG_PHONETIC);
                        String spelling = row.getCustomElements().getValue(TAG_SPELLING);
                        String meaning = row.getCustomElements().getValue(TAG_MEANING);
                        if (phonetic == null) phonetic = "";
                        if (spelling == null) spelling = "";
                        if (meaning == null) meaning = "";

                        stmt.clearBindings();
                        stmt.bindString(1, spelling);
                        stmt.bindString(2, phonetic);
                        stmt.bindString(3, meaning);
                        stmt.bindString(4, sheetTitle + " - "+ title);
                        stmt.bindString(5, spelling);
                        stmt.bindString(6, sheetTitle + " - " + title);
                        stmt.bindLong(7, updateTime);
                        stmt.execute();

                        publishProgress(++mPosDialog);
                    }
                    stmt.close();

                    dbHelper.deleteWords(sheetTitle, updateTime, mBooks);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            return mPosDialog;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            mDialog.dismiss();
            mDialog = null;
            mPosDialog = 0;

            Toast.makeText(SheetClientActivity.this, result+" words are imported.", Toast.LENGTH_SHORT).show();

            Log.i(TAG, "import is done.");
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.primary)));
            getSupportActionBar().setTitle(R.string.title_sheet_list);
            invalidateOptionsMenu();
            mBookList.clear();
            replaceFragment(SHEET_LIST_FRAGMENT);
        }
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    private String fetchToken() throws IOException {
        try {
            // **
            return GoogleAuthUtil.getToken(this, mEmail, mScopes);
            // **
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
            //...
        }
        return null;
    }

    public void setCurrentSheet(int position) {
        mCurrentSheet = mSheetAdapter.getItem(position);
        new GetBookListTask().execute();
    }

    public void importBook() {
        ArrayList<String> checkedTitle = mBookAdapter.getCheckedBook();
        int wordSize = mBookAdapter.getWordSize();

        new ImportBookTask(checkedTitle, wordSize).execute();
    }

    public void replaceFragment(int newFragmentIndex) {
        Log.d(TAG, "replaceFragment " + newFragmentIndex);
        Fragment newFragment = getFragment(newFragmentIndex);

        // replace fragment
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.sheet_fragment, newFragment);

        // Commit the transaction
        transaction.commit();
        mCurrentFragmentIndex = newFragmentIndex;
    }

    private Fragment getFragment(int index) {
        Fragment newFragment = null;

        switch (index) {
            case SHEET_LIST_FRAGMENT:
                newFragment = new SheetListFragment();
                break;
            case SHEET_BOOK_FRAGMENT:
                newFragment = new SheetBookFragment();
                break;
            default:
                Log.e(TAG, "unexpected index " + index);
                break;
        }
        return newFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mCurrentFragmentIndex==SHEET_LIST_FRAGMENT) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        else {
            if (mIsSelectMode) {
                getMenuInflater().inflate(R.menu.menu_sheet_book_select_import, menu);
            }
            else {
                getMenuInflater().inflate(R.menu.menu_sheet_book_select, menu);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_all:
                mBookAdapter.checkAll(true);
                return true;

            case R.id.action_import:
                importBook();
                return true;

            /*
            case R.id.action_settings:
                return true;
                */
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectedNumChanged(int selectItemNum) {
        if (selectItemNum > 0) {
            if (!mIsSelectMode) {
                mIsSelectMode = true;
                invalidateOptionsMenu();
            }
            getSupportActionBar().setTitle(String.valueOf(selectItemNum));
        }
        else {
            if (mIsSelectMode) {
                mIsSelectMode = false;
                invalidateOptionsMenu();
            }
            getSupportActionBar().setTitle(String.valueOf(selectItemNum));
        }
    }
}
