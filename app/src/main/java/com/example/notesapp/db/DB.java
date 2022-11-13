package com.example.notesapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notesapp.Models.Notes;

@Database(entities = Notes.class, version = 1, exportSchema = false)
public abstract class DB extends RoomDatabase{
    private static DB db;
    private static String dbName = "noteDb";

    public synchronized static DB getInstance(Context context){
        if (db == null){
            db = Room.databaseBuilder(context.getApplicationContext(), DB.class, dbName).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return db;
    }

    public abstract MainDAO dao();
}
