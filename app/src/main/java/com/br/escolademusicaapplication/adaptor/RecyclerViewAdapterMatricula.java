package com.br.escolademusicaapplication.adaptor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.escolademusicaapplication.R;
import com.br.escolademusicaapplication.OBJETOS.Professor;
import com.br.escolademusicaapplication.TelaMatriculaActivity;

import java.util.List;

public class RecyclerViewAdapterMatricula extends RecyclerView.Adapter<RecyclerViewAdapterMatricula.ViewHolder> {

    private Context context;
    private List<Professor> professores;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Professor professor);
    }

    public RecyclerViewAdapterMatricula(Context context, List<Professor> professores) {
        this.context = context;
        this.professores = professores;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeprof;
        TextView txtDisciplina, txtid;
        ImageView iconProf;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNomeprof = itemView.findViewById(R.id.txtNomeProfe);
            txtDisciplina = itemView.findViewById(R.id.txtDisciplinaProfe);
            iconProf = itemView.findViewById(R.id.iconeProfe);
            txtid = itemView.findViewById(R.id.txtId);

            // Remove o clique do item da lista daqui
        }
    }

    @NonNull
    @Override
    public RecyclerViewAdapterMatricula.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_itens_professor, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterMatricula.ViewHolder holder, int position) {
        Professor professor = professores.get(position);
        holder.txtNomeprof.setText(professor.getProfessor_nome());
        holder.txtDisciplina.setText(professor.getProfessor_disciplina());
        holder.txtid.setText(String.valueOf(professor.getProfessor_id()));

        // Exibir a imagem do professor
        String fotoBase64 = professor.getProfessor_foto();
        if (fotoBase64 != null && !fotoBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(fotoBase64, Base64.DEFAULT);
            holder.iconProf.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        } else {
            holder.iconProf.setImageResource(R.drawable.user);
        }

        // Defina o clique do item da lista para atualizar a tela de matrícula com os detalhes do professor
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(professores.get(position));

                        // Atualiza os detalhes do professor na tela de matrícula
                        if (context instanceof TelaMatriculaActivity) {
                            TelaMatriculaActivity telaMatriculaActivity = (TelaMatriculaActivity) context;
                            telaMatriculaActivity.updateProfessorDetails(professor);
                        }
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return professores.size();
    }
}
