package org.example;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;
public class Server {
    static Vector<ClientHandler> clients = new Vector<>();//Vector to store client handlers
    static HashMap<String, Integer> messageCounts = new HashMap<>();//Mark # of each client's messages
    static List<String> messageHistory = new ArrayList<>();//List of message history
    public static void main(String[] args) throws IOException{
        ServerSocket ss = new ServerSocket(214);//It's Setsuna's birthday! （She is the heroine of White Album 2)
        Socket s;
        while(true){
            s = ss.accept();
            System.out.println("New client request received : " + s);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            for(String record : messageHistory) dos.writeUTF(record);//Send chat history to the new client

            dos.writeUTF("Enter your username for the group chat:");
            String name = dis.readUTF();

            synchronized (clients) {
                for(ClientHandler mc : clients){
                    mc.dos.writeUTF("SERVER: " + name + " has entered the chat!" + "  " + getCurrentTime());
                }
            }
            ClientHandler mtch = new ClientHandler(s, name, dis, dos);
            Thread t = new Thread(mtch);
            clients.add(mtch);
            messageCounts.put(name, 0);
            t.start();
        }
    }
    //Get current time
    public static String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }
    //Two functions to count # of messages
    public static void addMessageCount(String name){
        int count = messageCounts.getOrDefault(name, 0);
        messageCounts.put(name, count + 1);
    }
    public static int getMessageCount(String name){
        return messageCounts.getOrDefault(name, 0);
    }
    //Save chat records to local file
    public static void saveRecords(){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("chatRecords.txt"));
            for(String record : messageHistory) {
                writer.write(record);
                writer.newLine();
            }
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    //Search chat records by keyword
    public static List<String> searchKeyword(String keyword){
        List<String> searchResults = new ArrayList<>();
        for(String record : messageHistory){
            if(record.substring(0, record.length() - 29).contains(keyword)) searchResults.add(record);
        }
        return searchResults;
    }
    //Search chat records by username
    public static List<String> searchUsername(String username){
        List<String> searchResults = new ArrayList<>();
        for(String record : messageHistory){
            if(record.contains(username + ": ")) searchResults.add(record);
        }
        return searchResults;
    }
}

class ClientHandler implements Runnable{
    private final String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;

    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos){
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        System.out.println(Server.getCurrentTime() + " " + name + " has connected.");//Report on the server when a user enters
    }
    public void run(){
        String send;
        while(true){
            try{
                send = dis.readUTF();
                //The feature of searching username
                if(send.contains("#search username")){
                    String username = send.replace("#search username ", "");
                    List<String> searchResults = Server.searchUsername(username);
                    for(String searchRecord : searchResults) {
                        this.dos.writeUTF("#" + searchRecord);
                    }
                    continue;
                }
                //The feature of searching keyword
                if(send.contains("#search keyword")){
                    String keyword = send.replace("#search keyword ", "");
                    List<String> searchResults = Server.searchKeyword(keyword);
                    for(String searchRecord : searchResults) {
                        this.dos.writeUTF("#" + searchRecord);
                    }
                    continue;
                }

                Server.addMessageCount(name);
                String message = name + ": " + send + "  " + Server.getCurrentTime() + " " + Server.getMessageCount(name) + "message(s)";
                Server.messageHistory.add(message);
                Server.saveRecords();
                System.out.println(message);//Print message on server
                //send messages to all the clients
                synchronized (Server.clients) {
                    for (ClientHandler mc : Server.clients) {
                        if (mc != this) mc.dos.writeUTF(message);
                    }
                }
            } catch(SocketException e){//Deal with the problem when a client disconnects
                System.out.println(name + " has disconnected.");
                Server.clients.remove(this);
                try {
                    synchronized (Server.clients) {
                        for (ClientHandler mc : Server.clients) {
                            mc.dos.writeUTF("SERVER: " + name + " has left the chat!" + "  " + Server.getCurrentTime());
                        }
                    }
                } catch (IOException ex){
                    e.printStackTrace();
                }
                break;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}

