package com.shimnssso.wordsmaster;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import com.shimnssso.wordsmaster.data.DbHelper;
import com.shimnssso.wordsmaster.wordStudy.WordListActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class ForegroundService extends Service implements MediaPlayer.OnCompletionListener, TextToSpeech.OnInitListener {
    private static final String TAG = "ForegroundService";

    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAY = 1;
    private static final int STATE_TTS = 2;
    private static final int STATE_PLAY_ALL = 3;


    private int mCurState = STATE_IDLE;
    private MediaPlayer mPlayer = null;
    private TextToSpeech mTts = null;
    private HashMap<String, String> mParams = null;

    Cursor mCursor = null;

    Handler mHandler = new Handler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == null) {
            return START_REDELIVER_INTENT;
        }
        switch (action) {
            case Constants.Action.TTS: {
                if (mCurState == STATE_PLAY_ALL && mCursor != null) {
                    stopPlay();
                    int position = intent.getIntExtra("position", -1);
                    if (position != -1) {
                        mCursor.moveToPosition(position);
                        String spelling = mCursor.getString(1);
                        playOrTts(spelling);
                    }
                }
                else {
                    if (mCurState != STATE_IDLE) stopPlay();
                    String spelling = intent.getStringExtra("spelling");
                    Log.d(TAG, "action: " + action + ", spelling: " + spelling);

                    mCurState = STATE_TTS;
                    mTts.speak(spelling, TextToSpeech.QUEUE_FLUSH, mParams);
                }
                break;
            }
            case Constants.Action.PLAY: {
                if (mCurState == STATE_PLAY_ALL && mCursor != null) {
                    stopPlay();
                    int position = intent.getIntExtra("position", -1);
                    if (position != -1) {
                        mCursor.moveToPosition(position);
                        String spelling = mCursor.getString(1);
                        playOrTts(spelling);
                    }
                }
                else {
                    if (mCurState != STATE_IDLE) stopPlay();
                    String spelling = intent.getStringExtra("spelling");
                    Log.d(TAG, "action: " + action + ", spelling: " + spelling);

                    mCurState = STATE_PLAY;
                    play(getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3");
                }
                break;
            }
            case Constants.Action.PLAY_ALL: {
                if (mCurState != STATE_IDLE) stopPlay();
                String book = intent.getStringExtra("book");
                boolean starred = intent.getBooleanExtra("starred", false);
                int position = intent.getIntExtra("position", 0);
                Log.d(TAG, "book: " + book + ", starred: " + starred + ", position: " + position);

                if (starred)
                    mCursor = DbHelper.getInstance(this).getStarredWordList(book);
                else
                    mCursor = DbHelper.getInstance(this).getWordList(book);

                mCurState = STATE_PLAY_ALL;

                mCursor.moveToPosition(position);
                String spelling = mCursor.getString(1);
                playOrTts(spelling);
                break;
            }

            case Constants.Action.STOP: {
                mCurState = STATE_IDLE;
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }
                break;
            }
            default:
                Log.e(TAG, "unexpected action. " + action);
                break;
        }
        /*
        else if (commandType == Constants.CommandType.FOREGROUND_START) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Intent previousIntent = new Intent(this, ForegroundService.class);
            previousIntent.setAction(Constants.ACTION.PREV_ACTION);
            PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                    previousIntent, 0);

            Intent playIntent = new Intent(this, ForegroundService.class);
            playIntent.setAction(Constants.ACTION.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                    playIntent, 0);

            Intent nextIntent = new Intent(this, ForegroundService.class);
            nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
            PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                    nextIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.truiton_short);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Truiton Music Player")
                    .setTicker("Truiton Music Player")
                    .setContentText("My Music")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_previous,
                            "Previous", ppreviousIntent)
                    .addAction(android.R.drawable.ic_media_play, "Play",
                            pplayIntent)
                    .addAction(android.R.drawable.ic_media_next, "Next",
                            pnextIntent).build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);
        } else if (commandType == Constants.CommandType.FOREGROUND_STOP) {
            Constants.ACTION.STOPFOREGROUND_ACTION)) {
                Log.i(LOG_TAG, "Received Stop Foreground Intent");
                stopForeground(true);
                stopSelf();
        }
        */
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);

        mTts = new TextToSpeech(getApplicationContext(), this);
        mParams = new HashMap<>();
        mParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "test");
    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
            mTts = null;
        }

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion. " + mp);
        mPlayer.release();

        if (mCurState == STATE_PLAY_ALL) {
            if (mCursor.moveToNext()) {

                Message m = mHandler.obtainMessage(WordListActivity.MSG_PLAY_DONE, mCursor.getPosition(), 0);
                WordListActivity.mHandler.sendMessage(m);

                final String spelling = mCursor.getString(1);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playOrTts(spelling);
                    }
                }, 1000);
            }
            else {
                WordListActivity.mHandler.sendEmptyMessage(WordListActivity.MSG_PLAY_ALL_FINISHED);

                mCursor.close();
                mCursor = null;
                mCurState = STATE_IDLE;
            }
        }
        else {
            mCurState = STATE_IDLE;
        }
    }

    @Override
    public void onInit(int status) {
        Log.d(TAG, "onInit");

        if (status == TextToSpeech.SUCCESS)
        {
            int result = mTts.setLanguage(Locale.CHINESE);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Toast.makeText(getApplicationContext(), "This Language is not supported", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Ready to Speak", Toast.LENGTH_LONG).show();
                mTts.setOnUtteranceProgressListener(mListener);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "TTS is not available", Toast.LENGTH_LONG).show();
        }
    }

    private UtteranceProgressListener mListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            Log.d(TAG, "TTS onDone. " + utteranceId);
            if (mCurState == STATE_PLAY_ALL) {
                Log.d(TAG, "mCurState == STATE_PLAY_ALL. ");
                if (mCursor.moveToNext()) {

                    Message m = mHandler.obtainMessage(WordListActivity.MSG_PLAY_DONE, mCursor.getPosition(), 0);
                    WordListActivity.mHandler.sendMessage(m);

                    final String spelling = mCursor.getString(1);
                    Log.d(TAG, "spelling: " + spelling);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            playOrTts(spelling);
                        }
                    }, 1000);
                }
                else {
                    WordListActivity.mHandler.sendEmptyMessage(WordListActivity.MSG_PLAY_ALL_FINISHED);

                    mCurState = STATE_IDLE;
                    mCursor.close();
                    mCursor = null;
                }
            }
            else {
                mCurState = STATE_IDLE;
            }
        }

        @Override
        public void onError(String utteranceId) {
            Log.d(TAG, "TTS onError. " + utteranceId);
        }
    };


    public void stopPlay() {
        if (mCurState == STATE_PLAY) {
            mPlayer.stop();
            mPlayer.release();
        } else if (mCurState == STATE_TTS) {
            mTts.stop();
        }
        mCurState = STATE_IDLE;
    }

    public boolean play(String path) {
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(path);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void playOrTts(String spelling) {
        Log.d(TAG, "playOrTts. spelling: " + spelling);
        try {
            FileInputStream fis = new FileInputStream (new File(getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3"));
            fis.close();

            play(spelling);
        } catch (FileNotFoundException e) {

            mTts.speak(spelling, TextToSpeech.QUEUE_FLUSH, mParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
