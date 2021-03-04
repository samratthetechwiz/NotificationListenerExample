package com.example.notificationlistenerexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String INTERNAL_RECEIVER_ACTION = "com.example.notificationlistenerexample.INTERNAL_MESSAGE";
    public final static String TAG = "DETECT NOTIFICATION";
    private NotificationReceiver notificationReceiver;
    private TextView notificationTextView;
    private Button sendNotiButton;

    private boolean runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationReceiver = new NotificationReceiver();

        notificationTextView = (TextView)findViewById(R.id.notificationTextView);
        notificationTextView.setMovementMethod(new ScrollingMovementMethod());

        sendNotiButton = findViewById(R.id.sendNotiButton);

        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);

        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver, new IntentFilter(INTERNAL_RECEIVER_ACTION));

        intent = new Intent(MainActivity.this,
                NLService.class);
        startService(intent);

        sendNotiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showNotification("Hello Everyone");
            }
        });
        Log.d(TAG,"OnCreate Main Activity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationReceiver);
    }

    private void showNotification(String message){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"default")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("TEST NOTIFICATION")
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("com.example.notificationlistenerexample");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "com.example.notificationlistenerexample",
                    "Notification Listener Example",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        notificationManager.notify(1,builder.build());
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Receiving");
            String temp = intent.getStringExtra("notification_event")
                    + "\n";
            Log.d(TAG,"NLR : " + temp);
            notificationTextView.setText(temp);
        }
    }
}