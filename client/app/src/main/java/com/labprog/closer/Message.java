package com.labprog.closer;

public class Message {
    private String userId;
    private String groupId;
    private String content;

    public Message(String userId, String groupId, String content) {
        this.userId = userId;
        this.groupId = groupId;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getContent() {
        return content;
    }
}
