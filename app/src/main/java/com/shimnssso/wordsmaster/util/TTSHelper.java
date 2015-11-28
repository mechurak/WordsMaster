package com.shimnssso.wordsmaster.util;

import android.content.Context;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import com.shimnssso.wordsmaster.wordStudy.WordListActivity;

import java.util.HashMap;
import java.util.Locale;

public class TTSHelper extends UtteranceProgressListener implements TextToSpeech.OnInitListener {
    private static final String TAG = "TTSHelper";
    private static TTSHelper mInstance;
    private Context mContext;
    private HashMap<String, String> mParams;

    TextToSpeech tts;

    public static TTSHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TTSHelper(context);
        }
        return mInstance;
    }

    public TTSHelper(Context context) {
        mContext = context;
        tts = new TextToSpeech(context, this);
        mParams = new HashMap<>();
        mParams.put(Engine.KEY_PARAM_UTTERANCE_ID, "test");
    }

    public void destroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        mInstance = null;
    }

    @Override
    public void onInit(int status) {
        Log.d(TAG, "onInit");

        if (status == TextToSpeech.SUCCESS)
        {
            int result = tts.setLanguage(Locale.CHINESE);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Toast.makeText(mContext, "This Language is not supported", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(mContext, "Ready to Speak", Toast.LENGTH_LONG).show();
                tts.setOnUtteranceProgressListener(this);
            }
        }
        else
        {
            Toast.makeText(mContext, "TTS is not available", Toast.LENGTH_LONG).show();
        }
    }

    public void speak(String spelling) {
        tts.speak(spelling, TextToSpeech.QUEUE_FLUSH, mParams);
    }

    @Override
    public void onStart(String utteranceId) {
        Log.d(TAG, "TTS onStart. " + utteranceId);
    }

    @Override
    public void onDone(String utteranceId) {
        Log.d(TAG, "TTS onDone. " + utteranceId);
        WordListActivity.mHandler.sendEmptyMessageDelayed(WordListActivity.MSG_PLAY_DONE, 500);
    }

    @Override
    public void onError(String utteranceId) {
        Log.d(TAG, "TTS onError. " + utteranceId);
    }
}
