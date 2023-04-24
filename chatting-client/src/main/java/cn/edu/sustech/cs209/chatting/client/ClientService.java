package cn.edu.sustech.cs209.chatting.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientService extends Thread {
    private Socket soc;
    private Scanner in;
    private PrintWriter out;
    private String user;
    private boolean exit = false;
    private String[] onlineUsers;
    
    public ClientService() {
        try {
            onlineUsers = new String[0];
            soc = new Socket("localhost", 8889);
            in = new Scanner(soc.getInputStream());
            out = new PrintWriter(soc.getOutputStream());
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
        System.out.println("start service");
        try {
            while (!Thread.interrupted()) {
                String str;
                if ((str = in.next()) != null) {
                    System.out.println("Received:" + str);
                    execute(str);
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
        }
    }
    
    private void execute(String command) {
        if (command.equals("UPDATE_ONLINE_USERS")) {
            onlineUsers = in.next().split(",");
            return;
        }
    }
    
    public void refreshOnlineUsers() throws InterruptedException {
        out.println("GET_ONLINE_USERS");
        out.flush();
        Thread.sleep(50);
    }
    
    public void login(String user) {
        this.user = user;
        out.println("LOG_IN " + user);
        out.flush();
    }
    
    public void quit() throws IOException {
        System.out.println("call quit()");
        interrupt();
    }
    
    public String[] getOnlineUsers() {
        return onlineUsers;
    }
}
