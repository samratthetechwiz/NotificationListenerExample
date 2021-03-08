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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String INTERNAL_RECEIVER_ACTION = "com.example.notificationlistenerexample.INTERNAL_MESSAGE";
    public final static String TAG = "DETECT NOTIFICATION";
    private NotificationReceiver notificationReceiver;
    private TextView notificationTextView;
    private TextView randomString;
    private Button sendNotiButton;
    private Button processNotiButton;
    public NotiClassify notiClassify;
    public Properties prop = null;
    public Set<Object> keys = null;
    public InputStream is = null;
    public HashMap<String, String> categories = new HashMap<String, String>();
    public String category = null;

    private boolean runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationReceiver = new NotificationReceiver();

        notificationTextView = (TextView)findViewById(R.id.notificationTextView);
        notificationTextView.setText("");

        randomString = (EditText)findViewById(R.id.editTextRandomString);

        sendNotiButton = findViewById(R.id.sendNotiButton);
        processNotiButton = findViewById(R.id.processEditTextButton);

        try {
            is = getBaseContext().getAssets().open("category.properties");
            keys = getPropAllKeys();
            for(Object k:keys){
                String key = (String)k;
                String property = prop.getProperty(key);
                categories.put(key, property);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);

        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver, new IntentFilter(INTERNAL_RECEIVER_ACTION));

        intent = new Intent(MainActivity.this,
                NLService.class);
        startService(intent);*/

        /*sendNotiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showNotification("Hello Everyone");
            }
        });*/

        processNotiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = notificationTextView.getText().toString();
                String randomText = randomString.getText().toString();
                notificationTextView.setText(msg + "\n" + randomText + "\n");

                notiClassify = new NotiClassify(randomText, categories);
                boolean trackable = notiClassify.checkTraceable();

                if(trackable){
                    Toast.makeText(getApplicationContext(), "Notification is Trackable", Toast.LENGTH_LONG).show();
                    msg = notificationTextView.getText().toString();
                    notificationTextView.setText(msg + "It is TRACKABLE" + "\n");
                    category = notiClassify.getCategory();
                    if(category != null) {
                        msg = notificationTextView.getText().toString();
                        notificationTextView.setText(msg + "Category : " + category + "\n");
                        category = null;
                    }
                }else{
                    msg = notificationTextView.getText().toString();
                    notificationTextView.setText(msg + "It is NOT TRACKABLE" + "\n");
                }
            }
        });
        Log.d(TAG,"OnCreate Main Activity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(notificationReceiver);
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

    public Set<Object> getPropAllKeys(){
        Set<Object> keys = null;
        try {
            this.prop = new Properties();
            prop.load(is);
            keys = prop.keySet();
            return keys;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keys;
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Receiving");
            String temp = intent.getStringExtra("notification_event")
                    + "\n";
            Log.d(TAG,"NLR : " + temp);
            //notificationTextView.setText(temp);
        }
    }
}