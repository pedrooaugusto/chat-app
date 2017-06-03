package com.trickstar.projecteggman;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.trickstar.projecteggman.ListenerEditText.KeyImeChange;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Cliente extends Activity {
    private BufferedReader entrada;
    private PrintWriter saida;
    private String nome;
    private String ip;
    private int porta;
    private boolean continua = true;
    Socket socket;
    private TextView nomeSala;
    private ListenerEditText caixaTexto;
    private Button send;
    private ListView messages;
    private String line;
    private int cor;
	ArrayAdapterMenssagens adapter;
	List<Menssagem> arrayMsg = new ArrayList<Menssagem>();
	List<String>pessoasNaSala = new ArrayList<String>();
	AlertDialog dialogEndereco, identificacao;
	LinearLayout contentNomeSala;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente);
		//Iniciando variaveis
		nomeSala = (TextView) findViewById(R.id.clienteNomeSala);
		contentNomeSala = (LinearLayout) findViewById(R.id.titleContent);
		send = (Button) findViewById(R.id.buttonSend);
		caixaTexto = (ListenerEditText) findViewById(R.id.editTextMessage);
		messages = (ListView) findViewById(R.id.listViewCaixaMessages);
        adapter = new ArrayAdapterMenssagens(this, arrayMsg);
        messages.setAdapter(adapter);
        
        //Colocando eventos
        contentNomeSala.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String nomes = "";
				for(String nome : pessoasNaSala)
				{
					nomes+=nome+"\n";
				}
				String text = "Nome da Sala: "+nomeSala.getText().toString()+"\n" +
							  "End. IP: "+ip+"\n" +
							  "Nº Porta: "+porta+"\n" +
							  "---PESSOAS NA SALA---\n" +
							  nomes;
				Diversos.showAlert("INFO.", text, Cliente.this);
			}
		});
        
        send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(caixaTexto.getText().toString().equals("sair."))
				{
					new Thread()
					{
						public void run()
						{
							finish();
						}
					}.start();
				}
				else
				{
					saida.println(formatar(caixaTexto.getText().toString(), false));
	                //addBubble("MR. Clever", caixaTexto.getText().toString(), cor);
					caixaTexto.setText("");
				}
			}
		});
	
		caixaTexto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				contentNomeSala.setVisibility(View.GONE);
			}
		});
		
		caixaTexto.setKeyImeChangeListener(new KeyImeChange() {
			@Override
			public void onKeyIme(int keyCode, KeyEvent event) 
			{
				if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) 
				{
					contentNomeSala.setVisibility(View.VISIBLE);
				}
			}
		});
		dialogEndereco().show();
	}
	
	@Override
	protected void onDestroy() {
		try
		{
			continua = false;
			entrada.close();
			saida.close();
			socket.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		super.onDestroy();
	}
	boolean inseriuNome = false;
	private AlertDialog getNome()
	{
		final AlertDialog.Builder form = new AlertDialog.Builder(this);
		inseriuNome = false;
		form.setCancelable(false);
	    LayoutInflater inflater = this.getLayoutInflater();
	    form.setView(inflater.inflate(R.layout.formulario_cliente_nome, null))
            .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   Dialog dialogBox = (Dialog) dialog;
                   EditText nomeET = (EditText) dialogBox.findViewById(R.id.DialogNome);
                   Spinner corET = (Spinner) dialogBox.findViewById(R.id.DialogCor);
                   String nomeS = nomeET.getText().toString();
                   String corS = corET.getSelectedItem().toString();
                   if(!nomeS.isEmpty())
                   {
                	   nome = nomeS;
                	   if(corS.equals("Red"))
                		   cor = Color.rgb(229,89,89);
                	   else if(corS.equals("Green"))
                		   cor = Color.rgb(153,255,102);
                	   else if(corS.equals("Blue"))
                		   cor = Color.rgb(128,153,228);
                	   else
                		   cor = Color.rgb(153,153,153);
                	   dialog.dismiss();
                   }
                   else
                   {
                	   nome = "The Walrus";
            		   cor = Color.rgb(153,153,153);
            		   dialog.dismiss();
                   }
                   /*synchronized(nome)
                   {
                	   nome.notify();
                   }*/
                   inseriuNome = true;
               }
           })
           .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   dialog.dismiss();
            	   inseriuNome = true;
            	   Cliente.this.finish();
               }
           });
    	AlertDialog alerta = form.create();
    	alerta.setCanceledOnTouchOutside(false);
    	return alerta;
	}
	
	private AlertDialog dialogEndereco()
	{
		final AlertDialog.Builder form = new AlertDialog.Builder(this);
		form.setCancelable(false);
	    LayoutInflater inflater = this.getLayoutInflater();
	    form.setView(inflater.inflate(R.layout.formulario_cliente, null))
           .setPositiveButton("Continuar", null)
           .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   dialog.dismiss();
            	   Cliente.this.finish();
               }
           });
    	final AlertDialog alerta = form.create();
    	alerta.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface arg0) 
			{
				Button b = alerta.getButton(AlertDialog.BUTTON_POSITIVE);
			    b.setOnClickListener(new View.OnClickListener() 
			    {
			    	@Override
			    	public void onClick(View view) 
			    	{
			    		EditText ipET = (EditText) alerta.findViewById(R.id.DialogIP);
			    		EditText portaET = (EditText) alerta.findViewById(R.id.DialogPorta);
			    		TextView crono = (TextView) alerta.findViewById(R.id.cronoForm);
			    		String ipS = ipET.getText().toString();
			    		String portaS = portaET.getText().toString();
			    		if(!ipS.isEmpty() && !portaS.isEmpty())
			    		{
			    			ip = ipS;
			    			porta = Integer.parseInt(portaS);
			    			new loop().start();
			    			progress(crono, alerta).start();
			    		}
			    	}
			    });
			}
		});
    	alerta.setCanceledOnTouchOutside(false);
    	return alerta;
	}
	
	boolean notConnect = false;
	int cont = 0;
	private Thread progress(final TextView v, final AlertDialog al)
	{
		return new Thread()
		{
			public void run()
			{
				while(!notConnect)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() 
						{
							v.setText(String.valueOf(cont++)+"/5s");
						}
					});
				}
				al.dismiss();
			}
		};
	}
	
	private class loop extends Thread
    {
            @Override
            public void run()
            {
                try 
                {
    				socket = new Socket(ip, porta);
    				notConnect = true;
    				entrada = new BufferedReader(new InputStreamReader( socket.getInputStream(), Charset.forName("UTF-8")));
    				saida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")), true);
                	while(continua)
                    {
                        line = entrada.readLine();
                        if(line.startsWith("enter_username:"))
                        {
                            runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									getNome().show();
								}
							});
                            while(!inseriuNome)
                            {
                            	sleep(500);
                            }
                    		/*synchronized(nome)
                    		{
                    			nome.wait();
                        		saida.println(formatar(nome, true));
                    		}*/
                        	saida.println(formatar(nome, true));
                        }
                        else if(line.startsWith("accept_username:"))
                        {
                        	
                        }
                        else if(line.startsWith("send_message:"))
                        {
                            List<String> l = parseList(line.substring(13), false);
                            if(l.get(0).equals(nome))
                            {
                                l.set(0, "Eu");
                                addBubble(l.get(0), l.get(1), cor);
                            }
                            else
                            {
                                addBubble(l.get(0), l.get(1), Color.parseColor("#D3D6DB"));
                            }
                        }
                        else if(line.startsWith("new_user:"))
                        {
                            List<String> nomesLogadosList = parseList(line.substring(9), true);
                            pessoasNaSala = nomesLogadosList;
                            String last = nomesLogadosList.get(nomesLogadosList.size() - 1);
                        	addBubble("~Clara", last+" entrou na sala.", Color.parseColor("#D3D6DB"));
                        }
                        else if(line.startsWith("nome_sala:"))
                        {
                        	runOnUiThread(new Runnable() {
								@Override
								public void run() {
		                        	nomeSala.setText(desformatar(line.substring(10)));
								}
							});
                        }
                        else if(line.startsWith("out_user:"))
                        {
                        	String o = desformatar(line.substring(9));
                        	pessoasNaSala.remove(o);
                        	addBubble("~Clara",o+" saiu da sala.", Color.parseColor("#D3D6DB"));
                        }
                    }
                }catch (Exception ex)
                {
                    notConnect = true;
                    addBubble("~Clara", "Look my hor...Quero dizer ocorreu um erro:\n"+ex.getMessage(), Color.RED);
                	ex.printStackTrace();
                }
            };
    }
	
	private List<String> parseList(String a, boolean space)
    {
        String regex = (space) ? "\\[|\\]| " : "\\[|\\]|";
        String stringList = a.replaceAll(regex, "").replaceFirst(" ", "");
        List<String> list = new ArrayList<String>(Arrays.asList(stringList.split(",")));
        return list;
    }
	
	private String formatar(String texto, boolean space)
    {
        texto = texto.replaceAll("\n", "@<newLine/>");
        texto = texto.replaceAll("\\[", "@<conchelte1/>").replaceAll("\\]", "@<conchelte2/>");
        texto = texto.replaceAll(",", "@<virgula/>");
        texto = space ? texto.replaceAll(" ", "@<space/>") : texto;//Quando espaços são inseridos no nome isso da pau
        return texto;
    }
	
    private String desformatar(String texto)
    {
        texto = texto.replaceAll("@<newLine/>", "\n");
        texto = texto.replaceAll("@<conchelte1/>", "\\[").replaceAll("@<conchelte2/>", "\\]");
        texto = texto.replaceAll("@<virgula/>", ",");
        texto = texto.replaceAll("@<space/>"," ");//Quando espaços são inseridos no nome isso da pau
        return texto;
    }

	private void addBubble(final String who, final String msg, final int fundoColor)
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Menssagem menssagem = new Menssagem(desformatar(who), desformatar(msg), fundoColor);
		    	adapter.add(menssagem);
			}
		});
    }
}
