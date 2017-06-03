package com.trickstar.projecteggman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
//import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class Service_Servidor extends Service {
	
    private List<String> nomes = new ArrayList<String>();
    private List<PrintWriter> saidas = new ArrayList<PrintWriter>();
    String sala = "";
    Handler handler;
    boolean esperandoCon = true;
    private List<solicitacaoDeConexao> th = new ArrayList<solicitacaoDeConexao>();
    ServerSocket servidor;
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		handler = new Handler(){
	    public void handleMessage(Message c) {
	            // TODO Auto-generated method stub
	            super.handleMessage(c);
	            Toast.makeText(Service_Servidor.this, "Novo cliente!", Toast.LENGTH_SHORT).show();
	        }

	    };
		mostrarNotificacao(intent);
		try
		{
			String porta = intent.getStringExtra("porta");
			String nome = intent.getStringExtra("nome_sala");
			servidor = new ServerSocket(Integer.parseInt(porta));
            sala = nome;
            esperarCon(servidor).start();
		}catch(Exception e)
		{
			Log.i("no", e.getMessage());
		}
		return START_STICKY;
	}
	
	public void onDestroy() 
	{
		shutDown();
		Toast.makeText(getApplicationContext(), "O servidor "+sala+" foi parado", Toast.LENGTH_SHORT).show();
		try {
			servidor.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		stopForeground(true);
		super.onDestroy();
	}
    private void shutDown()
    {
        for(solicitacaoDeConexao t : th)
        {
            t.kill();
        }
        esperandoCon = false;
    }
	public Thread esperarCon(final ServerSocket servidor)
    {
		Toast.makeText(getApplicationContext(), "Servidor iniciado...", Toast.LENGTH_SHORT).show();
		Thread t  = new Thread()
        {
            public void run()
            {
            	while(esperandoCon) 
                {
                    try
                    {
                    	solicitacaoDeConexao sd = new solicitacaoDeConexao(servidor.accept());
                    	handler.sendEmptyMessage(0);
                        th.add(sd);
                        sd.start();
                    }
                    catch (Exception ex)
                    {
                    	Log.i("no", ex.getMessage());
                    }
                }
            }
        };
        return t;
    }
	
	private class solicitacaoDeConexao extends Thread
    {
        private String nome;
        private Socket socket;
        private BufferedReader entrada;
        private PrintWriter saida;
        private boolean alive = true;
        private solicitacaoDeConexao(Socket s)
        {
            socket = s;
        }
        @Override
        public void run()
        {
            try
            {
				entrada = new BufferedReader(new InputStreamReader( socket.getInputStream(), Charset.forName("UTF-8")));
				saida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")), true);
                while(alive)
                {
                    saida.println("enter_username:");
                    nome = entrada.readLine();
                    if(nome == null)
                    {
                        return;
                    }
                    if(!nomes.contains(nome))
                    {
                        nomes.add(nome);
                        break;
                    }
                }
                saida.println("accept_username:");
                saidas.add(saida);
                for(PrintWriter writer : saidas) 
                {
                    writer.println("new_user:"+nomes);
                }
                saida.println("nome_sala:"+sala);
                while(alive) 
                {
                    String menssagem = entrada.readLine();
                    if(menssagem == null) 
                    {
                        return;
                    }
                    if(menssagem.equals("@<out_user>"))
                    {
                        break;
                    }
                    List<String> l = new ArrayList<String>();
                    l.add(nome);
                    l.add(menssagem);
                    for(PrintWriter writer : saidas) 
                    {
                        writer.println("send_message:"+l);
                    }
                }
            }catch (IOException e) 
            {
                e.printStackTrace();
            }finally 
            {
                if(nome != null) 
                {
                    nomes.remove(nome);;
                }
                if(saida != null) 
                {
                    saidas.remove(saida);
                }
                try 
                {
                    socket.close();
                }catch (IOException e) 
                {
                   e.printStackTrace();
                }
                for(PrintWriter writer : saidas) 
                {
                    writer.println("out_user:"+nome);
                }
            }
        }
        public void kill()
        {
            List<String> l = new ArrayList<String>();
            l.add("~Clara");
            l.add("A casa caiu.");
            saida.println("send_message:"+l);
            alive = false;
            try
            {
                socket.close();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            
        }

    }
	
	private void mostrarNotificacao(Intent in)
	{
		String porta = in.getStringExtra("porta");
		String nome = in.getStringExtra("nome_sala");
		String ip = in.getStringExtra("IP");
		
		Intent i = new Intent(this, Servidor.class);
	   	i.putExtra("nome_sala", nome);
	    i.putExtra("end_ip", ip);
	    i.putExtra("num_porta", porta);
	    int N_ID = (int) System.currentTimeMillis();
		PendingIntent intent = PendingIntent.getActivity(this, N_ID, i, 0);
		Resources r = getResources();
	    Notification notification = new NotificationCompat.Builder(this)
	            .setTicker(nome)
	            .setSmallIcon(R.drawable.ic_notification)
	            .setContentTitle(nome)
	            .setContentText(r.getString(R.string.notification_text))
	            .setContentIntent(intent)
	            .setAutoCancel(false)
	            .build();
	    
	    startForeground(N_ID, notification);
	}
}
