package com.shimnssso.wordsmaster;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.shimnssso.wordsmaster.util.AudioHelper;
import com.shimnssso.wordsmaster.util.TTSHelper;

import java.io.File;

public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {
            case Constants.Action.TTS: {
                String spelling = intent.getStringExtra("spelling");
                Log.d(TAG, "action: " + action + ", spelling: " + spelling);

                TTSHelper ttsHelper = TTSHelper.getInstance(getApplicationContext());
                if (ttsHelper != null) {
                    ttsHelper.speak(spelling);
                }
                break;
            }
            case Constants.Action.PLAY: {
                String spelling = intent.getStringExtra("spelling");
                Log.d(TAG, "action: " + action + ", spelling: " + spelling);

                AudioHelper.play(getFilesDir().getAbsolutePath() + File.separator + spelling + ".mp3");
                break;
            }
            case Constants.Action.STOP: {
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
