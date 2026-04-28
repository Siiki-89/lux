package com.example.lux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FormGaragem extends AppCompatActivity {
    private Switch switchligado;
    String lampada;
    DatabaseReference mDatabase;
    View view;

    SeekBar seekBar;
// ...
    private int statusLampada, intensidadeLampada, intensidade;;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_garagem);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//botão de voltar
        view = findViewById(R.id.view);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        switchligado = findViewById(R.id.switch1);
        seekBar = findViewById(R.id.seekBar);

        //Obter valor compartilhado pela memoria
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lampada = sharedPreferences.getString("Garagem", "não tem garagem");

        //Verificar Status da lampada
        mDatabase.child(lampada).child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    statusLampada = snapshot.getValue(Integer.class);
                    if (statusLampada==1){
                        switchligado.setChecked(true);
                    } else{
                        switchligado.setChecked(false);
                    }
                } catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Verificar Intensidade da lampada
        mDatabase.child(lampada).child("Intensidade").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    intensidadeLampada = snapshot.getValue(Integer.class);
                    seekBar.setProgress(intensidadeLampada);
                } catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //------------- Obter valor do seekbar e enviar ao banco --------------

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                intensidade = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void salvo(){
        snackbar = Snackbar.make(view, "Salvo!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    };

    //--------------Envia para o realtime database o sinal para ligar o led-------------
    public void enviar(View view){
        int led;
        mDatabase.child(lampada).child("Intensidade").setValue(intensidade);
        if (switchligado.isChecked()){
            led = 1;
            mDatabase.child(lampada).child("Status").setValue(led); //Existe no realtime do firebase um campo chamado "led", esse campo está vinculado
            //ao arduino, o arduino estará agurdando o "status" ser alterado para assim executar o comando de liga e desliga
            salvo();
        } else {
            led = 0;
            mDatabase.child(lampada).child("Status").setValue(led);
            salvo();
        }
    }

}