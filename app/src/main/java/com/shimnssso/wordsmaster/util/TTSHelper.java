package com.shimnssso.wordsmaster.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class TTSHelper implements TextToSpeech.OnInitListener{
    private static final String TAG = "TTSHelper";
    private static TTSHelper mInstance;
    private Context mContext;

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
    }

    public void destroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
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
            }
        }
        else
        {
            Toast.makeText(mContext, "TTS is not available", Toast.LENGTH_LONG).show();
        }
    }

    public void speak(String spelling) {
        tts.speak(spelling, TextToSpeech.QUEUE_FLUSH, null);
    }
}
