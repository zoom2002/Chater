package com.example.chater.model;

public class Conversation {
    private int targetUserId;
    private String targetUserName;
    private String lastMessage;
    private long lastTimestamp;
    private int unreadCount;

    public Conversation(int targetUserId, String targetUserName, String lastMessage, long lastTimestamp, int unreadCount) {
        this.targetUserId = targetUserId;
        this.targetUserName = targetUserName;
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
        this.unreadCount = unreadCount;
    }
    
    public Conversation() {}

    public int getTargetUserId() { return targetUserId; }
    public void setTargetUserId(int targetUserId) { this.targetUserId = targetUserId; }
    public String getTargetUserName() { return targetUserName; }
    public void setTargetUserName(String targetUserName) { this.targetUserName = targetUserName; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public long getLastTimestamp() { return lastTimestamp; }
    public void setLastTimestamp(long lastTimestamp) { this.lastTimestamp = lastTimestamp; }
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
