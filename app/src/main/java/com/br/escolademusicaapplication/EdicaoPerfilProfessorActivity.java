package com.br.escolademusicaapplication;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Professor;
import com.br.escolademusicaapplication.OBJETOS.ProfessorSingleton;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EdicaoPerfilProfessorActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    CircleImageView fotoUsuario;
    Button btnExcluir, btnAtualizar, btnInicio;
    TextView txtNomeRecebido, txtIdDados;
    private Bitmap fotoCapturada;
    String fotoEmString = "";
    private Conexao conexao;
    private Spinner spinnerDisciplina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicao_perfil_professor);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        conexao = Conexao.getInstance(this);

        fotoUsuario = findViewById(R.id.fotoUsuario);
        txtIdDados = findViewById(R.id.txtIdDados);
        btnAtualizar = findViewById(R.id.btnAtualizaCadastro);
        btnExcluir = findViewById(R.id.btnExluirCadastro);
        btnInicio = findViewById(R.id.btnVoltar);
        txtNomeRecebido = findViewById(R.id.txtNomeRecebidoProfessor);
        spinnerDisciplina = findViewById(R.id.spinnerDisciplinaRecebidoProfessor);

        Professor professorLogado = ProfessorSingleton.getInstance().getProfessor();

        if (professorLogado != null) {
            txtIdDados.setText("ID: " + professorLogado.getProfessor_id());
            txtNomeRecebido.setText(professorLogado.getProfessor_nome());

            if (professorLogado.getProfessor_foto() != null && !professorLogado.getProfessor_foto().isEmpty()) {
                byte[] decodedString = Base64.decode(professorLogado.getProfessor_foto(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                fotoUsuario.setImageBitmap(decodedBitmap);
            }
        }

        List<String> disciplinasArray = Arrays.asList(getResources().getStringArray(R.array.disciplinas_array));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, disciplinasArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplina.setAdapter(adapter);

        String disciplina = professorLogado.getProfessor_disciplina();
        if (disciplina != null && !disciplina.isEmpty()) {
            int position = disciplinasArray.indexOf(disciplina);
            spinnerDisciplina.setSelection(position);
        }

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
                voltarPortalProfesor();
            }
        });
    }

    private void confirmarExclusao() {
        String nome = ProfessorSingleton.getInstance().getProfessor().getProfessor_nome();
        AlertDialog.Builder confirmaExclusao = new AlertDialog.Builder(EdicaoPerfilProfessorActivity.this);
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
        int professorId = ProfessorSingleton.getInstance().getProfessor().getProfessor_id();

        SQLiteDatabase db = conexao.getWritableDatabase();
        String[] whereArgs = {String.valueOf(professorId)};
        int linhasAfetadas = db.delete("professor", "professor_id = ?", whereArgs);
        db.close();

        if (linhasAfetadas > 0) {
            Toast.makeText(getApplicationContext(), "Professor excluído com sucesso", Toast.LENGTH_SHORT).show();
            voltarPrincipal();
        } else {
            Toast.makeText(getApplicationContext(), "Erro ao excluir o professor", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarProfessor() {
        String idText = txtIdDados.getText().toString().replace("ID: ", "");
        int professorId = Integer.parseInt(idText);
        String nome = txtNomeRecebido.getText().toString();
        String disciplina = spinnerDisciplina.getSelectedItem().toString();

        Professor professor = ProfessorSingleton.getInstance().getProfessor();
        professor.setProfessor_id(professorId);
        professor.setProfessor_nome(nome);
        professor.setProfessor_disciplina(disciplina);
        professor.setProfessor_foto(fotoEmString);

        boolean sucesso = conexao.atualizarProfessor(professor);
        if (sucesso) {
            Toast.makeText(getApplicationContext(), "Professor atualizado com sucesso", Toast.LENGTH_SHORT).show();
            voltarPortalProfesor();
        } else {
            Toast.makeText(getApplicationContext(), "Erro ao atualizar o professor", Toast.LENGTH_SHORT).show();
        }
    }

    public void voltarPortalProfesor() {
        Intent intent = new Intent(this, PortalProfessorActivity.class);
        startActivity(intent);
        finish();
    }

    public void voltarPrincipal() {
        Intent intent = new Intent(this, TelaPrincipalActivity.class);
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
            fotoEmString = ""; // Definindo como vazio caso a foto não seja capturada
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão de câmera concedida", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
