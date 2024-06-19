package com.br.escolademusicaapplication.OBJETOS;

public class Galeria {
    private int galeria_id;
    private String galeria_titulo;
    private String galeria_descricao;
    private String galeria_imagem;

    public Galeria(int id, String titulo, String descricao, String imagem) {
        this.galeria_id = id;
        this.galeria_titulo = titulo;
        this.galeria_descricao = descricao;
        this.galeria_imagem = imagem;
    }

    public int getGaleria_id() {
        return galeria_id;
    }

    public void setGaleria_id(int galeria_id) {
        this.galeria_id = galeria_id;
    }

    public String getGaleria_titulo() {
        return galeria_titulo;
    }

    public void setGaleria_titulo(String galeria_titulo) {
        this.galeria_titulo = galeria_titulo;
    }

    public String getGaleria_descricao() {
        return galeria_descricao;
    }

    public void setGaleria_descricao(String galeria_descricao) {
        this.galeria_descricao = galeria_descricao;
    }

    public String getGaleria_imagem() {
        return galeria_imagem;
    }

    public void setGaleria_imagem(String galria_imagem) {
        this.galeria_imagem = galria_imagem;
    }
}
