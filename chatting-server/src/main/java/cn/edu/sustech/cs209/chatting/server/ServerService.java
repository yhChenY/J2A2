package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MessageType;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ServerService extends Thread {
  private Socket soc;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private String user;

  public ServerService(Socket socket) {
    soc = socket;
  }

  @Override
  public void run() {
    try {
      try {
//                System.out.println(666);
        Scanner sc = new Scanner(soc.getInputStream());
//                System.out.println(678);
        out = new ObjectOutputStream(soc.getOutputStream());
//                System.out.println(777);
        in = new ObjectInputStream(soc.getInputStream());
//                System.out.println(888);
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
    } catch (IOException | InterruptedException | ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
  }

  private void doService() throws IOException, InterruptedException, ClassNotFoundException, SQLException {
    while (true) {
      Message m;
      if ((m = (Message) in.readObject()) != null) {
        System.out.println("Received " + m);
        execute(m);
      } else {
        System.out.println("No input:server");
        Thread.sleep(500);
      }
    }
  }

  private void execute(Message m) throws IOException, SQLException {
    if (m.getType() == MessageType.FETCH_USER_LIST) {
      String data = Arrays.toString(ChatServer.onlineUsers.toArray());
      Message to = new Message(MessageType.FETCH_USER_LIST, System.currentTimeMillis(), "server", "user", data);
      sendMessage(to);
    } else if (m.getType() == MessageType.LOGIN) {
      String data = m.getData();
      user = data;
      System.out.println("Add user " + data);
      ChatServer.onlineUsers.add(data);
    } else if (m.getType() == MessageType.SEND) {
      DBOpe.addMessage(m);
    } else if (m.getType() == MessageType.FETCH_MEMS) {
      String uu = m.getData();
      ArrayList<Message> messages = DBOpe.getMessages(0L);
      List<Message> filtered = messages.stream().filter(mm -> uu.equals(m.getSentBy()) || m.getSendTo().contains(uu)).collect(Collectors.toList());
      for (Message tmp : filtered) {
        sendMessage(tmp);
      }
    }
  }

  private void sendMessage(Message m) throws IOException {
    out.writeObject(m);
    out.flush();
  }
}
