package com.br.escolademusicaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Aluno;
import com.br.escolademusicaapplication.OBJETOS.Professor;
import com.br.escolademusicaapplication.OBJETOS.Turma;

import java.util.List;

public class
MainActivity extends AppCompatActivity {
    private Conexao conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        conexao = Conexao.getInstance(this);

      buscaTodosAlunos();
       // buscaTodosProfessores();
        //buscaTodasTurmas();
    }

    public void buscaTodosAlunos() {
        List<Aluno> alunos = conexao.buscarTodosAlunos();

        // Verifique se a lista de alunos não está vazia
        if (!alunos.isEmpty()) {
            // Iterar sobre a lista de alunos
            for (Aluno aluno : alunos) {
                // Faça o que for necessário com os dados do aluno
                Log.d("Aluno", "ID: " + aluno.getAluno_id());
                Log.d("Aluno", "Nome: " + aluno.getAluno_nome());
                Log.d("Aluno", "Status: " + aluno.getStatus());
                Log.d("Aluno", "Telefone: " + aluno.getAluno_telefone());
                Log.d("Aluno", "CPF: " + aluno.getAluno_cpf());
                Log.d("Aluno", "dtanas: " + aluno.getAluno_dataNascimento());
                Log.d("Aluno", "matricula: " + aluno.getAluno_matricula());
                Log.d("Aluno", "senha: " + aluno.getAluno_senha());
                Log.d("Aluno", "-------------------------------------");
            }
        } else {
            Toast.makeText(this, "Nenhum aluno cadastrado.", Toast.LENGTH_SHORT).show();
        }
    }

    public void buscaTodosProfessores() {
        List<Professor> professores = conexao.buscarTodosProfessores();

        // Verifique se a lista de professores não está vazia
        if (!professores.isEmpty()) {
            // Iterar sobre a lista de professores
            for (Professor professor : professores) {
                // Faça o que for necessário com os dados do professor
                Log.d("Professor", "ID: " + professor.getProfessor_id());
                Log.d("Professor", "Nome: " + professor.getProfessor_nome());
                Log.d("Professor", "Disciplina: " + professor.getProfessor_disciplina());
                Log.d("Professor", "telefone: " + professor.getProfessor_telefone());
                Log.d("Professor", "-------------------------------------");
            }
        } else {
            Toast.makeText(this, "Nenhum professor cadastrado.", Toast.LENGTH_SHORT).show();
        }
    }
        public void buscaTodasTurmas() {
            List<Turma> turmas = conexao.buscarTodasTurmas();

            // Verifique se a lista de turmas não está vazia
            if (!turmas.isEmpty()) {
                // Iterar sobre a lista de turmas
                for (Turma turma : turmas) {
                    // Faça o que for necessário com os dados da turma
                    Log.d("Turma", "ID: " + turma.getTurma_id());
                    Log.d("Turma", "Professor ID: " + turma.getProfessor_id());
                    Log.d("Turma", "Alunos: " + turma.getAlunos());
                    Log.d("Turma", "Data de Encerramento: " + turma.getData_encerramento());
                    Log.d("Turma", "-------------------------------------");
                }
            } else {
                Toast.makeText(this, "Nenhuma turma cadastrada.", Toast.LENGTH_SHORT).show();
            }
        }

        // Métodos buscaTodosAlunos() e buscaTodosProfessores() permanecem como estão
    }


