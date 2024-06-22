package com.br.escolademusicaapplication.OBJETOS;

public class Curso {

    private int curso_id;
    private String curso_titulo;
    private String curso_descricao;
    private String curso_imagem;

    public Curso(int id, String titulo, String descricao, String imagem) {
        this.curso_id = id;
        this.curso_titulo = titulo;
        this.curso_descricao = descricao;
        this.curso_imagem = imagem;
    }


    public int getCurso_id() {
        return curso_id;
    }

    public void setCurso_id(int curso_id) {
        this.curso_id = curso_id;
    }

    public String getCurso_titulo() {
        return curso_titulo;
    }

    public void setCurso_titulo(String curso_titulo) {
        this.curso_titulo = curso_titulo;
    }

    public String getCurso_descricao() {
        return curso_descricao;
    }

    public void setCurso_descricao(String curso_descricao) {
        this.curso_descricao = curso_descricao;
    }

    public String getCurso_imagem() {
        return curso_imagem;
    }

    public void setCurso_imagem(String curso_imagem) {
        this.curso_imagem = curso_imagem;
    }
}
