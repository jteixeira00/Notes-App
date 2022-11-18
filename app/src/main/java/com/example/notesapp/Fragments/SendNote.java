package com.example.notesapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.notesapp.MainActivity;
import com.example.notesapp.R;
import com.google.android.material.slider.Slider;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SendNote#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendNote extends Fragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    Slider qosSlider;
    EditText topicInput;
    Button sendBtn;
    Button cancelBtn;
    View root;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendNote.
     */

    public static SendNote newInstance(String param1, String param2) {
        SendNote fragment = new SendNote();
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
        root = inflater.inflate(R.layout.fragment_send_note, container, false);
        qosSlider = (Slider) root.findViewById(R.id.qosSlider);
        topicInput = (EditText) root.findViewById(R.id.topicInput);
        sendBtn = (Button) root.findViewById(R.id.sendButtonSN);
        cancelBtn = (Button) root.findViewById(R.id.cancelButtonSN);

        String msg = getArguments().getString("msg");

        Log.d("SEND_NOTE", cancelBtn.getText().toString());

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("yoyo", "CANCEL");
                ((MainActivity) getActivity()).getFrag1();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("yoyo", "SEND");
                sendNote(msg, (int) qosSlider.getValue(), topicInput.getText().toString(), false);
                ((MainActivity) getActivity()).getFrag1();
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_4, menu);
        MenuItem back_button = menu.findItem(R.id.go_back);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.go_back) {
            ((MainActivity) getActivity()).getFrag1();
        }
        return super.onOptionsItemSelected(item);
    }

    public Slider getQosSlider() {
        return qosSlider;
    }

    public void sendNote(String msg, int qos, String topic, boolean init){
        MainActivity.helper.publish(msg, qos, topic, init);
    }
}