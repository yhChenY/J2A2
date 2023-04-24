package cn.edu.sustech.cs209.chatting.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerService extends Thread {
    private Socket soc;
    private Scanner in;
    private PrintWriter out;
    private String user;
    
    public ServerService(Socket socket) {
        soc = socket;
    }
    
    @Override
    public void run() {
        try {
            try {
                in = new Scanner(soc.getInputStream());
                out = new PrintWriter(soc.getOutputStream());
                doService();
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                soc.close();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void doService() throws IOException, InterruptedException {
        while (true) {
            String str;
            if ((str = in.nextLine()) != null) {
                System.out.println("Received:" + str);
                execute(str);
            } else {
                System.out.println("No input:server");
                Thread.sleep(500);
            }
        }
    }
    
    private void execute(String command) {
        String[] strs = command.split(" ");
        if (strs[0].equals("GET_ONLINE_USERS")) {
            StringBuilder sb = new StringBuilder("UPDATE_ONLINE_USERS ");
            sb.append(ChatServer.onlineUsers.get(0));
            if (ChatServer.onlineUsers.size() > 1) {
                for (String s : ChatServer.onlineUsers.subList(1, ChatServer.onlineUsers.size())) {
                    sb.append(",").append(s);
                }
            }
            out.println(sb.toString());
            out.flush();
        } else if (strs[0].equals("LOG_IN")) {
            String user = strs[1];
            System.out.println("Add user "+user);
            ChatServer.onlineUsers.add(user);
        }
    }
}
