package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    final static int ServerPort = 214;//It's Setsuna's birthday! （She is the heroine of White Album 2)
    public static void main(String[] args) throws UnknownHostException, IOException{
        Scanner scanner = new Scanner(System.in);
        InetAddress ip = InetAddress.getByName("localhost");
        Socket s = new Socket(ip, ServerPort);
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        //The Thread of sending messages
        Thread sendMessage = new Thread(() ->{
            while(true){
                String message = scanner.nextLine();
                try{
                    dos.writeUTF(message);
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
        //The Thread of reading messages
        Thread readMessage = new Thread(() -> {
            while(true){
                try{
                    String message = dis.readUTF();
                    System.out.println(message);
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
}
