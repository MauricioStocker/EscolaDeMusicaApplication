package com.br.escolademusicaapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class TelaPrincipalActivity extends AppCompatActivity {

    private ImageView imageViewLogin, imgWhatsapp;
    private ImageView imageViewImagensEscola;
    private ImageView imageViewCursos;
    private ImageView imageViewRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        imageViewLogin = findViewById(R.id.imageViewLogin);
        imageViewImagensEscola = findViewById(R.id.imageViewImagensEscola);
        imageViewCursos = findViewById(R.id.imageViewCursos);
        imageViewRank = findViewById(R.id.imageViewRank);
        imgWhatsapp = findViewById(R.id.imgWhatsapp);


        // Carregar e iniciar a animação para cada ImageView
        startGifAnimation(imageViewLogin, R.raw.login1);
        startGifAnimation(imageViewImagensEscola, R.raw.university);
        startGifAnimation(imageViewCursos, R.raw.pictures);
        startGifAnimation(imageViewRank, R.raw.podium);
        imgWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" + "+5541995430914";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        imageViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chamaTelaLogin();
            }
        });
        imageViewImagensEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaGaleriaFotos();
            }
        });
        imageViewCursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaTelaListaCurso();
            }
        });
        imageViewRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaTelaRank();
            }
        });
    }



    private void startGifAnimation(ImageView imageView, int gifResource) {
        Glide.with(this).asGif().load(gifResource).into(imageView);
    }
    public void chamaTelaLogin(){
        Intent intent =  new Intent(this, TelaLoginActivity.class);
        startActivity(intent);
    }
    public void chamaGaleriaFotos(){
        Intent intent =  new Intent(this, GaleriaFotosActivity.class);
        startActivity(intent);
    }
    public void chamaTelaListaCurso(){
        Intent intent =  new Intent(this, ListaCursosActivity.class);
        startActivity(intent);
    }
    public void chamaTelaRank(){
        Intent intent =  new Intent(this, ListaAlunosActivity.class);
        startActivity(intent);
    }
}
