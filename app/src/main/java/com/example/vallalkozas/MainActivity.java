package com.example.vallalkozas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.vallalkozas.dataClasses.DayData;
import com.example.vallalkozas.dataClasses.PlaceData;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

// TODO: add summary page -> summarize hours spent at one place + work on save system because currently it adds duplicate data

public class MainActivity extends AppCompatActivity {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

    private CalendarView calendar;
    private Button goToAddWorker;
    private Button editChosenDayButton;
    private Button navigateToSummaryButton;
    private TextView chosenDayDataTextView;
    private String chosenDate;
    private DayData chosenDayData;

    private SQLiteManager sqLiteManager = SQLiteManager.dbInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sqLiteManager.ClearDB();

        // getApplicationContext().deleteDatabase("VallalkozasDB");

        calendar = (CalendarView) findViewById(R.id.calendarView);
        goToAddWorker = (Button) findViewById(R.id.goToAddWorker);
        editChosenDayButton = (Button) findViewById(R.id.editChosenDayButton);
        navigateToSummaryButton = (Button) findViewById(R.id.navigateToSummaryButton);
        chosenDayDataTextView = (TextView) findViewById(R.id.chosenDayDataTextView);

        calendar.setFirstDayOfWeek(2);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                chosenDate = year + "-" + (month+1) + "-" + dayOfMonth;

                chosenDayData = sqLiteManager.collectDayData(chosenDate);
                chosenDayDataTextView.setText(chosenDayData.convertToString());

            }

        });

        chosenDayData = sqLiteManager.collectDayData(sdf.format(calendar.getDate()));

        Log.v("MainActivity", sdf.format(calendar.getDate()));
        chosenDayDataTextView.setText(chosenDayData.convertToString());

        goToAddWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addWorkerIntent = new Intent(MainActivity.this, AddWorkerActivity.class);
                startActivity(addWorkerIntent);
            }
        });

        navigateToSummaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SummaryActivity.class));
            }
        });

        editChosenDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DayDataActivity.class);
                intent.putExtra("date", chosenDate);
                intent.putExtra("dayData", (Serializable) chosenDayData);
                startActivity(intent);
            }
        });
    }

}