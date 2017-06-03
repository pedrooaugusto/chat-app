package com.trickstar.projecteggman;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Servidor extends Activity {

	EditText sala, ip, porta;
	Button run;
	String noWifi = "SEM REDE WIFI";
    HashMap<String, Integer> notifications = new HashMap<String, Integer>();
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servidor);
		run = (Button) findViewById(R.id.button1);
		ip = (EditText) findViewById(R.id.editText3);
		sala = (EditText) findViewById(R.id.editText1);
		porta = (EditText) findViewById(R.id.editText2);
		if(getIntent().getExtras() != null)
		{
			try
			{
				Intent i = getIntent();
				ip.setText(i.getStringExtra("end_ip"));
				sala.setText(i.getStringExtra("nome_sala"));
				String c = i.getStringExtra("num_porta");
				porta.setText(c);
		        ip.setFocusable(false);
		        ip.setClickable(false);
	            run.setText("Stop");
	            sala.setFocusable(false);
	            sala.setClickable(false);
	            porta.setFocusable(false);
	            porta.setClickable(false);
			}catch(Exception e)
			{
				Diversos.showAlert("no", e.getLocalizedMessage(), this);
			}
		}
		else
		{
			String localIP = noWifi;
			try 
			{
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				while(interfaces.hasMoreElements()) 
				{
					NetworkInterface iface = interfaces.nextElement();
					if (iface.isLoopback() || !iface.isUp())
						continue;
					Enumeration<InetAddress> addresses = iface.getInetAddresses();
					while(addresses.hasMoreElements()) 
					{
						InetAddress addr = addresses.nextElement();
						if(addr.getHostAddress().contains("."))
						{
							localIP = addr.getHostAddress();
						}
					}
				}
			}
			catch (SocketException e) 
			{
				throw new RuntimeException(e);
			}
			ip.setText(localIP);
	        ip.setFocusable(false);
	        ip.setClickable(false);
		}
	}
	
	public void runServer(View v)
	{
		if(run.getText().equals("Stop"))
		{
			stopService(new Intent(this, Service_Servidor.class));
			finish();
		}
		else
		{
			if(	!sala.getText().toString().isEmpty() && !ip.getText().toString().isEmpty() &&
				!porta.getText().toString().isEmpty() && !ip.getText().toString().equals(noWifi))
			{
				try
				{
	                Intent n = new Intent(this, Service_Servidor.class);
	                n.putExtra("porta", porta.getText().toString());
	                n.putExtra("nome_sala", sala.getText().toString());
	                n.putExtra("IP", ip.getText().toString());
	                run.setText("Stop");
	                sala.setFocusable(false);
	                sala.setClickable(false);
	                porta.setFocusable(false);
	                porta.setClickable(false);
	                startService(n);
				}
				catch(Exception e)
				{
					Diversos.showAlert("Error", e.getMessage(),this);
				}
			}
			else
			{
				Diversos.showAlert("Em Branco", "1-Preencha todos os campos\n2-Esteja conectado a uma rede wifi", this);
			}
		}
	}	
}
