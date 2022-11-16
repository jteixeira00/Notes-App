package com.example.notesapp.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.notesapp.Adapters.NotesAdapter;
import com.example.notesapp.MainActivity;
import com.example.notesapp.Models.Notes;
import com.example.notesapp.Models.Topic;
import com.example.notesapp.R;
import com.example.notesapp.db.DB;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Topics#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Topics extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Slider qosSlider;
    EditText input;
    Button btnSub;
    Button btnUnsub;
    Button btnUnsubAll;
    TextView topicList;
    View root;
    ArrayList<Topic> topicsArray;

    public Topics() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Topics.
     */
    // TODO: Rename and change types and number of parameters
    public static Topics newInstance(String param1, String param2) {
        Topics fragment = new Topics();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_topics, container, false);
        qosSlider = root.findViewById(R.id.qosSlider);
        input = root.findViewById(R.id.inputTopic);
        btnSub = (Button) root.findViewById(R.id.subTopic);
        btnUnsub = (Button) root.findViewById(R.id.unsubTopic);
        btnUnsubAll = (Button) root.findViewById(R.id.unsubAllTopics);
        topicList = (TextView) root.findViewById(R.id.subbedTopics);
        loadTopics();
        setHasOptionsMenu(true);

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTopic(input.getText().toString(), (int) qosSlider.getValue());
            }
        });
        btnUnsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeTopic(input.getText().toString());
            }
        });
        btnUnsubAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllTopics();
            }
        });

        return root;
    }

    private void loadTopics() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TOPIC", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("topics", null);
        Type type = new TypeToken<ArrayList<Topic>>() {

        }.getType();

        topicsArray = gson.fromJson(json, type);

        if (topicsArray == null || topicsArray.size() < 1) {
            topicsArray = new ArrayList<Topic>();
            topicList.setText("Choose QOS and write topic name, then press SUBSCRIBE to add topic");
        } else {
            topicList.setText("Subscribed Topics:\n");
            for (int i = 0; i < topicsArray.size(); i++) {
                topicList.setText(topicList.getText().toString() + '\n' + topicsArray.get(i).topicName + "\nwith qos:" + topicsArray.get(i).qos + "\n\n");
            }
        }
    }

    private void addTopic(String topicName, int qos) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TOPIC", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        //Fragment1.helper.subscribeToTopic(topicName, qos);

        topicsArray.add(new Topic(topicName, qos));
        String json = gson.toJson(topicsArray);

        editor.putString("topics", json);
        editor.apply();
        topicList.setText("Subscribed Topics:\n");
        loadTopics();
    }

    private void removeTopic(String topicName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TOPIC", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString("topics", null);
        Type type = new TypeToken<ArrayList<Topic>>() {

        }.getType();

        topicsArray = gson.fromJson(json, type);
        for(int i = 0; i < topicsArray.size(); i++){
            if(i > topicsArray.size())break;
            if(topicsArray.get(i).topicName.equals(topicName)){
                topicsArray.remove(i);
                //Fragment1.helper.unsubscribeFromTopic(topicName);
                i--;
            }
        }

        gson = new Gson();
        json = gson.toJson(topicsArray);
        editor.putString("topics", json);
        editor.apply();
        loadTopics();
    }

    private void removeAllTopics() {
        topicsArray = new ArrayList<Topic>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TOPIC", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(topicsArray);

        editor.putString("topics", json);
        editor.apply();
        loadTopics();
    }
}