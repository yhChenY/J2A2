package cn.edu.sustech.cs209.chatting.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
    public static ArrayList<String> onlineUsers;
    public static void main(String[] args) throws IOException {
        onlineUsers = new ArrayList<>();
        onlineUsers.add("ROOT");
        ServerSocket server = new ServerSocket(8889);
        while(true){
            try{
                System.out.println("Server waiting");
                Socket socket = server.accept();
                System.out.println("Connected");
                ServerService serverService = new ServerService(socket);
                serverService.start();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
