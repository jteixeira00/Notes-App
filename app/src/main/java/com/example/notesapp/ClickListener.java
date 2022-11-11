package com.example.notesapp;

import androidx.cardview.widget.CardView;

import com.example.notesapp.Models.Notes;

public interface ClickListener {
    void onClick(Notes note);
    void onLongClick(Notes note, CardView cardView);
}
