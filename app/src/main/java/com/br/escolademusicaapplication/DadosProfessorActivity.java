package com.br.escolademusicaapplication;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Professor;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DadosProfessorActivity extends AppCompatActivity {

    private List<String> disciplinasArray; //
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    CircleImageView fotoUsuario;
    Button btnExcluir, btnAtualizar, btnInicio;
    TextView txtNomeRecebido, txtIdDados;
    Spinner spinnerDisciplinaRecebidoProfessor;

    ImageView imageViewFotoUsuario;
    private Bitmap fotoCapturada;
    String fotoEmString = "";
    private Conexao conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_professor);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        fotoUsuario = findViewById(R.id.fotoUsuario);
        txtIdDados = findViewById(R.id.txtIdDados);
        btnAtualizar = findViewById(R.id.btnAtualizaCadastro);
        btnExcluir = findViewById(R.id.btnExluirCadastro);
        btnInicio = findViewById(R.id.btnVoltar);
        txtNomeRecebido = findViewById(R.id.txtNomeRecebidoProfessor);
        spinnerDisciplinaRecebidoProfessor = findViewById(R.id.spinnerDisciplinaRecebidoProfessor);

        conexao = Conexao.getInstance(this);

        fotoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fotoCapturada != null) {
                    fotoUsuario.setImageBitmap(fotoCapturada);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra("id", -1);
            if (id != -1) {
                txtIdDados.setText("ID: " + String.valueOf(id));
            }

            String nome = intent.getStringExtra("nome");
            String disciplina = intent.getStringExtra("disciplina");

            if (nome != null) {
                txtNomeRecebido.setText(nome);
            }

            // Configurar o spinner com as disciplinas do banco e da lista local
            disciplinasArray = new ArrayList<>();

            // Adicionar as disciplinas do banco de dados
            List<String> disciplinasBanco = obterDisciplinasDoBanco();
            disciplinasArray.addAll(disciplinasBanco);

            // Adicionar as disciplinas da lista local (caso existam)
            List<String> disciplinasLocais = Arrays.asList(getResources().getStringArray(R.array.disciplinas_array));
            disciplinasArray.addAll(disciplinasLocais);

            // Criar o adapter para o spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, disciplinasArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDisciplinaRecebidoProfessor.setAdapter(adapter);

            // Selecionar a disciplina recebida, se disponível
            if (disciplina != null) {
                int position = disciplinasArray.indexOf(disciplina);
                spinnerDisciplinaRecebidoProfessor.setSelection(position);
            }
        }

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarProfessor();
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarExclusao();
            }
        });

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarPortalAdm();
            }
        });
    }

    private List<String> obterDisciplinasDoBanco() {
        // Implemente a lógica para obter as disciplinas do banco de dados aqui
        // Exemplo fictício:
        List<String> disciplinas = new ArrayList<>();
        disciplinas.add("Matemática");
        disciplinas.add("Português");
        disciplinas.add("História");
        return disciplinas;
    }

    private void confirmarExclusao() {
        Intent intent = getIntent();
        String nome = intent.getStringExtra("nome");
        AlertDialog.Builder confirmaExclusao = new AlertDialog.Builder(DadosProfessorActivity.this);
        confirmaExclusao.setTitle("Atenção!");
        confirmaExclusao.setMessage("Tem certeza que quer excluir " + nome + "? ");
        confirmaExclusao.setCancelable(false);
        confirmaExclusao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                excluirProfessor();
            }
        });
        confirmaExclusao.setNegativeButton("Não", null);
        confirmaExclusao.create().show();
    }

    private void excluirProfessor() {
        String idText = txtIdDados.getText().toString().replace("ID: ", "");
        if (!idText.isEmpty()) {
            int professorId = Integer.parseInt(idText);

            SQLiteDatabase db = conexao.getWritableDatabase();

            // Defina a cláusula WHERE e os argumentos
            String whereClause = "professor_id = ?";
            String[] whereArgs = {String.valueOf(professorId)};

            // Exclui o professor (e todos os registros associados na tabela aluno_professor)
            int linhasAfetadas = db.delete("professor", whereClause, whereArgs);
            db.close();

            if (linhasAfetadas > 0) {
                Toast.makeText(getApplicationContext(), "Professor excluído com sucesso", Toast.LENGTH_SHORT).show();
                voltarLista();
            } else {
                Toast.makeText(getApplicationContext(), "Erro ao excluir o professor", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "ID do professor não encontrado", Toast.LENGTH_SHORT).show();
        }
    }


    private void atualizarProfessor() {
        String idText = txtIdDados.getText().toString().replace("ID: ", "");
        int professorId = Integer.parseInt(idText);

        String nome = txtNomeRecebido.getText().toString();
        String disciplina = spinnerDisciplinaRecebidoProfessor.getSelectedItem().toString();

        Professor professor = new Professor();
        professor.setProfessor_id(professorId);
        professor.setProfessor_nome(nome);
        professor.setProfessor_disciplina(disciplina);
        professor.setProfessor_foto(fotoEmString);

        boolean sucesso = conexao.atualizarProfessor(professor);
        if (sucesso) {
            Toast.makeText(getApplicationContext(), "Professor atualizado com sucesso", Toast.LENGTH_SHORT).show();
            voltarLista();
        } else {
            Toast.makeText(getApplicationContext(), "Erro ao atualizar o professor", Toast.LENGTH_SHORT).show();
        }
    }

    public void voltarLista() {
        Intent intent = new Intent(this, ListaProfessorActivity.class);
        startActivity(intent);
        finish();
    }

    public void voltarPortalAdm() {
        Intent intent = new Intent(this, PainelAdmActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dados) {
        super.onActivityResult(requestCode, resultCode, dados);
        if (resultCode == RESULT_OK && requestCode == 1) {
            try {
                fotoCapturada = (Bitmap) Objects.requireNonNull(dados.getExtras()).get("data");
                fotoUsuario.setImageBitmap(fotoCapturada);

                byte[] fotoEmBytes;
                ByteArrayOutputStream streamDaFotoEmBytes = new ByteArrayOutputStream();

                fotoCapturada.compress(Bitmap.CompressFormat.PNG, 70, streamDaFotoEmBytes);
                fotoEmBytes = streamDaFotoEmBytes.toByteArray();

                fotoEmString = Base64.encodeToString(fotoEmBytes, Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            fotoUsuario.setImageResource(android.R.drawable.ic_menu_camera);
            fotoEmString = "null";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão de câmera concedida, iniciar a captura de foto ou qualquer outra ação relacionada à câmera
                Toast.makeText(this, "Permissão de câmera concedida", Toast.LENGTH_SHORT).show();
            } else {
                // Permissão de câmera negada, informar ao usuário
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
    }
