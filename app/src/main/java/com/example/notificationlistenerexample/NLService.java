package com.example.notificationlistenerexample;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NLService extends NotificationListenerService {

    private NLServiceReceiver nlServiceReceiver;
    private String INTERNAL_RECEIVER_ACTION = "com.example.notificationlistenerexample.INTERNAL_MESSAGE";
    public final static String TAG = "DETECT NOTIFICATION";

    @Override
    public void onCreate() {
        super.onCreate();
        nlServiceReceiver = new NLServiceReceiver();
        Log.d(TAG,"NLService");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTERNAL_RECEIVER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(nlServiceReceiver,new IntentFilter(INTERNAL_RECEIVER_ACTION));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.d(TAG,"**********  onNotificationPosted");
        Log.d(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        Intent i = new  Intent(INTERNAL_RECEIVER_ACTION);
        i.putExtra("notification_event","onNotificationPosted :"
                + " " + sbn.getPackageName()
                + " " + sbn.getNotification().tickerText + "\n");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d(TAG,"********** onNotificationRemoved");
        Log.d(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
        Intent i = new  Intent(INTERNAL_RECEIVER_ACTION);
        i.putExtra("notification_event","onNotificationRemoved : "
                + sbn.getPackageName() + "\n");
        sendBroadcast(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlServiceReceiver);
    }



    class NLServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent i1 = new  Intent(INTERNAL_RECEIVER_ACTION);
            i1.putExtra("notification_event","=====================");
            sendBroadcast(i1);
            int i=1;
            for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                Intent i2 = new  Intent(INTERNAL_RECEIVER_ACTION);
                i2.putExtra("notification_event",i +" "
                        + sbn.getPackageName() + "\n");
                sendBroadcast(i2);
                i++;
            }
            Intent i3 = new  Intent(INTERNAL_RECEIVER_ACTION);
            i3.putExtra("notification_event","===== Notification List ====");
            sendBroadcast(i3);
        }
    }
}
