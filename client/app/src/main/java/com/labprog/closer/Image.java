package com.labprog.closer;

public class Image {
    private int imageId;
    private int groupId;
    private int userId;
    private String imageUrl;
    private String username;

    public Image(int imageId, int groupId, int userId, String imageUrl, String username) {
        this.imageId = imageId;
        this.groupId = groupId;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.username = username;
    }

    public int getImageId() {
        return imageId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getUserId() {
        return userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
