package com.trickstar.projecteggman;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView sobre;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sobre = (TextView) findViewById(R.id.textView4);
        sobre.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				clickInfo(v);
			}
        });
    }
	
    public void clickInfo(View v)
    {
    	Dialog dialog=new Dialog(this,android.R.style.Theme_NoTitleBar_Fullscreen);
    	dialog.setContentView(R.layout.html_info);
    	WebView wb = (WebView) dialog.findViewById(R.id.webView1);
    	wb.loadUrl("file:///android_asset/infoClara.html");
        dialog.show();
    }
    
    public void clickServidor(View v)
    {
		Intent i = new Intent(this, Servidor.class);
		MainActivity.this.startActivity(i);
    }
    public void clickCliente(View v)
    {
		Intent i = new Intent(this, Cliente.class);
		MainActivity.this.startActivity(i);
    }
}
