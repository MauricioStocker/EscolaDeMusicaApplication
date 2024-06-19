package com.br.escolademusicaapplication.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.br.escolademusicaapplication.DadosProfessorActivity;
import com.br.escolademusicaapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<String> nome = new ArrayList<String>();
    String[] disciplina;
    View viewOnCreate;

    String[] foto;
    List<Integer> ids = new ArrayList<>();

    ViewHolder viewHolderLocal;

    public RecyclerViewAdapter(Context contextRecebido, List<Integer> idsRecebidos, String[] nomeRecebido, String[] disciplinaRecebida, String[] fotoRecebido) {
        context = contextRecebido;
        ids.addAll(idsRecebidos);
        nome.addAll(Arrays.asList(nomeRecebido));
        disciplina = disciplinaRecebida;
        foto = fotoRecebido;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtNomeprof;
        public TextView txtDisciplina;

        public CircleImageView imagemProfessor;
        public ImageView icone;

        public TextView txtId;
        public ViewHolder(View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtNomeprof = itemView.findViewById(R.id.txtNomeProfe);
            txtDisciplina = itemView.findViewById(R.id.txtDisciplinaProfe);
            icone = itemView.findViewById(R.id.iconeProfe);
            imagemProfessor = itemView.findViewById(R.id.fotoUsuario);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Implemente o que você deseja fazer quando um item da lista é clicado
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }


    @NonNull

    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        viewOnCreate = LayoutInflater.from(context).inflate(R.layout.recyclerview_itens_professor, parent, false);
        viewHolderLocal = new ViewHolder(viewOnCreate);
        return viewHolderLocal;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final int currentPosition = position;
        holder.txtId.setText(String.valueOf(ids.get(currentPosition)));
        holder.txtNomeprof.setText(nome.get(currentPosition));
        holder.txtDisciplina.setText(disciplina[currentPosition]);
        if (foto[currentPosition].equals("") || foto[currentPosition].equals("null")) {
            holder.icone.setImageResource(R.drawable.user);
        } else {
            byte[] imagemEmBytes;
            imagemEmBytes = Base64.decode(foto[currentPosition], Base64.DEFAULT);
            Bitmap imagemDecodificada = BitmapFactory.decodeByteArray(imagemEmBytes, 0, imagemEmBytes.length);
            holder.icone.setImageBitmap(imagemDecodificada);
        }
        viewOnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DadosProfessorActivity.class);
                intent.putExtra("id", ids.get(position));
                intent.putExtra("nome", nome.get(position));
                intent.putExtra("disciplina", disciplina[position]);
                intent.putExtra("foto", foto[position]);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });
        holder.icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
			/*	String nomeProfessor = nome.get(currentPosition);
				Conexao conexao = new Conexao(context);
				conexao.apagaProfessor(nomeProfessor);
				nome.remove(currentPosition);
				notifyItemRemoved(currentPosition);
				notifyItemRangeChanged(currentPosition, nome.size());*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return nome.size();
    }
}