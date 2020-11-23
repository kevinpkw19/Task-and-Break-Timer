package com.TTLabs.TaskBreakTimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class databaseHelper extends SQLiteOpenHelper {
    public static final String databaseName = "Tasks.db";
    public static final String tableName = "dataTable";
    public static final String col_0 = "ID";
    public static final String col_1 = "taskName";
    public static final String col_2 = "taskLength";
    public static final String col_3 = "breakLength";


    public databaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + tableName +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,taskName TEXT,taskLength INT,breakLength INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+tableName);
        onCreate(db);
    }

    public boolean insertData(String taskName,int taskLength,int breakLength) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1,taskName);
        contentValues.put(col_2, taskLength);
        contentValues.put(col_3,breakLength);
        long result = db.insert(tableName,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public List<String> getAllTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> all_tasks = new ArrayList<String>();
//        Cursor dbCursor = db.rawQuery("SELECT * FROM dataTable", null);
        Cursor dbCursor= db.query(tableName,null,null,null,null,null,null);
        while(dbCursor.moveToNext())
        {
            String taskNames = dbCursor.getString(dbCursor.getColumnIndex("taskName"));
            all_tasks.add(taskNames);
        }
        return all_tasks;

    }

    public String getTaskLength(String taskChosen ) {
        String value = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {col_2};
        Cursor dbCursor = db.query(tableName, columns, col_1 + "= '" + taskChosen + "'", null, null, null, null);

        while (dbCursor.moveToNext()) {
            value = dbCursor.getString(dbCursor.getColumnIndex(col_2));
        }
        return value;
    }

    public String getBreakLength(String taskChosen ){
        String value="";
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {col_3};
        Cursor dbCursor= db.query(tableName,columns,col_1+"= '"+taskChosen+"'",null,null,null,null);

        while(dbCursor.moveToNext())
        {
            value= dbCursor.getString(dbCursor.getColumnIndex(col_3));
            }
        return value;
    }

    public String getID(String taskChosen ) {
        String value = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {col_0};
        Cursor dbCursor = db.query(tableName, columns, col_1 + "= '" + taskChosen + "'", null, null, null, null);

        while (dbCursor.moveToNext()) {
            value = dbCursor.getString(dbCursor.getColumnIndex(col_0));
        }
        return value;
    }

    public String getTask(String ID) {
        String value = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {col_1};
        Cursor dbCursor = db.query(tableName, columns, col_0 + "= '" + ID + "'", null, null, null, null);

        while (dbCursor.moveToNext()) {
            value = dbCursor.getString(dbCursor.getColumnIndex(col_1));
        }
        return value;
    }

    public boolean checkTaskName(String taskName) {
        String value = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {col_1};
        Cursor dbCursor = db.query(tableName, columns, col_1 + "= '" + taskName + "'", null, null, null, null);

        while (dbCursor.moveToNext()) {
            value = dbCursor.getString(dbCursor.getColumnIndex(col_1));
        }
        if (value== ""){
            return false;
        }else{
            return true;
        }

    }

    public boolean UpdateData(String ID,String taskName,int taskLength,int breakLength){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(col_0,ID);
        contentValues.put(col_1,taskName);
        contentValues.put(col_2,taskLength);
        contentValues.put(col_3,breakLength);
        db.update(tableName, contentValues,"ID=?",new String[] {ID});
        return true;
    }
    public boolean DeleteData(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName,col_0 + "=" + ID,null);
        return true;
    }

}
