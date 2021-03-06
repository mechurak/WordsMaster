package com.shimnssso.wordsmaster.wordTest;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shimnssso.wordsmaster.R;

public class OrderTestActivity extends AppCompatActivity{
    private final static String TAG = "OrderTestActivity";
    private WordBlockAdapter mAdapter = null;
    RecyclerView mMixedRecyclerView;
    MySpanSizeLookUp mySpanSizeLookUp;
    GridLayoutManager layoutManager;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private String mSpelling;
    private String mPhonetic;
    private String mMeaning;

    TextView txt_spelling;
    TextView txt_phonetic;
    TextView txt_meaning;

    CheckBox chk_spelling;
    CheckBox chk_phonetic;
    CheckBox chk_meaning;

    private boolean mSpellingMode = false;
    private boolean mPhoneticAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.order_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_order_test);
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
                finish();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Toast.makeText(OrderTestActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        Intent intent = getIntent();
        mSpelling = intent.getStringExtra("spelling");
        mPhonetic = intent.getStringExtra("phonetic");
        mMeaning = intent.getStringExtra("meaning");
        Log.i(TAG, "word: " + mSpelling);

        if (mPhonetic == null || mPhonetic.length() < 2) {
            mAdapter = new WordBlockAdapter(OrderTestActivity.this, mSpelling, false);
            mSpellingMode = true;
            mPhoneticAvailable = false;
            invalidateOptionsMenu();
        }
        else {
            mAdapter = new WordBlockAdapter(OrderTestActivity.this, mPhonetic, false);
            mSpellingMode = false;
            mPhoneticAvailable = true;
        }

        mMixedRecyclerView = (RecyclerView)findViewById(R.id.list_mixed);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
                    @Override
                    public boolean onMove(
                            final RecyclerView recyclerView,
                            final RecyclerView.ViewHolder viewHolder,
                            final RecyclerView.ViewHolder target) {
                        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(
                            final RecyclerView.ViewHolder viewHolder,
                            final int swipeDir) {
                    }

                    @Override
                    public boolean isLongPressDragEnabled() {
                        return true;
                    }
                };

        ItemTouchHelper newTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        newTouchHelper.attachToRecyclerView(mMixedRecyclerView);


        layoutManager = new GridLayoutManager(this, 100, LinearLayoutManager.VERTICAL, false);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        mySpanSizeLookUp = new MySpanSizeLookUp(mAdapter, 100, deviceWidth);
        layoutManager.setSpanSizeLookup(mySpanSizeLookUp);
        mMixedRecyclerView.setLayoutManager(layoutManager);
        mMixedRecyclerView.setAdapter(mAdapter);

        Log.i(TAG, "setAdapter");

        txt_spelling = (TextView)findViewById(R.id.txt_spelling);
        txt_spelling.setText(mSpelling);
        txt_phonetic = (TextView)findViewById(R.id.txt_phonetic);
        txt_phonetic.setText(mPhonetic);
        txt_meaning = (TextView)findViewById(R.id.txt_meaning);
        txt_meaning.setText(mMeaning);

        chk_spelling = (CheckBox) findViewById(R.id.chk_word_spelling);
        chk_spelling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    txt_spelling.setVisibility(View.VISIBLE);
                else
                    txt_spelling.setVisibility(View.GONE);
            }
        });
        chk_phonetic = (CheckBox) findViewById(R.id.chk_word_phonetic);
        chk_phonetic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    txt_phonetic.setVisibility(View.VISIBLE);
                else
                    txt_phonetic.setVisibility(View.GONE);
            }
        });
        chk_meaning = (CheckBox)findViewById(R.id.chk_word_meaning);
        chk_meaning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    txt_meaning.setVisibility(View.VISIBLE);
                else
                    txt_meaning.setVisibility(View.GONE);
            }
        });

        chk_spelling.setChecked(false);
        chk_phonetic.setChecked(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_order, menu);
        if (!mPhoneticAvailable) {
            menu.removeItem(R.id.action_order_type);
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
        Log.d(TAG, "onOptionsItemSelected. id " + id);

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_order_type:
                if (mSpellingMode) {
                    if (mPhonetic == null || mPhonetic.length()<2) {
                        Log.e(TAG, "ignore unset mSpellingMode");
                        return true;
                    }
                    item.setTitle(R.string.action_order_type_spelling);
                    mAdapter = new WordBlockAdapter(OrderTestActivity.this, mPhonetic, false);
                    mSpellingMode = false;
                }
                else {
                    item.setTitle(R.string.action_order_type_phonetic);

                    boolean isChiness = false;
                    for(int i = 0 ; i < mSpelling.length() ; i++) {
                        char ch = mSpelling.charAt(i);
                        Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(ch);

                        if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(unicodeBlock) ||
                                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A.equals(unicodeBlock) ||
                                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B.equals(unicodeBlock) ||
                                Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS.equals(unicodeBlock) ||
                                Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT.equals(unicodeBlock)) {
                            isChiness = true;
                            break;
                        }
                    }
                    Log.d(TAG, "isChiness: " + isChiness + ". " + mSpelling);

                    mAdapter = new WordBlockAdapter(OrderTestActivity.this, mSpelling, isChiness);
                    mSpellingMode = true;
                }

                layoutManager = new GridLayoutManager(this, 100, LinearLayoutManager.VERTICAL, false);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int deviceWidth = displayMetrics.widthPixels;
                mySpanSizeLookUp = new MySpanSizeLookUp(mAdapter, 100, deviceWidth);
                layoutManager.setSpanSizeLookup(mySpanSizeLookUp);
                mMixedRecyclerView.setLayoutManager(layoutManager);
                mMixedRecyclerView.setAdapter(mAdapter);
                return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
