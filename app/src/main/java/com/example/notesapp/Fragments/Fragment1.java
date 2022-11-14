package com.example.notesapp.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notesapp.Adapters.NotesAdapter;
import com.example.notesapp.MainActivity;
import com.example.notesapp.Models.Notes;
import com.example.notesapp.R;
import com.example.notesapp.RecyclerViewInterface;
import com.example.notesapp.db.DB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment1 extends Fragment implements RecyclerViewInterface, PopupMenu.OnMenuItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    NotesAdapter notesAdapter;
    List<Notes> notes = new ArrayList<>();
    DB db ;
    FloatingActionButton add;
    View root;
    Notes selectedNote;
    public Fragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment1 newInstance(String param1, String param2) {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment1, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler);
        db = DB.getInstance(getActivity());
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            Notes new_note = (Notes) bundle.getSerializable("note");
            if(bundle.getString("req") == "insert"){
                db.dao().insert(new_note);
            }
            else{
                db.dao().update(new_note.getID(), new_note.getTitle(), new_note.getNote());
            }
            notesAdapter.notifyDataSetChanged();
        }
        notes.clear();
        notes = db.dao().getAll();
        updateRecycler(notes);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_1, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Search for a note");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                notes = db.dao().getAll();
                updateRecycler(notes);
                notesAdapter.getFilter().filter(s);
                notesAdapter.notifyDataSetChanged();
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_action) {
            ((MainActivity)getActivity()).getNoteTaker();
            System.out.println("new note");
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesAdapter = new NotesAdapter((MainActivity)getActivity(), notes, this);
        recyclerView.setAdapter(notesAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        Notes note = null;
        int id = notesAdapter.getID(position);
        for (Notes x : db.dao().getAll()){
            if(x.getID() ==  id){
                note = x;
                break;
            }
        }
        bundle.putSerializable("old_note", note);
        System.out.println(((Notes)bundle.getSerializable("old_note")).getTitle());
        ((MainActivity)getActivity()).getNoteTaker(bundle);
    }

    @Override
    public void sendUpdatedList(List<Notes> filteredList) {
        updateRecycler(filteredList);
    }

    @Override
    public void LongItemClick(int position) {
        int id = notesAdapter.getID(position);
        for (Notes x : db.dao().getAll()){
            if(x.getID() ==  id){
                selectedNote = x;
                break;
            }
        }
        PopupMenu popupMenu = new PopupMenu((MainActivity)getActivity(), recyclerView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.delete:
                db.dao().delete(selectedNote);
                return true;
            case R.id.change_title:
                AlertDialog.Builder alert = new AlertDialog.Builder((MainActivity)getActivity());
                alert.setTitle("Title");
                alert.setMessage("Message :");


                final EditText input = new EditText((MainActivity)getActivity());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        db.dao().update(selectedNote.getID(), value, selectedNote.getNote());
                        updateRecycler(db.dao().getAll());
                        return;
                    }
                });

                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                return;
                            }
                        });
                alert.show();
        }
        updateRecycler(db.dao().getAll());
        return false;
    }
}