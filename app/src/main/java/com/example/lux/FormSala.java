package com.example.lux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FormSala extends AppCompatActivity {

    ImageView imgView, imgAmarelo, imgVerde, imgAzul, imgRoxo, imgLaranja, imgVermelho;
    Switch aSwitch;
    Bitmap bitmap;
    DatabaseReference mDatabase;
    View view;
    String lampada;
    int statusLampada;
    private Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_sala);
        imgView = findViewById(R.id.colorPickers);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        aSwitch= findViewById(R.id.switch2);
        view = findViewById(R.id.viewColor);

        //Cores
        imgAmarelo = findViewById(R.id.imgAmarelo);
        imgAzul = findViewById(R.id.imgAzul);
        imgVerde = findViewById(R.id.imgVerde);
        imgVermelho = findViewById(R.id.imgVermelho);
        imgRoxo = findViewById(R.id.imgRoxo);
        imgLaranja = findViewById(R.id.imgLaranja);


        imgView.setDrawingCacheEnabled(true);
        imgView.buildDrawingCache(true);

        //Obter valor compartilhado pela memoria
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lampada = sharedPreferences.getString("Sala", "não tem sala");

        //------------- Obter a cor do RGB e enviar ao banco --------------

        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                        bitmap = imgView.getDrawingCache();
                        int pixels = bitmap.getPixel((int) event.getX(), (int) event.getY());

                        int r = Color.red(pixels);
                        int g = Color.green(pixels);
                        int b = Color.blue(pixels);
                        view.setBackgroundColor(Color.rgb(r, g, b));

                        mDatabase.child(lampada).child("R").setValue(r);
                        mDatabase.child(lampada).child("G").setValue(g);
                        mDatabase.child(lampada).child("B").setValue(b);

                        salvo();
                    }
                } catch (Exception e){

                }
                return true;
            }
        });

        //------------- Ligar e desligar lampada --------------
        aSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int led;
                if(aSwitch.isChecked()){
                    led = 0;
                    mDatabase.child(lampada).child("Status").setValue(led);
                    salvo();
                }
                else{
                    led=1;
                    mDatabase.child(lampada).child("Status").setValue(led);
                    salvo();
                }
                return false;
            }
        });

        //------------- Obter o valor das cores e enviar ao banco --------------
        imgAmarelo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatabase.child(lampada).child("R").setValue(237);
                mDatabase.child(lampada).child("G").setValue(217);
                mDatabase.child(lampada).child("B").setValue(0);
                view.setBackgroundColor(Color.rgb(237, 217, 0));
                salvo();
                return false;
            }
        });
        imgLaranja.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatabase.child(lampada).child("R").setValue(255);
                mDatabase.child(lampada).child("G").setValue(119);
                mDatabase.child(lampada).child("B").setValue(9);
                view.setBackgroundColor(Color.rgb(255, 119, 9));
                salvo();
                return false;
            }
        });
        imgAzul.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatabase.child(lampada).child("R").setValue(31);
                mDatabase.child(lampada).child("G").setValue(41);
                mDatabase.child(lampada).child("B").setValue(130);
                view.setBackgroundColor(Color.rgb(31, 41, 130));
                salvo();
                return false;
            }
        });
        imgRoxo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatabase.child(lampada).child("R").setValue(123);
                mDatabase.child(lampada).child("G").setValue(43);
                mDatabase.child(lampada).child("B").setValue(138);
                view.setBackgroundColor(Color.rgb(123, 43, 138));
                salvo();
                return false;
            }
        });
        imgVermelho.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatabase.child(lampada).child("R").setValue(190);
                mDatabase.child(lampada).child("G").setValue(29);
                mDatabase.child(lampada).child("B").setValue(37);
                view.setBackgroundColor(Color.rgb(190, 29, 37));
                salvo();
                return false;
            }
        });
        imgVerde.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatabase.child(lampada).child("R").setValue(130);
                mDatabase.child(lampada).child("G").setValue(242);
                mDatabase.child(lampada).child("B").setValue(16);
                view.setBackgroundColor(Color.rgb(130, 242, 16));
                salvo();
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        final int[] i = {0};
        mDatabase.child(lampada).child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(i[0] ==0){
                    i[0]++;
                    try {
                        statusLampada = snapshot.getValue(Integer.class);
                        if (statusLampada==1){
                            aSwitch.setChecked(true);
                        } else{
                            aSwitch.setChecked(false);
                        }
                    } catch (Exception e){}
                }
            };

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        super.onStart();
    }

    //-------------Abrir tela--------------
    public void abrirPreset(View view){
        Intent intent = new Intent(getApplicationContext(), FormPreset.class);
        startActivity(intent);
    }
    private void salvo(){
        snackbar = Snackbar.make(view, "Salvo!", Snackbar.LENGTH_SHORT);
        snackbar.show();

    };




}