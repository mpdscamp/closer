package com.labprog.closer;

public class Image {
    private String groupId;
    private String userId;
    private String imageUrl;
    private String username;

    public Image(String userId, String groupId, String content, String username) {
        this.groupId = groupId;
        this.userId = userId;
        this.imageUrl = content;
        this.username = content;
    }

    public String getUserId() {
        return userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

