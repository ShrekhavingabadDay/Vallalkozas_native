package com.example.vallalkozas;

import android.app.MediaRouteButton;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vallalkozas.dataClasses.DayData;

import java.io.Serializable;
import java.util.ArrayList;

public class DayDataActivity extends AppCompatActivity {

    private static final String TAG = "DayDataActivity";

    private ArrayList<String> placeNames;
    private ArrayAdapter<String> placeNamesAdapter;

    private ListView listView;
    private EditText placeNameEditor;
    private Button addPlaceButton;
    private Button saveAllButton;

    private String chosenDate;

    private DayData dayData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daydata_layout);

        placeNameEditor = (EditText) findViewById(R.id.placeNameInput);
        addPlaceButton = (Button) findViewById(R.id.addPlaceButton);
        saveAllButton = (Button) findViewById(R.id.saveAllButton);
        listView = (ListView) findViewById(R.id.listView);

        TextView chosenDateDisplay = (TextView) findViewById(R.id.chosenDate);

        Intent incomingIntent = getIntent();
        chosenDate = incomingIntent.getStringExtra("date");
        dayData = (DayData) incomingIntent.getSerializableExtra("dayData");

        chosenDateDisplay.setText(dayData.formatDate(chosenDate) + " szerkesztése");

        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        saveAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDB(dayData);
                Intent backToMain = new Intent(DayDataActivity.this, MainActivity.class);
                backToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backToMain);
                DayDataActivity.this.finish();
            }
        });

        placeNameEditor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addPlaceButton.performClick();
                    return true;
                }
                return false;
            }
        });

        placeNames = dayData.getPlaceNames();
        placeNamesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeNames);
        listView.setAdapter(placeNamesAdapter);
        setUpListViewListener();

    }

    private void setUpListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editPlace = new Intent(DayDataActivity.this, PlaceDataActivity.class);

                //editPlace.putExtra("place", placeNames.get(position));
                //editPlace.putExtra("date", chosenDate);
                editPlace.putExtra("placeData", (Serializable) dayData.getPlace(position));
                editPlace.putExtra("date", chosenDate);
                editPlace.putExtra("dayData", (Serializable) dayData);
                editPlace.putExtra("placePos", position);

                startActivity(editPlace);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();
                Toast.makeText(context, "Helyszín eltávolítva", Toast.LENGTH_SHORT).show();

                dayData.removePlace(position);
                placeNames.remove(position);

                placeNamesAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void addItem() {
        String placeName = placeNameEditor.getText().toString();

        if (!(placeName.equals(""))){

            dayData.addPlace(placeName);

            placeNamesAdapter.add(placeName);

            placeNameEditor.setText("");
        }else{
            Toast.makeText(getApplicationContext(), "A helyszínnév nem lehet üres!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToDB(DayData d){
        SQLiteManager sqLiteManager = SQLiteManager.dbInstance(this);
        sqLiteManager.writeDayToDB(d);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DayDataActivity.this);
        builder.setTitle("A szerkesztett adatok nincsenek elmentve.")
                .setMessage("Biztos kilépsz?")
                .setPositiveButton("Kilépés", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DayDataActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Mégse", null);
        AlertDialog alert  = builder.create();
        alert.show();
    }

}
