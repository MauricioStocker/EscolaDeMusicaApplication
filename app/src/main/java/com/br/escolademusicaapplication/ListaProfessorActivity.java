package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Professor;
import com.br.escolademusicaapplication.PainelAdmActivity;
import com.br.escolademusicaapplication.adaptor.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaProfessorActivity extends AppCompatActivity {
    private Conexao conexao; // Declaração da instância de Conexao
    EditText txtBuscaNome;
    RecyclerView recyclerView;
    Context context;
    LinearLayout linearLayout;
    RecyclerView.Adapter recycleViewAdapter;
    RecyclerView.LayoutManager recycleViewLayoutManager;

    Button btnVoltarAdm;
    List<Professor> professors = new ArrayList<>();
    List<Integer> ids = new ArrayList<>();
    List<String> nomes = new ArrayList<>();
    List<String> disciplinas = new ArrayList<>();
    List<String> sexos = new ArrayList<>();
    List<String> fotos = new ArrayList<>();
    Integer[] dados_ids;
    String[] dados_nomes = new String[]{};
    String[] dados_disciplina = new String[]{};

    String[] dados_foto = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_professor);
        conexao = Conexao.getInstance(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        txtBuscaNome = findViewById(R.id.txtBuscaNome);
        txtBuscaNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String nomeDigitado = s.toString().toLowerCase(); // Converter o texto digitado para minúsculas
                List<Professor> copiaProfessor = new ArrayList<>(professors);
                ids = new ArrayList<>();
                nomes = new ArrayList<>();
                disciplinas = new ArrayList<>();
                fotos = new ArrayList<>();
                for (Professor professor : copiaProfessor) {
                    String nomeProfessor = professor.getProfessor_nome().toLowerCase(); // Converter o nome do professor para minúsculas
                    String disciplina = String.valueOf(professor.getProfessor_disciplina()).toLowerCase(); // Converter a disciplina para minúsculas
                    if (nomeProfessor.contains(nomeDigitado) || disciplina.contains(nomeDigitado)) {
                        ids.add(professor.getProfessor_id());
                        nomes.add(professor.getProfessor_nome());
                        disciplinas.add(String.valueOf(professor.getProfessor_disciplina()));
                        fotos.add(String.valueOf(professor.getProfessor_foto()));
                    }
                }
                dados_ids = ids.toArray(new Integer[0]);
                dados_nomes = nomes.toArray(new String[0]);
                dados_disciplina = disciplinas.toArray(new String[0]);
                dados_foto = fotos.toArray(new String[0]);
                preencheRecycler();
            }
        });


        btnVoltarAdm = findViewById(R.id.btnVoltarAdm);
        btnVoltarAdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaTelaAdm();
                finish();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        buscaProfessor();

    }

    @Override
    public void onResume() {
        super.onResume();
        buscarTodosProfessores();
    }

    public void buscaProfessor() {
        professors = conexao.buscarTodosProfessores();
        for (Professor professor : professors) {

            ids.add(professor.getProfessor_id());
            nomes.add(professor.getProfessor_nome());
            disciplinas.add(String.valueOf(professor.getProfessor_disciplina()));
            sexos.add(String.valueOf(professor.getPorfessor_sexo()));
            fotos.add(String.valueOf(professor.getProfessor_foto()));
        }
        dados_ids = ids.toArray(new Integer[0]);
        dados_nomes = nomes.toArray(new String[0]);
        dados_disciplina = disciplinas.toArray(new String[0]);

        dados_foto = fotos.toArray(new String[0]);

        recycleViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recycleViewLayoutManager);
        preencheRecycler();
    }

    private void preencheRecycler() {
        recycleViewAdapter = new RecyclerViewAdapter(this, Arrays.asList(dados_ids), dados_nomes, dados_disciplina, dados_foto);
        recyclerView.setAdapter(recycleViewAdapter);
    }

    private void buscarTodosProfessores() {
        conexao.buscarTodosProfessores();
    }

    public void chamaTelaAdm() {
        Intent intent = new Intent(this, PainelAdmActivity.class);
        startActivity(intent);
        finish();
    }
}