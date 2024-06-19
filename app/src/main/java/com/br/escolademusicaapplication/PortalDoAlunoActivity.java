package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Aluno;
import com.br.escolademusicaapplication.OBJETOS.Aluno_Professor;
import com.br.escolademusicaapplication.OBJETOS.AlunoSingleton;
import com.br.escolademusicaapplication.adaptor.AlunoProfessorAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PortalDoAlunoActivity extends AppCompatActivity {
    private Conexao conexao;

    CircleImageView fotoAluno;


    Button btnAtualizar, btnExcluir, btnVoltar, btnMatricula, btnAva;
    TextView txtNomeRecebido, txtIdRecebido;
    private Bitmap fotoCapturada;
    private String fotoEmString = "";
    Intent intent;

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private List<Aluno_Professor> listaAlunoProfessor = new ArrayList<>();
    private AlunoProfessorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal_do_aluno);
        conexao = Conexao.getInstance(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        fotoAluno = findViewById(R.id.fotoAluno);
        btnMatricula = findViewById(R.id.btnMatricula);
        btnAtualizar = findViewById(R.id.btnAtualizaCadastroAluno);
        btnExcluir = findViewById(R.id.btnExcluirCadastroAluno);
        btnVoltar = findViewById(R.id.btnVoltarTelaInicial);
        btnAva = findViewById(R.id.btnAva);
        txtNomeRecebido = findViewById(R.id.txtNomeRecebidoProfessor);
        txtIdRecebido = findViewById(R.id.txtIdRecebido);


        // Configurar dados do aluno a partir do Singleton
        Aluno aluno = AlunoSingleton.getInstance().getAluno();
        if (aluno != null) {
            txtNomeRecebido.setText(aluno.getAluno_nome());
            txtIdRecebido.setText(String.valueOf(aluno.getAluno_id()));

            // Carregar a foto do aluno, se existir
            if (aluno.getAluno_foto() != null && !aluno.getAluno_foto().isEmpty()) {
                byte[] fotoBytes = Base64.decode(aluno.getAluno_foto(), Base64.DEFAULT);
                Bitmap fotoAlunoBitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                fotoAluno.setImageBitmap(fotoAlunoBitmap);
            } else {
                // Caso não haja foto, exibe um ícone padrão
                fotoAluno.setImageResource(android.R.drawable.ic_menu_camera);
            }
        }

        btnAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaAva();
            }
        });
        btnMatricula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idAluno = txtIdRecebido.getText().toString().replace("ID: ", "");

                Aluno aluno = conexao.buscarAlunoPorId(idAluno);
                if (aluno != null) {
                    intent = new Intent(PortalDoAlunoActivity.this, TelaMatriculaActivity.class);
                    intent.putExtra("aluno", aluno);
                    startActivity(intent);
                } else {
                    Toast.makeText(PortalDoAlunoActivity.this, "Dados do aluno não encontrados", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Obter o ID do aluno logado do Singleton e atualizar a tela
        int idAlunoLogado = AlunoSingleton.getInstance().getAluno().getAluno_id();
        atualizarDadosDoAluno(idAlunoLogado);

        fotoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fotoCapturada != null) {
                    fotoAluno.setImageBitmap(fotoCapturada);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                }
            }
        });

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarAluno();
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implemente a lógica de exclusão do aluno aqui
                confirmarExclusao();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                voltarTelaInicial();
                finish();
            }
        });

        // Inicializar o adapter e associá-lo à ListView
        ListView listViewAlunoProfessor = findViewById(R.id.listaAulas);
        adapter = new AlunoProfessorAdapter(this, listaAlunoProfessor);
        listViewAlunoProfessor.setAdapter(adapter);
        listarAlunosDoAluno(idAlunoLogado);
    }

    private void confirmarExclusao() {
        String nome = txtNomeRecebido.getText().toString();
        AlertDialog.Builder confirmaExclusao = new AlertDialog.Builder(PortalDoAlunoActivity.this);
        confirmaExclusao.setTitle("Atenção!");
        confirmaExclusao.setMessage("Tem certeza que quer excluir " + nome + "? ");
        confirmaExclusao.setCancelable(false);
        confirmaExclusao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                excluirAluno();
            }
        });
        confirmaExclusao.setNegativeButton("Não", null);
        confirmaExclusao.create().show();
    }

    private void excluirAluno() {
        int idAluno = AlunoSingleton.getInstance().getAluno().getAluno_id();

        SQLiteDatabase db = conexao.getWritableDatabase();

        String[] whereArgs = {String.valueOf(idAluno)};
        int linhasAfetadas = db.delete("aluno", "aluno_id = ?", whereArgs);
        db.close();

        if (linhasAfetadas > 0) {
            Toast.makeText(getApplicationContext(), "Aluno excluído com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Erro ao excluir o Aluno", Toast.LENGTH_SHORT).show();
        }
    }

    // Método chamado após a captura de uma foto pela câmera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dados) {
        super.onActivityResult(requestCode, resultCode, dados);
        if (resultCode == RESULT_OK && requestCode == 1) {
            try {
                fotoCapturada = (Bitmap) Objects.requireNonNull(dados.getExtras()).get("data");
                fotoAluno.setImageBitmap(fotoCapturada);

                byte[] fotoEmBytes;
                ByteArrayOutputStream streamDaFotoEmBytes = new ByteArrayOutputStream();

                fotoCapturada.compress(Bitmap.CompressFormat.PNG, 70, streamDaFotoEmBytes);
                fotoEmBytes = streamDaFotoEmBytes.toByteArray();

                fotoEmString = Base64.encodeToString(fotoEmBytes, Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            fotoAluno.setImageResource(android.R.drawable.ic_menu_camera);
            fotoEmString = "null";

        }
    }

    // Método para atualizar os dados do aluno
    private void atualizarAluno() {
        int idAluno = AlunoSingleton.getInstance().getAluno().getAluno_id();
        String nome = txtNomeRecebido.getText().toString();

        // Cria um objeto Aluno com os dados atualizados
        Aluno aluno = new Aluno();
        aluno.setAluno_id(idAluno);
        aluno.setAluno_nome(nome);
        aluno.setAluno_foto(fotoEmString);

        // Chama o método de atualização na classe de conexão com o banco de dados
        boolean sucesso = conexao.atualizarAluno(aluno);

        // Exibe uma mensagem de sucesso ou erro, dependendo do resultado da atualização
        if (sucesso) {
            Toast.makeText(getApplicationContext(), "Aluno atualizado com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Erro ao atualizar o aluno", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para listar os alunos-professores associados ao aluno logado
    private void listarAlunosDoAluno(int idAlunoLogado) {
        if (adapter != null) {
            // Se o adapter não for nulo, continue com a operação
            List<Aluno_Professor> listaAlunosProfessor = conexao.listarAlunosDoAlunoTelaPortal(idAlunoLogado);

            // Verificar se a lista retornada não está vazia
            if (listaAlunosProfessor != null && !listaAlunosProfessor.isEmpty()) {
                // Limpar a lista atual e adicionar os novos resultados
                adapter.clear();
                adapter.addAll(listaAlunosProfessor);

                // Notificar o adapter sobre a mudança nos dados
                adapter.notifyDataSetChanged();
            } else {
                // Se a lista estiver vazia, você pode optar por não fazer nada ou exibir uma mensagem informativa
                Toast.makeText(this, "A lista de alunos está vazia", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Método para atualizar os dados do aluno na tela
    private void atualizarDadosDoAluno(int idAluno) {
        // Obtém os dados do aluno do Singleton
        Aluno aluno = AlunoSingleton.getInstance().getAluno();

        if (aluno != null) {
            // Atualiza os dados do aluno na tela
            txtNomeRecebido.setText(aluno.getAluno_nome());

            // Busca a foto do aluno e exibe na tela
            String fotoAlunoBase64 = conexao.buscarFotoAluno(aluno.getAluno_id());
            if (fotoAlunoBase64 != null && !fotoAlunoBase64.isEmpty()) {
                byte[] fotoBytes = Base64.decode(fotoAlunoBase64, Base64.DEFAULT);
                Bitmap fotoAlunoBitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                fotoAluno.setImageBitmap(fotoAlunoBitmap);
            } else {
                // Caso não haja foto, exibe um ícone padrão
                fotoAluno.setImageResource(android.R.drawable.ic_menu_camera);
            }

            // Outros detalhes do aluno...

            // Atualiza a lista de alunos-professores associados
            listarAlunosDoAluno(idAluno);
        } else {
            // Lidar com o caso em que os dados do aluno não foram encontrados no Singleton
            Toast.makeText(this, "Dados do aluno não encontrados", Toast.LENGTH_SHORT).show();
        }
    }

    public void voltarTelaInicial() {
        Intent intent1 = new Intent(this, TelaPrincipalActivity.class);
        startActivity(intent1);
        finish();
    }

    public void exibirAjuda(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda!");

        // Criar a SpannableString para cada ícone e explicação
        SpannableString icon1 = createIconSpannable(R.drawable.ic_launcher_logout_foreground);
        SpannableString text1 = new SpannableString("Voltar para a tela inicial, realiza o logoff!\n");

        SpannableString icon2 = createIconSpannable(R.drawable.ic_launcher_delete1_foreground);
        SpannableString text2 = new SpannableString("Inativar o cadastro." + "\n" + "Para reativar, é preciso" +
                "entrar em contato com administrador!.\n");

        SpannableString icon3 = createIconSpannable(R.drawable.ic_launcher_salvar2_foreground);
        SpannableString text3 = new SpannableString("Atualizar a foto do perfil, quando clicar" +
                " na foto do seu perfil, abrirá a câmera do seu celular, e você pode atualizar uma nova foto," +
                " toda vez que você for atualizar a foto, tire uma nova, e clique no botão de atualizar!\n");

        SpannableString icon4 = createIconSpannable(R.drawable.ic_launcher_matricula_foreground);
        SpannableString text4 = new SpannableString("Ir para a tela de matrícula!\n");

        // Concatenar as SpannableStrings
        CharSequence fullText = TextUtils.concat(icon1, text1, icon2, text2, icon3, text3, icon4, text4);

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

    public void chamaAva() {
        Intent intent = new Intent(this, QuestionarioActivity.class);
        // Passar dados do aluno para a próxima Activity
        intent.putExtra("nomeAluno", txtNomeRecebido.getText().toString());
        intent.putExtra("idAluno", txtIdRecebido.getText().toString());
        startActivity(intent);
        finish(); // Isso pode ser omitido se você deseja que a tela de Portal do Aluno permaneça na pilha de atividades
    }


}
