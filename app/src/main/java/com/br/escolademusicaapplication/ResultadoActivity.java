package com.br.escolademusicaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Aluno;
import com.br.escolademusicaapplication.OBJETOS.Aluno_Professor;

import java.util.List;

public class ResultadoActivity extends AppCompatActivity {

    private TextView textViewResultado;
    private TextView textViewMensagem, txtIdAlunoResultado;
    private Button buttonFechar;
    private ImageView imageViewResultado;
    private Conexao conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        textViewResultado = findViewById(R.id.textViewResultado);
        textViewMensagem = findViewById(R.id.textViewMensagem);
        buttonFechar = findViewById(R.id.buttonFechar);
        imageViewResultado = findViewById(R.id.imageViewResultado);
        txtIdAlunoResultado = findViewById(R.id.txtIdAlunoResultado);
        conexao = Conexao.getInstance(this);

        Intent intent = getIntent();
        String nomeAluno = intent.getStringExtra("nomeAluno"); // Recebendo o nome do aluno
        String idAluno = intent.getStringExtra("idAluno"); // Recebendo o ID do aluno

        TextView textViewNomeAluno = findViewById(R.id.textViewNomeAluno);
        textViewNomeAluno.setText("Nome: " + nomeAluno);

        txtIdAlunoResultado.setText("ID: " + idAluno); // Exibir o ID do aluno

        int acertos = intent.getIntExtra("acertos", 0);
        int totalPerguntas = intent.getIntExtra("totalPerguntas", 0);

        textViewResultado.setText("Você acertou " + acertos + " de " + totalPerguntas + " perguntas.");

        if (acertos >= 3) {
            textViewMensagem.setText("Parabéns! Você passou!");
            imageViewResultado.setImageResource(R.drawable.ic_launcher_atualizar1_foreground);
        } else {
            textViewMensagem.setText("Que pena, você precisa estudar mais.");
            imageViewResultado.setImageResource(R.drawable.delete2);
        }

        buttonFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Professor com ID 1
                int idProfessor = 1;

                // Nota do aluno (você pode obter esse valor da tela ou de outra fonte de dados)
                int nota = getIntent().getIntExtra("acertos", 0);

                // Buscar o aluno pelo ID
                Aluno aluno = conexao.buscarAlunoPorId(idAluno);

                // Verificar se o aluno foi encontrado
                if (aluno != null) {
                    // Salvar a nota do aluno usando o método existente
                    salvarNotaDoAluno(aluno, idProfessor, nota);

                    // Retornar à tela do portal do aluno
                    Intent intent = new Intent(ResultadoActivity.this, PortalDoAlunoActivity.class);
                    intent.putExtra("nomeAluno", nomeAluno);
                    intent.putExtra("idAluno", idAluno);
                    startActivity(intent);
                    finish(); // Fechar a tela ResultadoActivity
                } else {
                    // Se o aluno não for encontrado, exibir uma mensagem de erro
                    Toast.makeText(ResultadoActivity.this, "Aluno não encontrado.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Método para salvar a nota do aluno
    private void salvarNotaDoAluno(Aluno aluno, int idProfessor, int nota) {
        // Salvar a nota do aluno usando o método existente
        boolean atualizacaoSucesso = salvarEdicaoNotasFaltasAluno(aluno.getAluno_id(), idProfessor, nota, nota, 0); // Nota do primeiro bimestre

        // Verifica se a atualização foi bem-sucedida
        if (atualizacaoSucesso) {
            Toast.makeText(ResultadoActivity.this, "Notas alteradas com sucesso!", Toast.LENGTH_SHORT).show();
            // Aqui você pode adicionar outras lógicas, se necessário
        } else {
            Toast.makeText(ResultadoActivity.this, "Não foi possível alterar as notas do aluno.", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para salvar a edição das notas e faltas do aluno
    private boolean salvarEdicaoNotasFaltasAluno(int alunoId, int idProfessor, int novaNotaPrimeiroBim, int novaNotaSegundoBim, int novasFaltas) {
        // Chame o método para atualizar as notas e faltas do aluno no banco de dados
        boolean atualizacaoSucesso = conexao.atualizarNotasFaltasAluno(alunoId, idProfessor, novaNotaPrimeiroBim, novaNotaSegundoBim, novasFaltas);
        // Verifica se a atualização foi bem-sucedida
        if (atualizacaoSucesso) {
            // Atualiza a lista de alunos
            List<Aluno_Professor> listaAlunosProfessor = conexao.buscarAlunosDoProfessor(idProfessor);

        }
        return atualizacaoSucesso;
    }

}
