package com.br.escolademusicaapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.br.escolademusicaapplication.OBJETOS.AlunoSingleton;
import com.br.escolademusicaapplication.OBJETOS.Professor;
import com.br.escolademusicaapplication.R;
import com.br.escolademusicaapplication.adaptor.RecyclerViewAdapterMatricula;
import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Aluno;

import java.util.ArrayList;
import java.util.List;

public class TelaMatriculaActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapterMatricula recyclerViewAdapter;
    List<Professor> professores = new ArrayList<>();
    TextView txtNomeProfe, txtIdProf, txtNomeAluno, txtIdAluno, txtDisciplinaProfessor;
    ImageView fotoAluno, fotoProfessor;
    Button btnRealizarMatricula, btnVoltarPortal, btnAjudaMatricula;

    private Conexao conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_matricula);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Inicialize os componentes da tela
        recyclerView = findViewById(R.id.recyclerView);
        txtNomeAluno = findViewById(R.id.nomeAluno);
        txtIdAluno = findViewById(R.id.idAluno);
        fotoAluno = findViewById(R.id.fotoAluno);
        txtNomeProfe = findViewById(R.id.nomeProfessor);
        txtIdProf = findViewById(R.id.idProfessor);
        fotoProfessor = findViewById(R.id.fotoProfessor);
        txtDisciplinaProfessor = findViewById(R.id.disciplinaProfessor);
        btnRealizarMatricula = findViewById(R.id.btnRealizarMatricula);
        btnVoltarPortal = findViewById(R.id.btnVoltarPortal);

        btnAjudaMatricula = findViewById(R.id.btnAjudaMatricula);
        btnAjudaMatricula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibirAjuda(v);
            }
        });
        // Inicializar a conexão
        conexao = Conexao.getInstance(this);

        // Configurar o RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapterMatricula(this, professores);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Exibir os dados do aluno na tela
        exibirDadosAluno();

        // Configurar o clique do botão "Realizar Matrícula"
        btnRealizarMatricula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarAlunoProfessor();
            }
        });

        // Buscar os professores no banco de dados
        buscaProfessores();

        // Configurar o clique do botão "Voltar para o Portal do Aluno"
        btnVoltarPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaMatriculaActivity.this, PortalDoAlunoActivity.class);
                int idAluno = AlunoSingleton.getInstance().getAluno().getAluno_id();
                intent.putExtra("aluno_id", idAluno);
                startActivity(intent);
                finish();
            }
        });

        // Configurar o clique dos itens do RecyclerView
        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapterMatricula.OnItemClickListener() {
            @Override
            public void onItemClick(Professor professor) {
                // Atualizar os detalhes do professor na tela de matrícula
                updateProfessorDetails(professor);
            }
        });
    }

    // Método para exibir os dados do aluno na tela
    private void exibirDadosAluno() {
        Intent intent = getIntent();
        if (intent != null) {
            Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");

            if (aluno != null) {
                String fotoAlunoBase64 = conexao.buscarFotoAluno(aluno.getAluno_id());
                txtIdAluno.setText("ID: " + aluno.getAluno_id());
                txtNomeAluno.setText("Aluno : " + aluno.getAluno_nome());
                if (fotoAlunoBase64 != null && !fotoAlunoBase64.isEmpty()) {
                    byte[] fotoBytes = Base64.decode(fotoAlunoBase64, Base64.DEFAULT);
                    Bitmap fotoAlunoBitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                    fotoAluno.setImageBitmap(fotoAlunoBitmap);
                } else {
                    fotoAluno.setImageResource(R.drawable.user);
                }
            } else {
                Toast.makeText(this, "Dados do aluno não encontrados", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Método para buscar os professores no banco de dados
    private void buscaProfessores() {
        professores.clear();
        professores.addAll(conexao.buscarTodosProfessores());
        recyclerViewAdapter.notifyDataSetChanged();
    }

    // Método para salvar a matrícula do aluno com o professor selecionado
    private void salvarAlunoProfessor() {
        String idAlunoString = txtIdAluno.getText().toString().replace("ID: ", "");
        String idProfessorString = txtIdProf.getText().toString().replace("ID: ", "");

        // Verificar se o ID do professor está vazio ou contém apenas espaços em branco
        if (TextUtils.isEmpty(idProfessorString.trim())) {
            // Mostrar uma mensagem de erro informando ao usuário que um professor deve ser selecionado
            Toast.makeText(this, "Por favor, selecione um professor antes de salvar a matrícula.", Toast.LENGTH_SHORT).show();
            return; // Retorna sem tentar salvar a matrícula, já que não há professor selecionado
        }

        int idAluno = Integer.parseInt(idAlunoString);

        // Verificar se o ID do professor é válido
        int idProfessor;
        try {
            idProfessor = Integer.parseInt(idProfessorString);
        } catch (NumberFormatException e) {
            // Se o ID do professor não puder ser convertido em um número inteiro, mostrar uma mensagem de erro
            Toast.makeText(this, "ID do professor inválido, por favor selecione um professor " +
                    "para se matricular.", Toast.LENGTH_SHORT).show();
            return; // Retorna sem tentar salvar a matrícula
        }

        // Verificar se o vínculo entre o aluno e o professor já existe
        if (alunoProfessorExists(idAluno, idProfessor)) {
            Toast.makeText(this, "ERRO AO MATRICULAR, VOCÊ JÁ SE MATRICULOU NESSA DISCIPLINA" +
                    "E SERÁ REDIRECIONADO AO PORTAL DO ALUNO", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PortalDoAlunoActivity.class);
            intent.putExtra("aluno_id", idAluno);
            startActivity(intent);
            return; // Retorna sem tentar salvar a matrícula, pois já existe o vínculo
        }

        Aluno aluno = conexao.buscarAlunoPorId(String.valueOf(idAluno));
        Professor professor = conexao.buscarProfessorPorId(String.valueOf(idProfessor));

        boolean sucesso = conexao.salvarAlunoProfessor(aluno, professor);

        if (sucesso) {
            Toast.makeText(this, "MATRÍCULA SALVA COM SUCESSO", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PortalDoAlunoActivity.class);
            intent.putExtra("aluno_id", idAluno);
            startActivity(intent);
        } else {
            Toast.makeText(this, "ERRO AO MATRICULAR, VOCÊ JÁ SE MATRICULOU NESSA DISCIPLINA" +
                    "E SERÁ REDIRECIONADO AO PORTAL DO ALUNO", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PortalDoAlunoActivity.class);
            intent.putExtra("aluno_id", idAluno);
            startActivity(intent);
        }
    }

    // Método para verificar se o vínculo entre aluno e professor já existe
    private boolean alunoProfessorExists(int idAluno, int idProfessor) {
        String sql = "SELECT COUNT(*) FROM aluno_professor WHERE id_aluno = ? AND id_professor = ?";
        Cursor cursor = conexao.getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(idAluno), String.valueOf(idProfessor)});

        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        return false;
    }

    // Método para atualizar os detalhes do professor na tela de matrícula
    public void updateProfessorDetails(Professor professor) {
        txtNomeProfe.setText("Professor : " + professor.getProfessor_nome());
        txtIdProf.setText("ID: " + professor.getProfessor_id());
        txtDisciplinaProfessor.setText("DISCIPLINA : " + professor.getProfessor_disciplina());

        if (professor.getProfessor_foto() != null && !professor.getProfessor_foto().isEmpty()) {
            byte[] decodedString = Base64.decode(professor.getProfessor_foto(), Base64.DEFAULT);
            fotoProfessor.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        } else {
            fotoProfessor.setImageResource(R.drawable.user);
        }
    }

    public void exibirAjuda(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda!");

        // Criar a SpannableString para cada ícone e explicação
        SpannableString icon1 = createIconSpannable(R.drawable.ic_launcher_voltar2_foreground);
        SpannableString text1 = new SpannableString("Voltar para a tela do portal do aluno!\n");

        SpannableString icon2 = createIconSpannable(R.drawable.ic_launcher_salvar2_foreground);
        SpannableString text2 = new SpannableString("Realiza o registro da matrícula. Ao escolher " +
                "o professor da lista, vendo que ele foi selecionado no campo dos professores selecionados " +
                "acima da lista, com seus dados, clique no botão salvar matrícula!\n");

        // Concatenar as SpannableStrings
        CharSequence fullText = TextUtils.concat(icon1, text1, icon2, text2);

        // Definir o texto personalizado no AlertDialog
        builder.setMessage(fullText);

        // Adicionar o botão de OK
        builder.setPositiveButton("OK", null);

        // Mostrar o AlertDialog
        builder.show();
    }

    // Função auxiliar para criar a SpannableString para um ícone
    private SpannableString createIconSpannable(int drawableId) {
        Drawable drawable = getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        SpannableString spannable = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        return spannable;
    }
}
