package com.example.vallalkozas;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vallalkozas.dataClasses.DayData;
import com.example.vallalkozas.dataClasses.MyWorker;
import com.example.vallalkozas.dataClasses.PlaceData;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaceDataActivity extends AppCompatActivity {

    private static final String TAG = "PlaceDataActivity";

    // for the spinner
    private ArrayList<String> workerNames;
    private ArrayAdapter<String> workerNameAdapter;
    private SQLiteManager sqLiteManager = SQLiteManager.dbInstance(this);

    private PlaceData mPlaceData;
    private DayData mDayData;
    private int mPlacePos;

    private RecyclerView recyclerView;
    private TextView placeNameTextView;
    private EditText Note;
    // private EditText editWorkerName;
    private Spinner selectWorkerSpinner;
    private EditText editWorkerHours;
    private Button addWorkerButton;
    private Button saveAndBackButton;
    private Button saveNoteButton;
    private Button openAddWorkerDialogButton;

    private String chosenDate;

    private WorkerAdapter workerAdapter;

    // private ArrayList<MyWorker> workerList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placedata_layout);

        recyclerView = findViewById(R.id.recyclerView);
        placeNameTextView = findViewById(R.id.placeName);
        Note = findViewById(R.id.noteInput);
        // editWorkerName = (EditText) findViewById(R.id.editWorkerName);
        selectWorkerSpinner = findViewById(R.id.selectWorker);
        editWorkerHours = findViewById(R.id.editHours);
        addWorkerButton = findViewById(R.id.addWorkerButton);
        saveAndBackButton = findViewById(R.id.saveAndBack);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        openAddWorkerDialogButton = findViewById(R.id.openAddWorkerDialogButton);

        addWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendWorker();
            }
        });

        saveAndBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_and_back();
            }
        });

        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteText = Note.getText().toString();
                mPlaceData.setNote(noteText);
                Toast.makeText(getApplicationContext(), "Jegyzet mentése sikeres!", Toast.LENGTH_SHORT).show();
            }
        });

        openAddWorkerDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddWorkerDialog();
            }
        });

        Intent incomingIntent = getIntent();
        mPlaceData = (PlaceData) incomingIntent.getSerializableExtra("placeData");
        chosenDate = incomingIntent.getStringExtra("date");
        mDayData = (DayData) incomingIntent.getSerializableExtra("dayData");
        mPlacePos = incomingIntent.getIntExtra("placePos", 0);

        // workerList = PopulateWorkerData();

        placeNameTextView.setText(mDayData.formatDate(chosenDate) + " - " + mPlaceData.PlaceName);
        Note.setText(mPlaceData.Note);

        workerNames = sqLiteManager.getAllWorkerNames();
        workerNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, workerNames);
        selectWorkerSpinner.setAdapter(workerNameAdapter);

        setAdapter();

        /*recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(PlaceDataActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log
            }
        }));*/

    }

    private void save_and_back() {
        Intent goBack = new Intent(PlaceDataActivity.this, DayDataActivity.class);
        mDayData.setPlace(mPlacePos, mPlaceData);
        goBack.putExtra("dayData", (Serializable) mDayData);
        goBack.putExtra("date", chosenDate);
        goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goBack);
    }

    private void appendWorker(){
        String input_name;

        try {
            input_name = selectWorkerSpinner.getSelectedItem().toString();
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), "A név nem lehet üres!", Toast.LENGTH_SHORT).show();
            return;
        }
        int input_hours;

        try {
            input_hours = Integer.parseInt(editWorkerHours.getText().toString());
        }catch (NumberFormatException e){
            Toast.makeText(getApplicationContext(), "Helytelen számadatok!", Toast.LENGTH_SHORT).show();
            return;
        }

        mPlaceData.addWorker(input_name, input_hours);
        workerAdapter.notifyDataSetChanged();
        editWorkerHours.setText("");
        // Log.v(TAG, "worker added" + mPlaceData.workers.size());
    }

    private void setAdapter() {
        if (mPlaceData.workers == null) {
            mPlaceData.setWorkers(new ArrayList<MyWorker>());
        }
        // Log.v(TAG, Integer.toString(mPlaceData.workers.size()));
        workerAdapter = new WorkerAdapter(mPlaceData.workers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(workerAdapter);
    }

    private void openAddWorkerDialog(){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(PlaceDataActivity.this);
        mydialog.setTitle("Munkás neve: ");

        final EditText nameInput = new EditText(PlaceDataActivity.this);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        mydialog.setView(nameInput);

        mydialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputName=nameInput.getText().toString();
                sqLiteManager.writeWorkerToDB(inputName);
                workerNames.add(inputName);
                workerNameAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), inputName +" sikeresen hozzáadva a listához!", Toast.LENGTH_SHORT).show();
            }
        });

        mydialog.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        mydialog.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceDataActivity.this);
        builder.setTitle("A szerkesztett adatok nincsenek elmentve.")
                .setMessage("Biztos kilépsz?")
                .setPositiveButton("Kilépés", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PlaceDataActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Mégse", null);
        AlertDialog alert  = builder.create();
        alert.show();
    }


}
