package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Aluno;
import com.br.escolademusicaapplication.adaptor.AlunoRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListaAlunosRegistradoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlunoRecyclerViewAdapter recyclerViewAdapter;
    private List<Aluno> alunos;
    private EditText txtBuscaNome;
    private Button btnVoltarAdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos_registrado);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        btnVoltarAdm = findViewById(R.id.btnVoltarAdm);
        btnVoltarAdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                voltaAdm();
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewAlunos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializa a lista de alunos
        alunos = new ArrayList<>();

        // Encontra e configura o EditText de busca
        txtBuscaNome = findViewById(R.id.txtBuscaNome);
        txtBuscaNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filtrarAlunos(s.toString().toLowerCase());
            }
        });

        buscarAlunosRegistrados();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza a lista de alunos sempre que a tela for retomada
        buscarAlunosRegistrados();
    }

    private void buscarAlunosRegistrados() {
        Conexao conexao = Conexao.getInstance(this);
        alunos = conexao.buscarTodosAlunos();

        if (alunos != null && !alunos.isEmpty()) {
            // Define as opções de status para o spinner
            String[] opcoesStatus = {"Status1", "Status2", "Status3"};

            // Configura o listener de clique para o adaptador
            AlunoRecyclerViewAdapter.OnItemClickListener listener = new AlunoRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Aluno aluno) {
                    // Abre a tela de detalhes do aluno com os dados do aluno clicado
                    Intent intent = new Intent(ListaAlunosRegistradoActivity.this, DadosAlunoActivity.class);
                    // Passa o ID do aluno como extra na Intent
                    intent.putExtra("alunoId", aluno.getAluno_id());
                    startActivity(intent);
                }
            };

            // Cria o adaptador com o contexto, lista de alunos, opções de status e ouvinte de cliques
            recyclerViewAdapter = new AlunoRecyclerViewAdapter(this, alunos, opcoesStatus, listener);
            recyclerView.setAdapter(recyclerViewAdapter);
        } else {
            Toast.makeText(this, "Nenhum aluno registrado encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void filtrarAlunos(String texto) {
        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.filtrarAlunos(texto); // Chama o método de filtragem no adaptador
        }
    }

    public void voltaAdm(){
        Intent intent = new Intent(this, PainelAdmActivity.class);
        startActivity(intent);
    }
}
