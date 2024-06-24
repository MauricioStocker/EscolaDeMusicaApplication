package com.br.escolademusicaapplication.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.escolademusicaapplication.DAO.Conexao;
import com.br.escolademusicaapplication.OBJETOS.Turma;
import com.br.escolademusicaapplication.R;

import java.util.List;

public class TurmaAdapter extends RecyclerView.Adapter<TurmaAdapter.TurmaViewHolder> {

	private Context context;
	private List<Turma> turmas;
	private Conexao conexao;

	public TurmaAdapter(Context context, List<Turma> turmas) {
		this.context = context;
		this.turmas = turmas;
		this.conexao = Conexao.getInstance(context); // Inicializa a conexão
	}

	@NonNull
	@Override
	public TurmaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_turma_finalizada, parent, false);
		return new TurmaViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull TurmaViewHolder holder, int position) {
		Turma turma = turmas.get(position);
		holder.textViewTurmaId.setText(String.valueOf(turma.getTurma_id()));

		// Busca e exibe o nome do professor
		String nomeProfessor = conexao.buscarNomeProfessor(turma.getProfessor_id());
		holder.textViewProfessorNome.setText(nomeProfessor);

		holder.textViewAlunos.setText(turma.getAlunos());
		holder.textViewDataEncerramento.setText(turma.getData_encerramento());
	}

	@Override
	public int getItemCount() {
		return turmas.size();
	}

	public static class TurmaViewHolder extends RecyclerView.ViewHolder {

		TextView textViewTurmaId;
		TextView textViewProfessorNome;
		TextView textViewAlunos;
		TextView textViewDataEncerramento;

		public TurmaViewHolder(@NonNull View itemView) {
			super(itemView);
			textViewTurmaId = itemView.findViewById(R.id.textViewTurmaId);
			textViewProfessorNome = itemView.findViewById(R.id.textViewProfessorNome);
			textViewAlunos = itemView.findViewById(R.id.textViewAlunos);
			textViewDataEncerramento = itemView.findViewById(R.id.textViewDataEncerramento);
		}
	}
}
