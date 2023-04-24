package cn.edu.sustech.cs209.chatting.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerService implements Runnable {
    private Socket soc;
    private BufferedReader in;
    private PrintWriter out;
    private String user;
    
    public ServerService(Socket socket) {
        soc = socket;
    }
    
    @Override
    public void run() {
        try {
            try {
                in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                out = new PrintWriter(soc.getOutputStream());
                System.out.println("Service start");
                doService();
                System.out.println("Service finished");
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
            if ((str = in.readLine()) != null) {
                execute(str);
            } else {
                System.out.println("No input:server");
                Thread.sleep(500);
            }
        }
    }
    
    private void execute(String command) {
        String[] strs = command.split(" ");
        if(strs[0].equals("GET_ONLINE_USERS")){
            out.println("123");
            out.flush();
        }
    }
}
