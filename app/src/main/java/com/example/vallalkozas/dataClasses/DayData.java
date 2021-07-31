package com.example.vallalkozas.dataClasses;

import android.util.Log;

import com.example.vallalkozas.dataClasses.PlaceData;

import java.io.Serializable;
import java.util.ArrayList;

public class DayData implements Serializable {
    public String date;
    public ArrayList<PlaceData> places;

    // necessary array for parsing monthnumber to monthname
    public String months[] = { "január",
                        "február",
                        "március",
                        "április",
                        "május",
                        "június",
                        "július",
                        "augusztus",
                        "szeptember",
                        "október",
                        "november",
                        "december" };

    public DayData(ArrayList<PlaceData> init_Places, String date){
        this.places = init_Places;
        this.date = date;
    }

    public void addPlace(String placeName){
        PlaceData newPlace = new PlaceData(placeName);
        this.places.add(newPlace);
    }

    public void removePlace(int position){
        this.places.remove(position);
    }

    public PlaceData getPlace(int position){
        return this.places.get(position);
    }

    public void setPlace(int pos, PlaceData place){ this.places.set(pos, place); }

    public ArrayList<String> getPlaceNames(){
        ArrayList<String> placeNames = new ArrayList<String>();
        for (int i = 0; i<this.places.size(); ++i){
            placeNames.add(this.places.get(i).PlaceName);
        }
        return placeNames;
    }

    public String formatDate(String chosenDateString){
        String[] splitDate = chosenDateString.split("-");
        String chosenMonthName = this.months[Integer.parseInt(splitDate[1])];

        return (splitDate[0]+"."+chosenMonthName+"." +splitDate[2] + ".");
    }
}
