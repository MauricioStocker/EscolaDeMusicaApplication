package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class PotalDoAlunoActivity extends AppCompatActivity {

    TextView txtNomeLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potal_do_aluno);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Recupera o nome do usuário do Intent
        String nomeUsuario = getIntent().getStringExtra("nomeUsuario");

        // Encontra o TextView na layout XML
        txtNomeLogado = findViewById(R.id.txtNomeLogado);

        // Define o texto do TextView com o nome do usuário
        if (nomeUsuario != null && !nomeUsuario.isEmpty()) {
            txtNomeLogado.setText("Bem-vindo : " + nomeUsuario);
        } else {
            txtNomeLogado.setText("Usuário não identificado");
        }

    }
}