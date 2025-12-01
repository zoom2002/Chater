package com.example.chater.model;

public class Message {
    public static final int SENDER_SELF = 0;

    private long id;
    private String content;
    private long timestamp;
    private int senderId; // 0表示自己，其他表示对方ID
    private int chatId; // 所属会话ID（即对方用户的ID）
    
    public Message(String content, long timestamp, int senderId, int chatId) {
        this.content = content;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.chatId = chatId;
    }

    public Message() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public int getChatId() { return chatId; }
    public void setChatId(int chatId) { this.chatId = chatId; }
}
