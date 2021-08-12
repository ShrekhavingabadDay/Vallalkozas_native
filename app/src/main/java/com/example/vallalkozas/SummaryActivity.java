package com.example.vallalkozas;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vallalkozas.dataClasses.DayData;

import java.util.HashMap;

public class SummaryActivity extends AppCompatActivity {

    private SQLiteManager sqLiteManager = SQLiteManager.dbInstance(this);
    private TextView summaryTextView;
    private Intent incomingIntent;
    private String chosenMonth;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_layout);

        incomingIntent = getIntent();

        chosenMonth = incomingIntent.getStringExtra("month");

        summaryTextView = (TextView) findViewById(R.id.summaryText);

        String workerData = stringifySummary(sqLiteManager.workerSummary(chosenMonth));
        String placeData = stringifySummary(sqLiteManager.placeSummary(chosenMonth));

        summaryTextView.setText(DayData.formatSummaryMonth(chosenMonth) + "\n\n" + workerData + "\n" + placeData);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String stringifySummary(HashMap<String, Integer> summaryHashmap){

        if (summaryHashmap.containsKey(null)){
            return " - ";
        }

        StringBuilder stringBuilder = new StringBuilder();

        summaryHashmap.forEach((key, value) ->
                stringBuilder.append(key + " - " + value + "\n"));

        return stringBuilder.toString();
    }
}
