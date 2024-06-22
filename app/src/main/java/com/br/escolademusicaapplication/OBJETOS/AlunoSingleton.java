package com.br.escolademusicaapplication.OBJETOS;

public class AlunoSingleton {
    private static AlunoSingleton instance;
    private Aluno aluno;

    private AlunoSingleton() {
        // Construtor privado para evitar instanciação externa
    }

    public static AlunoSingleton getInstance() {
        if (instance == null) {
            instance = new AlunoSingleton();
        }
        return instance;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
}
