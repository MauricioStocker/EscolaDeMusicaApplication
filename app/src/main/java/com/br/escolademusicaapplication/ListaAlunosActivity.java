package com.br.escolademusicaapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.adaptor.RankAlunoAdapter;
import com.br.escolademusicaapplication.OBJETOS.Aluno_Professor;
import com.bumptech.glide.Glide;

import java.util.List;

public class ListaAlunosActivity extends AppCompatActivity {
    private Conexao conexao;
    private ImageView filtroPrimeiroBim, filtroSegundoBim, filtroBimTotal;
    private boolean filtroPrimeiroBimAtivo = false;
    private boolean filtroSegundoBimAtivo = false;

    private Button btnVoltarPrincipalRank;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);
        conexao = Conexao.getInstance(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        btnVoltarPrincipalRank = findViewById(R.id.btnVoltarPrincipalRank);
        btnVoltarPrincipalRank.setOnClickListener(v -> voltarTelaPrincipal());
        filtroPrimeiroBim = findViewById(R.id.filtroPrimeiroBim);
        startGifAnimation(filtroPrimeiroBim, R.raw.funil);
        filtroSegundoBim = findViewById(R.id.filtroSegundoBim);
        startGifAnimation(filtroSegundoBim, R.raw.funil);
        filtroBimTotal = findViewById(R.id.filtroBimTotal);
        startGifAnimation(filtroBimTotal, R.raw.funil);

        // Configurar os listeners dos filtros
        filtroPrimeiroBim.setOnClickListener(v -> {
            filtroPrimeiroBimAtivo = true;
            filtroSegundoBimAtivo = false;
            // Atualizar o RecyclerView com os dados do primeiro bimestre
            updateRecyclerView();
        });

        filtroSegundoBim.setOnClickListener(v -> {
            filtroPrimeiroBimAtivo = false;
            filtroSegundoBimAtivo = true;
            // Atualizar o RecyclerView com os dados do segundo bimestre
            updateRecyclerView();
        });

        filtroBimTotal.setOnClickListener(v -> {
            filtroPrimeiroBimAtivo = false;
            filtroSegundoBimAtivo = false;
            // Atualizar o RecyclerView com os dados do total dos bimestres
            updateRecyclerView();
        });

        // Obter a referência do RecyclerView no layout
        RecyclerView recyclerViewAlunos = findViewById(R.id.recyclerViewAlunos);

        // Configurar o layout manager para o RecyclerView (no caso, usaremos LinearLayoutManager)
        recyclerViewAlunos.setLayoutManager(new LinearLayoutManager(this));

        // Obter a lista de alunos com os dados necessários (nome do aluno, nome do professor e nota total)
        List<Aluno_Professor> listaAlunos = getListaAlunos();

        // Criar um adaptador personalizado para exibir os dados no RecyclerView
        RankAlunoAdapter adapter = new RankAlunoAdapter(this, listaAlunos, filtroPrimeiroBimAtivo, filtroSegundoBimAtivo);

        // Definir o adaptador personalizado no RecyclerView
        recyclerViewAlunos.setAdapter(adapter);
    }

    // Método fictício para obter a lista de alunos com os dados necessários
    private List<Aluno_Professor> getListaAlunos() {
        // Supondo que o contexto da activity esteja disponível

        // Chamar o método adequado para obter a lista de alunos de acordo com o filtro selecionado
        if (filtroPrimeiroBimAtivo) {
            return conexao.listarAlunosPorNotaPrimeiroBimestre();
        } else if (filtroSegundoBimAtivo) {
            return conexao.listarAlunosPorNotaSegundoBimestre();
        } else {
            return conexao.listarAlunosPorNota(); // Nota total
        }
    }

    // Método para atualizar o RecyclerView com os dados do filtro selecionado
    private void updateRecyclerView() {
        List<Aluno_Professor> listaAlunos = getListaAlunos();
        RankAlunoAdapter adapter = new RankAlunoAdapter(this, listaAlunos, filtroPrimeiroBimAtivo, filtroSegundoBimAtivo);
        RecyclerView recyclerViewAlunos = findViewById(R.id.recyclerViewAlunos);
        recyclerViewAlunos.setAdapter(adapter);
    }

    private void startGifAnimation(ImageView imageView, int gifResource) {
        Glide.with(this).asGif().load(gifResource).into(imageView);
    }

    public void voltarTelaPrincipal() {
        Intent intent = new Intent(this, TelaPrincipalActivity.class);
        startActivity(intent);
        finish();
    }
}
