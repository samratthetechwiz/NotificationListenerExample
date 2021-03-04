package com.example.notificationlistenerexample;

import android.util.Log;

import java.util.StringTokenizer;

public class NotiClassify {

    public final static String TAG = "DETECT NOTIFICATION";

    public void checkTraceable(String noti){

        StringTokenizer stringTokenizer = new StringTokenizer(noti);

        while (stringTokenizer.hasMoreTokens()) {
            Log.d(TAG,"Checking Tokens");
            String temp = stringTokenizer.nextToken();
            boolean trace = temp.matches("([0-1]?[0-9]|2[0-3]):[0-5][0-9]");
            Log.d(TAG, String.valueOf(trace));
        }
    }
}
