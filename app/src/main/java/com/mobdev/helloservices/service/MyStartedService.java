package com.mobdev.helloservices.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mobdev.helloservices.R;

import java.util.Random;

/**
 * Created by Marco Picone picone.m@gmail.com on 02,May,2020
 * Mobile System Development - University Course
 */
public class MyStartedService extends Service {

    private static final String TAG = "MyStartedService";

    public static final String NEW_VALUE_INTENT_ACTION = "service_new_value";

    public static final String INTENT_VALUE_EXTRA = "value";

    private int ONGOING_NOTIFICATION = 1111;

    private int currentValue = 0;

    private boolean isRunning = false;

    private Thread myThread = null;

    private long SLEEP_TIME_MS = 1000;

    private Random random = new Random();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(MyStartedService.TAG, "MyStartedService ---> onCreate()");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(MyStartedService.TAG, "MyStartedService ---> onDestroy()");
        super.onDestroy();
        isRunning = false;
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MyStartedService.TAG, "MyStartedService ---> onStartCommand()");
        startServiceTask();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(MyStartedService.TAG, "MyStartedService ---> onUnbind()");
        return super.onUnbind(intent);
    }

    public void startServiceTask() {

        Log.d(MyStartedService.TAG, "MyStartedService ---> Starting ...");

        isRunning = true;

        //Show the notification and set the service as foreground to prevent it to be destroyed by the OS
        setServiceAsForeground();

        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning){
                    try {
                        Thread.sleep(SLEEP_TIME_MS);
                        currentValue = random.nextInt();
                        notifyValueUpdate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        myThread.start();
    }

    private void setServiceAsForeground()
    {
        Log.d(MyStartedService.TAG, "MyStartedService ---> setServiceAsForeground()");

        // Prepare the intent triggered if the notification is selected
        Intent intent = new Intent(this, MyStartedService.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        // Build the notification
        // Use NotificationCompat.Builder instead of just Notification.Builder to support older Android versions
        Notification notification  = builder.setContentTitle("My Started Service")
                .setContentText("Running ...")
                .setSmallIcon(R.drawable.icon_notification_exclamation_mark)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        //Start the service as foreground
        startForeground(ONGOING_NOTIFICATION, notification);
    }

    private void notifyValueUpdate() {

        Log.d(TAG, "MyStartedService ---> notifyValueUpdate()");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NEW_VALUE_INTENT_ACTION);

        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_VALUE_EXTRA, currentValue);
        broadcastIntent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}
