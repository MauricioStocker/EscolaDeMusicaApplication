package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Aluno;
import com.br.escolademusicaapplication.OBJETOS.AlunoSingleton;
import com.br.escolademusicaapplication.OBJETOS.Aluno_Professor;
import com.br.escolademusicaapplication.TelaPrincipalActivity;
import com.br.escolademusicaapplication.adaptor.AlunoProfessorAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PortalDoAlunoActivity extends AppCompatActivity {

	private static final int REQUEST_CAMERA_PERMISSION = 1;
	private static final int PICK_IMAGE_REQUEST = 2;
	private static final int REQUEST_IMAGE_CAPTURE = 3;

	private CircleImageView fotoAluno;
	private Button btnAtualizar, btnExcluir, btnVoltar, btnMatricula,btnAva;
	private TextView txtNomeRecebido, txtIdRecebido, txtMatriculaRecebido;
	private Bitmap fotoCapturada;
	private String fotoEmString = "";
	private Conexao conexao;

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
		txtNomeRecebido = findViewById(R.id.txtNomeRecebidoProfessor);
		txtIdRecebido = findViewById(R.id.txtIdRecebido);
		txtMatriculaRecebido = findViewById(R.id.txtMatriculaRecebido);
		btnAva = findViewById(R.id.btnAva);
		Button btnCarteirinha = findViewById(R.id.btnCarteirinha);
		btnCarteirinha.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mostrarCarteirinha();
			}
		});
		btnAva.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chamaAva();
			}
		});


		// Configurar dados do aluno a partir do Singleton
		Aluno aluno = AlunoSingleton.getInstance().getAluno();
		if (aluno != null) {
			txtNomeRecebido.setText(aluno.getAluno_nome());
			txtIdRecebido.setText(String.valueOf(aluno.getAluno_id()));
			txtMatriculaRecebido.setText("Matrícula: " + aluno.getAluno_matricula());

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


		btnMatricula.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String idAluno = txtIdRecebido.getText().toString().replace("ID: ", "");

				Aluno aluno = conexao.buscarAlunoPorId(idAluno);
				if (aluno != null) {
					Intent intent = new Intent(PortalDoAlunoActivity.this, TelaMatriculaActivity.class);
					intent.putExtra("aluno", aluno);
					startActivity(intent);
				} else {
					Toast.makeText(PortalDoAlunoActivity.this, "Dados do aluno não encontrados", Toast.LENGTH_SHORT).show();
				}
			}
		});

		fotoAluno.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				escolherOpcaoImagem();
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
				confirmarInativacao();
			}
		});

		btnVoltar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				voltarTelaInicial();
			}
		});

		// Inicializar o adapter e associá-lo à ListView
		ListView listViewAlunoProfessor = findViewById(R.id.listaAulas);
		adapter = new AlunoProfessorAdapter(this, listaAlunoProfessor);
		listViewAlunoProfessor.setAdapter(adapter);

		int idAlunoLogado = AlunoSingleton.getInstance().getAluno().getAluno_id();
		listarAlunosDoAluno(idAlunoLogado);
		atualizarDadosDoAluno(idAlunoLogado);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case PICK_IMAGE_REQUEST:
					if (data != null && data.getData() != null) {
						Uri imageUri = data.getData();
						try {
							Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
							Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 600);
							fotoEmString = convertBitmapToBase64(resizedBitmap);
							fotoAluno.setImageBitmap(resizedBitmap);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				case REQUEST_IMAGE_CAPTURE:
					if (data != null && data.getExtras() != null) {
						Bitmap photo = (Bitmap) data.getExtras().get("data");
						Bitmap resizedPhoto = resizeBitmap(photo, 800, 600);
						fotoEmString = convertBitmapToBase64(resizedPhoto);
						fotoAluno.setImageBitmap(resizedPhoto);
					}
					break;
			}
		} else {
			// Se não houver resultado, defina fotoEmString como nulo ou mantenha a foto existente
			fotoEmString = null;
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
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		return Base64.encodeToString(byteArray, Base64.DEFAULT);
	}

	private void atualizarAluno() {
		int idAluno = AlunoSingleton.getInstance().getAluno().getAluno_id();
		String nome = txtNomeRecebido.getText().toString();

		Aluno aluno = new Aluno();
		aluno.setAluno_id(idAluno);
		aluno.setAluno_nome(nome);

		if (fotoEmString != null) {
			aluno.setAluno_foto(fotoEmString);
		} else {
			// Manter a foto existente ou definir uma foto padrão
			aluno.setAluno_foto(AlunoSingleton.getInstance().getAluno().getAluno_foto());
		}

		boolean sucesso = conexao.atualizarAluno(aluno);

		if (sucesso) {
			Toast.makeText(getApplicationContext(), "Aluno atualizado com sucesso", Toast.LENGTH_SHORT).show();
			// Atualizar a imagem na ImageView fotoAluno
			Bitmap fotoAtualizada = decodeBase64ToBitmap(aluno.getAluno_foto());
			fotoAluno.setImageBitmap(fotoAtualizada);
		} else {
			Toast.makeText(getApplicationContext(), "Erro ao atualizar o aluno", Toast.LENGTH_SHORT).show();
		}
	}


	// Método auxiliar para decodificar a string Base64 em Bitmap
	private Bitmap decodeBase64ToBitmap(String base64String) {
		byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
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


	// Continuação da implementação da PortalDoAlunoActivity

	// Método para confirmar a exclusão do aluno
	// Método para confirmar a inativação do aluno
	private void confirmarInativacao() {
		String nome = txtNomeRecebido.getText().toString();
		AlertDialog.Builder confirmaInativacao = new AlertDialog.Builder(PortalDoAlunoActivity.this);
		confirmaInativacao.setTitle("Atenção!");
		confirmaInativacao.setMessage("Tem certeza que quer inativar " + nome + "? ");
		confirmaInativacao.setCancelable(false);
		confirmaInativacao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				inativarAluno();
			}
		});
		confirmaInativacao.setNegativeButton("Não", null);
		confirmaInativacao.create().show();
	}


	// Método para excluir o aluno do banco de dados
	// Método para inativar o aluno no banco de dados
	private void inativarAluno() {
		int idAluno = AlunoSingleton.getInstance().getAluno().getAluno_id();

		SQLiteDatabase db = conexao.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("aluno_status", "inativo");

		String[] whereArgs = {String.valueOf(idAluno)};
		int linhasAfetadas = db.update("aluno", values, "aluno_id = ?", whereArgs);

		if (linhasAfetadas > 0) {
			Toast.makeText(getApplicationContext(), "Aluno inativado com sucesso", Toast.LENGTH_SHORT).show();
			voltarTelaInicial();
		} else {
			Toast.makeText(getApplicationContext(), "Erro ao inativar o Aluno", Toast.LENGTH_SHORT).show();
		}
	}


	// Método para voltar para a tela inicial
	public void voltarTelaInicial() {
		Intent intent1 = new Intent(this, TelaPrincipalActivity.class);
		startActivity(intent1);
		finish();
	}

	// Método para exibir a ajuda ao usuário
	public void exibirAjuda(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Ajuda!");

		// Criar a SpannableString para cada ícone e explicação
		SpannableString icon1 = createIconSpannable(R.drawable.ic_launcher_logout_foreground);
		SpannableString text1 = new SpannableString("Voltar para a tela inicial, realiza o logoff!\n");

		SpannableString icon2 = createIconSpannable(R.drawable.ic_launcher_delete1_foreground);
		SpannableString text2 = new SpannableString("Inativar o cadastro. Para reativar, é preciso entrar em contato com o administrador!\n");

		SpannableString icon3 = createIconSpannable(R.drawable.ic_launcher_atualiza_foto_foreground);
		SpannableString text3 = new SpannableString("Atualizar a foto do perfil. Clique na foto do seu perfil para abrir a câmera do seu celular e atualizar uma nova foto.\n");

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

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CAMERA_PERMISSION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permissão concedida, pode abrir a câmera se necessário
			} else {
				// Permissão negada, avise o usuário ou tome outra ação apropriada
				Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Atualiza os dados do aluno ao retomar a atividade
		int idAlunoLogado = AlunoSingleton.getInstance().getAluno().getAluno_id();
		atualizarDadosDoAluno(idAlunoLogado);
	}

	// Método para atualizar os dados do aluno na tela
	private void atualizarDadosDoAluno(int idAluno) {
		// Busca o aluno completo no banco de dados
		Aluno aluno = conexao.buscarAlunoPorId(idAluno);

		if (aluno != null) {
			// Preenche os dados do aluno
			txtNomeRecebido.setText(aluno.getAluno_nome());
			txtMatriculaRecebido.setText("Matrícula: " + aluno.getAluno_matricula());

			// Busca a foto do aluno e exibe na tela
			String fotoAlunoBase64 = aluno.getAluno_foto();
			if (fotoAlunoBase64 != null && !fotoAlunoBase64.isEmpty()) {
				byte[] fotoBytes = Base64.decode(fotoAlunoBase64, Base64.DEFAULT);
				Bitmap fotoAlunoBitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
				fotoAluno.setImageBitmap(fotoAlunoBitmap);
			} else {
				// Caso não haja foto, exibe um ícone padrão
				fotoAluno.setImageResource(android.R.drawable.ic_menu_camera);
			}

			// Atualiza a lista de alunos-professores associados
			listarAlunosDoAluno(idAluno);
		} else {
			Toast.makeText(this, "Dados do aluno não encontrados", Toast.LENGTH_SHORT).show();
		}
	}


	public void chamaAva() {
		Intent intent = new Intent(this, QuestionarioActivity.class);
		// Passar dados do aluno para a próxima Activity
		intent.putExtra("nomeAluno", txtNomeRecebido.getText().toString());
		intent.putExtra("idAluno", txtIdRecebido.getText().toString());
		startActivity(intent);
		finish(); // Isso pode ser omitido se você deseja que a tela de Portal do Aluno permaneça na pilha de atividades
	}
	private void mostrarCarteirinha() {
		// Obtém o ID do aluno logado
		int idAlunoLogado = AlunoSingleton.getInstance().getAluno().getAluno_id();

		// Busca o aluno completo no banco de dados
		Aluno aluno = conexao.buscarAlunoPorId(idAlunoLogado);

		// Cria e configura o diálogo
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_carteirinha);
		dialog.setTitle("Carteirinha do Estudante");

		// Obtém as referências das views do diálogo
		ImageView fotoCarteirinha = dialog.findViewById(R.id.fotoCarteirinha);
		TextView nomeCarteirinha = dialog.findViewById(R.id.nomeCarteirinha);
		TextView matriculaCarteirinha = dialog.findViewById(R.id.matriculaCarteirinha);
		TextView cpfCarteirinha = dialog.findViewById(R.id.cpfCarteirinha);
		TextView dataNascCarteirinha = dialog.findViewById(R.id.dataNascCarteirinha);

		// Preenche os dados na carteirinha
		if (aluno != null) {
			nomeCarteirinha.setText(aluno.getAluno_nome());
			matriculaCarteirinha.setText("Matrícula: " + aluno.getAluno_matricula());
			cpfCarteirinha.setText("CPF: " + aluno.getAluno_cpf());
			dataNascCarteirinha.setText("Data de Nascimento: " + aluno.getAluno_dataNascimento());

			// Verifica se há uma foto válida para exibir
			if (aluno.getAluno_foto() != null && !aluno.getAluno_foto().isEmpty()) {
				byte[] fotoBytes = Base64.decode(aluno.getAluno_foto(), Base64.DEFAULT);
				Bitmap fotoAlunoBitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
				fotoCarteirinha.setImageBitmap(fotoAlunoBitmap);
			} else {
				// Caso não haja foto, você pode exibir um ícone padrão ou deixar em branco
				fotoCarteirinha.setImageResource(R.drawable.user);
			}
		}

		dialog.show();
	}



}
