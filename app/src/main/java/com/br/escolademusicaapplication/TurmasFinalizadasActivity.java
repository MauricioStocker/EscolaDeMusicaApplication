package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Turma;
import com.br.escolademusicaapplication.adaptor.TurmaAdapter;

import java.util.ArrayList;
import java.util.List;

public class TurmasFinalizadasActivity extends AppCompatActivity {

	private RecyclerView recyclerView;
	private TurmaAdapter adapter;
	private List<Turma> turmasList;
	private Conexao conexao;

	private Button bntVoltarPainelAdm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_turmas_finalizadas);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		bntVoltarPainelAdm = findViewById(R.id.bntVoltarPainelAdm);
		bntVoltarPainelAdm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				voltaTelaAdm();

			}
		});

		recyclerView = findViewById(R.id.recyclerViewTurmas);
		turmasList = new ArrayList<>();
		adapter = new TurmaAdapter(this, turmasList);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(adapter);

		conexao = Conexao.getInstance(this);

		// Carregar e exibir as turmas finalizadas
		carregarTurmasFinalizadas();
	}

	private void carregarTurmasFinalizadas() {
		// Obter a lista de todas as turmas finalizadas da base de dados
		List<Turma> turmasFinalizadas = conexao.buscarTodasTurmas();

		// Limpar a lista existente e adicionar as turmas finalizadas
		turmasList.clear();
		turmasList.addAll(turmasFinalizadas);

		// Notificar o adapter sobre a mudança de dados
		adapter.notifyDataSetChanged();
	}
	public void voltaTelaAdm(){
		Intent intent = new Intent(this, PainelAdmActivity.class);
		startActivity(intent);
	}
}
