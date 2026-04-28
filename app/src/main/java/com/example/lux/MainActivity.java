package com.example.lux;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText txtS, txtE;
    private ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if(usuarioAtual != null){//Codigo utilizado para verificar se o usuario estava logado antes de fechar o aplicativo pela ultima vez, caso já...
            abrirPrincipal();//abre a tela principal
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtS = findViewById(R.id.editSenha);
        txtE = findViewById(R.id.editEmail);
        progressBar = findViewById(R.id.progressBar);
    }

    //-------------Abrir telas--------------
    public void abrirCadastro(View view){
        Intent intent = new Intent(getApplicationContext(), FormCadastro.class);
        startActivity(intent);
        finish();
    }
    public void abrirPrincipal(){
        Intent intent = new Intent(getApplicationContext(), FormPrincipal.class);
        startActivity(intent);
        finish();
    }

    //-------------Condições para chamar metodo de autenticação--------------
    public void conferirLogin(View view){

        String email = txtE.getText().toString();
        String senha = txtS.getText().toString();


        if(senha.isEmpty() || email.isEmpty()){//Verifica se os campos foram preenchidos
            Snackbar snackbar = Snackbar.make(view, "Preencha os campos corretamente", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            AutenticarUsuario(view);
        }
    }

    //-------------Verifica se o email e senha existem no banco de dados--------------
    private void AutenticarUsuario(View view){
        String email = txtE.getText().toString();
        String senha = txtS.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {//É utilizado uma função
            //já criada pelo Firebase
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            abrirPrincipal();
                        }
                    },3000);
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        erro ="Erro ao logar usuario";
                    }
                    Snackbar snackbar = Snackbar.make(view, erro , Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });
    }
}