package com.br.escolademusicaapplication.adaptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.br.escolademusicaapplication.OBJETOS.Aluno;
import com.br.escolademusicaapplication.R;
import java.util.ArrayList;
import java.util.List;

public class AlunoRecyclerViewAdapter extends RecyclerView.Adapter<AlunoRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Aluno> alunos;
    private List<Aluno> alunosFiltrados; // Lista de alunos após a filtragem
    private OnItemClickListener listener; // Interface de clique
    private String[] statusOptions; // Opções de status para o Spinner

    // Construtor para inicializar o adaptador com o contexto, a lista de alunos e o ouvinte de clique
    public AlunoRecyclerViewAdapter(Context context, List<Aluno> alunos, String[] statusOptions, OnItemClickListener listener) {
        this.context = context;
        this.alunos = alunos;
        this.statusOptions = statusOptions;
        this.listener = listener; // Configura o listener
        this.alunosFiltrados = new ArrayList<>(alunos); // Inicializa a lista de alunos filtrados com a lista completa de alunos
    }

    // Interface de clique para lidar com os eventos de clique
    public interface OnItemClickListener {
        void onItemClick(Aluno aluno);
    }

    // Classe ViewHolder para manter as visualizações de item de lista
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeAluno;
        TextView txtCpfAluno;
        TextView txtTelefoneAluno;
        TextView txtStatus;
        ImageView fotoAluno;

        ViewHolder(View itemView) {
            super(itemView);
            txtNomeAluno = itemView.findViewById(R.id.txtNomeAluno);
            txtCpfAluno = itemView.findViewById(R.id.txtCpfAluno);
            txtTelefoneAluno = itemView.findViewById(R.id.txtTelefoneAluno);
            txtStatus = itemView.findViewById(R.id.txtStatusAluno);
            fotoAluno = itemView.findViewById(R.id.iconeAluno);

            // Configura o listener de clique para o item de visualização
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Chama o método onItemClick da interface e passa o aluno clicado
                        listener.onItemClick(alunosFiltrados.get(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_aluno_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obtém o aluno na posição atual da lista de alunos filtrados
        Aluno aluno = alunosFiltrados.get(position);

        // Define os detalhes do aluno nos respectivos TextViews
        holder.txtNomeAluno.setText(aluno.getAluno_nome());
        holder.txtCpfAluno.setText("CPF: " + aluno.getAluno_cpf());
        holder.txtTelefoneAluno.setText("Telefone: " + aluno.getAluno_telefone());
        holder.txtStatus.setText("Status: " + aluno.getStatus());

        // Define a imagem do aluno no ImageView correspondente
        if (aluno.getAluno_foto() != null && !aluno.getAluno_foto().isEmpty()) {
            byte[] imagemBytes = Base64.decode(aluno.getAluno_foto(), Base64.DEFAULT);
            Bitmap imagemBitmap = BitmapFactory.decodeByteArray(imagemBytes, 0, imagemBytes.length);
            holder.fotoAluno.setImageBitmap(imagemBitmap);
        } else {
            holder.fotoAluno.setImageResource(R.drawable.user); // Imagem de placeholder padrão
        }
    }

    @Override
    public int getItemCount() {
        return alunosFiltrados.size(); // Retorna o tamanho da lista de alunos filtrados
    }

    // Método para filtrar os alunos por nome ou CPF
    public void filtrarAlunos(String texto) {
        alunosFiltrados.clear(); // Limpa a lista de alunos filtrados
        if (texto.isEmpty()) {
            alunosFiltrados.addAll(alunos); // Adiciona todos os alunos à lista filtrada se o texto de filtro estiver vazio
        } else {
            String textoLowerCase = texto.toLowerCase().trim(); // Converte o texto de filtro para minúsculas e remove espaços extras
            for (Aluno aluno : alunos) {
                // Verifica se o nome ou CPF do aluno contém o texto de filtro
                if (aluno.getAluno_nome().toLowerCase().contains(textoLowerCase) || aluno.getAluno_cpf().contains(textoLowerCase)) {
                    alunosFiltrados.add(aluno); // Adiciona o aluno à lista filtrada
                }
            }
        }
        notifyDataSetChanged(); // Notifica o RecyclerView sobre as alterações nos dados
    }
}
