package com.example.notesapp.Fragments;

import android.app.Fragment;
import android.os.Bundle;



import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.notesapp.MainActivity;
import com.example.notesapp.Models.Notes;
import com.example.notesapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteTaker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteTaker extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText editTitle;
    EditText editNote;
    ImageView save;
    Notes notes;
    View root;

    public NoteTaker() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoteTaker.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteTaker newInstance(String param1, String param2) {
        NoteTaker fragment = new NoteTaker();
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
        root = inflater.inflate(R.layout.fragment_note_taker, container, false);
        save = (ImageView) root.findViewById(R.id.save);
        editTitle = (EditText) root.findViewById(R.id.editTitle);
        editNote = (EditText) root.findViewById(R.id.editNote);

        Bundle bundle = this.getArguments();

        if (bundle!=null){
            notes = (Notes) bundle.getSerializable("old_note");
            editTitle.setText(notes.getTitle());
            editNote.setText(notes.getNote());
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString();
                String note = editNote.getText().toString();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();
                if(bundle==null){
                    notes = new Notes();
                }
                notes.setTitle(title);
                notes.setNote(note);
                notes.setDate(dateFormatter.format(date));


                Bundle bundleRes = new Bundle();
                if(bundle == null){
                    bundleRes.putString("req", "insert");
                }
                else{
                    bundleRes.putString("req", "update");
                }
                bundleRes.putSerializable("note", notes);
                ((MainActivity)getActivity()).getFrag1(bundleRes);
            
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_2, menu);
        MenuItem back = menu.findItem(R.id.go_back);
        MenuItem save_button = menu.findItem(R.id.save_button);
        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.go_back) {
            ((MainActivity)getActivity()).getFrag1();
        }
        if (id == R.id.save){
            Bundle bundle = this.getArguments();
            String title = editTitle.getText().toString();
            String note = editNote.getText().toString();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
            Date date = new Date();
            if(bundle==null){
                notes = new Notes();
            }
            notes.setTitle(title);
            notes.setNote(note);
            notes.setDate(dateFormatter.format(date));


            Bundle bundleRes = new Bundle();
            if(bundle == null){
                bundleRes.putString("req", "insert");
            }
            else{
                bundleRes.putString("req", "update");
            }
            bundleRes.putSerializable("note", notes);
            ((MainActivity)getActivity()).getFrag1(bundleRes);
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onResume(){

        Bundle bundle = this.getArguments();

        if (bundle!=null){
            notes = (Notes) bundle.getSerializable("old_note");
            editTitle.setText(notes.getTitle());
            editNote.setText(notes.getNote());
        }
        super.onResume();
    }
}