package com.br.escolademusicaapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MaterialEstudoActivity extends AppCompatActivity {
	private TextView tvNomeAluno;
	private TextView tvMaterialTitulo;
	private TextView tvMaterialConteudo;
	private ImageView imgMaterial;
	private String nomeAluno;
	private String idAluno;
	private Button btnVoltarPortalAva;
	private int numTentativas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_material_estudo);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}

		// Inicialização das variáveis
		tvNomeAluno = findViewById(R.id.tvNomeAluno);
		tvMaterialTitulo = findViewById(R.id.tvMaterialTitulo);
		tvMaterialConteudo = findViewById(R.id.tvMaterialConteudo);
		imgMaterial = findViewById(R.id.imgMaterial);
		btnVoltarPortalAva = findViewById(R.id.btnVoltarPortalAva);
		btnVoltarPortalAva.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				voltarPortal();
			}
		});

		// Recuperar os dados do aluno da Intent
		nomeAluno = getIntent().getStringExtra("nomeAluno");
		idAluno = getIntent().getStringExtra("idAluno");

		// Exibir o nome do aluno
		tvNomeAluno.setText("Aluno: " + nomeAluno);

		// Definir o conteúdo do material de estudo
		String titulo = "Materia para estudo sobre a prova virtual";
		String conteudo = "1. Johann Sebastian Bach é considerado o 'pai' da música clássica.\n\n" +
				"2. A flauta é conhecida como o instrumento musical mais antigo.\n\n" +
				"3. Ludwig van Beethoven é famoso por suas sinfonias, incluindo a 'Sinfonia Nº 5'.\n\n" +
				"4. O Blues é um gênero musical que se originou no sul dos Estados Unidos na década de 1920.\n\n" +
				"5. O violino é um instrumento musical conhecido por suas cordas vibrantes e é frequentemente usado em orquestras.";

		tvMaterialTitulo.setText(titulo);
		tvMaterialConteudo.setText(conteudo);

		// Substitua pela lógica para carregar a imagem correta
		imgMaterial.setImageResource(R.drawable.materialetudo);

		// Lógica para controlar as tentativas
		verificarTentativas();

		Button btnIrParaQuestionario = findViewById(R.id.btnIrParaQuestionario);
		btnIrParaQuestionario.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (numTentativas < 2) {
					// Permitir que o usuário vá para o questionário
					abrirQuestionario();
				} else {
					// Exibir mensagem de limite de tentativas atingido
					Toast.makeText(MaterialEstudoActivity.this, "Você atingiu o limite de 2 tentativas!\nA nota maior tirada será a que permanecerá", Toast.LENGTH_LONG).show();

				}
			}
		});
	}

	private void abrirQuestionario() {
		// Passar dados do aluno para a próxima Activity
		Intent intent = new Intent(MaterialEstudoActivity.this, QuestionarioActivity.class);
		intent.putExtra("nomeAluno", nomeAluno);
		intent.putExtra("idAluno", idAluno);
		startActivity(intent);
	}

	private void verificarTentativas() {
		// Verificar tentativas já realizadas pelo usuário
		SharedPreferences prefs = getSharedPreferences("tentativas_" + idAluno, MODE_PRIVATE);
		numTentativas = prefs.getInt("numTentativas", 0);
	}

	private void salvarTentativas() {
		// Salvar o número de tentativas quando necessário
		SharedPreferences prefs = getSharedPreferences("tentativas_" + idAluno, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("numTentativas", numTentativas);
		editor.apply();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Ao destruir a atividade, garantir que as tentativas sejam salvas
		salvarTentativas();
	}
	public void voltarPortal(){
		Intent intent = new Intent(this, PortalDoAlunoActivity.class);
		startActivity(intent);
		finish();
	}
}
