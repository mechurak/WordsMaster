package com.shimnssso.wordsmaster.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import com.shimnssso.wordsmaster.wordStudy.WordListActivity;

import java.io.IOException;

public class AudioHelper {
    private final static String TAG = "AudioHelper";
    private static MediaRecorder mRecorder = null;
    private static MediaPlayer mPlayer = null;

    public static boolean startRecord(String path) {
        mRecorder = new MediaRecorder();
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mRecorder.setOutputFile(path);

            mRecorder.prepare();
            mRecorder.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean stopRecord() {
        if (mRecorder == null) {
            Log.e(TAG, "mRecorder is null");
            return false;
        }
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        return true;
    }


    public static boolean play(String path) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(path);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "onCompletion. " + mp);
                    mPlayer.release();
                    mPlayer = null;
                    WordListActivity.mHandler.sendEmptyMessageDelayed(WordListActivity.MSG_PLAY_DONE, 500);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
