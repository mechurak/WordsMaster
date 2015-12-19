package com.shimnssso.wordsmaster.util;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class AudioHelper {
    private final static String TAG = "AudioHelper";
    private static MediaRecorder mRecorder = null;

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
}
