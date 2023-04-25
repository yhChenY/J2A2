package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MessageType;

import java.sql.*;
import java.util.ArrayList;

public class DBOpe {
    private static final String driver = "org.postgresql.Driver";
    private static final String url = "jdbc:postgresql://localhost:5432/j2a2";
    private static final String user = "postgres";
    private static final String password = "cyh.1592364780.";
    
    public static void addMessage(Message m) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        String sql = "insert into messages values(?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1,m.getSentBy());
        ps.setString(2,m.getSendTo());
        ps.setLong(3,m.getTimestamp());
        ps.setString(4,m.getData());
        ps.executeUpdate();
        connection.close();
    }
    
    
    public static ArrayList<Message> getMessages(Long time) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        String sql = "select * from messages where time > ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1,time);
        ResultSet resultSet = ps.executeQuery();
        ArrayList<Message> ans = new ArrayList<>();
        while (resultSet.next()){
            ans.add(new Message(MessageType.SEND, resultSet.getLong("time"), resultSet.getString("sentby"), resultSet.getString("sendto"), resultSet.getString("content")));
        }
        connection.close();
        return ans;
    }
}
