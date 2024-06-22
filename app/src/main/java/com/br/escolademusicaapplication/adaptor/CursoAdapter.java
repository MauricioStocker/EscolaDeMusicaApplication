package com.br.escolademusicaapplication.adaptor;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.br.escolademusicaapplication.OBJETOS.Curso;

import java.util.List;

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.CursoViewHolder> {

    private List<Curso> cursos;
    private Context context;
    private OnItemClickListener listener;



    public interface OnItemClickListener {
        void onItemClick(Curso curso);
    }

    public CursoAdapter(List<Curso> cursos, Context context, OnItemClickListener listener) {
        this.cursos = cursos;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_curso, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = cursos.get(position);

        if (curso != null) {
            holder.textViewTitulo.setText(curso.getCurso_titulo());
            holder.textViewDescricao.setText(curso.getCurso_descricao());

            if (curso.getCurso_imagem() != null && !curso.getCurso_imagem().isEmpty()) {
                byte[] decodedString = Base64.decode(curso.getCurso_imagem(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imageViewCurso.setImageBitmap(decodedByte);
            } else {
                holder.imageViewCurso.setImageResource(R.drawable.user);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(curso);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursos.size();
    }

    public static class CursoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo;
        TextView textViewDescricao;
        ImageView imageViewCurso;

        public CursoViewHolder(View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricao);
            imageViewCurso = itemView.findViewById(R.id.imageViewGaleria);
        }
    }
}
