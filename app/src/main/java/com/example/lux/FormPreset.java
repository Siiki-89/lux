package com.example.lux;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;



public class FormPreset extends AppCompatActivity {
    ImageView imgView;
    Bitmap bitmap;

    TextView mColorValues;
    EditText editText;
    int r, g, b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_preset);

        editText = findViewById(R.id.editNome);

        imgView = findViewById(R.id.colorPickers2);
        mColorValues = findViewById(R.id.displayValues2);

        imgView.setDrawingCacheEnabled(true);
        imgView.buildDrawingCache(true);
        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                        bitmap = imgView.getDrawingCache();
                        int pixels = bitmap.getPixel((int) event.getX(), (int) event.getY());

                        r = Color.red(pixels);
                        g = Color.green(pixels);
                        b = Color.blue(pixels);

                        String hex = "#" + Integer.toHexString(pixels);
                        mColorValues.setText("RGB: " + r + ", " + g + ", " + b + " \nHEX:" + hex);

                    }
                } catch (Exception e){

                }

                return true;
            }
        });
    }
    public void enviarPreset(View view){
        String nome = editText.getText().toString();
        //bancoPreset.enviarRealDataBase(nome, "R", r);
        //bancoPreset.enviarRealDataBase(nome, "G", g);
        //bancoPreset.enviarRealDataBase(nome, "B", b);

    }

    //------------Abrir tela---------------
    public void abrirSala(View view){
        Intent intent = new Intent(getApplicationContext(), FormSala.class);
        startActivity(intent);
    }
}