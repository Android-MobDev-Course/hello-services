package com.mobdev.helloservices.service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Random;

/**
 * Created by Marco Picone picone.m@gmail.com on 02,May,2020
 * Mobile System Development - University Course
 */
public class MyJobIntentService extends JobIntentService {

    public static final String RANDOM_LIMIT_EXTRA = "random_limit_extra";

    private static final String TAG = "MyJobIntentService";

    public static final String NEW_VALUE_INTENT_ACTION = "intent_service_new_value";

    public static final String INTENT_VALUE_EXTRA = "value";

    private int currentValue = 0;

    private Thread myThread = null;

    private long SLEEP_TIME_MS = 1000;

    private Random random = new Random();

    private void notifyValueUpdate() {

        Log.d(TAG, "MyJobIntentService ---> notifyValueUpdate()");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NEW_VALUE_INTENT_ACTION);

        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_VALUE_EXTRA, currentValue);
        broadcastIntent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Log.d(TAG, "MyJobIntentService ---> onHandleWork()");

        try {

            Thread.sleep(SLEEP_TIME_MS);

            if(intent.getExtras() != null && intent.getExtras().containsKey(RANDOM_LIMIT_EXTRA))
                currentValue = random.nextInt(intent.getExtras().getInt(RANDOM_LIMIT_EXTRA));
            else
                currentValue = random.nextInt();

            notifyValueUpdate();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
