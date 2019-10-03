package com.bfc.foregroundservicedemo;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import static com.bfc.foregroundservicedemo.App.CHANNEL_ID;

//
// Created by TRUONGDUCBINH on 2019-10-02.
//
public class ExampleService extends Service {

    private static final String TAG = "ExampleService";
    private String mInput;
    private int mCounter = 0;
    private Handler mServiceHandler;
    private PowerManager.WakeLock mScreenWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mScreenWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "foregroundservicedemo:mywakelock");
        mScreenWakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mInput = intent.getStringExtra("inputExtra");

        Notification notification = getNotification();

        startForeground(1, notification);

        increment(60000);

        return START_STICKY;
    }

    private Notification getNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText(mInput + ", " + mCounter)
                .setSmallIcon(R.drawable.ic_android)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    private void increment(final int duration) {
        mCounter++;
        Notification notification = getNotification();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, notification);

        mServiceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                increment(duration);
            }
        }, duration);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScreenWakeLock != null) mScreenWakeLock.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
