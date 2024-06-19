package com.br.escolademusicaapplication.OBJETOS;

public class ProfessorSingleton {
    private static ProfessorSingleton instance;
    private Professor professor;

    private ProfessorSingleton() {
        // Construtor privado para evitar instanciação externa
    }

    public static ProfessorSingleton getInstance() {
        if (instance == null) {
            instance = new ProfessorSingleton();
        }
        return instance;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}
