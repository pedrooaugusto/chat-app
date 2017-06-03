package com.trickstar.projecteggman;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArrayAdapterMenssagens extends ArrayAdapter<Menssagem> {
	Context ctx;
	List<Menssagem> msgs = null;
	Typeface sUI;
	Typeface sLG;
	public ArrayAdapterMenssagens(Context context, List<Menssagem> msgs) 
	{
		super(context, 0, msgs);
		this.ctx = context;
		this.msgs = msgs;
		sUI = Typeface.createFromAsset(context.getAssets(), "fonts/segoeui.ttf");
		sLG = Typeface.createFromAsset(context.getAssets(), "fonts/segoeuil.ttf");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Menssagem msg = msgs.get(position);
        if(convertView == null)
        {
        	convertView = LayoutInflater.from(ctx).inflate(R.layout.bubble_message, null);
        }
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.container);
        container.setBackgroundColor(msg.getCor());
        
        TextView nome = (TextView) convertView.findViewById(R.id.nome);
        nome.setText(msg.getNome());
        nome.setTypeface(sUI, Typeface.BOLD);
        nome.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        
        TextView textViewMensagem = (TextView) convertView.findViewById(R.id.menssagem);
        textViewMensagem.setText(msg.getMenssagem());
        textViewMensagem.setTypeface(sLG);
        textViewMensagem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        
        return convertView;
	}
}
