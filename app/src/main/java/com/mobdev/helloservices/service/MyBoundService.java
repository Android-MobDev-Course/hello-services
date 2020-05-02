package com.mobdev.helloservices.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

/**
 * Created by Marco Picone picone.m@gmail.com on 02,May,2020
 * Mobile System Development - University Course
 */
public class MyBoundService extends Service {

    private static final String TAG = "MyBoundService";

    //Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private int currentValue = 0;

    private boolean isRunning = false;

    private Thread myThread = null;

    private long SLEEP_TIME_MS = 1000;

    private Random random = new Random();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MyBoundService getService() {
            return MyBoundService.this;
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(MyBoundService.TAG,"MyBoundService ---> onBind()");
        startServiceTask();
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(MyBoundService.TAG,"MyBoundService ---> onCreate()");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(MyBoundService.TAG,"MyBoundService ---> onDestroy()");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MyBoundService.TAG,"MyBoundService ---> onStartCommand()");
        startServiceTask();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(MyBoundService.TAG,"MyBoundService ---> onUnbind()");
        return super.onUnbind(intent);
    }

    public void startServiceTask() {

        Log.d(MyBoundService.TAG, "MyBoundService ---> Starting ...");

        isRunning = true;

        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning){
                    try {
                        Thread.sleep(SLEEP_TIME_MS);
                        currentValue = random.nextInt();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        myThread.start();
    }

    public int getCurrentValue() {
        Log.d(MyBoundService.TAG,"MyBoundService ---> getCurrentValue()");
        return currentValue;
    }
}
