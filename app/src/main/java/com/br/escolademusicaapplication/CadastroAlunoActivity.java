
package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Aluno;
import com.br.escolademusicaapplication.TelaLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class CadastroAlunoActivity extends AppCompatActivity {
    private String cpf;
    private static int ultimaMatricula = 1;

    EditText txtnome, txtTelefone, txtDataNasc, txtEndereco, txtCep;
    EditText txtCpf;
    EditText txtPasswordCadastro;
    Button btnCadastrar, btnVoltarCadastroAluno, btnBuscarCep;
    private Conexao conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_aluno);
        conexao = Conexao.getInstance(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        txtTelefone = findViewById(R.id.txtTelefone);
        txtnome = findViewById(R.id.txtNome);
        txtCpf = findViewById(R.id.txtCpf);
        txtDataNasc = findViewById(R.id.txtDataNasc);
        txtPasswordCadastro = findViewById(R.id.txtPasswordCadastro);
        txtEndereco = findViewById(R.id.txtEndereco);
        txtCep = findViewById(R.id.txtCep);
        btnBuscarCep = findViewById(R.id.btnBuscarCep);

        mascaraCpf();
        mascaraTelefone();
        setupDatePicker();

        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnVoltarCadastroAluno = findViewById(R.id.btnVoltarCadastroAluno);

        btnVoltarCadastroAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltarTelaLogin();
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camposPreenchidos()) {
                    if (cpfEhValido(txtCpf.getText().toString())) {
                        mostrarDialogoConfirmacao();
                    } else {
                        Toast.makeText(CadastroAlunoActivity.this, "CPF inválido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroAlunoActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBuscarCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cep = txtCep.getText().toString().trim();
                if (!cep.isEmpty()) {
                    new BuscarEnderecoTask().execute(cep);
                } else {
                    Toast.makeText(CadastroAlunoActivity.this, "Por favor, insira um CEP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean camposPreenchidos() {
        String nome = txtnome.getText().toString().trim();
        String cpf = txtCpf.getText().toString().trim();
        String telefone = txtTelefone.getText().toString().trim();
        String dataNasc = txtDataNasc.getText().toString().trim();
        String endereco = txtEndereco.getText().toString().trim();
        String senha = txtPasswordCadastro.getText().toString().trim();

        return !nome.isEmpty() && !cpf.isEmpty() && !telefone.isEmpty() && !dataNasc.isEmpty() && !endereco.isEmpty() && !senha.isEmpty();
    }

    private void mascaraCpf() {
        txtCpf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cpf = s.toString().replaceAll("[^\\d]", "");
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

                txtCpf.removeTextChangedListener(this);
                txtCpf.setText(formattedCpf.toString());
                txtCpf.setSelection(txtCpf.getText().length());
                txtCpf.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private boolean cpfEhValido(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");

        if (cpf.length() != 11) {
            return false;
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char) (r + 48);
            }

            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            if ((dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void insereAluno(String nome, String cpf, String senha, String telefone, String data) {
        cpf = cpf.replaceAll("[^\\d]", "");

        Aluno aluno = new Aluno();
        aluno.setAluno_nome(nome);
        aluno.setAluno_cpf(cpf);
        aluno.setAluno_senha(senha);
        aluno.setAluno_telefone(telefone);
        aluno.setAluno_dataNascimento(data);
        aluno.setStatus("inativo");

        // Gerar matrícula automática
        String matricula = gerarMatricula();
        aluno.setAluno_matricula(matricula);

        // Insere o aluno no banco de dados
        int idAluno = conexao.insereAluno(aluno);

        if (idAluno > 0) {
            Toast.makeText(this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            mostrarDialogoSucesso();
        } else {
            Toast.makeText(this, "Erro ao cadastrar aluno!", Toast.LENGTH_SHORT).show();
        }
    }


    private void voltarTelaLogin() {
        Intent intent = new Intent(this, TelaLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void mostrarDialogoConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Termos da LGPD");

        ScrollView scrollView = new ScrollView(this);

        EditText editText = new EditText(this);
        editText.setText("Ao se cadastrar, você concorda com os termos da LGPD. Estes termos incluem a proteção de seus dados pessoais e o uso responsável de informações fornecidas durante o cadastro. Se você não concorda com estes termos, por favor, clique em 'Recusar' para cancelar o cadastro.");

        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);

        scrollView.addView(editText);

        builder.setView(scrollView);

        builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insereAluno(txtnome.getText().toString(), txtCpf.getText().toString(), txtPasswordCadastro.getText().toString(), txtTelefone.getText().toString(),txtDataNasc.getText().toString());
            }
        });

        builder.setNegativeButton("Recusar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarDialogoSucesso() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cadastro Realizado");
        builder.setMessage("Cadastro realizado com sucesso! Sua conta está pendente de ativação pelo" +
                " administrador. Aguarde a validação. O administrador entrará em contato para " +
                "avisar quando sua conta estiver ativa e você poderá fazer login.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                voltarTelaLogin();
            }
        });

        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mascaraTelefone() {
        txtTelefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String telefone = s.toString();
                if (count > 0) {
                    if (telefone.length() == 2) {
                        txtTelefone.setText("(" + telefone + ") ");
                        txtTelefone.setSelection(txtTelefone.getText().length());
                    } else if (telefone.length() == 10) {
                        txtTelefone.setText(telefone + "-");
                        txtTelefone.setSelection(txtTelefone.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setupDatePicker() {
        txtDataNasc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int ano = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CadastroAlunoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                txtDataNasc.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, ano, mes, dia);
                datePickerDialog.show();
            }
        });
    }

    private class BuscarEnderecoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String cep = params[0];
            String urlString = "https://viacep.com.br/ws/" + cep + "/json/";

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder jsonResult = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        jsonResult.append(line);
                    }

                    reader.close();
                    return jsonResult.toString();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String endereco = jsonObject.getString("logradouro") + ", " +
                            jsonObject.getString("bairro") + ", " +
                            jsonObject.getString("localidade") + " - " +
                            jsonObject.getString("uf");

                    txtEndereco.setText(endereco);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CadastroAlunoActivity.this, "Erro ao buscar o endereço", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CadastroAlunoActivity.this, "CEP não encontrado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private int getProximaMatriculaDoBanco() {
        // Simula a consulta ao banco de dados para obter a próxima matrícula
        int proximaMatricula = ultimaMatricula;

        // Incrementa o contador da matrícula para o próximo aluno
        ultimaMatricula++;

        return proximaMatricula;
    }

    private String gerarMatricula() {
        // Obtém o ano atual
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);

        // Formata a matrícula com o ano atual e o número sequencial
        String matriculaFormatada = anoAtual + String.format("%04d", ultimaMatricula++);

        return matriculaFormatada;
    }


}
