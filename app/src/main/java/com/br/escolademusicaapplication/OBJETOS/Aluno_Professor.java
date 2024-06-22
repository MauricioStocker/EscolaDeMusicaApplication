package com.br.escolademusicaapplication.OBJETOS;

public class Aluno_Professor {

    private Aluno aluno;
    private Professor professor;

    private int aluno_notaPrimeiroBim;
    private int aluno_notaSegundoBim;

    private int aluno_faltas;

    public int getAluno_notaPrimeiroBim() {
        return aluno_notaPrimeiroBim;
    }

    public void setAluno_notaPrimeiroBim(int aluno_notaPrimeiroBim) {
        this.aluno_notaPrimeiroBim = aluno_notaPrimeiroBim;
    }

    public int getAluno_notaSegundoBim() {
        return aluno_notaSegundoBim;
    }

    public void setAluno_notaSegundoBim(int aluno_notaSegundoBim) {
        this.aluno_notaSegundoBim = aluno_notaSegundoBim;
    }

    public int getAluno_faltas() {
        return aluno_faltas;
    }

    public void setAluno_faltas(int aluno_faltas) {
        this.aluno_faltas = aluno_faltas;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}
