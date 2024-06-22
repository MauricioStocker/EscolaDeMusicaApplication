package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Aluno;

public class DadosAlunoActivity extends AppCompatActivity {

    private TextView txtIdDados, txtNomeRecebido, txtTelefoneRecebido;
    private Spinner spinnerStatusAluno;
    private Button btnAtualizaCadastro, btnVoltar;
    private Conexao conexao;
    private ImageView imgWhats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_aluno);

        // Recuperar o ID do aluno da Intent
        int alunoId = getIntent().getIntExtra("alunoId", -1); // Substitua defaultValue pelo valor padrão apropriado

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        txtIdDados = findViewById(R.id.txtIdDados);
        txtNomeRecebido = findViewById(R.id.txtNomeRecebidoProfessor);
        txtTelefoneRecebido = findViewById(R.id.txtTelefoneRecebido);
        spinnerStatusAluno = findViewById(R.id.spinnerStatusAluno);
        btnAtualizaCadastro = findViewById(R.id.btnAtualizaCadastro);
        btnVoltar = findViewById(R.id.btnVoltar);

        imgWhats = findViewById(R.id.imgWhats);

        conexao = Conexao.getInstance(this);

        // Receber dados do aluno da tela anterior
        carregarDetalhesAluno(alunoId);

        btnAtualizaCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarStatusAluno();
            }
        });

        imgWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" + txtTelefoneRecebido.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Voltar para a tela anterior
            }
        });
    }

    private void atualizarStatusAluno() {
        // Recuperar o ID do aluno
        String idText = txtIdDados.getText().toString().replace("ID: ", "");
        int alunoId = Integer.parseInt(idText);

        // Obter o novo status do aluno do Spinner
        String novoStatus = String.valueOf(spinnerStatusAluno.getSelectedItem());

        // Atualizar o status do aluno no banco de dados
        boolean sucesso = conexao.atualizarStatusAluno(alunoId, novoStatus);

        if (sucesso) {
            Toast.makeText(getApplicationContext(), "Status do aluno atualizado com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Erro ao atualizar o status do aluno", Toast.LENGTH_SHORT).show();
        }
        setResult(RESULT_OK);
        //finish();
    }

    private void carregarDetalhesAluno(int alunoId) {
        // Buscar o aluno pelo ID usando a classe Conexao
        Conexao conexao = Conexao.getInstance(this);
        Aluno aluno = conexao.buscarAlunoPorId(String.valueOf(alunoId));

        if (aluno != null) {
            // Configurar os detalhes do aluno nos componentes da interface do usuário
            txtIdDados.setText("ID: " + String.valueOf(aluno.getAluno_id()));
            txtNomeRecebido.setText(aluno.getAluno_nome());
            txtTelefoneRecebido.setText(aluno.getAluno_telefone());

            // Configurar o status no Spinner
            String[] opcoesStatus = getResources().getStringArray(R.array.spinner_status_aluno);
            int position = -1;
            for (int i = 0; i < opcoesStatus.length; i++) {
                if (opcoesStatus[i].equals(aluno.getStatus())) {
                    position = i;
                    break;
                }
            }
            if (position >= 0) {
                spinnerStatusAluno.setSelection(position);
            }
        } else {
            Toast.makeText(this, "Aluno não encontrado.", Toast.LENGTH_SHORT).show();
            // Aqui você pode decidir o que fazer se o aluno não for encontrado
        }
    }


}
