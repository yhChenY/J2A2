package cn.edu.sustech.cs209.chatting.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientService implements Runnable{
    private Socket soc;
    private Scanner in;
    private PrintWriter out;
    private String user;
    
    public ClientService(){
        try{
            soc = new Socket("localhost",8889);
            in = new Scanner(soc.getInputStream());
            out = new PrintWriter(soc.getOutputStream());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            doClientService();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void doClientService() throws InterruptedException{

    }
    
    public String[] getOnlineUsers(){
        String[] ans =null;
        out.println("GET_ONLINE_USERS");
        out.flush();
        while (in.hasNext()){
            String str = in.next();
            ans = str.split(",");
        }
        return ans;
    }
}
