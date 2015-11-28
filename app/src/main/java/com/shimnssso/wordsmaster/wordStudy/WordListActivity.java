package com.shimnssso.wordsmaster.wordStudy;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.shimnssso.wordsmaster.ForegroundService;
import com.shimnssso.wordsmaster.R;
import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.util.TTSHelper;
import com.shimnssso.wordsmaster.wordTest.OrderTestActivity;

public class WordListActivity extends AppCompatActivity {
    private final static String TAG = "WordListActivity";
    public final static int WORD_LIST_FRAGMENT = 0;
    public final static int WORD_CARD_FRAGMENT = 1;

    public final static int TYPE_SPELLING = 100;
    public final static int TYPE_PHONETIC = 101;
    public final static int TYPE_MEANING = 102;

    public final static int MSG_PLAY_DONE = 1000;
    public final static int MSG_PLAY_READY = 1001;

    public static Handler mHandler = null;
    private DbHelper mDbHelper = null;
    private Fragment mCurrentFragment = null;
    Cursor cursor = null;
    private String mBookTitle = null;

    WordAdapter mAdapter = null;
    public WordAdapter getAdapter() {
        return mAdapter;
    }

    CheckBox chk_word_spelling;
    CheckBox chk_word_phonetic;
    CheckBox chk_word_meaning;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    int mCurrentFragmentIndex = WORD_LIST_FRAGMENT;
    private boolean mStarredMode = false;
    private boolean mPlayMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.word_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_word_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.title_word_list,
                R.string.title_word_list);


        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // event when click home button
                Log.e(TAG, "onClick");
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                getSupportActionBar().setTitle(R.string.title_word_list);
                invalidateOptionsMenu();

                replaceFragment(WORD_LIST_FRAGMENT);
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Toast.makeText(WordListActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        Intent intent = getIntent();
        mBookTitle = intent.getStringExtra("book");
        Log.i(TAG, "bookTitle: " + mBookTitle);

        mDbHelper = DbHelper.getInstance();
        cursor = mDbHelper.getWordList(mBookTitle);
        //cursor = mDbHelper.getStarredWordList(bookTitle);
        mAdapter = new WordAdapter(this, cursor);

        /*
        chk_all = (CheckBox)findViewById(R.id.chk_all);
        chk_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAdapter.checkAll(b);
                mAdapter.notifyDataSetInvalidated();
            }
        });
        */

        chk_word_spelling = (CheckBox) findViewById(R.id.chk_word_spelling);
        chk_word_spelling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WordInterface temp = (WordInterface)mCurrentFragment;
                temp.setVisible(TYPE_SPELLING, isChecked);
            }
        });
        chk_word_phonetic = (CheckBox) findViewById(R.id.chk_word_phonetic);
        chk_word_phonetic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WordInterface temp = (WordInterface)mCurrentFragment;
                temp.setVisible(TYPE_PHONETIC, isChecked);
            }
        });
        chk_word_meaning = (CheckBox)findViewById(R.id.chk_word_meaning);
        chk_word_meaning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WordInterface temp = (WordInterface) mCurrentFragment;
                temp.setVisible(TYPE_MEANING, isChecked);
            }
        });

        replaceFragment(mCurrentFragmentIndex);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                WordInterface temp = (WordInterface)mCurrentFragment;

                switch (msg.what) {
                    case MSG_PLAY_DONE:
                        Log.d(TAG, "received MSG_PLAY_DONE");
                        if (!mPlayMode) break;

                        int nextPosition = mAdapter.getCurrentId()+1;
                        if (nextPosition >= mAdapter.getItemCount()) nextPosition = 0;
                        temp.moveTo(nextPosition);
                        Message m = mHandler.obtainMessage(MSG_PLAY_READY, nextPosition, 0);
                        mHandler.sendMessageDelayed(m, 50);
                        break;

                    case MSG_PLAY_READY:
                        Log.d(TAG, "received MSG_PLAY_READY. position: " + msg.arg1);
                        if (!mPlayMode) break;

                        temp.play(msg.arg1);
                        break;

                    default:
                        break;
                }
            }
        };

        // start ForegroundService
        Intent startIntent = new Intent(this, ForegroundService.class);
        startService(startIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        int currentId = mDbHelper.getCurrentWordId();
        mAdapter.setCurrentId(currentId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        mDbHelper.setCurrentWordId(mAdapter.getCurrentId());
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        mDbHelper.close();
        Intent stopIntent = new Intent(this, ForegroundService.class);
        stopService(stopIntent);

        super.onDestroy();
    }


    public void replaceFragment(int newFragmentIndex) {
        Log.d(TAG, "replaceFragment " + newFragmentIndex);
        Fragment newFragment = getFragment(newFragmentIndex);

        // replace fragment
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.word_fragment, newFragment);

        // Commit the transaction
        transaction.commit();
        mCurrentFragmentIndex = newFragmentIndex;
        mCurrentFragment = newFragment;
    }

    private Fragment getFragment(int index) {
        Fragment newFragment = null;

        switch (index) {
            case WORD_LIST_FRAGMENT:
                newFragment = new WordListFragment();
                break;
            case WORD_CARD_FRAGMENT:
                newFragment = new WordCardFragment();
                break;
            default:
                Log.e(TAG, "unexpected index " + index);
                break;
        }
        return newFragment;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "back button is pressed");
        if (mCurrentFragmentIndex == WORD_CARD_FRAGMENT) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            getSupportActionBar().setTitle(R.string.title_word_list);
            invalidateOptionsMenu();

            replaceFragment(WORD_LIST_FRAGMENT);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_list, menu);
        if (mCurrentFragmentIndex==WORD_LIST_FRAGMENT) {

        }
        else {
            menu.removeItem(R.id.action_starred);
            menu.removeItem(R.id.action_card);
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

            case R.id.action_card:
                mDrawerToggle.setDrawerIndicatorEnabled(false);
                getSupportActionBar().setTitle(R.string.title_word_card);
                invalidateOptionsMenu();

                replaceFragment(WORD_CARD_FRAGMENT);

                return true;

            case R.id.action_play:
                if (mPlayMode) {
                    item.setTitle(R.string.action_play);
                    mPlayMode = false;

                }
                else {
                    item.setTitle(R.string.action_stop);
                    mPlayMode = true;

                    WordInterface temp = (WordInterface)mCurrentFragment;
                    temp.play(mAdapter.getCurrentId());
                }
                return true;

            case R.id.action_starred:
                if (mStarredMode) {
                    item.setTitle(R.string.action_starred_only);
                    cursor.close();
                    cursor = mDbHelper.getWordList(mBookTitle);
                    mAdapter = new WordAdapter(this, cursor);
                    mStarredMode = false;
                }
                else {
                    item.setTitle(R.string.action_starred_all);
                    cursor.close();
                    cursor = mDbHelper.getStarredWordList(mBookTitle);
                    mAdapter = new WordAdapter(this, cursor);
                    mStarredMode = true;
                }
                replaceFragment(mCurrentFragmentIndex);
                return true;

            case R.id.action_order_test:
                Intent intent = new Intent(getApplicationContext(), OrderTestActivity.class);
                Cursor c = (Cursor)mAdapter.getItem();
                intent.putExtra("word", c.getString(2));
                startActivity(intent);
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


    public interface WordInterface {
        void setVisible(int type, boolean visible);
        void moveTo(int position);
        void play(int position);
    }
}
