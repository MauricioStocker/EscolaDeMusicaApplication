package com.br.escolademusicaapplication.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.escolademusicaapplication.OBJETOS.Aluno_Professor;
import com.br.escolademusicaapplication.R;

import java.util.List;

public class ProfessorAlunoAdapter extends RecyclerView.Adapter<ProfessorAlunoAdapter.ViewHolder> {

	private List<Aluno_Professor> listaAlunosProfessor;
	private OnItemClickListener onItemClickListener;
	private Context context;

	public ProfessorAlunoAdapter(Context context, List<Aluno_Professor> listaAlunosProfessor, OnItemClickListener onItemClickListener) {
		this.context = context;
		this.listaAlunosProfessor = listaAlunosProfessor;
		this.onItemClickListener = onItemClickListener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_professor_aluno, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Aluno_Professor alunoProfessor = listaAlunosProfessor.get(position);
		holder.txtNomeAluno.setText("NOME : " + alunoProfessor.getAluno().getAluno_nome());
		holder.txtNotaPrimeiroBi.setText("NOTA PRIMEIRO BIMESTRE : " + String.valueOf(alunoProfessor.getAluno_notaPrimeiroBim()));
		holder.txtNotaSegundoBi.setText("NOTA SEGUNDO BIMESTRE : " + String.valueOf(alunoProfessor.getAluno_notaSegundoBim()));
		holder.txtFaltas.setText("FALTAS : " + String.valueOf(alunoProfessor.getAluno_faltas()));

		// Configurar o clique no item
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Chamar o método onItemClick da interface ao clicar em um item
				if (onItemClickListener != null) {
					onItemClickListener.onItemClick(alunoProfessor);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return listaAlunosProfessor.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		TextView txtNomeAluno, txtDisciplina, txtNotaPrimeiroBi, txtNotaSegundoBi, txtFaltas;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			txtNomeAluno = itemView.findViewById(R.id.txtNomeAluno);
			txtDisciplina = itemView.findViewById(R.id.txtDisciplina);
			txtNotaPrimeiroBi = itemView.findViewById(R.id.txtNotaPrimeiroBi);
			txtNotaSegundoBi = itemView.findViewById(R.id.txtNotaSegundoBi);
			txtFaltas = itemView.findViewById(R.id.txtFaltas);
		}
	}

	// Interface para o ouvinte de clique
	public interface OnItemClickListener {
		void onItemClick(Aluno_Professor alunoProfessor);
	}
}
