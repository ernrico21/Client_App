package com.example.ernrico21.client_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button btnConnect;
    EditText txtIP;
    EditText txtPort;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect=(Button)findViewById(R.id.btnConnect);
        txtIP=(EditText)findViewById(R.id.txtIP);
        txtPort =(EditText)findViewById(R.id.txtPort);

        intent = new Intent(this,MessageActivity.class);

        btnConnect.setOnClickListener(buttonConnectListener);

    }

    View.OnClickListener buttonConnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int port=0;
            String ip="";
            //regex per indirizzi IP
            Pattern pattern= Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
            ip=txtIP.getText().toString();
            Matcher matcher=pattern.matcher(ip);
            if(!matcher.matches()){
                Toast.makeText(MainActivity.this,"INSERT A VALID IPV4 ADRRES",Toast.LENGTH_LONG).show();
                return;
            }
            String portText=txtPort.getText().toString();
            try {
                port=Integer.parseInt(portText);

            }catch (NumberFormatException e){
                Toast.makeText(MainActivity.this,"INSERT A VALID PORT NUMBER",Toast.LENGTH_LONG).show();
                return;
            }

            intent.putExtra("Port",portText);
            intent.putExtra("IP",ip);
            startActivity(intent);

        }
    };


}
