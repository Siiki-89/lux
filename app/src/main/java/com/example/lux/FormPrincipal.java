package com.example.lux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.lux.databinding.ActivityFormPrincipalBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class FormPrincipal extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    View view;
    TextView txtUsuario;
    String usuarioID;
    private int cont=0;

    private Button btnEnvair;
    private ImageView imageView, imgPowerOnSala, imgPowerOnGaragem;

    DatabaseReference mDatabase;
    private Uri uriImagem;
    private FirebaseStorage storage;
    private String email = null;
    int statusGaragem, statusSala;

    Snackbar snackbar;
    @Override
    protected void onStart() {
        super.onStart();

        txtUsuario = findViewById(R.id.txtNomeUsuario);
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        imageView = findViewById(R.id.imageUser);
        storage = FirebaseStorage.getInstance();
        btnEnvair = findViewById(R.id.btnEnviar);

        imgPowerOnGaragem = findViewById(R.id.imgPowerGaragem);
        imgPowerOnSala = findViewById(R.id.imgPowerSala);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //-------------OBTER E-MAIL DO USUARIO--------------
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                email = profile.getEmail();
            }
        }
        final String[] comodo = new String[2]; //comodos
        //-------------OBTER NOME DO USUARIO ATRAVES DO E-MAIL---------------
        DocumentReference reference = db.collection("Usuarios").document(email);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        String informativo =(String) documentSnapshot.getData().get("nome");
                        informativo = informativo.substring(0,1).toUpperCase().concat(informativo.substring(1));
                        txtUsuario.setText(informativo);
                        //System.out.println("informativo"+informativo);
                        //armazenar dados na memoria do celular para obter em outras telas
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Garagem", informativo+"Garagem"); // Substitua "chave" pelo nome da chave e "valor" pelo valor que você deseja salvar
                        editor.putString("Sala", informativo+"Sala"); // Substitua "chave" pelo nome da chave e "valor" pelo valor que você deseja salvar
                        editor.apply();
                    }

                } else {

                }
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        comodo[0] = sharedPreferences.getString("Garagem", "não tem garagem");
        comodo[1] = sharedPreferences.getString("Sala", "não tem sala");

        if(cont==0){// Contra bugs
            fireIMG(email);
            cont++;
        }

        //------------- Obter Status da lamapada garagem e alterar img de acordo com status obtido ---------------
        mDatabase.child(comodo[0]).child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    statusGaragem = snapshot.getValue(Integer.class);
                    if(statusGaragem==0){
                        imgPowerOnGaragem.setImageResource(R.drawable.ligh_off);
                    } else {
                        imgPowerOnGaragem.setImageResource(R.drawable.ligh_on);
                    }
                } catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //------------- Ligar e desligar lampada da garagem ---------------


        imgPowerOnGaragem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(statusGaragem==0){
                    mDatabase.child(comodo[0]).child("Status").setValue(1);
                    snackbar = Snackbar.make(view, "Lampada ligada", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    mDatabase.child(comodo[0]).child("Status").setValue(0);
                    snackbar = Snackbar.make(view, "Lampada Desligada", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });
        //------------- Obter Status da lamapada sala e alterar img de acordo com status obtido ---------------
        mDatabase.child(comodo[1]).child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    statusSala = snapshot.getValue(Integer.class);
                    if(statusSala==0){
                        imgPowerOnSala.setImageResource(R.drawable.ligh_off);
                    } else {
                        imgPowerOnSala.setImageResource(R.drawable.ligh_on);
                    }
                } catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //------------- Ligar e desligar lampada da sala ---------------

        imgPowerOnSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(statusSala==0){
                    mDatabase.child(comodo[1]).child("Status").setValue(1);
                    snackbar = Snackbar.make(view, "Lampada ligada", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    mDatabase.child(comodo[1]).child("Status").setValue(0);
                    snackbar = Snackbar.make(view, "Lampada desligada", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_principal);
    }

    //-----------Menu de opções-------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    //------------Obtem a opção selecionada nas opções criadas a partir do inflater (menu_layout)----------------
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.sair:
                deslogar();
                return true;
            case R.id.imagem:
                obterImagem(view);
                return true;
            default:
                return false;
        }
    }

    //-----------Obtem uma imagem a partidar da galeria do celular---------

    public void obterImagem(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Escolha sua imagem"), 0);
    }

    //--------------Obtem a imagem selecionada e poe no imageView para o usuario observar-------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== RESULT_OK){
            if(requestCode == 0){
                if(data != null){
                    uriImagem = data.getData();
                    Glide.with(getBaseContext()).asBitmap().load(uriImagem).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(imageView);
                }
            }
            btnEnvair.setVisibility(View.VISIBLE);
        }
    }

    //--------------Faz o envio da imagem obtida ao banco--------------
    private void uploudImagem(String email){//a imagem é salva no banco recebendo o email da pessoa, assim usando o email como referencia para coleta-la sempre quando precisar

        StorageReference reference = storage.getReference().child("imagens");//cria uma pasta chamada imagens
        StorageReference nomeImagem = reference.child(email +".jpg");//da a essa imagem um nome
        //comandos utilizados para reduzir o tamanho da imagem
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        UploadTask uploadTask = nomeImagem.putBytes(bytes.toByteArray());
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {//completa a tarefa se estiver tudo certo
                if(task.isSuccessful()){
                    System.out.println("Foto enviada");
                    Toast.makeText(getApplicationContext(),"Foto salva com sucesso!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Falha ao enviar foto!",Toast.LENGTH_SHORT).show();
                    System.out.println("Falha ao enviar foto");
                }
            }
        });
    }

    //--------------Coleta a imagem do banco de dados--------------
   private void fireIMG(String email){//Busca a imagem a partir do email do usuario
        StorageReference storageRef;
        storageRef = FirebaseStorage.getInstance().getReference("imagens/"+email+".jpg");//busca pela pasta imagens
        ProgressDialog progressDialog = new ProgressDialog(FormPrincipal.this);
        progressDialog.setMessage("");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            File localFile = File.createTempFile("tempfile",".jpg");
            storageRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());//converte para poder utiliza-la em uma ImageView

                    imageView.setImageBitmap(bitmap);//poe a imagem no imageView
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            });

        } catch (IOException e){
        }
    }

    //--------------Chama função ao clicar no botão--------------
    public void enviarImagem(View view){
        uploudImagem(email);
        btnEnvair.setVisibility(View.INVISIBLE);
    }

    //----------------ABRIR TELAS---------------
    public void abrirGaragem(View view){
        Intent intent = new Intent(getApplicationContext(), FormGaragem.class);
        startActivity(intent);
    }
    public void abrirSala(View view){
        Intent intent = new Intent(getApplicationContext(), FormSala.class);
        startActivity(intent);
    }
    private void abrirMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
    //----------------SAIR DO USUARIO----------
    public void deslogar(){
        FirebaseAuth.getInstance().signOut();
        abrirMain();
    }
}