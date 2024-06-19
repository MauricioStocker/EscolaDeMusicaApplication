package com.br.escolademusicaapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.br.escolademusicaapplication.OBJETOS.Galeria;
import com.br.escolademusicaapplication.adaptor.GaleriaAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class AddGaleriaActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    private EditText editTextTitulo;
    private EditText editTextDescricao;
    private ImageView imageViewGaleria;
    private Button buttonSalvar, buttonAtualizar, buttonExcluir, btnVoltarGaleria;
    private String imagemBase64;
    private Conexao conexao;
    private RecyclerView recyclerViewGalerias;
    private GaleriaAdapter galeriaAdapter;
    private List<Galeria> listaGalerias;
    private int galeriaIdSelecionada = -1; // Para saber qual galeria está sendo editada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_galeria);
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
        imageViewGaleria = findViewById(R.id.imageViewGaleria);
        buttonSalvar = findViewById(R.id.btnSalvarGaleria);
        buttonAtualizar = findViewById(R.id.btnAtualizarGaleria);
        buttonExcluir = findViewById(R.id.btnExcluirGaleria);
        recyclerViewGalerias = findViewById(R.id.recyclerViewGalerias);
        btnVoltarGaleria = findViewById(R.id.btnVoltarGaleria);

        btnVoltarGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarPainelAdm();
                finish();
            }
        });

        imageViewGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherOpcaoImagem();
            }
        });

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarGaleria();
                escondeTeclado();
            }
        });

        buttonAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarGaleria();
                escondeTeclado();
            }
        });

        buttonExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirGaleria();
                escondeTeclado();
            }
        });

        recyclerViewGalerias.setLayoutManager(new LinearLayoutManager(this));
        carregarGalerias();
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
                            imageViewGaleria.setImageBitmap(resizedBitmap);
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
                        imageViewGaleria.setImageBitmap(resizedPhoto);
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

    private void salvarGaleria() {
        String titulo = editTextTitulo.getText().toString();
        String descricao = editTextDescricao.getText().toString();

        if (titulo.isEmpty() || descricao.isEmpty() || imagemBase64 == null) {
            Toast.makeText(this, "Preencha todos os campos e selecione uma imagem.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = conexao.insertGaleria(titulo, descricao, imagemBase64);

        if (isInserted) {
            Toast.makeText(this, "Galeria salva com sucesso!", Toast.LENGTH_SHORT).show();
            carregarGalerias();
            limparCampos();
        } else {
            Toast.makeText(this, "Erro ao salvar galeria.", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarGaleria() {
        String titulo = editTextTitulo.getText().toString();
        String descricao = editTextDescricao.getText().toString();

        if (titulo.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = conexao.updateGaleria(galeriaIdSelecionada, titulo, descricao, imagemBase64);

        if (isUpdated) {
            Toast.makeText(this, "Galeria atualizada com sucesso!", Toast.LENGTH_SHORT).show();
            carregarGalerias();
            limparCampos();
        } else {
            Toast.makeText(this, "Erro ao atualizar galeria.", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirGaleria() {
        if (galeriaIdSelecionada == -1) {
            Toast.makeText(this, "Nenhuma galeria selecionada para exclusão.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isDeleted = conexao.deleteGaleria(galeriaIdSelecionada);

        if (isDeleted) {
            Toast.makeText(this, "Galeria excluída com sucesso!", Toast.LENGTH_SHORT).show();
            carregarGalerias();
            limparCampos();
            galeriaIdSelecionada = -1;
        } else {
            Toast.makeText(this, "Erro ao excluir galeria.", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarGalerias() {
        try {
            listaGalerias = conexao.getAllGalerias();

            if (listaGalerias != null && !listaGalerias.isEmpty()) {
                Log.d("AddGaleriaActivity", "Quantidade de galerias carregadas: " + listaGalerias.size());

                galeriaAdapter = new GaleriaAdapter(this, listaGalerias, new GaleriaAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Galeria galeria) {
                        galeriaIdSelecionada = galeria.getGaleria_id();
                        editTextTitulo.setText(galeria.getGaleria_titulo());
                        editTextDescricao.setText(galeria.getGaleria_descricao());

                        if (galeria.getGaleria_imagem() != null && !galeria.getGaleria_imagem().isEmpty()) {
                            Bitmap bitmap = convertBase64ToBitmap(galeria.getGaleria_imagem());
                            imageViewGaleria.setImageBitmap(bitmap);
                            imagemBase64 = galeria.getGaleria_imagem();
                        } else {
                            imageViewGaleria.setImageResource(R.drawable.user); // Substitua "R.drawable.user" pelo recurso padrão
                            imagemBase64 = null;
                        }

                        buttonSalvar.setVisibility(View.GONE);
                        buttonAtualizar.setVisibility(View.VISIBLE);
                        buttonExcluir.setVisibility(View.VISIBLE);
                    }
                });

                recyclerViewGalerias.setAdapter(galeriaAdapter);
            } else {
                Log.d("AddGaleriaActivity", "A lista de galerias está vazia ou nula");

                // Configurar um adaptador vazio ou nulo para evitar o erro "No adapter attached"
                recyclerViewGalerias.setAdapter(null);

                // Exibir uma mensagem ao usuário informando que a lista está vazia
                Toast.makeText(this, "Nenhuma galeria encontrada.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("AddGaleriaActivity", "Erro ao carregar galerias: " + e.getMessage());
            e.printStackTrace();

            // Exibir uma mensagem de erro ao usuário
            Toast.makeText(this, "Erro ao carregar galerias. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();

            // Configurar um adaptador vazio ou nulo em caso de erro
            recyclerViewGalerias.setAdapter(null);
        }
    }

    private Bitmap convertBase64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }



    private void limparCampos() {
        editTextTitulo.setText("");
        editTextDescricao.setText("");
        imageViewGaleria.setImageResource(R.drawable.user); // Substitua por sua imagem placeholder
        imagemBase64 = null;
        galeriaIdSelecionada = -1;
        buttonAtualizar.setVisibility(View.GONE);
        buttonExcluir.setVisibility(View.GONE);
    }

    private void escondeTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void voltarPainelAdm() {
        Intent intent = new Intent(AddGaleriaActivity.this, PainelAdmActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
            } else {
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
