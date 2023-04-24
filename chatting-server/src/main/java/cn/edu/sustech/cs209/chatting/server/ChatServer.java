package cn.edu.sustech.cs209.chatting.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
    private ArrayList<String> onlineUsers;
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8889);
        System.out.println("Waiting for clients to connect");
        Socket s = server.accept();
        System.out.println("Connected");
        ServerService serverService = new ServerService(s);
        Thread t = new Thread(serverService);
        t.start();
    }
}
