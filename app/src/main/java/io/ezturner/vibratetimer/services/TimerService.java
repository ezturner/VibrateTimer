package io.ezturner.vibratetimer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import io.ezturner.vibratetimer.R;
import io.ezturner.vibratetimer.activities.TimerActivity;

/**
 * This service is maintained in the background while the application is enabled
 * and listens for volume changes, modifying the system volume as needed.
 *
 * This class utilizes the Singleton pattern.
 * Created by Ethan on 3/10/2016.
 */
public class TimerService extends Service {

    private int NOTIFICATION_ID = 2784782;

    private static TimerService instance;
    private boolean timerRunning = false, isForeground = false;

    public static TimerService getInstance(){
        return instance;
    }

    private static final String LOG_TAG = TimerService.class.getSimpleName();

    private IBinder binder;

    private int timerLength = 0;

    private long startTime;

    private Handler timerHandler;

    /**
     * Creates the service, and begins listening for volume changes if we are currently limiting volume.
     */
    @Override
    public void onCreate(){
        Log.d(LOG_TAG, "TimerService started");
        instance = this;

        timerHandler = new Handler();
    }

    @Override
    @Nullable
    public IBinder onBind(Intent arg0){
        return binder;
    }

    public void cancelTimer() {
        stopForeground();
        timerHandler.removeCallbacksAndMessages(null);
        timerRunning = false;
    }

    public class VolumeServiceBinder extends Binder {
        /**
         * Returns the instance of this service for a client to make method calls on it.
         * @return the instance of this service.
         */
        public TimerService getService() {
            return TimerService.this;
        }
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "TimerService destroyed");
        instance = null;
    }

    private void startForeground(){
        if(!isForeground) {
            Intent notificationIntent = new Intent(this, TimerActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_ic_timer_black_48dp)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.notification_message))
                    .setContentIntent(pendingIntent).build();

            startForeground(NOTIFICATION_ID, notification);
            isForeground = true;
        }
    }

    private void stopForeground(){
        stopForeground(true);
        isForeground = false;
    }

    public void startTimer(int secondsLength){
        if (secondsLength < 5)
            secondsLength = 5;

        startForeground();
        timerRunning = true;

        startTime = System.currentTimeMillis();
        this.timerLength = secondsLength;

        timerHandler.postDelayed(endTimer, secondsLength * 1000 );
    }

    private Runnable endTimer = new Runnable() {
        @Override
        public void run() {
            vibrate();
        }
    };



    private void vibrate(){

    }

    public int getTimerLength(){
        return timerLength;
    }

    public long getMillisecondsLeft(){
        long msElapsed = System.currentTimeMillis() - startTime;
        return (timerLength * 1000) - msElapsed;
    }

    public boolean isTimerRunning(){
        return timerRunning;
    }
}
