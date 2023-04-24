package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MessageType;

import java.io.*;
import java.net.Socket;

public class ClientService extends Thread {
    private Socket soc;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String user;
    private boolean exit = false;
    private String[] onlineUsers;
    
    public ClientService() {
        try {
            onlineUsers = new String[0];
            soc = new Socket("localhost", 8888);
            System.out.println("Connected");
            in = new ObjectInputStream(soc.getInputStream());
//            System.out.println("in created");
            out = new ObjectOutputStream(soc.getOutputStream());
//            System.out.println("out created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
//            refreshOnlineUsers();
            doClientService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void doClientService() throws IOException {
        System.out.println("start client service");
        try {
            while (!Thread.interrupted()) {
                Message m;
                if ((m = (Message)in.readObject() ) != null) {
                    System.out.println("Received:" + m);
                    execute(m);
                } else {
                    System.out.println("No input:client");
                    Thread.sleep(500);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("quit");
            in.close();
            out.close();
            soc.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void execute(Message m) {
        if(m.getType() == MessageType.FETCH_USER_LIST){
            String data = m.getData();
            onlineUsers = data.substring(1,data.length()-1).split(", ");
        }
    }
    
    public void refreshOnlineUsers() throws InterruptedException, IOException {
        Message m = new Message(MessageType.FETCH_USER_LIST,System.currentTimeMillis(),"client","server","nodata");
        sendMessage(m);
        Thread.sleep(50);
    }
    
    public void login(String user) throws IOException {
        this.user = user;
        Message m = new Message(MessageType.LOGIN,System.currentTimeMillis(),user,"server",user);
        sendMessage(m);
    }
    
    public void quit() throws IOException {
        System.out.println("call quit()");
        interrupt();
    }
    
    public String[] getOnlineUsers() {
        return onlineUsers;
    }
    
    private void sendMessage(Message m) throws IOException {
        out.writeObject(m);
        out.flush();
    }
}
