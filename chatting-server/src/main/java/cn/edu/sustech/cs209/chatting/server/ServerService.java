package cn.edu.sustech.cs209.chatting.server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerService implements Runnable{
    private Socket soc;
    private Scanner in;
    private PrintWriter out;

    public ServerService(Socket socket){
        soc = socket;
    }
    @Override
    public void run() {

    }
}
