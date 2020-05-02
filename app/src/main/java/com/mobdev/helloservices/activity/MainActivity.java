package com.mobdev.helloservices.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobdev.helloservices.R;
import com.mobdev.helloservices.service.MyBoundService;
import com.mobdev.helloservices.service.MyJobIntentService;
import com.mobdev.helloservices.service.MyStartedService;

import java.util.Locale;
import java.util.Random;

/**
 * Created by Marco Picone picone.m@gmail.com on 02,May,2020
 * Mobile System Development - University Course
 */
public class MainActivity extends AppCompatActivity {

    private Button startStartedServiceButton = null;

    private Button stopStartedServiceButton = null;

    private TextView startedServiceInfoTextView = null;

    private Button startBoundServiceButton = null;

    private Button stopBoundServiceButton = null;

    private Button readBoundServiceDataButton = null;

    private TextView boundServiceInfoTextView = null;

    private Button scheduleJobIntentServiceWorkButton = null;

    private TextView jobIntentServiceInfoTextView = null;

    private MyServiceBroadcastReceiver myBroadcastReceiver;

    private ServiceConnection myBoundServiceConnection = null;

    private MyBoundService myBoundService = null;

    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mContext = this;

        init();
        registerServiceBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unRegisterServiceBroadcastReceiver();
        stopMyStartedService();

        stopMyBoundService();
    }

    private void init(){

        //STARTED SERVICE UI SECTION

        startStartedServiceButton = (Button)findViewById(R.id.startStartedService);
        stopStartedServiceButton = (Button)findViewById(R.id.stopStartedService);
        startedServiceInfoTextView = (TextView)findViewById(R.id.startedServiceBroadcastReceiverTextView);

        startStartedServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyStartedService();
            }
        });

        stopStartedServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMyStartedService();
            }
        });

        //BOUND SERVICE UI SECTION

        startBoundServiceButton = (Button)findViewById(R.id.startBoundService);
        stopBoundServiceButton = (Button)findViewById(R.id.stopBoundService);
        readBoundServiceDataButton = (Button)findViewById(R.id.boundServiceReadStatusButton);
        boundServiceInfoTextView = (TextView)findViewById(R.id.boundServiceBroadcastReceiverTextView);

        startBoundServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyBoundService();
            }
        });

        stopBoundServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMyBoundService();
            }
        });

        readBoundServiceDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myBoundService != null)
                    updateMyBoundServiceReceivedMessage(myBoundService.getCurrentValue());
                else
                    Toast.makeText(mContext, "Bound Service Not Connected !", Toast.LENGTH_LONG).show();
            }
        });

        //JOB INTENT SERVICE UI SECTION
        scheduleJobIntentServiceWorkButton = (Button)findViewById(R.id.startIntentServiceButton);
        jobIntentServiceInfoTextView = (TextView)findViewById(R.id.intentServiceBroadcastReceiverTextView);

        scheduleJobIntentServiceWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleJobIntentServiceWork();
            }
        });
    }

    private void startMyStartedService(){
        Intent intent = new Intent(this, MyStartedService.class);
        startService(intent);
    }

    private void stopMyStartedService(){
        Intent intent = new Intent(this, MyStartedService.class);
        stopService(intent);
    }

    private void startMyBoundService(){

        if(myBoundServiceConnection == null){

            myBoundServiceConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName className, IBinder service) {
                    // We've bound to LocalService, cast the IBinder and get LocalService instance
                    MyBoundService.LocalBinder binder = (MyBoundService.LocalBinder) service;
                    myBoundService  = binder.getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
                    myBoundService = null;
                }
            };
        }

        //Create the intent and bind to the Service
        Intent intent = new Intent(this, MyBoundService.class);
        bindService(intent, myBoundServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private void stopMyBoundService(){

        //Unbind from the service if it was correctly bound
        if(myBoundService != null) {
            unbindService(myBoundServiceConnection);
            myBoundService = null;
        }
    }

    private void scheduleJobIntentServiceWork(){

        Intent serviceIntent = new Intent();
        serviceIntent.putExtra(MyJobIntentService.RANDOM_LIMIT_EXTRA, 100);

        //jobId A unique job ID for scheduling; must be the same value for all work enqueued for the same class.
        int jobId = 1000;
        MyJobIntentService.enqueueWork(mContext, MyJobIntentService.class, jobId, serviceIntent);

    }

    private void registerServiceBroadcastReceiver(){

        if(myBroadcastReceiver == null)
            this.myBroadcastReceiver = new MyServiceBroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyStartedService.NEW_VALUE_INTENT_ACTION);
        intentFilter.addAction(MyJobIntentService.NEW_VALUE_INTENT_ACTION);

        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver,intentFilter);
    }

    private void unRegisterServiceBroadcastReceiver(){
        if(myBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReceiver);
    }

    private void updateMyStartedServiceReceivedMessage(int value){
        startedServiceInfoTextView.setText(String.format(Locale.ITALY, "Received Value: %d", value));
    }

    private void updateMyBoundServiceReceivedMessage(int value){
        boundServiceInfoTextView.setText(String.format(Locale.ITALY, "Received Value: %d", value));
    }

    private void updateMyJobIntentServiceReceivedMessage(int value){
        jobIntentServiceInfoTextView.setText(String.format(Locale.ITALY, "Received Value: %d", value));
    }

    public class MyServiceBroadcastReceiver extends BroadcastReceiver {

        private static final String TAG = "MyBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            //Intent from MyStartedService
            if(intent != null && intent.getAction().equals(MyStartedService.NEW_VALUE_INTENT_ACTION)
                    && intent.getExtras() != null
                    && !intent.getExtras().isEmpty()
                    && intent.getExtras().containsKey(MyStartedService.INTENT_VALUE_EXTRA))
                updateMyStartedServiceReceivedMessage(intent.getIntExtra(MyStartedService.INTENT_VALUE_EXTRA, 0));
            if(intent != null && intent.getAction().equals(MyJobIntentService.NEW_VALUE_INTENT_ACTION)
                    && intent.getExtras() != null
                    && !intent.getExtras().isEmpty()
                    && intent.getExtras().containsKey(MyJobIntentService.INTENT_VALUE_EXTRA))
                updateMyJobIntentServiceReceivedMessage(intent.getIntExtra(MyJobIntentService.INTENT_VALUE_EXTRA, 0));
            else
                Log.e(TAG, "Wrong Intent Received !");
        }
    }

}
