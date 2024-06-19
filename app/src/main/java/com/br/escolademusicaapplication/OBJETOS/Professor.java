package com.br.escolademusicaapplication.OBJETOS;

import java.io.Serializable;

public class Professor implements Serializable {
    private static Professor instance;

    private int professor_id;
    private String professor_nome;
    private String professor_cpf;
    private String professor_senha;
    private String professor_disciplina;
    private String professor_telefone;
    private String professor_endeco;
    private String porfessor_sexo;

    private String professor_foto;

    public static void setInstance(Professor instance) {
        Professor.instance = instance;
    }

    public String getProfessor_status() {
        return professor_status;
    }

    public void setProfessor_status(String professor_status) {
        this.professor_status = professor_status;
    }

    private String professor_status;

    public Professor() {
        // Construtor privado para impedir a criação de instâncias fora da classe
    }

    public Professor(int idProfessor) {
    }

    public static Professor getInstance() {
        if (instance == null) {
            instance = new Professor();
        }
        return instance;
    }

    public String getProfessor_foto() {
        return professor_foto;
    }

    public void setProfessor_foto(String professor_foto) {
        this.professor_foto = professor_foto;
    }

    public String getPorfessor_sexo() {
        return porfessor_sexo;
    }

    public void setPorfessor_sexo(String porfessor_sexo) {
        this.porfessor_sexo = porfessor_sexo;
    }


    public String getProfessor_telefone() {
        return professor_telefone;
    }

    public void setProfessor_telefone(String professor_telefone) {
        this.professor_telefone = professor_telefone;
    }

    public String getProfessor_endeco() {
        return professor_endeco;
    }

    public void setProfessor_endeco(String professor_endeco) {
        this.professor_endeco = professor_endeco;
    }


    public int getProfessor_id() {
        return professor_id;
    }

    public void setProfessor_id(int professor_id) {
        this.professor_id = professor_id;
    }

    public String getProfessor_nome() {
        return professor_nome;
    }

    public void setProfessor_nome(String professor_nome) {
        this.professor_nome = professor_nome;
    }

    public String getProfessor_cpf() {
        return professor_cpf;
    }

    public void setProfessor_cpf(String professor_cpf) {
        this.professor_cpf = professor_cpf;
    }

    public String getProfessor_senha() {
        return professor_senha;
    }

    public void setProfessor_senha(String professor_senha) {
        this.professor_senha = professor_senha;
    }

    public String getProfessor_disciplina() {
        return professor_disciplina;
    }

    public void setProfessor_disciplina(String professor_disciplina) {
        this.professor_disciplina = professor_disciplina;
    }


}
