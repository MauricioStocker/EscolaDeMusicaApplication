package com.br.escolademusicaapplication;// GaleriaFotosActivity.java
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Galeria;
import com.br.escolademusicaapplication.adaptor.GaleriaAdapter;
import java.util.List;

public class GaleriaFotosActivity extends AppCompatActivity {

	private RecyclerView recyclerViewGaleria;
	private GaleriaAdapter galeriaAdapter;
	private List<Galeria> listaGaleria;
	private Conexao conexao;
	private Button btnVoltarGaleria;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_galeria_fotos);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		btnVoltarGaleria = findViewById(R.id.btnVoltarGaleria);

		recyclerViewGaleria = findViewById(R.id.recyclerViewGaleria);
		recyclerViewGaleria.setLayoutManager(new LinearLayoutManager(this));
		btnVoltarGaleria.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				voltarParaTelaPrincipal();
			}
		});

		conexao = new Conexao(this);
		carregarGalerias();

		// Aqui você pode inicializar outros elementos da tela, se necessário
	}

	private void carregarGalerias() {
		try {
			listaGaleria = conexao.getAllGalerias();

			if (listaGaleria != null && !listaGaleria.isEmpty()) {
				Log.d("GaleriaFotosActivity", "Quantidade de itens na galeria: " + listaGaleria.size());

				galeriaAdapter = new GaleriaAdapter(this, listaGaleria, new GaleriaAdapter.OnItemClickListener() {
					@Override
					public void onItemClick(Galeria galeria) {
						// Implemente a ação ao clicar em um item da galeria, se necessário
					}
				});

				recyclerViewGaleria.setAdapter(galeriaAdapter);
			} else {
				Log.d("GaleriaFotosActivity", "A lista de galerias está vazia ou nula");

				// Configurar um adaptador vazio ou nulo para evitar o erro "No adapter attached"
				recyclerViewGaleria.setAdapter(null);

				// Exibir uma mensagem ao usuário informando que a lista está vazia
				Toast.makeText(this, "Nenhuma galeria encontrada.", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Log.e("GaleriaFotosActivity", "Erro ao carregar galerias: " + e.getMessage());
			e.printStackTrace();

			// Exibir uma mensagem de erro ao usuário
			Toast.makeText(this, "Erro ao carregar galerias. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();

			// Configurar um adaptador vazio ou nulo em caso de erro
			recyclerViewGaleria.setAdapter(null);
		}
	}



	public void voltarParaTelaPrincipal() {
		// Retornar à tela principal
		Intent intent = new Intent(this, TelaPrincipalActivity.class);
		startActivity(intent);
		finish();
	}
}
