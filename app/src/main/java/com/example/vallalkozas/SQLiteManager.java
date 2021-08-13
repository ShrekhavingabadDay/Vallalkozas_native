package com.example.vallalkozas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.vallalkozas.dataClasses.DayData;
import com.example.vallalkozas.dataClasses.MyWorker;
import com.example.vallalkozas.dataClasses.PlaceData;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager sqLiteManager;

    private static final String DB_NAME = "VallalkozasDB";
    private static final int DB_VERSION = 1;
    private static final String DAY_TABLE_NAME = "Day";
    private static final String PLACE_TABLE_NAME = "Place";
    private static final String WORKER_TABLE_NAME = "Worker";
    private static final String ALL_WORKER_TABLE_NAME = "AllWorkers";

    private static final String ID_FIELD = "id";
    private static final String DATE_FIELD = "date";
    private static final String NAME_FIELD = "name";
    private static final String HOURS_FIELD = "hours";
    private static final String DAY_ID_FIELD = "dayID";
    private static final String PLACE_ID_FIELD = "placeID";
    private static final String NOTE_FIELD = "note";

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
        StringBuilder all_worker_sql;

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
                .append(" TEXT, ")
                .append(NOTE_FIELD)
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

        // initializing *worker* table
        all_worker_sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(ALL_WORKER_TABLE_NAME)
                .append(" (")
                .append(ID_FIELD)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(NAME_FIELD)
                .append(" TEXT) ");

        db.execSQL(all_worker_sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void writeWorkersToDB(ArrayList<String> workersToAdd){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ALL_WORKER_TABLE_NAME, "1=1", null);

        for (int i = 0; i<workersToAdd.size(); ++i){

            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME_FIELD, workersToAdd.get(i));

            db.insert(ALL_WORKER_TABLE_NAME, "", contentValues);
        }
    }

    public void writeWorkerToDB(String workerNameToAdd){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_FIELD, workerNameToAdd);
        db.insert(ALL_WORKER_TABLE_NAME, "", contentValues);
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
                    contentValues.put(NOTE_FIELD, day.places.get(i).Note);

                    place_id = database.insert(PLACE_TABLE_NAME, "", contentValues);

                }else {
                    contentValues = new ContentValues();
                    contentValues.put(NOTE_FIELD, day.places.get(i).Note);

                    database.update(PLACE_TABLE_NAME, contentValues, DAY_ID_FIELD + "=" + day_id, null);

                    place_id = getPlaceId(day.places.get(i).PlaceName, day_id);
                }

                database.delete(WORKER_TABLE_NAME, DAY_ID_FIELD + "="+day_id+" AND " + PLACE_ID_FIELD + "="+place_id, null);

                for (int j = 0; j < day.places.get(i).workers.size(); ++j) {

                    contentValues = new ContentValues();

                    contentValues.put(DAY_ID_FIELD, day_id);
                    contentValues.put(PLACE_ID_FIELD, place_id);
                    contentValues.put(NAME_FIELD, day.places.get(i).workers.get(j).name);
                    contentValues.put(HOURS_FIELD, day.places.get(i).workers.get(j).hours);

                    /*if (workerInDB(day.places.get(i).workers.get(j).name, day_id, place_id)){
                        Log.v("SQLiteManager", "worker already in db " + day.places.get(i).workers.get(j).name);
                        database.update(WORKER_TABLE_NAME,
                                        contentValues,
                                        null,
                                        null
                        );
                    }else{*/
                    database.insert(WORKER_TABLE_NAME, "", contentValues);
                    //}
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
                contentValues.put(NOTE_FIELD, day.places.get(i).Note);

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
                "SELECT DISTINCT " + NAME_FIELD + " FROM " + ALL_WORKER_TABLE_NAME,
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
                "SELECT * FROM " + DAY_TABLE_NAME + " WHERE " + DATE_FIELD + "= ?",
                new String[]{dateString}
        )){
            Log.v("SQLiteManager239", Integer.toString(result.getCount()));
            if (result.getCount() > 0){
                return true;
            }
        }
        return false;
    }

    public boolean placeInDB(String placeName, long day_id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + PLACE_TABLE_NAME + " WHERE " + NAME_FIELD + "=?" +
                        " AND " + DAY_ID_FIELD + "=" + day_id,
                new String[]{placeName}
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
                "SELECT * FROM " + WORKER_TABLE_NAME + " WHERE " + NAME_FIELD + "=?" +
                        " AND " + DAY_ID_FIELD + "=" + day_id + " AND " + PLACE_ID_FIELD + "=" + place_id,
                new String[]{workerName}
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
                "SELECT " + ID_FIELD + " FROM " + DAY_TABLE_NAME + " WHERE " + DATE_FIELD + "=?",
                new String[]{dateString}
        )){
            if (result.getCount() > 0){
                result.moveToFirst();
                return result.getLong(0);

            }
        }
        return 0;
    }

    public long getPlaceId(String placeName, long day_id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery(
                "SELECT " + ID_FIELD + " FROM " + PLACE_TABLE_NAME + " WHERE " + NAME_FIELD + "=?" +
                        " AND " + DAY_ID_FIELD + "=" + day_id,
                new String[]{placeName}
        )){
            if (result.getCount() > 0){
                result.moveToFirst();
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

        sqLiteDatabase.rawQuery("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name =?", new String[]{DAY_TABLE_NAME});
        sqLiteDatabase.rawQuery("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name =?", new String[]{PLACE_TABLE_NAME});
        sqLiteDatabase.rawQuery("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name =?", new String[]{WORKER_TABLE_NAME});

    }

    public DayData collectDayData(String day){

        long dayId = getDayId(day);
        long placeId;

        int placeIndex;

        String workerName;
        int workingHours;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        DayData collectedDayData = new DayData(new ArrayList<>(), day);

        try (Cursor placeResult = sqLiteDatabase.rawQuery(
                "SELECT " + ID_FIELD + ", " + NAME_FIELD + ", " + NOTE_FIELD + " FROM " + PLACE_TABLE_NAME + " WHERE " + DAY_ID_FIELD + "=?",
            new String[]{Long.toString(dayId)}
        )){
            if (placeResult.getCount() > 0){
                while (placeResult.moveToNext()){
                    collectedDayData.addPlace(placeResult.getString(1));

                    placeIndex = collectedDayData.places.size() - 1;

                    collectedDayData.getPlace(placeIndex).setNote(placeResult.getString(2));

                    placeId = placeResult.getLong(0);

                    try (Cursor workerResult = sqLiteDatabase.rawQuery(
                            "SELECT " + NAME_FIELD+", " + HOURS_FIELD + " FROM " + WORKER_TABLE_NAME + " WHERE " + DAY_ID_FIELD + " = ? AND " + PLACE_ID_FIELD + " = ?",
                            new String[]{Long.toString(dayId), Long.toString(placeId)}
                    )){
                        if (workerResult.getCount() > 0){
                            while (workerResult.moveToNext()){

                                workerName = workerResult.getString(0);
                                workingHours = workerResult.getInt(1);

                                Log.v("sqlitemanager", collectedDayData.getPlace(placeIndex).PlaceName);

                                collectedDayData.getPlace(placeIndex).addWorker(workerName, workingHours);
                            }
                        }
                    }

                }
            }
        }

        return collectedDayData;

    }

    /*
    * "SELECT" + WORKER_TABLE_NAME + "." + NAME_FIELD + ", " + WORKER_TABLE_NAME + "." + HOURS_FIELD + ", " + DAY_TABLE_NAME+ "."+ DATE_FIELD +
    * " FROM " + WORKER_TABLE_NAME +
    * " INNER JOIN " + DAY_TABLE_NAME + " ON  " + DAY_TABLE_NAME + "." + ID_FIELD + " = " + WORKER_TABLE_NAME + "." + DAY_ID_FIELD
    * */

    /*
    * "SELECT " + PLACE_TABLE_NAME + "." + NAME_FIELD + ", SUM(" + WORKER_TABLE_NAME + "." + HOURS_FIELD + ") as sum_hours " +
    * "FROM "  + DAY_TABLE_NAME + " d " +
    * "INNER JOIN " + PLACE_TABLE_NAME + " p "
    *   "ON d." + ID_FIELD + " = p." + DAY_ID_FIELD +
    * " INNER JOIN " + WORKER_TABLE_NAME + " w "
    *   "ON w." + PLACE_ID_FIELD+" = p." +ID_FIELD+
    * "WHERE d." + DATE_FIELD +" =?"
    * */

    public HashMap<String, Integer> workerSummary(String chosenMonth){

        HashMap<String, Integer> workerSummary = new HashMap<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String workerName;
        int workingHours;

        try (Cursor result = sqLiteDatabase.rawQuery(
                "SELECT " + WORKER_TABLE_NAME + "." + NAME_FIELD + ", " + "SUM(" + WORKER_TABLE_NAME + "." + HOURS_FIELD + ")" +
                    " FROM " + WORKER_TABLE_NAME +
                    " INNER JOIN " + DAY_TABLE_NAME + " ON  " + DAY_TABLE_NAME + "." + ID_FIELD + " = " + WORKER_TABLE_NAME + "." + DAY_ID_FIELD +
                    " WHERE substr(" +  DAY_TABLE_NAME+ "."+ DATE_FIELD + ",1,7) = ? " +
                    "GROUP BY " + WORKER_TABLE_NAME + "." + NAME_FIELD,
                new String[]{ chosenMonth }
        )){
            if (result.getCount() > 0){
                while (result.moveToNext()){
                    workerName = result.getString(0);
                    workingHours = result.getInt(1);
                    workerSummary.put(workerName, workingHours);
                }
            }
        }
        return workerSummary;
    }

    public HashMap<String, Integer> placeSummary(String chosenMonth){
        HashMap<String, Integer> placeSummary = new HashMap<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String placeName;
        int placeHours;

        try (Cursor result = sqLiteDatabase.rawQuery(
                "SELECT p." + NAME_FIELD + ", SUM( w." + HOURS_FIELD + ") as sum_hours " +
                "FROM "  + DAY_TABLE_NAME + " d " +
                "INNER JOIN " + PLACE_TABLE_NAME + " p " +
                "ON d." + ID_FIELD + " = p." + DAY_ID_FIELD +
                " INNER JOIN " + WORKER_TABLE_NAME + " w " +
                "ON w." + PLACE_ID_FIELD+" = p." +ID_FIELD +
                " WHERE substr(d." + DATE_FIELD +",1,7) = ? "+
                "GROUP BY p." + NAME_FIELD ,
                new String[]{ chosenMonth }
        )){
            if (result.getCount() > 0){
                while (result.moveToNext()){
                    placeName = result.getString(0);
                    placeHours = result.getInt(1);
                    placeSummary.put(placeName, placeHours);
                }
            }
        }
        return placeSummary;
    }


}
