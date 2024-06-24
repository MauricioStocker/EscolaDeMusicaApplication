package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PainelAdmActivity extends AppCompatActivity {

	Button btnCadastraProfessor,btnAddCurso, btnAddGaleria;

	Button btnListaProfessor, btnVoltarTelaPricipal, btnListaAlunos,btnConsultaTurma;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_painel_adm);
		btnListaAlunos = findViewById(R.id.btnListaAluno);
		btnAddCurso = findViewById(R.id.btnAddCurso);
		btnAddGaleria = findViewById(R.id.btnAddGaleria);
		btnConsultaTurma = findViewById(R.id.btnConsultaTurma);
		btnConsultaTurma.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				chamaTurmas();
			}
		});
		btnAddGaleria.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				chamaAddGaleria();
			}
		});
		btnAddCurso.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				chamaAddCurso();
			}
		});
		btnListaAlunos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chamaTelaListaALunos();
			}
		});
		btnVoltarTelaPricipal = findViewById(R.id.btnVoltarTelaPricipal);
		btnVoltarTelaPricipal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chamaTelaPrincipal();
				finish();

			}
		});
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		btnListaProfessor = findViewById(R.id.btnExcluirProfessor);

		btnListaProfessor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chamaTelaListaProfessor();

			}
		});

		btnCadastraProfessor = findViewById(R.id.btnCadastraProfessor);
		btnCadastraProfessor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chamaTelaCadastraProfessor();

			}
		});
	}

	public void chamaTelaCadastraProfessor() {
		Intent intent = new Intent(this, CadastroProfessorActivity.class);
		startActivity(intent);
		finish();
	}

	public void chamaTelaListaProfessor() {
		Intent intent = new Intent(this, ListaProfessorActivity.class);
		startActivity(intent);
		finish();
	}
	public void chamaTelaListaALunos() {
		Intent intent = new Intent(this, ListaAlunosRegistradoActivity.class);
		startActivity(intent);
		finish();
	}

	public void chamaTelaPrincipal() {
		Intent intent = new Intent(this, TelaPrincipalActivity.class);
		startActivity(intent);
		finish();
	}
	public void chamaAddCurso() {
		Intent intent = new Intent(this, AddCursoActivity.class);
		startActivity(intent);
		finish();
	}
	public void chamaAddGaleria() {
		Intent intent = new Intent(this, AddGaleriaActivity.class);
		startActivity(intent);
		finish();
	}
	public void chamaTurmas() {
		Intent intent = new Intent(this, TurmasFinalizadasActivity.class);
		startActivity(intent);
		finish();
	}
}