package com.example.vallalkozas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class AddWorkerActivity extends AppCompatActivity {

    private SQLiteManager sqLiteManager = SQLiteManager.dbInstance(this);

    private ArrayList<String> newWorkerNames;
    private ArrayAdapter<String> newWorkerArrayAdapter;

    private TextView newlyAddedName;
    private EditText editWorkerName;
    private Button addWorkerToList;
    private Button saveWorkersToDB;
    private ListView listView;
    private TextView dbSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_worker_layout);

        // newlyAddedName = (TextView) findViewById(R.id.newWorkerNameTextView);
        editWorkerName = (EditText) findViewById(R.id.newWorkerNameTextInput);
        addWorkerToList = (Button) findViewById(R.id.appendWorkerButton);
        saveWorkersToDB = (Button) findViewById(R.id.saveWorkersButton);
        listView = (ListView) findViewById(R.id.listView);
        dbSize = (TextView) findViewById(R.id.dbSize);

        addWorkerToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameInputValue = editWorkerName.getText().toString();
                newWorkerNames.add(nameInputValue);
                newWorkerArrayAdapter.notifyDataSetChanged();
            }
        });

        saveWorkersToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteManager.writeWorkersToDB(newWorkerNames);
            }
        });

        File f = getApplicationContext().getDatabasePath("VallalkozasDB");
        long DBsize = f.length();

        dbSize.setText(Long.toString(DBsize) + " byte");

        newWorkerNames = sqLiteManager.getAllWorkerNames();
        newWorkerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, newWorkerNames);
        listView.setAdapter(newWorkerArrayAdapter);

        setUpListViewListener();

    }

    private void setUpListViewListener() {

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();
                Toast.makeText(context, "Munkás eltávolítva!", Toast.LENGTH_SHORT).show();

                newWorkerNames.remove(position);

                newWorkerArrayAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }
}
