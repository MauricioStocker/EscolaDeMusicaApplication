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

import com.br.escolademusicaapplication.OBJETOS.Galeria;
import com.br.escolademusicaapplication.R;

import java.util.List;

public class GaleriaAdapter extends RecyclerView.Adapter<GaleriaAdapter.GaleriaViewHolder> {

    private List<Galeria> listaGalerias;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Galeria galeria);
    }

    public GaleriaAdapter(Context context, List<Galeria> listaGalerias, OnItemClickListener listener) {
        this.context = context;
        this.listaGalerias = listaGalerias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GaleriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_galeria, parent, false);
        return new GaleriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GaleriaViewHolder holder, int position) {
        Galeria galeria = listaGalerias.get(position);

        if (galeria != null) {
            holder.textViewTitulo.setText(galeria.getGaleria_titulo());
            holder.textViewDescricao.setText(galeria.getGaleria_descricao());

            if (galeria.getGaleria_imagem() != null && !galeria.getGaleria_imagem().isEmpty()) {
                byte[] decodedString = Base64.decode(galeria.getGaleria_imagem(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imageViewGaleria.setImageBitmap(decodedByte);
            } else {
                holder.imageViewGaleria.setImageResource(R.drawable.user); // Defina a imagem padrão para a galeria aqui
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(galeria);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaGalerias.size();
    }

    public static class GaleriaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo;
        TextView textViewDescricao;
        ImageView imageViewGaleria;

        public GaleriaViewHolder(View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricao);
            imageViewGaleria = itemView.findViewById(R.id.imageViewGaleria);
        }
    }
}
