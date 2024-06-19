package com.br.escolademusicaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Curso;
import com.br.escolademusicaapplication.adaptor.CursoAdapter;
import java.util.List;

public class ListaCursosActivity extends AppCompatActivity {

	private RecyclerView recyclerViewCursos;
	private CursoAdapter cursoAdapter;
	private List<Curso> listaCursos;
	private Conexao conexao;
	private Button btnVoltarInicial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_cursos);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}

		recyclerViewCursos = findViewById(R.id.recyclerViewCursos);
		recyclerViewCursos.setLayoutManager(new LinearLayoutManager(this));

		btnVoltarInicial = findViewById(R.id.btnVoltarInicial);
		btnVoltarInicial.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				voltaTelaInicial();
			}
		});
		conexao = new Conexao(this);
		carregarCursos();
	}

	private void carregarCursos() {
		try {
			listaCursos = conexao.getAllCursos();

			if (listaCursos != null && !listaCursos.isEmpty()) {
				Log.d("ListaCursosActivity", "Quantidade de cursos carregados: " + listaCursos.size());

				cursoAdapter = new CursoAdapter(listaCursos, this, new CursoAdapter.OnItemClickListener() {
					@Override
					public void onItemClick(Curso curso) {
						// Aqui você pode definir a ação a ser tomada quando um curso for clicado.
						// Exemplo:
						// Intent intent = new Intent(ListaCursosActivity.this, EditCursoActivity.class);
						// intent.putExtra("curso_id", curso.getCurso_id());
						// startActivity(intent);
					}
				});

				recyclerViewCursos.setAdapter(cursoAdapter);
			} else {
				Log.d("ListaCursosActivity", "A lista de cursos está vazia ou nula");

				// Configurar um adaptador vazio ou nulo para evitar o erro "No adapter attached"
				recyclerViewCursos.setAdapter(null);

				// Exemplo de exibição de mensagem na tela
				// Por exemplo, mostrar um TextView com a mensagem "Nenhum curso encontrado"
				// Ou exibir um Snackbar informando ao usuário sobre a ausência de cursos
			}
		} catch (Exception e) {
			Log.e("ListaCursosActivity", "Erro ao carregar cursos: " + e.getMessage());
			e.printStackTrace();
			// Exemplo: Mostrar uma mensagem de erro ao usuário
			Toast.makeText(this, "Erro ao carregar cursos. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();

			// Configurar um adaptador vazio ou nulo em caso de erro
			recyclerViewCursos.setAdapter(null);
		}
	}


	public void voltaTelaInicial(){
		Intent intent = new Intent(this, TelaPrincipalActivity.class);
		startActivity(intent);
	}
}
