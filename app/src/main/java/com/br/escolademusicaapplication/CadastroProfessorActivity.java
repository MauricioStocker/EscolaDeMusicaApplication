package com.br.escolademusicaapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Professor;

public class CadastroProfessorActivity extends AppCompatActivity {

    EditText txtNomeProf;
    EditText txtCpfProf;
    EditText txtSenhaProf;
    EditText txtTelefoneProf;
    EditText txtEnderecoProf;
    Spinner spinnerDisciplina;
    Button btnCadastrarProfessor, btnVoltarCadas;
    Switch sexoProfessor;
    private Conexao conexao;

    private String cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_professor);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        txtNomeProf = findViewById(R.id.txtNomeProf);
        txtCpfProf = findViewById(R.id.txtCpfProf);
        txtSenhaProf = findViewById(R.id.txtPasswordProf);
        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
        txtTelefoneProf = findViewById(R.id.txtTelefoneProf);
        txtEnderecoProf = findViewById(R.id.txtEnderecoProf);
        btnVoltarCadas = findViewById(R.id.btnVoltarAdmCadas);
        conexao = Conexao.getInstance(this);
        mascaraCpf();
        mascaraTelefone();

        // Configurar o Spinner com as disciplinas
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.disciplinas_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplina.setAdapter(adapter);

        btnVoltarCadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaTelaAdm();
                finish();
            }
        });

        btnCadastrarProfessor = findViewById(R.id.btnCadastrarProfessor);
        btnCadastrarProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = txtNomeProf.getText().toString().trim();
                String senha = txtSenhaProf.getText().toString().trim();
                cpf = txtCpfProf.getText().toString().trim();
                String disciplina = spinnerDisciplina.getSelectedItem().toString();
                String telefone = txtTelefoneProf.getText().toString().trim();
                String endereco = txtEnderecoProf.getText().toString().trim();

                // Remover a máscara do CPF
                String cpfSemMascara = cpf.replaceAll("[^\\d]", "");

                if (nome.isEmpty() || senha.isEmpty() || cpf.isEmpty() || disciplina.isEmpty() || telefone.isEmpty() || endereco.isEmpty()) {
                    Toast.makeText(CadastroProfessorActivity.this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                } else if (cpfSemMascara.length() != 11) {
                    Toast.makeText(CadastroProfessorActivity.this, "CPF inválido. Deve conter 11 números.", Toast.LENGTH_SHORT).show();
                } else if (!cpfEhValido(cpfSemMascara)) {
                    Toast.makeText(CadastroProfessorActivity.this, "CPF inválido. Por favor, insira um CPF válido.", Toast.LENGTH_SHORT).show();
                } else {
                    insereProfessor(nome, senha, cpfSemMascara, disciplina, telefone);
                }
            }
        });
    }

    private void insereProfessor(String nome, String senha, String cpf, String disciplina, String telefone) {
        // Salvar o professor no banco de dados usando o CPF sem máscara
        Professor professor = new Professor();
        professor.setProfessor_nome(nome);
        professor.setProfessor_senha(senha);
        professor.setProfessor_cpf(cpf);
        professor.setProfessor_disciplina(disciplina);
        professor.setProfessor_telefone(telefone);

        boolean resultado = conexao.insereProfessor(professor);
        if (resultado) {
            Toast.makeText(this, "Professor cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CadastroProfessorActivity.class);
            startActivity(intent);
            txtNomeProf.setText("");
            txtCpfProf.setText("");
            txtSenhaProf.setText("");
            txtTelefoneProf.setText("");
            txtEnderecoProf.setText("");
            spinnerDisciplina.setSelection(0);
            txtNomeProf.requestFocus();
            finish();
        } else {
            Toast.makeText(this, "Erro ao cadastrar Professor!", Toast.LENGTH_SHORT).show();
        }
    }

    public void chamaTelaLista() {
        Intent intent = new Intent(this, ListaProfessorActivity.class);
        startActivity(intent);
    }

    public void chamaTelaAdm() {
        Intent intent = new Intent(this, PainelAdmActivity.class);
        startActivity(intent);
    }

    private void mascaraCpf() {
        txtCpfProf.addTextChangedListener(new TextWatcher() {
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

                txtCpfProf.removeTextChangedListener(this); // Remove o listener temporariamente para evitar loop infinito
                txtCpfProf.setText(formattedCpf.toString());
                txtCpfProf.setSelection(txtCpfProf.getText().length()); // Define a seleção no final do texto
                txtCpfProf.addTextChangedListener(this); // Restaura o listener após a formatação
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Não é necessário implementar nesta parte
            }
        });
    }

    private void mascaraTelefone() {
        txtTelefoneProf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Não é necessário implementar nesta parte
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String telefone = s.toString().replaceAll("[^\\d]", ""); // Remover caracteres não numéricos
                StringBuilder formattedTelefone = new StringBuilder();

                int length = telefone.length();
                if (length >= 2) {
                    formattedTelefone.append("(").append(telefone.substring(0, 2)).append(") ");
                    if (length > 6) {
                        formattedTelefone.append(telefone.substring(2, 7)).append("-");
                        if (length > 10) {
                            formattedTelefone.append(telefone.substring(7, 11));
                        } else {
                            formattedTelefone.append(telefone.substring(7));
                        }
                    } else {
                        formattedTelefone.append(telefone.substring(2));
                    }
                } else {
                    formattedTelefone.append(telefone);
                }

                txtTelefoneProf.removeTextChangedListener(this); // Remove o listener temporariamente para evitar loop infinito
                txtTelefoneProf.setText(formattedTelefone.toString());
                txtTelefoneProf.setSelection(txtTelefoneProf.getText().length()); // Define a seleção no final do texto
                txtTelefoneProf.addTextChangedListener(this); // Restaura o listener após a formatação
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Não é necessário implementar nesta parte
            }
        });
    }

    private boolean cpfEhValido(String cpf) {
        // Remover todos os caracteres não numéricos do CPF
        cpf = cpf.replaceAll("[^\\d]", "");

        // Verifica se o CPF tem 11 dígitos após a remoção dos caracteres especiais
        if (cpf.length() != 11) return false;

        // Convertendo a string do CPF para um array de inteiros
        int[] digitos = new int[11];
        for (int i = 0; i < 11; i++) {
            digitos[i] = Integer.parseInt(cpf.substring(i, i + 1));
        }

        // Verificar se todos os dígitos são iguais
        boolean todosIguais = true;
        for (int i = 1; i < 11; i++) {
            if (digitos[i] != digitos[0]) {
                todosIguais = false;
                break;
            }
        }
        if (todosIguais) return false;

        // Calcular o primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += digitos[i] * (10 - i);
        }
        int primeiroVerificador = soma % 11;
        if (primeiroVerificador < 2) primeiroVerificador = 0;
        else primeiroVerificador = 11 - primeiroVerificador;

        // Verificar o primeiro dígito verificador
        if (primeiroVerificador != digitos[9]) return false;

        // Calcular o segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += digitos[i] * (11 - i);
        }
        int segundoVerificador = soma % 11;
        if (segundoVerificador < 2) segundoVerificador = 0;
        else segundoVerificador = 11 - segundoVerificador;

        // Verificar o segundo dígito verificador
        return segundoVerificador == digitos[10];
    }
}