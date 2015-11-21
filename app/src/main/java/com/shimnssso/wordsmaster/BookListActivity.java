package com.shimnssso.wordsmaster;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.shimnssso.wordsmaster.data.BookAdapter;
import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.googleSheet.SheetClientActivity;

import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity {
    private final static String TAG = "BookListActivity";
    private DbHelper mDbHelper = null;
    private BookAdapter mAdapter = null;
    RecyclerView mListView;

    CheckBox chk_all;
    Button btn_study;
    Button btn_delete;
    Button btn_import;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FloatingActionButton mFab;

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

        mFab = (FloatingActionButton)findViewById(R.id.actionButton);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Snackbar.make(mListView , "import from google", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(BookListActivity.this, SheetClientActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = DbHelper.getInstance(this);
        ArrayList<BookAdapter.Book> mBookList = mDbHelper.getBookList();
        mAdapter = new BookAdapter(BookListActivity.this, R.layout.book_list, mBookList);

        mListView = (RecyclerView)findViewById(R.id.list_book);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mAdapter);
        Log.i(TAG, "setAdapter");

        /*
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookAdapter.Book book = mAdapter.getItem(position);
                String title = book.getTitle();

                mDbHelper.setCurrentWordId(0);

                Intent intent = new Intent(BookListActivity.this, WordListActivity.class);
                intent.putExtra("book", title);
                startActivity(intent);
            }
        });
        */

        /*
        chk_all = (CheckBox)findViewById(R.id.chk_all);
        chk_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAdapter.checkAll(b);
                mAdapter.notifyDataSetChanged();
            }
        });

        btn_study = (Button)findViewById(R.id.btn_study);
        btn_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookListActivity.this, SequenceTestActivity.class);
                startActivity(intent);
            }
        });

        btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> checkedBooks = mAdapter.getCheckedBook();
                if (checkedBooks.size() > 0) {
                    mDbHelper.deleteWords(checkedBooks);
                    mAdapter.removeCheckedItem();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        btn_import = (Button)findViewById(R.id.btn_import);
        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookListActivity.this, SheetClientActivity.class);
                startActivity(intent);
            }
        });
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
