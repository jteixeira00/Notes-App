package com.example.notesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.notesapp.Fragments.Fragment1;

public class  PopupActivity extends Activity {

    private AlertDialog dialog;
    private Fragment1 frag1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        Intent intent = getIntent();
        createNewMessagePopup(intent.getStringExtra("note"));
        frag1 = new Fragment1();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
    }

    public void createNewMessagePopup(String message){
        Log.d("popup crll", "a");
        AlertDialog.Builder dialogBd = new AlertDialog.Builder(this);
        final View newMessagePopupView = getLayoutInflater().inflate(R.layout.new_message_popup, null);

        Log.d("popup crll", "b");


        TextView newNoteTitle = (TextView) newMessagePopupView.findViewById(R.id.newNoteTitleContent);
        TextView newNoteNote = (TextView) newMessagePopupView.findViewById(R.id.newNoteNoteContent);
        Button denyNote = (Button) newMessagePopupView.findViewById(R.id.denyBtn);
        Button acceptNote = (Button) newMessagePopupView.findViewById(R.id.acceptBtn);

        String[] output = new String[2];
        if(message.indexOf('&')!=-1){
            output = message.split("&", 2);
        } else {
            output[0] = "titulo #" + (int) Math.floor(Math.random() * 1000);
            output[1] = message;
        }

        newNoteTitle.setText(output[0]);
        newNoteNote.setText(output[1]);


        dialogBd.setView(newMessagePopupView);
        dialog = dialogBd.create();
        dialog.show();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        dialog.getWindow().setLayout((6 * width)/7, (4 * height)/5);

        Log.d("popup", "d");

        denyNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        String[] finalOutput = output;
        acceptNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                frag1.newNote(finalOutput[0], finalOutput[1]);

                dialog.dismiss();
            }
        });

    }
}