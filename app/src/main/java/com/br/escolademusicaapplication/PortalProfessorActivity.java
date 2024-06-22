package com.br.escolademusicaapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Aluno_Professor;
import com.br.escolademusicaapplication.OBJETOS.Professor;
import com.br.escolademusicaapplication.OBJETOS.ProfessorSingleton;
import com.br.escolademusicaapplication.adaptor.ProfessorAlunoAdapter;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PortalProfessorActivity extends AppCompatActivity implements ProfessorAlunoAdapter.OnItemClickListener {
    private Conexao conexao;
    private TextView txtRecebeNomeProfe, txtRecebeIdProfe, txtRecebeDisciplinaProfe;
    private CircleImageView fotoProfessorPortal;
    private RecyclerView alunosRecyclerView;
    private ProfessorAlunoAdapter adapter;
    private Button btnAlteraNotasAlunos, btnEditarCadastro, btnVoltar;
    private int alunoSelecionadoId;

    @SuppressLint({"WrongViewCast", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal_professor);
        conexao = Conexao.getInstance(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Professor professor = ProfessorSingleton.getInstance().getProfessor();

        txtRecebeIdProfe = findViewById(R.id.txtRecebeIdProfe);
        txtRecebeNomeProfe = findViewById(R.id.txtRecebeNomeProfe);
        txtRecebeDisciplinaProfe = findViewById(R.id.txtRecebeDisciplinaProfe);
        fotoProfessorPortal = findViewById(R.id.fotoProfessorPortal);
        alunosRecyclerView = findViewById(R.id.alunosRecyclerView);
        btnAlteraNotasAlunos = findViewById(R.id.btnAlteraNotasAlunos);
        btnEditarCadastro = findViewById(R.id.btnEditarCadastro);
        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sairTelaInicial();
            }
        });
        btnEditarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaTelaEdicao(ProfessorSingleton.getInstance().getProfessor());
            }
        });
        btnAlteraNotasAlunos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica se um aluno foi selecionado
                if (alunoSelecionadoId == 0) {
                    // Se nenhum aluno foi selecionado, exibe uma mensagem de erro
                    Toast.makeText(PortalProfessorActivity.this, "Por favor, escolha um aluno para editar suas notas.", Toast.LENGTH_SHORT).show();
                    return; // Impede que o restante do código seja executado
                }

                // Obter os valores das notas e faltas do aluno selecionado
                TextView txtRecebenotaPrimeiroBimLista = findViewById(R.id.txtRecebenotaPrimeiroBimLista);
                TextView txtRecebenotaSegundoBimLista = findViewById(R.id.txtRecebenotaSegundoBimLista);
                TextView txtRecebeFaltaLista = findViewById(R.id.txtRecebeFaltaLista);

                // Obter o ID do professor (que já está disponível)
                int idProfessor = ProfessorSingleton.getInstance().getProfessor().getProfessor_id();

                // Obter as notas e faltas do aluno selecionado
                int novaNotaPrimeiroBim = Integer.parseInt(txtRecebenotaPrimeiroBimLista.getText().toString());
                int novaNotaSegundoBim = Integer.parseInt(txtRecebenotaSegundoBimLista.getText().toString());
                int novasFaltas = Integer.parseInt(txtRecebeFaltaLista.getText().toString());

                // Agora você tem todos os valores necessários para chamar o método salvarEdicaoNotasFaltasAluno
                salvarEdicaoNotasFaltasAluno(alunoSelecionadoId, idProfessor, novaNotaPrimeiroBim,
                        novaNotaSegundoBim, novasFaltas);

                // Limpar os campos e esconder o teclado
                limparCamposAluno();
                escondeTeclado();
            }
        });

        if (professor != null) {
            txtRecebeIdProfe.setText(String.valueOf(professor.getProfessor_id()));
            txtRecebeNomeProfe.setText("Nome: " + professor.getProfessor_nome());
            txtRecebeDisciplinaProfe.setText("Disciplina: " + professor.getProfessor_disciplina());
            if (professor.getProfessor_foto() != null && !professor.getProfessor_foto().isEmpty()) {
                byte[] decodedString = Base64.decode(professor.getProfessor_foto(), Base64.DEFAULT);
                fotoProfessorPortal.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            } else {
                fotoProfessorPortal.setImageResource(R.drawable.user);
            }
            List<Aluno_Professor> listaAlunosProfessor = conexao.buscarAlunosDoProfessor(professor.getProfessor_id());
            if (listaAlunosProfessor.isEmpty()) {
                // Mantenha a lógica existente para lidar com a lista vazia
            } else {
                setupRecyclerView(listaAlunosProfessor);
            }
        } else {
            // Mantenha a lógica existente para lidar com erro ao obter dados do professor
        }
    }

    private void setupRecyclerView(List<Aluno_Professor> listaAlunosProfessor) {
        adapter = new ProfessorAlunoAdapter(this, listaAlunosProfessor, this);
        alunosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alunosRecyclerView.setAdapter(adapter);
    }

    // Implementação do método onItemClick da interface ProfessorAlunoAdapter.OnItemClickListener
    @Override
    public void onItemClick(Aluno_Professor alunoProfessor) {
        // Atualiza o layout do aluno com os dados do aluno clicado
        atualizarLayoutAluno(alunoProfessor);
    }

    @SuppressLint("SetTextI18n")
    private void atualizarLayoutAluno(Aluno_Professor alunoProfessor) {
        // Aqui você atualiza os elementos do layout do aluno com os dados do alunoProfessor
        // Exemplo:
        TextView txtRecebeNomeDaLista = findViewById(R.id.txtRecebeNomeDaLista);
        TextView txtRecebenotaPrimeiroBimLista = findViewById(R.id.txtRecebenotaPrimeiroBimLista);
        TextView txtRecebenotaSegundoBimLista = findViewById(R.id.txtRecebenotaSegundoBimLista);
        TextView txtRecebeFaltaLista = findViewById(R.id.txtRecebeFaltaLista);

        alunoSelecionadoId = alunoProfessor.getAluno().getAluno_id();

        txtRecebeNomeDaLista.setText(alunoProfessor.getAluno().getAluno_nome());
        txtRecebenotaPrimeiroBimLista.setText(String.valueOf(alunoProfessor.getAluno_notaPrimeiroBim()));
        txtRecebenotaSegundoBimLista.setText(String.valueOf(alunoProfessor.getAluno_notaSegundoBim()));
        txtRecebeFaltaLista.setText(String.valueOf(alunoProfessor.getAluno_faltas()));
    }

    public void salvarEdicaoNotasFaltasAluno(int alunoId, int idProfessor, int novaNotaPrimeiroBim, int novaNotaSegundoBim, int novasFaltas) {
        // Chame o método para atualizar as notas e faltas do aluno no banco de dados
        boolean atualizacaoSucesso = conexao.atualizarNotasFaltasAluno(alunoId, idProfessor, novaNotaPrimeiroBim, novaNotaSegundoBim, novasFaltas);
        // Verifica se a atualização foi bem-sucedida
        if (atualizacaoSucesso) {
            Toast.makeText(this, "Notas alteradas com sucesso!", Toast.LENGTH_SHORT).show();
            // Atualiza a lista de alunos
            List<Aluno_Professor> listaAlunosProfessor = conexao.buscarAlunosDoProfessor(idProfessor);
            if (!listaAlunosProfessor.isEmpty()) {
                setupRecyclerView(listaAlunosProfessor);
            }
        } else {
            Toast.makeText(this, "Não foi possível alterar as notas do aluno.", Toast.LENGTH_SHORT).show();
        }
    }

    private void limparCamposAluno() {
        // Limpar os campos de notas e faltas
        TextView txtRecebeNomeDaLista = findViewById(R.id.txtRecebeNomeDaLista);
        TextView txtRecebenotaPrimeiroBimLista = findViewById(R.id.txtRecebenotaPrimeiroBimLista);
        TextView txtRecebenotaSegundoBimLista = findViewById(R.id.txtRecebenotaSegundoBimLista);
        TextView txtRecebeFaltaLista = findViewById(R.id.txtRecebeFaltaLista);

        txtRecebeNomeDaLista.setText("");
        txtRecebenotaPrimeiroBimLista.setText("");
        txtRecebenotaSegundoBimLista.setText("");
        txtRecebeFaltaLista.setText("");
        alunoSelecionadoId = 0; // Reinicializar o ID do aluno selecionado
    }

    public void chamaTelaEdicao(Professor professor) {
        // Abrir a tela de edição de perfil e passar o professor como parâmetro
        Intent intent = new Intent(this, EdicaoPerfilProfessorActivity.class);
        intent.putExtra("professor", professor);
        startActivity(intent);
    }

    public void sairTelaInicial() {
        Intent intent = new Intent(this, TelaPrincipalActivity.class);
        startActivity(intent);
        finish();
    }

    private void escondeTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
