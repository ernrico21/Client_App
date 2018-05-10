package com.example.ernrico21.client_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    Button btnSendMessage;
    EditText txtMessage;
    ListView lstMessage;
    List<String>messageList;
    ArrayAdapter<String> adapter;
    Client client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        String ip= getIntent().getStringExtra("IP");
        String s_port = getIntent().getStringExtra("Port");
        int port=Integer.parseInt(s_port);

        btnSendMessage=(Button)findViewById(R.id.btnSendMessage);
        txtMessage=(EditText)findViewById(R.id.txtMessage);
        lstMessage=(ListView)findViewById(R.id.lstMessage);

        messageList=new ArrayList<String>();
        messageList.add("Messages:");
        adapter=new ArrayAdapter<String>(this,R.layout.raw,messageList);
        lstMessage.setAdapter(adapter);

        client=new Client(ip,port,MessageActivity.this);

        btnSendMessage.setOnClickListener(buttonSendMessageListener);
    }

    View.OnClickListener buttonSendMessageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String messageToSend=txtMessage.getText().toString();
            if (!messageToSend.equals("")){
                txtMessage.setText("");
                txtMessage.clearFocus();
                btnSendMessage.requestFocus();

                //nascondo la tastiera
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                client.sendMessage(messageToSend);
            }
            else{
                Toast.makeText(MessageActivity.this,"WRITE A MESSAGE TO SEND",Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.onDestroy();
    }

    /*
    Se viene premuto il tasto "Indietro" deve essere chiuso il socketserver
    altrimenti se dopo provo a ricrearlo va in errore.
    Chiedo creo un AlertBox per chiedere se è sicurto dell'operazione
     */

    @Override
    public void onBackPressed() {
        final Intent intent2 = new Intent(this,MainActivity.class);
        final AlertDialog alertDialog = new AlertDialog.Builder(MessageActivity.this).create();
        alertDialog.setTitle("ATTENTION");
        alertDialog.setMessage("THIS ACTION WILL CLOSE THE CONNECTION WITH SERVER");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CONTINUE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        client.onDestroy();
                        startActivity(intent2);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }
}
