package com.example.lux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends AppCompatActivity{

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private EditText txtN, txtS, txtE;
    String usuarioID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);


    }

    //-------------Abre a tela MainActivity---------------
    public void abrirMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    //-------------Faz a criação de um novo usuario---------------
    public void novoUsuario(View view){
        txtN = findViewById(R.id.txtUsuario);
        txtS = findViewById(R.id.editSenha);
        txtE = findViewById(R.id.txtEmail);

        String nome = txtN.getText().toString();
        String senha = txtS.getText().toString();
        String email = txtE.getText().toString();
        //--------------Verifica se os campos estão vazios--------------
        if(nome.isEmpty() || senha.isEmpty() || email.isEmpty()){
            Snackbar snackbar = Snackbar.make(view, "Preencha os campos corretamente", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() { //Função do firebase onde faz a inserção do email
                //e senha do usuario que cadastra-rá
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SalvarDadosUsuario();//Chama o metodo para salvar o nome do usuario no firebase
                        abrirMain();
                    } else {//Verificação das condições para criação da conta
                        String erro;
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e){
                            erro = "A senha deve conter mais de 6 dígitos!"; //Firebase não aceita cadastros em que a senha contem menos de 6 caracteres
                        } catch (FirebaseAuthInvalidCredentialsException e){
                            erro = "E-mail inválido";
                        } catch (FirebaseAuthUserCollisionException e){
                            erro = "Esta conta já foi cadastrada";
                        } catch (Exception e){
                            erro = "Erro ao cadastrar usuario";
                        }

                        Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT);
                        snackbar.show();

                    }
                }
            });
        }

    }

    //-------------Metodo criado para coletar o nome do usuario e inserir no banco de dados---------------
    private void SalvarDadosUsuario(){
        String nome = txtN.getText().toString();
        String email = txtE.getText().toString();
        nome = nome.toLowerCase();
        email = email.toLowerCase();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("nome",nome);

        usuarioID=FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(email);
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db", "Sucesso ao salvar os dados");
            }
        })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("db_error", "Erro ao salvar os dados"+e.toString());
                }
            });
    }

}