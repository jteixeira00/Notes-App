package com.example.notesapp;


import com.example.notesapp.Models.Notes;

import java.util.List;

public interface RecyclerViewInterface {
    void onItemClick(int position);
    void sendUpdatedList(List<Notes> filteredList);
    void LongItemClick(int position);
}
