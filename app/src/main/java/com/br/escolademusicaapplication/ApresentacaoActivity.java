package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ApresentacaoActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apresentacao);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				mainActivi();

			}
		}, 3000);//trasição de 3 segundos
	}

	private void mainActivi() {
		Intent intent = new Intent(ApresentacaoActivity.this, TelaPrincipalActivity.class);
		startActivity(intent);
		finish();


	}

}
