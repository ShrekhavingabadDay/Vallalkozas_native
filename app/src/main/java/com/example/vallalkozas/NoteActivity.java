package com.example.vallalkozas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    private SQLiteManager sqLiteManager = SQLiteManager.dbInstance(this);
    private TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);

        textView = findViewById(R.id.textView);

        textView.setText(joinThem(sqLiteManager.noteSummary()));
    }

    private String joinThem(ArrayList<String> toJoin){

        String finalString = "Jegyzetek:\n\n";

        for (int i = 0; i<toJoin.size(); ++i){
            if (i%2 == 0){
                finalString += toJoin.get(i)+ "\n" + new String(new char[toJoin.get(i).length()]).replace("\0", "─") + "\n";
            }else{
                finalString += toJoin.get(i) + "\n\n\n";
            }
        }

        return finalString;
    }
}
