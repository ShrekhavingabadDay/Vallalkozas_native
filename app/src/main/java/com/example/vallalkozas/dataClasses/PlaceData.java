package com.example.vallalkozas.dataClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaceData implements Serializable {
    public String PlaceName;
    public String Note;
    public ArrayList<MyWorker> workers;

    public PlaceData(String placeName, ArrayList<MyWorker> workers){
        this.PlaceName = placeName;
        this.workers = workers;
    }

    public void setNote(String noteText){
        this.Note = noteText;
    }

    public void addWorker(String workerName, int workingHours){
        MyWorker worker = new MyWorker(workerName, workingHours);
        this.workers.add(worker);
    }

    public void setWorkers(ArrayList<MyWorker> workers){ this.workers = workers; }

}
