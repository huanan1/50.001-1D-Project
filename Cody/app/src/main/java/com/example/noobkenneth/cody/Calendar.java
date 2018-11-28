package com.example.noobkenneth.cody;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.ImageView;
import java.text.SimpleDateFormat;

public class Calendar extends AppCompatActivity {

    CalendarView calendarView;
    ImageView imageView;
    int imageResource;
    int today;
    String TAG = "Logcat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //developer defined widgets
        calendarView = findViewById(R.id.calendarCalendarView);
        imageView = findViewById(R.id.calendarImageView);

        //get today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
        today = Integer.parseInt(dateFormat.format(calendarView.getDate()));

        //this sets the imageView image to the OOTD taken today
        imageResource = getResources().getIdentifier("@drawable/ootd" + today, null, ""+getApplicationContext().getPackageName());
        Log.i(TAG,"imageResource: "+ imageResource);
        imageView.setImageResource(imageResource);

        //when a date on the calendar is selected
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                String date = String.format("%d%02d%02d", year, month+1, dayOfMonth); //ISO 8601 compliant
                Log.i(TAG, "Date selected: " +date); //Logcat for the date selected

                //this sets the imageView image to a file with the date selected
                imageResource = getResources().getIdentifier("@drawable/ootd" + date, null, ""+getApplicationContext().getPackageName());
                Log.i(TAG,"imageResource: "+ imageResource);
                imageView.setImageResource(imageResource);
            }
        });
    }
}