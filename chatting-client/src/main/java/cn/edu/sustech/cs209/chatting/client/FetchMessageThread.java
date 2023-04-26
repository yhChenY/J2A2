package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MessageType;

import java.io.IOException;

public class FetchMessageThread extends Thread {
  private long time;
  private ClientService service;

  private Controller controller;

  public FetchMessageThread(ClientService s, Controller c) {
    service = s;
    controller = c;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(1000);
        Message m = new Message(MessageType.FETCH_USER_LIST, System.currentTimeMillis(), "user", "[server]", "user");
        service.sendMessage(m);
        Thread.sleep(1000);
        m = new Message(MessageType.FETCH_MEMS, System.currentTimeMillis(), service.user, "[server]", service.user);
        service.sendMessage(m);
      } catch (InterruptedException | IOException e) {
//        throw new RuntimeException(e);
      }
    }
  }
}
