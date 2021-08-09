package com.example.vallalkozas;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class SummaryActivity extends AppCompatActivity {

    private SQLiteManager sqLiteManager = SQLiteManager.dbInstance(this);

    private TextView summaryTextView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_layout);

        summaryTextView = (TextView) findViewById(R.id.summaryText);

        HashMap<String, Integer> workerData = sqLiteManager.workerSummary();

        summaryTextView.setText(stringifySummary(workerData));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String stringifySummary(HashMap<String, Integer> summaryHashmap){
        StringBuilder stringBuilder = new StringBuilder();

        summaryHashmap.forEach((key, value) ->
                stringBuilder.append(key + " - " + value + "\n"));

        return stringBuilder.toString();
    }
}
