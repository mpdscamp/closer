package com.labprog.closer;

public class Image {
    private int imageId;
    private int groupId;
    private int userId;
    private String imageUrl;
    private String username;
    private int likes;
    private int dislikes;

    public Image(int imageId, int groupId, int userId, String imageUrl, String username, int likes, int dislikes) {
        this.imageId = imageId;
        this.groupId = groupId;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.username = username;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
