package com.example.vallalkozas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vallalkozas.dataClasses.DayData;
import com.example.vallalkozas.dataClasses.MyWorker;

import java.util.ArrayList;

public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager sqLiteManager;

    private static final String DB_NAME = "VallalkozasDB";
    private static final int DB_VERSION = 1;
    private static final String DAY_TABLE_NAME = "Day";
    private static final String PLACE_TABLE_NAME = "Place";
    private static final String WORKER_TABLE_NAME = "Worker";

    private static final String ID_FIELD = "id";
    private static final String DATE_FIELD = "date";
    private static final String NAME_FIELD = "name";
    private static final String HOURS_FIELD = "hours";
    private static final String DAY_ID_FIELD = "dayID";
    private static final String PLACE_ID_FIELD = "placeID";

    public SQLiteManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static SQLiteManager dbInstance(Context context){
        if (sqLiteManager == null)
            sqLiteManager = new SQLiteManager(context);
        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder day_sql;
        StringBuilder place_sql;
        StringBuilder worker_sql;

        // initializing *day* table
        day_sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(DAY_TABLE_NAME)
                .append(" (")
                .append(ID_FIELD)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(DATE_FIELD)
                .append(" TEXT) ");

        db.execSQL(day_sql.toString());

        // initializing *place* table
        place_sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(PLACE_TABLE_NAME)
                .append(" (")
                .append(ID_FIELD)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(DAY_ID_FIELD)
                .append(" INT, ")
                .append(NAME_FIELD)
                .append(" TEXT)");

        db.execSQL(place_sql.toString());

        // initializing *worker* table
        worker_sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(WORKER_TABLE_NAME)
                .append(" (")
                .append(ID_FIELD)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(DAY_ID_FIELD)
                .append(" INT, ")
                .append(PLACE_ID_FIELD)
                .append(" INT, ")
                .append(NAME_FIELD)
                .append(" TEXT, ")
                .append(HOURS_FIELD)
                .append(" INT)");

        db.execSQL(worker_sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void writeDayToDB(DayData day){

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues;

        long day_id;
        long place_id;

        if (dayInDB(day.date)){

            day_id = getDayId(day.date);

            for (int i = 0; i < day.places.size(); ++i) {
                if (!placeInDB(day.places.get(i).PlaceName, day_id)){
                    contentValues = new ContentValues();
                    contentValues.put(DAY_ID_FIELD, day_id);
                    contentValues.put(NAME_FIELD, day.places.get(i).PlaceName);

                    place_id = database.insert(PLACE_TABLE_NAME, "", contentValues);

                }else {
                    place_id = getPlaceId(day.places.get(i).PlaceName, day_id);
                }

                for (int j = 0; j < day.places.get(i).workers.size(); ++j) {

                    contentValues = new ContentValues();

                    contentValues.put(DAY_ID_FIELD, day_id);
                    contentValues.put(PLACE_ID_FIELD, place_id);
                    contentValues.put(NAME_FIELD, day.places.get(i).workers.get(j).name);
                    contentValues.put(HOURS_FIELD, day.places.get(i).workers.get(j).hours);

                    if (workerInDB(day.places.get(i).workers.get(j).name, day_id, place_id)){
                        database.update(WORKER_TABLE_NAME,
                                        contentValues,
                                 NAME_FIELD + "=? AND "+
                                        DAY_ID_FIELD + "=? AND " +
                                        PLACE_ID_FIELD + "=? AND",
                                        new String[]{contentValues.get(NAME_FIELD).toString(),
                                                     contentValues.get(DAY_ID_FIELD).toString(),
                                                     contentValues.get(PLACE_ID_FIELD).toString()}
                        );
                    }else{
                        database.insert(WORKER_TABLE_NAME, "", contentValues);
                    }
                }
            }
        }
        else {

            contentValues = new ContentValues();
            contentValues.put(DATE_FIELD, day.date);

            day_id = database.insert(DAY_TABLE_NAME, "", contentValues);

            for (int i = 0; i < day.places.size(); ++i) {
                contentValues = new ContentValues();
                contentValues.put(DAY_ID_FIELD, day_id);
                contentValues.put(NAME_FIELD, day.places.get(i).PlaceName);

                place_id = database.insert(PLACE_TABLE_NAME, "", contentValues);

                for (int j = 0; j < day.places.get(i).workers.size(); ++j) {
                    contentValues = new ContentValues();

                    contentValues.put(DAY_ID_FIELD, day_id);
                    contentValues.put(PLACE_ID_FIELD, place_id);
                    contentValues.put(NAME_FIELD, day.places.get(i).workers.get(j).name);
                    contentValues.put(HOURS_FIELD, day.places.get(i).workers.get(j).hours);

                    database.insert(WORKER_TABLE_NAME, "", contentValues);
                }
            }
        }

    }

    public ArrayList<String> getAllWorkerNames(){
        ArrayList<String> allWorkerNames = new ArrayList<String>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery(
                "SELECT DISTINCT " + NAME_FIELD + " FROM " + WORKER_TABLE_NAME,
                null
        )){
            if (result.getCount() > 0){
                while (result.moveToNext()){
                    String name = result.getString(0);
                    allWorkerNames.add(name);
                }
            }
        }
        return allWorkerNames;
    }

    public boolean dayInDB(String dateString){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + DAY_TABLE_NAME + " WHERE " + DATE_FIELD + "=" + dateString,
                null
        )){
            if (result.getCount() > 0){
                return true;
            }
        }
        return false;
    }

    public boolean placeInDB(String placeName, long day_id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + PLACE_TABLE_NAME + " WHERE " + NAME_FIELD + "=" + placeName +
                        " AND " + DAY_ID_FIELD + "=" + day_id,
                null
        )){
            if (result.getCount() > 0){
                return true;
            }
        }
        return false;
    }

    public boolean workerInDB(String workerName, long day_id, long place_id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + WORKER_TABLE_NAME + " WHERE " + NAME_FIELD + "=" + workerName +
                        " AND " + DAY_ID_FIELD + "=" + day_id + " AND " + PLACE_ID_FIELD + "=" + place_id,
                null
        )){
            if (result.getCount() > 0){
                return true;
            }
        }
        return false;
    }

    public long getDayId(String dateString){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery(
                "SELECT " + ID_FIELD + " FROM " + DAY_TABLE_NAME + " WHERE " + DATE_FIELD + "=" + dateString,
                null
        )){
            if (result.getCount() > 0){
                return result.getLong(0);
            }
        }
        return 0;
    }

    public long getPlaceId(String placeName, long day_id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery(
                "SELECT" + ID_FIELD + "FROM " + PLACE_TABLE_NAME + " WHERE " + NAME_FIELD + "=" + placeName +
                        " AND " + DAY_ID_FIELD + "=" + day_id,
                null
        )){
            if (result.getCount() > 0){
                return result.getLong(0);
            }
        }
        return 0;
    }

    public void ClearDB(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete(DAY_TABLE_NAME, "1=1", null);
        sqLiteDatabase.delete(PLACE_TABLE_NAME, "1=1", null);
        sqLiteDatabase.delete(WORKER_TABLE_NAME, "1=1", null);
    }


}
