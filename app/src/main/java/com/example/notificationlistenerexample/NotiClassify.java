package com.example.notificationlistenerexample;

import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotiClassify {

    public final static String TAG = "DETECT NOTIFICATION";
    private String notiText;
    private String[] timeWords = {"AM","PM","am","pm","o'clock","Am","Pm","minutes"};
    private String regex = "(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)";
    private HashMap<String, String> categories = new HashMap<>();

    NotiClassify(String notiText, HashMap<String, String> categories){
        this.notiText = notiText;
        this.categories = categories;
    }


    //To check whether Notification is Trackable based on Time
    public boolean checkTraceable(){
        StringTokenizer stringTokenizer = new StringTokenizer(notiText);
        boolean hasNumber = false;
        boolean relatedToTime = false;
        while (stringTokenizer.hasMoreTokens()) {
            String temp = stringTokenizer.nextToken();
            //Base case to check for numeric digits
            boolean trackCase1 = temp.matches(regex);
            if(trackCase1){
                hasNumber = true;
                //Gets the starting and ending index of Time Related text
                int matches[] = printMatches(notiText,regex);
                if(matches[1] > 0) {
                    String checkForTime = notiText.substring(matches[1]);
                    //Case to check text where time can be identified by keywords succeeding number. Ex - '15 minutes'
                    boolean trackCase2 = stringContainsItemFromList(checkForTime, timeWords);
                    if (trackCase2)
                        relatedToTime = true;
                    //Case to check hh:mm:ss or hh:mm or mm:ss
                    String checkForColon = notiText.substring(matches[0],matches[1]);
                    if(checkForColon.contains(":") &&
                            isTimeStampValid(notiText.substring(matches[0],matches[1]))) {
                        relatedToTime = true;
                    }
                }
            }
        }
        if(hasNumber && relatedToTime) {
            Log.d(TAG,"NOTI IS Trackable");
            return true;
        }else{
            Log.d(TAG,"NOTI IS NOT Trackable");
            return false;
        }
    }

    public String getCategory(){
        Iterator hashMapIterator = categories.entrySet().iterator();
        while (hashMapIterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)hashMapIterator.next();
            String keyCategory = mapElement.getKey().toString();
            String temp = mapElement.getValue().toString();
            temp = temp.toUpperCase();
            String[] keyWords = temp.split(";");
            boolean belongsToCategory = stringContainsItemFromList(notiText.toUpperCase(),keyWords);
            if(belongsToCategory){
                return keyCategory;
            }
            /*for(int i = 0; i < keyWords.length; i++){
                Log.d(TAG,keyCategory + " : " + keyWords[i]);
            }*/
        }
        return "OTHER CATEGORY";
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
}
