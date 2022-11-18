package com.example.notesapp.TaskManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telecom.Call;

import com.example.notesapp.Adapters.NotesAdapter;
import com.example.notesapp.Fragments.SendNote;
import com.example.notesapp.Models.Notes;
import com.example.notesapp.db.DB;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskManager {
    final Executor executor = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());



    public interface Callback{
        void updateRecycler(List<Notes> notes);
        void itemClickHelper(Notes note);
        void queryChangeHelper(List<Notes> newNotes, String s);
        void LongItemClickHelper(Notes newSelected);
    }

    public void executeOnCreateView(NotesAdapter notesAdapter, Bundle bundle, DB db, Callback callback){
        executor.execute(() -> {
            List<Notes> notes = new ArrayList<>();
            if (bundle != null){
                Notes new_note = (Notes) bundle.getSerializable("note");
                if(Objects.equals(bundle.getString("req"), "insert")){
                    db.dao().insert(new_note);
                }
                else{
                    db.dao().update(new_note.getID(), new_note.getTitle(), new_note.getNote());
                }
                notesAdapter.notifyDataSetChanged();
            }

            notes = db.dao().getAll();
            List<Notes> finalNotes = notes;
            handler.post(() ->{
                callback.updateRecycler(finalNotes);
            });
        });
    }

    public void executeOnItemClick(NotesAdapter notesAdapter, DB db, int position, Callback callback){
        executor.execute(() -> {
            Notes note = null;
            int id = notesAdapter.getID(position);
            for (Notes x : db.dao().getAll()){
                if(x.getID() ==  id){
                    note = x;
                    break;
                }
            }
            Notes finalNote = note;
            handler.post(() ->{
                callback.itemClickHelper(finalNote);
            });
        });
    }

    public void executeOnQueryTextChange(DB db, String s, Callback callback){
        executor.execute(() -> {
            List<Notes> notes = new ArrayList<>();
            notes = db.dao().getAll();
            List<Notes> finalNotes = notes;
            handler.post(() ->{
                callback.queryChangeHelper(finalNotes, s);
            });
        });
    }

    public void executeDeleteItem(DB db, Notes selectedNote, Callback callback){
        executor.execute(() -> {
            List<Notes> notes = new ArrayList<>();
            db.dao().delete(selectedNote);
            notes = db.dao().getAll();
            List<Notes> finalNotes = notes;
            handler.post(() ->{
                callback.updateRecycler(finalNotes);
            });
        });
    }

    public void executeSendNote(DB db, Notes selectedNote, Callback callback){
        executor.execute(() -> {
            List<Notes> notes = new ArrayList<>();
            SendNote sender = new SendNote();

            //TODO rest of sent note paramaters
            sender.sendNote(selectedNote.getTitle() + "&" + selectedNote.getNote(), sender.getQosSlider().getValue(), sender.);

            notes = db.dao().getAll();
            List<Notes> finalNotes = notes;
            handler.post(() ->{
                callback.updateRecycler(finalNotes);
            });
        });
    }

    public void executeUpdateItem(DB db, Notes selectedNote, String value, Callback callback){
        executor.execute(() -> {
            List<Notes> notes = new ArrayList<>();
            db.dao().update(selectedNote.getID(), value, selectedNote.getNote());
            notes = db.dao().getAll();
            List<Notes> finalNotes = notes;
            handler.post(() ->{
                callback.updateRecycler(finalNotes);
            });
        });
    }

    public void executeLongItemClick(NotesAdapter notesAdapter, DB db, int position, Callback callback){
        executor.execute(() -> {
            Notes selectedNote = null;
            int id = notesAdapter.getID(position);
            for (Notes x : db.dao().getAll()){
                if(x.getID() ==  id){
                    selectedNote = x;
                    break;
                }
            }

            Notes finalSelectedNote = selectedNote;
            handler.post(() ->{
                callback.LongItemClickHelper(finalSelectedNote);
            });
        });
    }

    public void executeUpdateRecycler(DB db, Callback callback){
        executor.execute(() -> {
            List<Notes> notes = new ArrayList<>();
            notes = db.dao().getAll();
            List<Notes> finalNotes = notes;
            handler.post(() ->{
                callback.updateRecycler(finalNotes);
            });
        });
    }

    public void insertNewNote(DB db, Callback callback, Notes note){
        executor.execute(() -> {
            List<Notes> notes = new ArrayList<>();
            db.dao().insert(note);
            notes = db.dao().getAll();
            List<Notes> finalNotes = notes;

        });
    }


}
