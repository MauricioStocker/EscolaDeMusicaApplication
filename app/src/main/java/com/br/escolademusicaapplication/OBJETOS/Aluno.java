package com.br.escolademusicaapplication.OBJETOS;

import java.io.Serializable;

public class Aluno implements Serializable {
    private static Aluno instance;


    private int aluno_id;
    private String aluno_nome;
    private String aluno_cpf;
    private String aluno_senha;
    private String aluno_endereco;
    private String aluno_matricula;

    public String getAluno_matricula() {
        return aluno_matricula;
    }

    public void setAluno_matricula(String aluno_matricula) {
        this.aluno_matricula = aluno_matricula;
    }

    public static void setInstance(Aluno instance) {
        Aluno.instance = instance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String aluno_dataNascimento;
    private String aluno_telefone;
    private String aluno_sexo;
    private String aluno_foto;

    private String status;

    public Aluno() {
        // Construtor privado para evitar instanciação externa
    }

    public Aluno(int idAluno) {
    }

    public static Aluno getInstance() {
        if (instance == null) {
            instance = new Aluno();
        }
        return instance;
    }

    // Getters e setters omitidos para brevidade


    public Aluno(int id, String nome, String s, String s1, byte[] fotoAluno) {
    }


    public String getAluno_foto() {
        return aluno_foto;
    }

    public void setAluno_foto(String aluno_foto) {
        this.aluno_foto = aluno_foto;
    }

    public String getAluno_sexo() {
        return aluno_sexo;
    }

    public void setAluno_sexo(String aluno_sexo) {
        this.aluno_sexo = aluno_sexo;
    }


    public int getAluno_id() {
        return aluno_id;
    }

    public void setAluno_id(int aluno_id) {
        this.aluno_id = aluno_id;
    }

    public String getAluno_nome() {
        return aluno_nome;
    }

    public void setAluno_nome(String aluno_nome) {
        this.aluno_nome = aluno_nome;
    }

    public String getAluno_cpf() {
        return aluno_cpf;
    }

    public void setAluno_cpf(String aluno_cpf) {
        this.aluno_cpf = aluno_cpf;
    }

    public String getAluno_senha() {
        return aluno_senha;
    }

    public void setAluno_senha(String aluno_senha) {
        this.aluno_senha = aluno_senha;
    }

    public String getAluno_endereco() {
        return aluno_endereco;
    }

    public void setAluno_endereco(String aluno_endereco) {
        this.aluno_endereco = aluno_endereco;
    }

    public String getAluno_dataNascimento() {
        return aluno_dataNascimento;
    }

    public void setAluno_dataNascimento(String aluno_dataNascimento) {
        this.aluno_dataNascimento = aluno_dataNascimento;
    }

    public String getAluno_telefone() {
        return aluno_telefone;
    }

    public void setAluno_telefone(String aluno_telefone) {
        this.aluno_telefone = aluno_telefone;
    }


}
