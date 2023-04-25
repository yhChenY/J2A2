package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ChatTarget {
    private String title;
    private ArrayList<String> members;
    private ObservableList<Message> messages;
    
    public ChatTarget() {
        
    }
    
    public ChatTarget(String ptitle, String pmembers) {
        this.title = ptitle;
        messages = FXCollections.observableList(new ArrayList<>());
        members = new ArrayList<>();
        members.addAll(Arrays.asList(pmembers.substring(1, pmembers.length() - 1).split(", ")));
    }
    
    public String getTitle() {
        return title;
    }
    
    public ArrayList<String> getMembers() {
        return members;
    }
    
    public ObservableList<Message> getMessages() {
        return messages;
    }
    
    public void addMessage(Message m) {
        boolean flag = true;
        for (Message tmp : messages) {
            if (Objects.equals(tmp.getTimestamp(), m.getTimestamp()) && tmp.getSentBy().equals(m.getSentBy()) && tmp.getSendTo().equals(m.getSendTo())) {
                flag = false;
                break;
            }
        }
        if(flag){
            messages.add(m);
        }
    }
}
