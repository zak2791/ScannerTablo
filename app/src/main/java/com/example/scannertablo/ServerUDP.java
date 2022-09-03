package com.example.scannertablo;

import static android.content.Context.CLIPBOARD_SERVICE;



import android.content.ClipData;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ServerUDP implements Runnable {

    byte[] buf_send = new byte[32];// = null;
    byte[] buf_id = new byte[32];
    byte[] buf_recieve = new byte[20];
    static int port = 10000;
    DatagramSocket udpServerSocket = null;
    DatagramSocket udpClientSocket = null;
    boolean process = true;
    static boolean flag_send = false;

    public  static void setFlag_send() {flag_send = true;}

    @Override
    public void run() {
        try {
            udpServerSocket = new DatagramSocket(10000);
            udpServerSocket.setSoTimeout(2000);
            flag_send = false;
        }
        catch (SocketException e) {
            process = false;
        }
        try{
            udpClientSocket = new DatagramSocket();
        } catch (SocketException e) {
            Log.e("Udp Client:", "Socket Error:", e);
            process = false;
        }
        while(process) {
                try {
                    DatagramPacket dp;
                    DatagramPacket dpSend;                                                                      //////////////////////////////////////
                    dp = new DatagramPacket(buf_recieve, buf_recieve.length);
                    udpServerSocket.receive(dp);
                    InetAddress address = dp.getAddress();
                    String s = new String(buf_recieve, 0,7);
                    if(s.startsWith("Camera1") || s.startsWith("Camera2")) {
                        if(flag_send){
                            flag_send = false;
                            byte[] b = "Camera".getBytes();
                            dpSend = new DatagramPacket(b, b.length, address, 20000);
                            udpClientSocket.send(dpSend);

                            String message;
                            String text;
                            if(s.startsWith("Camera1")){
                                text = "srt:/" + address.toString() + ":1111";
                                message = "Подключено к  " + address.toString() + "\nURL srt:/" + address.toString() + ":1111 находится в буфере обмена. " +
                                        "Откройте приложение 'Larix Broadcaster', перейдите в настройки->Connections->Manage connections, " +
                                        "далее выберите необходимое соединение и там нажмите строку 'URL'. " +
                                        "Затем, в появившемся окне, длительным нажатием на строку с url-адресом выделите её содержимое " +
                                        "и в контекстном меню выберите 'вставить'.";
                            }
                            else {
                                text = "srt:/" + address.toString() + ":2222";
                                message = "Подключено к  " + address.toString() + "\nURL srt:/" + address.toString() + ":2222 находится в буфере обмена. " +
                                        "Откройте приложение 'Larix Broadcaster', перейдите в настройки->Connections->Manage connections, " +
                                        "далее выберите необходимое соединение и там нажмите строку 'URL'. " +
                                        "Затем, в появившемся окне, длительным нажатием на строку с url-адресом выделите её содержимое " +
                                        "и в контекстном меню выберите 'вставить'.";
                            }


                            Message msg = Message.obtain();
                            msg.obj = message;
                            MainActivity.h2.sendMessage(msg);
                            ClipData myClip;
                            myClip = ClipData.newPlainText("text", text);
                            MainActivity.myClipboard.setPrimaryClip(myClip);
                        }
                        else {
                            String message = "Подключить к\n" + address.toString() + " ?";
                            Message msg = Message.obtain();
                            msg.obj = message;
                            MainActivity.h.sendMessage(msg);
                        }
                    }
                } catch (SocketTimeoutException e){
                    Log.i("Udp timeout:", "timeout:", e);
                    String message = "Нет сигнала";
                    Message msg = Message.obtain();
                    msg.obj = message;
                    MainActivity.h.sendMessage(msg);
                } catch (IOException e) {
                    Log.i("Udp Send:", "IO Error:", e);
                }
        }
        if(udpServerSocket != null) { udpServerSocket.close(); }
        if(udpClientSocket != null) { udpClientSocket.close(); }
    }
}
