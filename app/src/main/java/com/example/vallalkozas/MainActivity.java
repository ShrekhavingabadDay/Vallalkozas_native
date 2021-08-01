package com.example.vallalkozas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.vallalkozas.dataClasses.DayData;
import com.example.vallalkozas.dataClasses.PlaceData;

import java.io.Serializable;
import java.util.ArrayList;

// TODO: show saved data when clicking day + add summary page + add "add worker" page

public class MainActivity extends AppCompatActivity {

    private CalendarView calendar;
    private Button goToAddWorker;
    private String chosenDate;

    private SQLiteManager sqLiteManager = SQLiteManager.dbInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sqLiteManager.ClearDB();

        calendar = (CalendarView) findViewById(R.id.calendarView);
        goToAddWorker = (Button) findViewById(R.id.goToAddWorker);

        calendar.setFirstDayOfWeek(2);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                chosenDate = year + "-" + (month+1) + "-" + dayOfMonth;

                Intent intent = new Intent(MainActivity.this, DayDataActivity.class);
                intent.putExtra("date", chosenDate);
                intent.putExtra("dayData", (Serializable) new DayData(new ArrayList<PlaceData>(), chosenDate));
                startActivity(intent);
            }

        });
        goToAddWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addWorkerIntent = new Intent(MainActivity.this, AddWorkerActivity.class);
                startActivity(addWorkerIntent);
            }
        });
    }

}