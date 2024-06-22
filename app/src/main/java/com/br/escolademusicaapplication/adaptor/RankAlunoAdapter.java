package com.br.escolademusicaapplication.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.escolademusicaapplication.OBJETOS.Aluno_Professor;
import com.br.escolademusicaapplication.R;

import java.util.List;

public class RankAlunoAdapter extends RecyclerView.Adapter<RankAlunoAdapter.ViewHolder> {
    private List<Aluno_Professor> alunoProfessorList;
    private Context context;
    private boolean filtroPrimeiroBimAtivo;
    private boolean filtroSegundoBimAtivo;

    public RankAlunoAdapter(Context context, List<Aluno_Professor> alunoProfessorList, boolean filtroPrimeiroBimAtivo, boolean filtroSegundoBimAtivo) {
        this.context = context;
        this.alunoProfessorList = alunoProfessorList;
        this.filtroPrimeiroBimAtivo = filtroPrimeiroBimAtivo;
        this.filtroSegundoBimAtivo = filtroSegundoBimAtivo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank_aluno, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Aluno_Professor alunoProfessor = alunoProfessorList.get(position);

        holder.textViewNomeAluno.setText("Nome do Aluno: " + alunoProfessor.getAluno().getAluno_nome());
        holder.textViewNomeProfessor.setText("Nome do Professor: " + alunoProfessor.getProfessor().getProfessor_nome());

        int notaTotal;

        // Verifica se o filtro de primeiro bimestre está ativo
        if (filtroPrimeiroBimAtivo) {
            notaTotal = alunoProfessor.getAluno_notaPrimeiroBim();
        }
        // Verifica se o filtro de segundo bimestre está ativo
        else if (filtroSegundoBimAtivo) {
            notaTotal = alunoProfessor.getAluno_notaSegundoBim();
        }
        // Caso contrário, calcula a média das notas dos dois bimestres
        else {
            int notaPrimeiroBim = alunoProfessor.getAluno_notaPrimeiroBim();
            int notaSegundoBim = alunoProfessor.getAluno_notaSegundoBim();
            notaTotal = (notaPrimeiroBim + notaSegundoBim) / 2;
        }

        holder.textViewNotaTotal.setText("Nota Total: " + notaTotal);

        // Define o ícone da posição do aluno
        if (position == 0) {
            holder.imageViewPositionIcon.setImageResource(R.mipmap.ic_launcher_medalhaouro);
        } else if (position == 1) {
            holder.imageViewPositionIcon.setImageResource(R.mipmap.ic_launcher_medalhaprata);
        } else if (position == 2) {
            holder.imageViewPositionIcon.setImageResource(R.mipmap.ic_launcher_medalhabronze);
        } else {
            // Oculta o ícone para os demais alunos
            holder.imageViewPositionIcon.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return alunoProfessorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNomeAluno;
        TextView textViewNomeProfessor;
        TextView textViewNotaTotal;
        ImageView imageViewPositionIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNomeAluno = itemView.findViewById(R.id.textViewNomeAluno);
            textViewNomeProfessor = itemView.findViewById(R.id.textViewNomeProfessor);
            textViewNotaTotal = itemView.findViewById(R.id.textViewNotaTotal);
            imageViewPositionIcon = itemView.findViewById(R.id.imageViewPositionIcon);
        }
    }
}
