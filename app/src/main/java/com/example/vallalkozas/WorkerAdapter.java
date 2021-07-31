package com.example.vallalkozas;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vallalkozas.dataClasses.MyWorker;

import java.util.ArrayList;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.MyViewHolder> {

    private ArrayList<MyWorker> workerList;
    private Context context;

    public WorkerAdapter(ArrayList<MyWorker> workerArray){
        workerList = workerArray;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTxt;
        private EditText hourNum;
        private Button deleteButton;

        public MyViewHolder(final View view){
            super(view);
            nameTxt = (TextView) view.findViewById(R.id.name);
            hourNum = (EditText) view.findViewById(R.id.hours);
            deleteButton = (Button) view.findViewById(R.id.deleteWorkerButton);
        }
    }

    @NonNull
    @Override
    public WorkerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.worker_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerAdapter.MyViewHolder holder, int position) {
        holder.hourNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String changedHours = holder.hourNum.getText().toString();

                    try {
                        workerList.get(position).changeHours(Integer.parseInt(changedHours));
                    }catch (NumberFormatException e){
                        Toast.makeText(context, "Helytelen számformátum!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                }
                return false;
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workerList.remove(position);
                notifyDataSetChanged();
            }
        });

        MyWorker currentWorker = workerList.get(position);
        String name = currentWorker.name;
        String hours = Integer.toString(currentWorker.hours);

        holder.nameTxt.setText(name);
        holder.hourNum.setText(hours);
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }
}
