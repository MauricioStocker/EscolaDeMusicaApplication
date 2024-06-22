package com.br.escolademusicaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestionarioActivity extends AppCompatActivity {

    private TextView textViewPergunta;
    private RadioGroup radioGroupOpcoes;
    private Button buttonResponder;
    private ProgressBar progressBar;

    private String nomeAluno; // Variável para armazenar o nome do aluno
    private String idAluno; // Variável para armazenar o ID do aluno

    private String[] perguntas = {
            "Quem é considerado o 'pai' da música clássica?",
            "Qual é o instrumento musical mais antigo?",
            "Qual compositor ficou famoso por suas sinfonias, como a 'Sinfonia Nº 5'?",
            "Qual é o gênero musical que se originou no sul dos Estados Unidos na década de 1920?",
            "Qual instrumento musical é conhecido por suas cordas vibrantes e é frequentemente usado em orquestras?"
    };

    private String[] respostasCorretas = {
            "Bach",
            "Flauta",
            "Beethoven",
            "Blues",
            "Violino"
    };

    private int indicePerguntaAtual = 0;
    private int contadorRespostasCorretas = 0;
    private int quantidadePerguntas = perguntas.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionario);

        textViewPergunta = findViewById(R.id.textViewPergunta);
        radioGroupOpcoes = findViewById(R.id.radioGroupOpcoes);
        buttonResponder = findViewById(R.id.buttonResponder);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        nomeAluno = intent.getStringExtra("nomeAluno"); // Recebendo o nome do aluno da tela Portal de Aluno
        idAluno = intent.getStringExtra("idAluno"); // Recebendo o ID do aluno da tela Portal de Aluno

        // Atualizar a exibição dos dados do aluno na tela
        TextView textViewNomeAluno = findViewById(R.id.textViewNomeAluno);
        TextView textViewIDAluno = findViewById(R.id.textViewIDAluno);
        textViewNomeAluno.setText("Nome: " + nomeAluno);
        textViewIDAluno.setText("ID: " + idAluno);

        exibirPergunta();

        buttonResponder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarResposta();
            }
        });
    }

    private void exibirPergunta() {
        textViewPergunta.setText(perguntas[indicePerguntaAtual]);

        // Embaralhar as opções de resposta
        List<String> opcoesList = Arrays.asList(
                respostasCorretas[indicePerguntaAtual],
                "Mozart",
                "Piano",
                "Jazz",
                "Saxofone"
        );
        Collections.shuffle(opcoesList);
        String[] opcoes = opcoesList.toArray(new String[0]);

        for (int i = 0; i < radioGroupOpcoes.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroupOpcoes.getChildAt(i);
            radioButton.setText(opcoes[i]);
        }
    }

    private void verificarResposta() {
        int idSelecionado = radioGroupOpcoes.getCheckedRadioButtonId();
        if (idSelecionado != -1) {
            RadioButton radioButtonSelecionado = findViewById(idSelecionado);
            String respostaSelecionada = radioButtonSelecionado.getText().toString();

            // Verificar se a resposta selecionada está correta
            for (String respostaCorreta : respostasCorretas) {
                if (respostaSelecionada.equals(respostaCorreta)) {
                    contadorRespostasCorretas++;
                    break; // Parar de verificar assim que encontrar a resposta correta
                }
            }

            indicePerguntaAtual++;
            if (indicePerguntaAtual < quantidadePerguntas) {
                progressBar.setProgress(indicePerguntaAtual + 1);
                radioGroupOpcoes.clearCheck();
                exibirPergunta();
            } else {
                exibirResultado();
            }
        } else {
            Toast.makeText(this, "Selecione uma resposta", Toast.LENGTH_SHORT).show();
        }
    }

    private void exibirResultado() {
        Intent intent = new Intent(this, ResultadoActivity.class);
        intent.putExtra("nomeAluno", nomeAluno); // Passando o nome do aluno para a tela Resultado
        intent.putExtra("idAluno", idAluno); // Passando o ID do aluno para a tela Resultado
        intent.putExtra("acertos", contadorRespostasCorretas);
        intent.putExtra("totalPerguntas", quantidadePerguntas);
        startActivity(intent);
    }
}
