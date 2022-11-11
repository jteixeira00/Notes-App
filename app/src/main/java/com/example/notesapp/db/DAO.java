package com.example.notesapp.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.notesapp.Models.Notes;
import java.util.List;

@Dao
public interface DAO {

    @Insert
    void insert(Notes note);

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Notes> getAll();

    @Query("UPDATE notes SET title = :title, note = :notes WHERE ID = :id")
    void update(int id, String title, String notes);

    @Delete
    void delete(Notes note);

}
