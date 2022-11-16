package com.example.notesapp.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notesapp.Adapters.NotesAdapter;
import com.example.notesapp.MQTTHelper;
import com.example.notesapp.MainActivity;
import com.example.notesapp.Models.Notes;
import com.example.notesapp.Models.Topic;
import com.example.notesapp.R;
import com.example.notesapp.RecyclerViewInterface;
import com.example.notesapp.TaskManager.TaskManager;
import com.example.notesapp.db.DB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment1 extends Fragment implements RecyclerViewInterface, PopupMenu.OnMenuItemClickListener, TaskManager.Callback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static MQTTHelper helper;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button topicsBtn;
    RecyclerView recyclerView;
    NotesAdapter notesAdapter;
    DB db ;
    FloatingActionButton add;
    View root;
    Notes selectedNote;
    TaskManager taskManager = new TaskManager();

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
        topicsBtn = (Button) root.findViewById(R.id.topics);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler);
        db = DB.getInstance(getActivity());
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        taskManager.executeOnCreateView(notesAdapter, bundle, db, this);

        topicsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getTopics();
            }
        });
        Log.d("yoyo", "start");
        connect();
        //checkTopics();
        Log.d("yoyo", "after connect");
        return root;
    }

    //calling this in onQueryTextChange != Callback callback, so had to create this function
    public void onQueryTextChangeAuxiliary(String s){
        taskManager.executeOnQueryTextChange(db, s, this);
    }

    //onQueryTextChange task manager goes here
    public void queryChangeHelper(List<Notes> newNotes, String s){
        updateRecycler(newNotes);
        notesAdapter.getFilter().filter(s);
        notesAdapter.notifyDataSetChanged();
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
                onQueryTextChangeAuxiliary(s);
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

    public void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesAdapter = new NotesAdapter((MainActivity)getActivity(), notes, this);
        recyclerView.setAdapter(notesAdapter);
    }

    //onItemClick task manager goes here
    public void itemClickHelper(Notes note){
        Bundle bundle = new Bundle();
        bundle.putSerializable("old_note", note);
        System.out.println(((Notes)bundle.getSerializable("old_note")).getTitle());
        ((MainActivity)getActivity()).getNoteTaker(bundle);
    }

    @Override
    public void onItemClick(int position) {
        taskManager.executeOnItemClick(notesAdapter, db, position, this);

    }

    @Override
    public void sendUpdatedList(List<Notes> filteredList) {
        updateRecycler(filteredList);
    }



    public void LongItemClickHelper(Notes newSelected){
        selectedNote = newSelected;

        //fix popupMenu so it overlaps now
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuOverlapAnchor);
        PopupMenu popupMenu = new PopupMenu(contextThemeWrapper, recyclerView);

        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public void LongItemClick(int position) {
        taskManager.executeLongItemClick(notesAdapter, db, position,this);
    }


    //calling this in onMenuItemClick:onClick != Callback callback, so had to create this function
    public void updateAuxiliary(String value){
        taskManager.executeUpdateItem(db, selectedNote, value, this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.delete:
                taskManager.executeDeleteItem(db, selectedNote,this);
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
                        updateAuxiliary(value);
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
        taskManager.executeUpdateRecycler(db, this);
        return false;
    }

    private void checkTopics(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TOPIC", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("topics", null);
        Type type = new TypeToken<ArrayList<Topic>>() {

        }.getType();

        ArrayList <Topic> topicsArray = gson.fromJson(json, type);

        if(topicsArray.size()>0)connect();
    }

    private void connect() {
        helper = new MQTTHelper(getActivity(), "clientId-vcvCWavi23", "testtopic/yoyo");

        helper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                helper.subscribeToTopic("testtopic/yoyo");
                //for(int i = 0; i < topicsArray.size(); i++) helper.subscribeToTopic(topicsArray.get(i).topicName, topicsArray.get(i).qos);
                Log.d("connected", "CONNECTED CRL");
            }

            @Override
            public void connectionLost(Throwable cause) {
                helper.stop();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                /*ArrayList<String> noteInfo = new ArrayList<String>();
                noteInfo.add(message.toString());*/
                Log.d("messageArrived", "msg:"+message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        helper.connect();
    }

    public void publishMessage(MQTTHelper client, Notes note, int qos, String topic, boolean init){
        try{
            byte[] encodedPayload;
            String[] msg = new String[3];
            if(init) {
                msg[0] = note.getTitle();
                msg[1] = note.getNote();
                msg[2] = note.getDate();
            }
            Gson gson = new Gson();
            String json = gson.toJson(msg); //não aceita arrays

            encodedPayload = json.getBytes(StandardCharsets.UTF_8);
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setQos(qos);

            client.mqttAndroidClient.publish(topic, message);
        } catch (MqttException e){
            Log.w("O", "MqttException");
        }
    }
}