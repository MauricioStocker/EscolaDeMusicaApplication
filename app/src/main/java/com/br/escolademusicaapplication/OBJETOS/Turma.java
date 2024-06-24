package com.br.escolademusicaapplication.OBJETOS;



public class Turma {

		int	turma_id;
		int	professor_id;
		String	alunos ;
		String	data_encerramento ;


	public int getTurma_id() {
		return turma_id;
	}

	public void setTurma_id(int turma_id) {
		this.turma_id = turma_id;
	}

	public int getProfessor_id() {
		return professor_id;
	}

	public void setProfessor_id(int professor_id) {
		this.professor_id = professor_id;
	}

	public String getAlunos() {
		return alunos;
	}

	public void setAlunos(String alunos) {
		this.alunos = alunos;
	}

	public String getData_encerramento() {
		return data_encerramento;
	}

	public void setData_encerramento(String data_encerramento) {
		this.data_encerramento = data_encerramento;
	}
}
