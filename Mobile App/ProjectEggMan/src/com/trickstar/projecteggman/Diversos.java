package com.trickstar.projecteggman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Diversos {
    public static void showAlert(String titulo, String menssagem, Activity quem)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(quem);
    	builder.setCancelable(false);
    	builder.setMessage(menssagem);
    	builder.setTitle(titulo);
    	builder.setPositiveButton("Goo goo g'joob", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	AlertDialog alerta = builder.create();
    	alerta.setCanceledOnTouchOutside(false);
    	alerta.show();
    }
}
