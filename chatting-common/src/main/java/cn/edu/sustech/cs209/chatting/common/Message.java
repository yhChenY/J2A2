package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public class Message implements Serializable {
    
    private final MessageType type;
    private Long timestamp;

    private String sentBy;

    private String sendTo;

    private String data;

    
    public Message(MessageType type, Long timestamp, String sentBy, String sendTo, String data) {
        this.type = type;
        this.timestamp = timestamp;
        this.sentBy = sentBy;
        this.sendTo = sendTo;
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getSentBy() {
        return sentBy;
    }

    public String getSendTo() {
        return sendTo;
    }

    public String getData() {
        return data;
    }
    
    public MessageType getType(){
        return type;
    }
    
    @Override
    public String toString(){
        return "MessageType: " + type
            + " from: " + sentBy
            + " to: " + sendTo
            + " time: " + timestamp.toString()
            + " data: " + data;
    }
}
