package com.example.vallalkozas.dataClasses;

import java.io.Serializable;

public class MyWorker implements Serializable {
    public String name;
    public int hours;

    public MyWorker(String init_name, int init_hours){
        name = init_name;
        hours = init_hours;
    }

    public void changeHours (int new_hours){
        this.hours = new_hours;
    }
}
