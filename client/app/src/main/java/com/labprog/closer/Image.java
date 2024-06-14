package com.labprog.closer;

public class Image {
    private String userId;
    private String groupId;
    private String content;

    public Image(String userId, String groupId, String content) {
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

