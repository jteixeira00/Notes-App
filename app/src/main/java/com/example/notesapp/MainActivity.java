package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.notesapp.Fragments.Fragment1;
import com.example.notesapp.Fragments.NoteTaker;
import com.example.notesapp.Fragments.SendNote;
import com.example.notesapp.Fragments.Topics;
import com.example.notesapp.Models.Notes;
import com.example.notesapp.Models.Topic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


interface fragmentInterface{
    public void getFrag1();
}

public class MainActivity extends AppCompatActivity implements fragmentInterface{
    Fragment frag1 = new Fragment1();
    Fragment frag2 = new NoteTaker();
    Fragment frag3 = new Topics();
    Fragment frag4 = new SendNote();
    public static MQTTHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connect(loadTopics());
        getFrag1();
    }


    public void getFrag1() {
        FragmentManager fragmentManager = getFragmentManager();
        //clean bundle
        frag1.setArguments(null);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relativeLayout, frag1);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //overloaded method
    public void getFrag1(Bundle bundle){
        FragmentManager fragmentManager = getFragmentManager();
        frag1.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relativeLayout, frag1);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getNoteTaker(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(frag2);
        frag2 = new NoteTaker();
        fragmentTransaction.replace(R.id.relativeLayout, frag2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getNoteTaker(Bundle bundle){
        FragmentManager fragmentManager = getFragmentManager();
        frag2.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relativeLayout, frag2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getTopics(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(frag3);
        frag3 = new Topics();
        fragmentTransaction.replace(R.id.relativeLayout, frag3);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getTopics(Bundle bundle){
        FragmentManager fragmentManager = getFragmentManager();
        frag3.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relativeLayout, frag3);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getSendNote(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(frag3);
        frag3 = new Topics();
        fragmentTransaction.replace(R.id.relativeLayout, frag4);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getSendNote(Bundle bundle){
        FragmentManager fragmentManager = getFragmentManager();
        frag3.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relativeLayout, frag4);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private ArrayList<Topic> loadTopics(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("TOPIC", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("topics", null);
        Type type = new TypeToken<ArrayList<Topic>>() {

        }.getType();

        ArrayList <Topic> topicsArray = gson.fromJson(json, type);

        if(topicsArray.size()>0)return topicsArray;
        else return null;
    }

    private void connect(ArrayList<Topic> topicsArray) {
        helper = new MQTTHelper(this, "clientId-vcvCWavi23", "testtopic/yoyo");

        helper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if(topicsArray != null) for(int i = 0; i < topicsArray.size(); i++)
                    helper.subscribeToTopic(topicsArray.get(i).topicName, topicsArray.get(i).qos);
            }

            @Override
            public void connectionLost(Throwable cause) {
                helper.stop();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                /*ArrayList<String> noteInfo = new ArrayList<String>();
                noteInfo.add(message.toString());*/
                receivedNote(message.toString());
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

    private void receivedNote(String message){
        Log.d("aaa", "received note:" + message);
        String[] output = new String[2];
        if(message.indexOf('&')!=-1){
            output = message.split("&", 2);
            Log.d("a", "has &");
        } else {
            output[0] = "titulo #" + (int) Math.floor(Math.random() * 1000);
            output[1] = message;
        }
        //newNote(output[0], output[1]);
    }

    /*private void newNote(String title, String note){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
        Date date = new Date();

        Bundle bundle = this.getArguments();
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
    }*/
}