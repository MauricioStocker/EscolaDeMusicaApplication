package com.br.escolademusicaapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Curso;
import com.br.escolademusicaapplication.adaptor.CursoAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class AddCursoActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private EditText editTextTitulo;
    private EditText editTextDescricao;
    private ImageView imageViewCurso;
    private Button buttonSalvar, buttonAtualizar, buttonExcluir, btnVoltarGaleria;
    private String imagemBase64;
    private Conexao conexao;
    private RecyclerView recyclerViewCursos;
    private CursoAdapter cursoAdapter;
    private List<Curso> listaCursos;
    private int cursoIdSelecionado = -1; // Para saber qual curso está sendo editado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_curso);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        conexao = Conexao.getInstance(this); // Instância única de conexão

        editTextTitulo = findViewById(R.id.txtTituloGaleria);
        editTextDescricao = findViewById(R.id.txtDescricaoGaleria);
        imageViewCurso = findViewById(R.id.imageViewGaleria);
        buttonSalvar = findViewById(R.id.btnSalvarGaleria);
        buttonAtualizar = findViewById(R.id.btnAtualizarGaleria);
        buttonExcluir = findViewById(R.id.btnExcluirGaleria);
        recyclerViewCursos = findViewById(R.id.recyclerViewCursos);
        btnVoltarGaleria = findViewById(R.id.btnVoltarGaleria);

        btnVoltarGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarPainelAdm();
                finish();
            }
        });

        imageViewCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherOpcaoImagem();
            }
        });

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarCurso();
                escondeTeclado();
            }
        });

        buttonAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarCurso();
                escondeTeclado();
            }
        });

        buttonExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirCurso();
                escondeTeclado();
            }
        });

        recyclerViewCursos.setLayoutManager(new LinearLayoutManager(this));
        carregarCursos();
    }

    private void escolherOpcaoImagem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma opção");
        builder.setItems(new CharSequence[]{"Galeria", "Câmera"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        escolherImagemGaleria();
                        break;
                    case 1:
                        tirarFoto();
                        break;
                }
            }
        });
        builder.show();
    }

    private void escolherImagemGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void tirarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    if (data != null && data.getData() != null) {
                        Uri imageUri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 600);
                            imagemBase64 = convertBitmapToBase64(resizedBitmap);
                            imageViewCurso.setImageBitmap(resizedBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    if (data != null && data.getExtras() != null) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        Bitmap resizedPhoto = resizeBitmap(photo, 800, 600);
                        imagemBase64 = convertBitmapToBase64(resizedPhoto);
                        imageViewCurso.setImageBitmap(resizedPhoto);
                    }
                    break;
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleRatio = Math.min((float) maxWidth / width, (float) maxHeight / height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRatio, scaleRatio);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void salvarCurso() {
        String titulo = editTextTitulo.getText().toString();
        String descricao = editTextDescricao.getText().toString();

        if (titulo.isEmpty() || descricao.isEmpty() || imagemBase64 == null) {
            Toast.makeText(this, "Preencha todos os campos e selecione uma imagem.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = conexao.insertCurso(titulo, descricao, imagemBase64);

        if (isInserted) {
            Toast.makeText(this, "Curso salvo com sucesso!", Toast.LENGTH_SHORT).show();
            carregarCursos();
            limparCampos();
            escondeTeclado();
        } else {
            Toast.makeText(this, "Erro ao salvar curso.", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarCurso() {
        String titulo = editTextTitulo.getText().toString();
        String descricao = editTextDescricao.getText().toString();

        if (titulo.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = conexao.updateCurso(cursoIdSelecionado, titulo, descricao, imagemBase64);

        if (isUpdated) {
            Toast.makeText(this, "Curso atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            carregarCursos();
            limparCampos();
            escondeTeclado();
        } else {
            Toast.makeText(this, "Erro ao atualizar curso.", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirCurso() {
        if (cursoIdSelecionado == -1) {
            Toast.makeText(this, "Nenhum curso selecionado para exclusão.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isDeleted = conexao.deleteCurso(cursoIdSelecionado);

        if (isDeleted) {
            Toast.makeText(this, "Curso excluído com sucesso!", Toast.LENGTH_SHORT).show();
            carregarCursos();
            limparCampos();
            cursoIdSelecionado = -1;
        } else {
            Toast.makeText(this, "Erro ao excluir curso.", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarCursos() {
        try {
            listaCursos = conexao.getAllCursos();

            if (listaCursos != null && !listaCursos.isEmpty()) {
                Log.d("AddCursoActivity", "Quantidade de cursos carregados: " + listaCursos.size());

                cursoAdapter = new CursoAdapter(listaCursos, this, new CursoAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Curso curso) {
                        cursoIdSelecionado = curso.getCurso_id();
                        editTextTitulo.setText(curso.getCurso_titulo());
                        editTextDescricao.setText(curso.getCurso_descricao());
                        if (curso.getCurso_imagem() != null && !curso.getCurso_imagem().isEmpty()) {
                            byte[] decodedString = Base64.decode(curso.getCurso_imagem(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageViewCurso.setImageBitmap(decodedByte);
                            imagemBase64 = curso.getCurso_imagem();
                        } else {
                            imageViewCurso.setImageResource(R.drawable.user);
                            imagemBase64 = null;
                        }
                        buttonSalvar.setVisibility(View.GONE);
                        buttonAtualizar.setVisibility(View.VISIBLE);
                        buttonExcluir.setVisibility(View.VISIBLE);
                    }
                });

                recyclerViewCursos.setAdapter(cursoAdapter);
            } else {
                Log.d("AddCursoActivity", "A lista de cursos está vazia ou nula");

                // Configurar um adaptador vazio ou nulo para evitar o erro "No adapter attached"
                recyclerViewCursos.setAdapter(null);

                // Exibir uma mensagem ao usuário informando que a lista está vazia
                Toast.makeText(this, "Nenhum curso encontrado.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("AddCursoActivity", "Erro ao carregar cursos: " + e.getMessage());
            e.printStackTrace();
            // Exibir uma mensagem de erro ao usuário
            Toast.makeText(this, "Erro ao carregar cursos. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();

            // Configurar um adaptador vazio ou nulo em caso de erro
            recyclerViewCursos.setAdapter(null);
        }
    }


    private void limparCampos() {
        editTextTitulo.getText().clear();
        editTextDescricao.getText().clear();
        imageViewCurso.setImageResource(R.drawable.user);
        imagemBase64 = null;
        cursoIdSelecionado = -1;
    }

    private void escondeTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void voltarPainelAdm() {
        Intent intent = new Intent(this, PainelAdmActivity.class);
        startActivity(intent);
        finish();
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

