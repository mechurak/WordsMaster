package com.shimnssso.wordsmaster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.googleSheet.SheetClientActivity;

import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity implements BookAdapter.BookAdpaterListener {
    private final static String TAG = "BookListActivity";
    private DbHelper mDbHelper = null;
    private BookAdapter mAdapter = null;
    RecyclerView mListView;
    private int mSelectedNum = 0;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.book_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_book_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.title_book_list,
                R.string.title_book_list);

        // for select mode
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // event when click home button
                Log.e(TAG, "onClick");
                mAdapter.checkAll(false);
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Toast.makeText(BookListActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        mDbHelper = DbHelper.getInstance(this);
        ArrayList<BookAdapter.Book> mBookList = mDbHelper.getBookList();
        mAdapter = new BookAdapter(BookListActivity.this, R.layout.book_list, mBookList);
        mAdapter.setListener(this);

        mListView = (RecyclerView)findViewById(R.id.list_book);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mAdapter);
        Log.i(TAG, "setAdapter");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list, menu);
        if (mSelectedNum > 0) {
            menu.removeItem(R.id.action_import);
            menu.removeItem(R.id.action_import_other);
            if (mSelectedNum == mAdapter.getItemCount()) {
                menu.removeItem(R.id.action_all);
            }
        }
        else {
            menu.removeItem(R.id.action_all);
            menu.removeItem(R.id.action_study);
            menu.removeItem(R.id.action_delete);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult. requestCode: " + requestCode + ", resultCode: " + resultCode);

        // update adapter
        ArrayList<BookAdapter.Book> mBookList = mDbHelper.getBookList();
        mAdapter = new BookAdapter(BookListActivity.this, R.layout.book_list, mBookList);
        mAdapter.setListener(this);
        mListView.setAdapter(mAdapter);
        Log.i(TAG, "setAdapter");
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
        Log.d(TAG, "onOptionsItemSelected. id " + id);

        switch (id) {
            case R.id.action_import:
                Intent intent = new Intent(BookListActivity.this, SheetClientActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.action_import_other:

                return true;

            case R.id.action_all:
                mAdapter.checkAll(true);
                return true;

            case R.id.action_delete:
                new AlertDialog.Builder(this).setTitle("Delete").setMessage("Are you sure?").
                        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<String> checkedBooks = mAdapter.getCheckedBook();
                                if (checkedBooks.size() > 0) {
                                    mDbHelper.deleteWords(checkedBooks);
                                    mAdapter.removeCheckedItem();
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }).
                        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).
                        show();
                return true;
        }


        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectedNumChanged(int selectItemNum) {
        int prev = mSelectedNum;
        mSelectedNum = selectItemNum;

        if (mSelectedNum > 0) {
            if (prev == 0 ) {
                mDrawerToggle.setDrawerIndicatorEnabled(false);
                //setTheme(R.style.SelectTheme);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.theme2_primary)));
                invalidateOptionsMenu();
            }
            getSupportActionBar().setTitle(String.valueOf(selectItemNum));
            if (mSelectedNum == mAdapter.getItemCount()) {
                invalidateOptionsMenu();
            }
        }

        else {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            //setTheme(R.style.AppTheme);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.primary)));
            getSupportActionBar().setTitle(R.string.title_book_list);
            invalidateOptionsMenu();
        }
    }
}