package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.notesapp.Fragments.Fragment1;
import com.example.notesapp.Fragments.NoteTaker;


interface fragmentInterface{
    public void getFrag1();
}

public class MainActivity extends AppCompatActivity implements fragmentInterface{
    Fragment frag1 = new Fragment1();
    Fragment frag2 = new NoteTaker();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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




}