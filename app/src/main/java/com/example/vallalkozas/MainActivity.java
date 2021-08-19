package com.example.vallalkozas;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.vallalkozas.dataClasses.DayData;
import com.example.vallalkozas.dataClasses.PlaceData;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

// TODO: change width of listview in DayDataActivity + ask for confirmation on all delete actions

public class MainActivity extends AppCompatActivity {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private CalendarView calendar;
    private Button goToAddWorker;
    private Button editChosenDayButton;
    private Button navigateToSummaryButton;
    private TextView chosenDayDataTextView;
    private String chosenDate;
    private DayData chosenDayData;
    private androidx.appcompat.widget.Toolbar toolbar;

    private SQLiteManager sqLiteManager = SQLiteManager.dbInstance(this);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sqLiteManager.ClearDB();

        // getApplicationContext().deleteDatabase("VallalkozasDB");

        calendar = (CalendarView) findViewById(R.id.calendarView);
        //goToAddWorker = (Button) findViewById(R.id.goToAddWorker);
        editChosenDayButton = (Button) findViewById(R.id.editChosenDayButton);
        //navigateToSummaryButton = (Button) findViewById(R.id.navigateToSummaryButton);
        chosenDayDataTextView = (TextView) findViewById(R.id.chosenDayDataTextView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        calendar.setFirstDayOfWeek(2);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String monthString = Integer.toString(month + 1);

                if (monthString.length() == 1) monthString = "0" + monthString;

                String dayString = Integer.toString(dayOfMonth);

                if (dayString.length() == 1) dayString = "0" + dayString;

                chosenDate = year + "-" + monthString + "-" + dayString;

                chosenDayData = sqLiteManager.collectDayData(chosenDate);
                chosenDayDataTextView.setText(chosenDayData.convertToString());

            }

        });

        chosenDate = sdf.format(calendar.getDate());

        chosenDayData = sqLiteManager.collectDayData(chosenDate);

        chosenDayDataTextView.setText(chosenDayData.convertToString());

        /*goToAddWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addWorkerIntent = new Intent(MainActivity.this, AddWorkerActivity.class);
                startActivity(addWorkerIntent);
            }
        });

        navigateToSummaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSummary = new Intent(MainActivity.this, SummaryActivity.class);

                goToSummary.putExtra("month", chosenDate.substring(0, 7));

                startActivity(goToSummary);
            }
        });*/

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.summary:
                Intent goToSummary = new Intent(MainActivity.this, SummaryActivity.class);
                goToSummary.putExtra("month", chosenDate.substring(0, 7));
                startActivity(goToSummary);
                return true;

            case R.id.database:
                Intent addWorkerIntent = new Intent(MainActivity.this, AddWorkerActivity.class);
                startActivity(addWorkerIntent);
                return true;

            case R.id.notes:
                Intent noteIntent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(noteIntent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}