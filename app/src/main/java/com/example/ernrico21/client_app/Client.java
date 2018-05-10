package com.example.ernrico21.client_app;

import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
/*
Classe per la creazione dei client
*/
public class Client {

    String messageToServer;
    String ip;
    int port;
    boolean connected;
    String messageFromServer;
    MessageActivity activity;
    Socket socket;
    /*
    Costrutture:
    String contente indirizzo ip
    int con il numero di porta
    activity con la listview da aggiornare quando arrivano i messaggi
     */
    Client(String ip, int port,MessageActivity activity ){

        this.ip=ip;
        this.port=port;
        this.activity=activity;
        messageToServer="";//Variabile che quando viene modificata viene inviata come messaggio al server
        messageFromServer="";
        connected=true;
        socket=null;
        ClientThread clientThread=new ClientThread();
        clientThread.start();

    }

    /*
    Thread dove sarà presente il socket in contatto con il server
     */
    private class ClientThread extends Thread{

        @Override
        public void run(){

            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(ip,port);//apro il socket
                dataOutputStream=new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF("Connection Request");//messagio opzione che ho deciso di inviare (al momento non ha nessun uso)
                dataOutputStream.flush();

                while (connected){
                    if(dataInputStream.available()>0){

                        messageFromServer=dataInputStream.readUTF();
                        /*
                        Se arriva un messaggio aggiorno la ListView nel main Thead
                         */
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.messageList.add(messageFromServer);
                                activity.adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    //controllo se la variabile non è vuota vuol dire che c'è un messaggio da inviare
                    if(!messageToServer.equals("")){
                        dataOutputStream.writeUTF("\n"+messageToServer+"\n");
                        dataOutputStream.flush();
                        messageToServer="";//" riazzero" la variabile
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"ERROR IN CONNECTING TO SERVER",Toast.LENGTH_LONG).show();
                    }
                });
            }
            finally {
                if(socket!= null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    /*
    Thread per la chiusura del socket
     */
    private class CloseSocketThread extends Thread{
        @Override
        public void run(){
            try {
                if (socket!=null){
                    connected=false;
                    DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF("ClientDisconnectedCloseSocket");//invio messaggio per informare che ho disconnesso il client
                    dataOutputStream.flush();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message){
        messageToServer=message;
    }

    public void onDestroy(){
        CloseSocketThread closeSocketThread=new CloseSocketThread();
        closeSocketThread.start();
    }

}
