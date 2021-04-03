package com.softgames.popat.utils;

import android.app.Activity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

public class UIUtils {

    private Activity activity;
    private View parentView;

    public UIUtils(Activity activity){
        this.activity = activity;
    }
    public UIUtils(){

    }
    public UIUtils(Activity activity, int parentResourceId){
        this.activity = activity;
        this.parentView = this.activity.findViewById(parentResourceId);
    }
    public void setParentView( int parentResourceId){
        this.parentView = this.activity.findViewById(parentResourceId);
    }
    public  void showShortSnakeBar(String msg){
        Snackbar.make(this.parentView, msg, Snackbar.LENGTH_SHORT).show();
    }
    public  void showLongSnakeBar(String msg){
        Snackbar.make(this.parentView, msg, Snackbar.LENGTH_LONG).show();
    }

    public String getMonthFromIndex(int index){
        String[] months= {
                "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AGU", "SEPT", "OCT", "NOV", "DEC"
        };
        return months[index];
    }
    public String getAmOrPm(int index){
        String[] am_pm = {"AM", "PM"};

        return am_pm[index];
    }
    public String getCurrentTimeString(){

        Calendar calendar = Calendar.getInstance();

        String date = String.valueOf(calendar.get(Calendar.DATE));
        String month = getMonthFromIndex(calendar.get(Calendar.MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        String hr="", mi="", se="";
        if(hours < 10){
            hr = "0"+hours;
        }else {
            hr = ""+hours;
        }
        if (minutes < 10){
            mi = "0"+minutes;
        }else {
            mi = ""+minutes;
        }
        if (seconds < 10){
            se = "0"+minutes;
        }else {
            se = ""+minutes;
        }
        String amPm = getAmOrPm(calendar.get(Calendar.AM_PM));

        return date+"/"+month+"/"+year+" "+hr+":"+mi+":"+se+" "+amPm;

    }
    public ArrayList<String> getMonthArrayToMonths(int currentMonthIndex){
        String[] months= {
                "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AGU", "SEPT", "OCT", "NOV", "DEC"
        };
        ArrayList<String> monthList = new ArrayList<>();
        for(int i = 0; i<=currentMonthIndex; ++i){
            monthList.add(months[i]);
        }

        return monthList;
    }
    public ArrayList<String> getYearArray(){
        ArrayList<String> yearArray = new ArrayList<>();
        yearArray.add("2021");
        yearArray.add("2020");

        return yearArray;
    }
}
