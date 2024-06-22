package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Aluno;
import com.br.escolademusicaapplication.OBJETOS.AlunoSingleton;
import com.br.escolademusicaapplication.OBJETOS.Professor;
import com.br.escolademusicaapplication.OBJETOS.ProfessorSingleton;

public class TelaLoginActivity extends AppCompatActivity {
    private Conexao conexao;

    ProgressBar progressLogin;
    EditText txtUsuario;
    EditText txtPassword;
    Button btnLogin, btnVoltarPrincipal, btnRecuperarSenha;
    Button btnTelaCadastro;
    AlertDialog alertDialog;
    String ultimoCaracterDigitado = "";
    private String cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);
        conexao = Conexao.getInstance(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressLogin = findViewById(R.id.progressLogin);
        btnTelaCadastro = findViewById(R.id.btnTelaCadastro);
        btnVoltarPrincipal = findViewById(R.id.btnVoltarPrincipal);
        btnRecuperarSenha = findViewById(R.id.btnRecuperarSenha);

        //mascara de campos cpf
        txtUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Não é necessário implementar nesta parte
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cpf = s.toString().replaceAll("[^\\d]", ""); // Remove caracteres não numéricos
                StringBuilder formattedCpf = new StringBuilder();

                int length = cpf.length();
                if (length > 3) {
                    formattedCpf.append(cpf.substring(0, 3)).append(".");
                    if (length > 6) {
                        formattedCpf.append(cpf.substring(3, 6)).append(".");
                        if (length > 9) {
                            formattedCpf.append(cpf.substring(6, 9)).append("-");
                            if (length > 11) {
                                formattedCpf.append(cpf.substring(9, 11));
                            } else {
                                formattedCpf.append(cpf.substring(9));
                            }
                        } else {
                            formattedCpf.append(cpf.substring(6));
                        }
                    } else {
                        formattedCpf.append(cpf.substring(3));
                    }
                } else {
                    formattedCpf.append(cpf);
                }

                txtUsuario.removeTextChangedListener(this); // Remove o listener temporariamente para evitar loop infinito
                txtUsuario.setText(formattedCpf.toString());
                txtUsuario.setSelection(txtUsuario.getText().length()); // Define a seleção no final do texto
                txtUsuario.addTextChangedListener(this); // Restaura o listener após a formatação
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Não é necessário implementar nesta parte
            }
        });


        btnRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criando o AlertDialog para o primeiro pop-up (CPF)
                AlertDialog.Builder builder = new AlertDialog.Builder(TelaLoginActivity.this);
                LayoutInflater inflater = TelaLoginActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_cpf_aluno, null);
                builder.setView(dialogView);

                EditText editTextCPF = dialogView.findViewById(R.id.editTextCPF);
                Button btnBuscarAluno = dialogView.findViewById(R.id.btnBuscarAluno);

                // Configurando o botão "Buscar Aluno" para o primeiro pop-up
                alertDialog = builder.create();
                btnBuscarAluno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String cpfAluno = editTextCPF.getText().toString();
                        Aluno aluno = conexao.buscarAlunoPorCPF(cpfAluno);
                        if (aluno != null) {
                            // Aluno encontrado, exibe o segundo pop-up (Nova Senha)
                            alertDialog.dismiss(); // Fecha o primeiro pop-up
                            exibirPopUpNovaSenha(aluno); // Passa o aluno como parâmetro
                        } else {
                            // Aluno não encontrado, exibe mensagem e fecha o pop-up
                            Toast.makeText(TelaLoginActivity.this, "Aluno não encontrado", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });

                // Exibindo o primeiro pop-up (CPF)
                alertDialog.show();
            }
        });
        btnVoltarPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaTelaPrincipal();
            }
        });

        // Inicia o ProgressBar como invisível
        progressLogin.setVisibility(View.GONE);

        btnTelaCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaTelaCadastro();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostra o ProgressBar ao clicar no botão de login
                progressLogin.setVisibility(View.VISIBLE);

                // Simula um processo de autenticação com uma Thread
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autenticaUsuario(txtUsuario.getText().toString(), txtPassword.getText().toString());
                        txtUsuario.setText("");
                        txtPassword.setText("");

                        escondeTeclado();
                    }
                }, 3000); // 3 segundos de espera
            }
        });
    }

    private void autenticaUsuario(String cpf, String senha) {
        cpf = cpf.replaceAll("[^\\d]", "");
        String tipoUsuario = conexao.autenticarUsuario(cpf, senha, this).toLowerCase();

        // Oculta o ProgressBar após o término do processo de autenticação
        progressLogin.setVisibility(View.GONE);

        if (!tipoUsuario.isEmpty()) {
            if (tipoUsuario.equals("aluno") && conexao.existeProfessorComCPF(cpf) ||
                    tipoUsuario.equals("professor") && conexao.existeAlunoComCPF(cpf)) {
                exibirDialogoEscolhaPerfil(cpf, senha);
            } else {
                abrirTelaCorrespondente(cpf, senha, tipoUsuario);
            }
        } else {
            // Lógica para lidar com a falha na autenticação
            Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_LONG).show();
        }
    }

    private void exibirDialogoEscolhaPerfil(String cpf, String senha) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha o perfil:");

        builder.setPositiveButton("Aluno", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                abrirTelaCorrespondente(cpf, senha, "aluno");
            }
        });

        builder.setNegativeButton("Professor", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                abrirTelaCorrespondente(cpf, senha, "professor");
            }
        });

        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lógica para cancelar o processo de login
                // Por exemplo, fechar a activity de login ou mostrar uma mensagem de cancelamento
                Toast.makeText(TelaLoginActivity.this, "Login cancelado", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setCancelable(false); // Impede o fechamento do diálogo ao clicar fora dele
        AlertDialog dialog = builder.create();
        dialog.show();

        // Impede o cancelamento do diálogo ao clicar fora dele
        dialog.setCanceledOnTouchOutside(false);
    }


    private void abrirTelaCorrespondente(String cpf, String senha, String tipoUsuario) {
        Intent intent = null;
        if (tipoUsuario.equals("admin")) {
            intent = new Intent(this, PainelAdmActivity.class);
        } else if (tipoUsuario.equals("professor")) {
            // Configurar os dados do professor na instância Singleton
            Professor professorAutenticado = conexao.buscarProfessor(cpf, senha, this);
            if (professorAutenticado != null) {
                ProfessorSingleton.getInstance().setProfessor(professorAutenticado);
                intent = new Intent(this, PortalProfessorActivity.class);
            }
        } else if (tipoUsuario.equals("aluno")) {
            // Configura os dados do aluno na instância Singleton
            Aluno alunoAutenticado = conexao.buscarAluno(cpf, senha, this);
            if (alunoAutenticado != null) {
                AlunoSingleton.getInstance().setAluno(alunoAutenticado);
                intent = new Intent(this, PortalDoAlunoActivity.class);
            } else {
                // Lógica para lidar com a falha na autenticação
                Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            // Lógica para lidar com a falha na autenticação
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_LONG).show();
            return;
        }

        if (intent != null) {
            startActivity(intent);

            // Limpa os campos de entrada e define o foco no campo de usuário
            txtUsuario.setText("");
            txtPassword.setText("");
            txtUsuario.requestFocus();
        } else {
            // Lógica para lidar com a falha na autenticação
            Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_LONG).show();
        }
    }


    public void chamaTelaCadastro() {
        Intent intent = new Intent(this, CadastroAlunoActivity.class);
        startActivity(intent);

    }

    public void chamaTelaPrincipal() {
        Intent intent = new Intent(this, TelaPrincipalActivity.class);
        startActivity(intent);

    }

    private void exibirPopUpNovaSenha(Aluno aluno) {
        // Criando o AlertDialog para o segundo pop-up (Nova Senha)
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaLoginActivity.this);
        LayoutInflater inflater = TelaLoginActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_nova_senha, null);
        builder.setView(dialogView);

        EditText editTextNovaSenha1 = dialogView.findViewById(R.id.editTextNovaSenha1);
        EditText editTextNovaSenha2 = dialogView.findViewById(R.id.editTextNovaSenha2);
        Button btnConfirmarSenha = dialogView.findViewById(R.id.btnConfirmarSenha);

        // Configurando o botão "Confirmar Senha" para o segundo pop-up
        AlertDialog alertDialog = builder.create();
        btnConfirmarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String novaSenha1 = editTextNovaSenha1.getText().toString();
                String novaSenha2 = editTextNovaSenha2.getText().toString();
                if (novaSenha1.equals(novaSenha2)) {
                    // Senhas coincidem, atualiza a senha do aluno
                    conexao.atualizarSenhaAluno(aluno.getAluno_cpf(), novaSenha1); // Supondo que aluno.getCpf() retorne o CPF do aluno
                    alertDialog.dismiss(); // Fecha o pop-up
                    Toast.makeText(TelaLoginActivity.this, "Senha atualizada com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    // Senhas não coincidem, exibe mensagem de erro
                    Toast.makeText(TelaLoginActivity.this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                    // Limpa os campos de senha
                    editTextNovaSenha1.setText("");
                    editTextNovaSenha2.setText("");
                }
            }
        });

        // Exibindo o segundo pop-up (Nova Senha)
        alertDialog.show();
    }

    public void exibirAjuda(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");

        // Criar a SpannableString para cada ícone e explicação
        SpannableString icon1 = createIconSpannable(R.drawable.ic_launcher_voltar2_foreground);
        SpannableString text1 = new SpannableString("Voltar para a tela inicial.\n");

        SpannableString icon2 = createIconSpannable(R.drawable.ic_launcher_login_foreground);
        SpannableString text2 = new SpannableString("Realiza o login, confira se seu CPF e senha" +
                " estão corretos e clique.\n");

        SpannableString icon3 = createIconSpannable(R.drawable.ic_launcher_cadastrar);
        SpannableString text3 = new SpannableString("Realiza o Cadastro, caso não tenha um login e senha!.\n");

        SpannableString icon4 = createIconSpannable(R.drawable.ic_launcher_rec_senha_foreground);
        SpannableString text4 = new SpannableString("Realiza a recuperação de sua senha, caso tenha esquecido!.\n");

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

    private void escondeTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
