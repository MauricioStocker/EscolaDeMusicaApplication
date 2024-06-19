package com.br.escolademusicaapplication.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.br.escolademusicaapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomListAdapte extends ArrayAdapter<String> {

	private Context context;
	private String[] descricoes;
	private int[] imagens;

	public CustomListAdapte(Context context, String[] descricoes, int[] imagens) {
		super(context, R.layout.recyclerview_itens_professor, descricoes);
		this.context = context;
		this.descricoes = descricoes;
		this.imagens = imagens;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		ViewHolder viewHolder;

		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.list_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.imageView = view.findViewById(R.id.imageView);
			viewHolder.textView = view.findViewById(R.id.textView);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.imageView.setImageResource(imagens[position]);
		viewHolder.textView.setText(descricoes[position]);

		return view;
	}

	static class ViewHolder {
		CircleImageView imageView;
		TextView textView;
	}
}
