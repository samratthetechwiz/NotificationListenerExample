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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    String[] timeWords = {"AM","PM","am","pm","o'clock","Am","Pm","minutes"};
    String regex = "(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)";

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

        /*Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);

        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver, new IntentFilter(INTERNAL_RECEIVER_ACTION));

        intent = new Intent(MainActivity.this,
                NLService.class);
        startService(intent);*/

        sendNotiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showNotification("Hello Everyone");
            }
        });

        processNotiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = notificationTextView.getText().toString();
                String randomText = randomString.getText().toString();
                notificationTextView.setText(msg + "\n" + randomText + "\n");

                StringTokenizer stringTokenizer = new StringTokenizer(randomText);
                boolean hasNumber = false;
                boolean relatedToTime = false;

                while (stringTokenizer.hasMoreTokens()) {
                    Log.d(TAG, "Checking Tokens");
                    String temp = stringTokenizer.nextToken();
                    boolean trackCase1 = temp.matches(regex);
                    if(trackCase1){
                        hasNumber = true;
                        int matches[] = printMatches(randomText,regex);
                        if(matches[1] > 0) {
                            String checkForTime = randomText.substring(matches[1]);
                            boolean trackCase2 = stringContainsItemFromList(checkForTime, timeWords);
                            if (trackCase2)
                                relatedToTime = true;
                            String checkForColon = randomText.substring(matches[0],matches[1]);
                            if(checkForColon.contains(":") &&
                                    isTimeStampValid(randomText.substring(matches[0],matches[1]))) {
                                relatedToTime = true;
                            }
                        }
                    }
                    Log.d(TAG, String.valueOf(trackCase1));
                }
                if(hasNumber && relatedToTime) {
                    Toast.makeText(getApplicationContext(), "Notification is Trackable", Toast.LENGTH_LONG).show();
                    msg = notificationTextView.getText().toString();
                    notificationTextView.setText(msg + "It is TRACKABLE");
                    Log.d(TAG,"NOTI IS Trackable");
                }else{
                    msg = notificationTextView.getText().toString();
                    notificationTextView.setText(msg + "It is NOT TRACKABLE");
                    Log.d(TAG,"NOTI IS NOT Trackable");
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

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        for(int i =0; i < items.length; i++) {
            if(inputStr.contains(items[i])) {
                return true;
            }
        }
        return false;
    }

    public static int[] printMatches(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        int pos[] = {-999,-999};
        // Check all occurrences
        while (matcher.find()) {
            Log.d(TAG,"Start index: " + matcher.start());
            Log.d(TAG," End index: " + matcher.end());
            Log.d(TAG," Found: " + matcher.group());
            pos[0] = matcher.start();
            pos[1] = matcher.end();
        }
        return pos;
    }

    public static boolean isTimeStampValid(String inputString) {
        int length = inputString.length();
        SimpleDateFormat format;

        if(length < 6) {
            format = new SimpleDateFormat("HH:mm");
            Log.d(TAG,"HH:mm format");
        }
        else {
            format = new SimpleDateFormat("HH:mm:ss");
            Log.d(TAG,"HH:mm:ss format");
        }
        try{
            format.parse(inputString);
            return true;
        }
        catch(ParseException e) {
            return false;
        }
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
            //notificationTextView.setText(temp);
        }
    }
}