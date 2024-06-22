package com.br.escolademusicaapplication.adaptor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.br.escolademusicaapplication.OBJETOS.Aluno_Professor;
import com.br.escolademusicaapplication.R;

import java.util.List;

public class AlunoProfessorAdapter extends ArrayAdapter<Aluno_Professor> {

	private List<Aluno_Professor> listaAlunoProfessor;
	private Context context;

	public AlunoProfessorAdapter(Context context, List<Aluno_Professor> listaAlunoProfessor) {
		super(context, R.layout.item_aluno_professor, listaAlunoProfessor);
		this.context = context;
		this.listaAlunoProfessor = listaAlunoProfessor;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.item_aluno_professor, null);

		// Obtenha os dados do Aluno_Professor na posição atual
		Aluno_Professor alunoProfessor = listaAlunoProfessor.get(position);

		// Inicialize as visualizações
		TextView txtNomeProfessor = view.findViewById(R.id.txtNomeProfessor);
		TextView txtDisciplina = view.findViewById(R.id.txtDisciplina);
		TextView txtNotaPrimeiroBim = view.findViewById(R.id.txtNotaPrimeiroBim);
		TextView txtNotaSegundoBim = view.findViewById(R.id.txtNotaSegundoBim);
		TextView txtFaltas = view.findViewById(R.id.txtFaltas);
		TextView txtTelefoneProfessor = view.findViewById(R.id.txtTelefoneProfessor);
		ImageView whatsappButton = view.findViewById(R.id.whatsapp_button);

		// Configure as visualizações com os dados do Aluno_Professor
		txtNomeProfessor.setText(alunoProfessor.getProfessor().getProfessor_nome());
		txtDisciplina.setText(alunoProfessor.getProfessor().getProfessor_disciplina());
		txtNotaPrimeiroBim.setText("Nota 1º Bim: " + alunoProfessor.getAluno_notaPrimeiroBim());
		txtNotaSegundoBim.setText("Nota 2º Bim: " + alunoProfessor.getAluno_notaSegundoBim());
		txtFaltas.setText("Faltas: " + alunoProfessor.getAluno_faltas());
		txtTelefoneProfessor.setText("Telefone: " + alunoProfessor.getProfessor().getProfessor_telefone());
		// Configurar o botão de WhatsApp
		whatsappButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneNumber = alunoProfessor.getProfessor().getProfessor_telefone();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://wa.me/" + phoneNumber));
				context.startActivity(intent);
			}
		});

		return view;
	}
}