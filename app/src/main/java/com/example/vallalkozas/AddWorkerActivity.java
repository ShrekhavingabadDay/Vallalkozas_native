package com.example.vallalkozas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    private Button clearDBbutton;
    private ListView listView;
    private TextView dbSize;

    private File f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_worker_layout);

        // newlyAddedName = (TextView) findViewById(R.id.newWorkerNameTextView);
        editWorkerName = (EditText) findViewById(R.id.newWorkerNameTextInput);
        addWorkerToList = (Button) findViewById(R.id.appendWorkerButton);
        saveWorkersToDB = (Button) findViewById(R.id.saveWorkersButton);
        clearDBbutton = (Button) findViewById(R.id.clearDBbutton);
        listView = (ListView) findViewById(R.id.listView);
        dbSize = (TextView) findViewById(R.id.dbSize);

        addWorkerToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameInputValue = editWorkerName.getText().toString();
                newWorkerNames.add(nameInputValue);
                newWorkerArrayAdapter.notifyDataSetChanged();
                editWorkerName.setText("");
            }
        });

        saveWorkersToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteManager.writeWorkersToDB(newWorkerNames);
            }
        });

        clearDBbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearDBClicked();
            }
        });

        f = getApplicationContext().getDatabasePath("VallalkozasDB");
        dbSize.setText("Az adatbázis mérete: " + getDatabaseSize(f));

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

    private void onClearDBClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddWorkerActivity.this);

        builder.setTitle("Az adatbázis adatai végleg törlődni fognak.")
                .setMessage("Biztos törlöd?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqLiteManager.ClearDB();
                        dbSize.setText("Az adatbázis mérete: " + getDatabaseSize(f));
                        Toast.makeText(getApplicationContext(), "Az adatbázis üres!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Mégse", null);
        AlertDialog alert  = builder.create();
        alert.show();
    }

    private String getDatabaseSize(File dbFile){
        long DBsizeB = dbFile.length();

        long DBsizeMB = DBsizeB/(1024*1024);

        if (DBsizeMB == 0){
            long DBsizeKB = DBsizeB/1024;
            if (DBsizeKB == 0){
                return DBsizeB + " byte";
            }
            else {
                return DBsizeKB + " KB";
            }
        }
        else{
            return DBsizeMB + " MB";
        }
    }
}
