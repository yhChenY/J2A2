package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MessageType;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientService extends Thread {
  private Socket soc;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  public String user;
  private boolean exit = false;
  private String[] onlineUsers;
  ObservableList<ChatTarget> chatTargets;
  Controller controller;

  public ClientService(ObservableList<ChatTarget> chatTargets, Controller c) {
    try {
      this.chatTargets = chatTargets;
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
        if ((m = (Message) in.readObject()) != null) {
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
    if (m.getType() == MessageType.FETCH_USER_LIST) {
      String data = m.getData();
      onlineUsers = data.substring(1, data.length() - 1).split(", ");
      try {
        controller.currentOnlineCnt.setText("Online: " + onlineUsers.length);
      } catch (NullPointerException e) {
//                System.out.println("NULL Pointer Ex");
        // do nothing
      }
    }
    if (m.getType() == MessageType.SEND) {
      boolean existChat = false;
      for (ChatTarget ct : chatTargets) {
        if (ct.getMembers().toString().equals(m.getSendTo()) && (m.getSentBy().equals(user) || ct.getMembers().contains(user))) {
          existChat = true;
          ct.addMessage(m);
          break;
        }
      }
      if (!existChat && (m.getSentBy().equals(user) || m.getSendTo().contains(user))) {
        String[] members = m.getSendTo().substring(1, m.getSendTo().length() - 1).split(", ");
        String title;
        if (members.length > 3) {
          title = members[0] + "," + members[1] + "," + members[2] + "...(" + members.length + ")";
        } else {
          title = members[0];
          for (int i = 1; i < members.length; i++) {
            title = title + "," + members[i];
          }
        }
        ChatTarget tmp = new ChatTarget(title, m.getSendTo());
        chatTargets.add(tmp);
        tmp.addMessage(m);
      }
//            controller.chatList.setItems(controller.chatTargets);
    }

//        if (m.getType() == MessageType.SEND) {
//            if (m.getSentBy().equals(user) || m.getSendTo().contains(user)) {
//                boolean flag = false;
//                for (ChatTarget ct : chatTargets) {
//                    boolean inflag = false;
//                    for (String mem : ct.getMembers()) {
//                        if (!m.getSendTo().contains(mem)) {
//                            inflag = true;
//                            break;
//                        }
//                    }
//                    if(inflag){
//
//                    }
//                }
//                if (flag) {
//                    String[] members = m.getSendTo().substring(1, m.getSendTo().length() - 1).split(", ");
//                    String title;
//                    if (members.length > 3) {
//                        title = members[0] + "," + members[1] + "," + members[2] + "...(" + members.length + ")";
//                    } else {
//                        title = members[0];
//                        for (int i = 1; i < members.length; i++) {
//                            title = title + "," + members[i];
//                        }
//                    }
//                    ChatTarget ct = new ChatTarget(title, );
//                } else {
//
//                }
//
//            }
//        }
  }

  public void refreshOnlineUsers() throws InterruptedException, IOException {
    Message m = new Message(MessageType.FETCH_USER_LIST, System.currentTimeMillis(), "client", "[server]", "nodata");
    sendMessage(m);
    Thread.sleep(50);
  }

  public void login(String user) throws IOException {
    this.user = user;
    Message m = new Message(MessageType.LOGIN, System.currentTimeMillis(), user, "[server]", user);
    sendMessage(m);
    Message test = new Message(MessageType.SEND, System.currentTimeMillis(), user, "[server]", "testData");
    sendMessage(test);
  }

  public void quit() throws IOException {
    System.out.println("call quit()");
    interrupt();
  }

  public String[] getOnlineUsers() {
    return onlineUsers;
  }

  public void sendMessage(Message m) throws IOException {
    out.writeObject(m);
    out.flush();
  }
}
